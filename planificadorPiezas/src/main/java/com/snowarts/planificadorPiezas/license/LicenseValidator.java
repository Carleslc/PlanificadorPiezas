package com.snowarts.planificadorPiezas.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.DumpError;
import com.snowarts.planificadorPiezas.license.utils.ConnectionUtils;
import com.snowarts.planificadorPiezas.presentation.utils.BounceProgressBar;
import com.snowarts.planificadorPiezas.presentation.utils.ErrorMessage;
import com.snowarts.planificadorPiezas.presentation.utils.Message;
import com.snowarts.planificadorPiezas.presentation.utils.PanelFactory;

public final class LicenseValidator {
	
	private static final YamlFile LICENSE_FILE = new YamlFile(System.getProperty("user.dir") + "/.license");
	
	static final String VALIDATION_SERVER = "http://67.207.76.44:8081/";
	private static final String CRYPT_ALGORITHM = "AES";
	private static boolean CHECK_OFFLINE = true;
	
	private String product, license;
	private Runnable onSuccess;
	private LocalDate expiration;
	private JFrame parent;
	
	private LicenseValidator(String product, String license, JFrame parent, Runnable onSuccess) {
		this.product = product;
		this.license = license;
		this.parent = parent;
		this.onSuccess = onSuccess;
	}
	
	public static void validate(String product, JFrame parent, Runnable onSuccess) throws IOException {
		new LicenseValidator(product, getLicense(), parent, onSuccess).validate();
	}
	
	private static String getLicense() throws IOException {
		String license = null;
		if (LICENSE_FILE.exists()) {
			try {
				LICENSE_FILE.load();
				license = LICENSE_FILE.getString("key");
			} catch (InvalidConfigurationException e) {
				throw new IOException(e);
			}
		}
		return license;
	}
	
	private void validate() {
		boolean saved = license != null;
		if (!saved) newLicense();
		else if (CHECK_OFFLINE && offline()) expirationWarning();
		else validateOnline(false);
	}
	
	private void expirationWarning() {
		if (expiration == null) return;
		LocalDate now = LocalDate.now();
		if (now.plusDays(30).isAfter(expiration)) {
			String[] options = { "Introducir nueva licencia", "Continuar con la licencia actual" };
			JPanel panel = PanelFactory.newPanel();
			JLabel warning = new JLabel("Tu licencia caducará en " + now.until(expiration, ChronoUnit.DAYS) +
					" días (" + DateUtils.format(expiration, FormatStyle.LONG) + ").");
			warning.setBorder(new EmptyBorder(5, 5, 5, 5));
			panel.add(warning);
			int opt = JOptionPane.showOptionDialog(parent, panel, "Advertencia", JOptionPane.NO_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			
			if (opt == 0) {
				license = LicenseDialog.showInput(parent, product);
				validateOnline(true);
			} else onSuccess.run();
		} else onSuccess.run();
	}
	
	private void validateOnline(boolean showIfIsValid) {
		final BounceProgressBar progress = licenseProgressBar();
		progress.start();
		new Thread(() -> {
			try {
				validateServer(showIfIsValid);
				expirationWarning();
				progress.finish();
			} catch (IOException e) {
				progress.finish();
				DumpError.dump(e);
				invalidate("No se ha podido validar la licencia. Prueba de nuevo más tarde.");
			}
		}).start();
	}
	
	private boolean offline() {
		SecretKey secret = decodeSecret(LICENSE_FILE.getString("expiration.secret"));
		expiration = decryptDate(LICENSE_FILE.getString("expiration.offline"), secret);
		return LocalDate.now().isBefore(expiration);
	}
	
	private BounceProgressBar licenseProgressBar() {
		return new BounceProgressBar().title("Licencia").message("Validando Licencia...");
	}
	
	private void newLicense() {
		LicenseDialog.showWithTrial(parent, product,
				(l, isTrial) -> {
					license = l;
					if (license == null) invalidate();
					else if (license.isEmpty()) invalidate("Licencia inválida.");
					else validateOnline(!isTrial);
				});
	}
	
	private void validateServer(boolean showIfIsValid) throws IOException {
		ConnectionUtils.connect(VALIDATION_SERVER + "products/" + product + "/licenses/" + license + ConnectionUtils.getFingerprintParameters(),
				con -> {
					boolean valid = con.getResponseCode() == HttpURLConnection.HTTP_OK;
					String response = ConnectionUtils.read(new BufferedReader(new InputStreamReader(valid ? con.getInputStream() : con.getErrorStream())));
					con.disconnect();
					Gson parser = new GsonBuilder().create();
					if (!valid) notValid(parser.fromJson(response, LicenseMessage.class));
					else validate(showIfIsValid, parser.fromJson(response, LicenseActivation.class));
				},
				e -> invalidate("No se ha podido validar la licencia debido a un error de red. Verifica tu conexión a Internet."));
	}
	
	private void notValid(LicenseMessage reason) throws IOException {
		if (reason.isExpiration()) {
			String message = reason.getMessage() + " Fecha de expiración: " + DateUtils.format(reason.getExpiration(), FormatStyle.LONG) + ".";
			ErrorMessage.show(message);
			newLicense();
		} else invalidate(reason.getMessage());
	}
	
	private void validate(boolean showIfIsValid, LicenseActivation activation) throws IOException {
		if (showIfIsValid && !LICENSE_FILE.exists()) Message.show("Licencia válida.");
		LICENSE_FILE.createNewFile(true);
		LICENSE_FILE.set("key", license);
		expiration = activation.getLicense().getExpiration();
		SecretKey secret = generateSecret();
		LICENSE_FILE.set("expiration.secret", encodeSecret(secret));
		LICENSE_FILE.set("expiration.offline", encryptDate(expiration, secret));
		LICENSE_FILE.save();
	}
	
	private SecretKey generateSecret() {
		try {
			return KeyGenerator.getInstance(CRYPT_ALGORITHM).generateKey();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private String encodeSecret(SecretKey secret) {
		return encode(secret.getEncoded());
	}
	
	private SecretKey decodeSecret(String encodedSecret) {
		byte[] decodedSecret = decode(encodedSecret);
		return new SecretKeySpec(decodedSecret, CRYPT_ALGORITHM);
	}
	
	private String encode(byte[] src) {
		return Base64.getEncoder().encodeToString(src);
	}
	
	private byte[] decode(String src) {
		return Base64.getDecoder().decode(src);
	}
	
	private String encryptDate(LocalDate date, Key secret) {
		try {
			Cipher cipher = Cipher.getInstance(secret.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, secret);
	        return encode(cipher.doFinal(date.toString().getBytes("UTF8")));
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private LocalDate decryptDate(String dateEncrypted, Key secret) {
		try {
			Cipher cipher = Cipher.getInstance(secret.getAlgorithm());
	        cipher.init(Cipher.DECRYPT_MODE, secret);
	        return LocalDate.parse(new String(cipher.doFinal(decode(dateEncrypted)), "UTF8"));
		} catch (NoSuchAlgorithmException | InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException | NoSuchPaddingException | UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	private void invalidate(String message) {
		ErrorMessage.show(message);
		invalidate();
	}
	
	private void invalidate() {
		parent.dispose();
		System.exit(0);
	}
}
