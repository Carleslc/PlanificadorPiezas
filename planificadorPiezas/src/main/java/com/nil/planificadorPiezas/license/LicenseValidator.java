package com.nil.planificadorPiezas.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.simpleyaml.exceptions.InvalidConfigurationException;
import org.simpleyaml.file.YamlFile;

import com.google.gson.GsonBuilder;
import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.presentation.utils.BounceProgressBar;
import com.nil.planificadorPiezas.presentation.utils.ErrorMessage;
import com.nil.planificadorPiezas.presentation.utils.Message;

import me.carleslc.serialnumber.Hardware;

public final class LicenseValidator {
	
	private static final YamlFile LICENSE_FILE = new YamlFile(System.getProperty("user.dir") + "/.license");
	
	private static final String VALIDATION_SERVER = "http://67.207.76.44:8081/";
	
	private String product, license;
	private JFrame parent;
	
	private LicenseValidator(String product, String license, JFrame parent) {
		this.product = product;
		this.license = license;
		this.parent = parent;
	}
	
	public static void validate(String product, JFrame parent, Runnable onSuccess) throws IOException {
		new LicenseValidator(product, getLicense(), parent).validate(onSuccess);
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
	
	private void validate(final Runnable onSuccess) {
		boolean saved = license != null;
		if (!saved) newLicense();
		final BounceProgressBar progress = licenseProgressBar();
		progress.start();
		new Thread(() -> {
			try {
				validateOnline();
				progress.finish();
				onSuccess.run();
			} catch (IOException e) {
				progress.finish();
				DumpError.dump(e);
				invalidate("No se ha podido validar la licencia.");
			}
		}).start();
	}
	
	private BounceProgressBar licenseProgressBar() {
		BounceProgressBar progress = new BounceProgressBar();
		progress.setTitle("Licencia");
		progress.setMessage("Validando Licencia...");
		return progress;
	}
	
	private void newLicense() {
		boolean blank = true;
		while (blank) {
			license = LicenseDialog.show(parent);
			if (license == null) invalidate();
			else if (!license.isEmpty()) blank = false;
		}
	}
	
	private void validateOnline() throws IOException {
		Map<String, String> parameters = new HashMap<>();
		parameters.put("lang", "es");
		parameters.put("fingerprint", Hardware.getSerialNumber());
		String params = ParameterStringBuilder.getParamsString(parameters);
		
		try {
			URL url = new URL(VALIDATION_SERVER
					+ "products/" + product + "/licenses/" + license + params);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Content-Type", "application/json");
			con.setConnectTimeout(10000);
			con.setReadTimeout(10000);
			
			boolean valid = isValid(con.getResponseCode());
			
			String response = read(new BufferedReader(new InputStreamReader(valid ? con.getInputStream() : con.getErrorStream())));
			
			con.disconnect();
			
			if (valid) {
				if (!LICENSE_FILE.exists()) Message.show("Licencia Válida.");
				LICENSE_FILE.createNewFile(true);
				LICENSE_FILE.set("key", license);
				LICENSE_FILE.save();
			} else invalidate(new GsonBuilder().create().fromJson(response, LicenseMessage.class).getMessage());
		} catch (SocketTimeoutException e) {
			invalidate("No se ha podido validar la licencia debido a un error de red. Verifica tu conexión a Internet.");
		}
	}
	
	private String read(BufferedReader in) throws IOException {
		String inputLine;
		StringBuilder builder = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			builder.append(inputLine);
		}
		in.close();
		return builder.toString();
	}
	
	private boolean isValid(int statusCode) {
		return statusCode == HttpURLConnection.HTTP_OK;
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
