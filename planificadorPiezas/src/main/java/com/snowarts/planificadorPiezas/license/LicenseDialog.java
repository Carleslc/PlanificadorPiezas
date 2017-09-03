package com.snowarts.planificadorPiezas.license;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.Validate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.snowarts.planificadorPiezas.domain.DumpError;
import com.snowarts.planificadorPiezas.license.utils.ConnectionUtils;
import com.snowarts.planificadorPiezas.presentation.utils.BounceProgressBar;
import com.snowarts.planificadorPiezas.presentation.utils.ErrorMessage;
import com.snowarts.planificadorPiezas.presentation.utils.PanelFactory;

final class LicenseDialog {

	private String product;
	private JFrame parent;
	private boolean trial;
	
	private LicenseDialog(JFrame parent, String product, boolean trial) {
		this.parent = parent;
		this.product = product;
		this.trial = trial;
	}
	
	static void showWithTrial(JFrame parent, String product, LicenseCallback callback) {
		Validate.notNull(parent);
		Validate.notNull(product);
		Validate.notNull(callback);
		new LicenseDialog(parent, product, true).menu(callback);
	}
	
	static String show(JFrame parent, String product) {
		Validate.notNull(parent);
		Validate.notNull(product);
		return new LicenseDialog(parent, product, false).fromInput();
	}
	
	static String showInput(JFrame parent, String product) {
		Validate.notNull(parent);
		Validate.notNull(product);
		return new LicenseDialog(parent, product, true).fromInput();
	}
	
	private String fromInput() {
		String[] options = { "Aceptar" };
		JPanel panel = PanelFactory.newPanel();
		JTextField license = new JTextField(15);
		if (!trial) panel.add(mustActivate());
		panel.add(label("Introduce una licencia válida:"));
		panel.add(license);
		int opt = JOptionPane.showOptionDialog(parent, panel, "Introducir licencia", JOptionPane.NO_OPTION,
				trial ? JOptionPane.QUESTION_MESSAGE : JOptionPane.WARNING_MESSAGE, null, options, options[0]);

		return opt == JOptionPane.OK_OPTION ? license.getText().trim() : null;
	}
	
	private void fromTrial(LicenseCallback callback) {
		final BounceProgressBar progress = trialProgressBar();
		progress.start();
		new Thread(() -> {
			try {
				getTrialLicense(callback);
				progress.finish();
			} catch (IOException e) {
				progress.finish();
				error("No se ha podido obtener la licencia. Prueba de nuevo más tarde.");
				DumpError.dump(e);
			}
		}).start();
	}
	
	private void getTrialLicense(LicenseCallback callback) throws IOException {
		ConnectionUtils.connect(LicenseValidator.VALIDATION_SERVER + "products/" + product + "/trial" + ConnectionUtils.getFingerprintParameters(),
				con -> {
					boolean valid = con.getResponseCode() == HttpURLConnection.HTTP_OK;
					String response = ConnectionUtils.read(new BufferedReader(new InputStreamReader(valid ? con.getInputStream() : con.getErrorStream())));
					con.disconnect();
					Gson parser = new GsonBuilder().create();
					if (!valid) error(parser.fromJson(response, LicenseMessage.class).getMessage());
					else callback.onLicense(parser.fromJson(response, LicenseActivation.class).getLicense().getKey(), true);
				},
				e -> error("No se ha podido obtener la licencia debido a un error de red. Verifica tu conexión a Internet."));
	}
	
	private BounceProgressBar trialProgressBar() {
		return new BounceProgressBar().title("Versión de prueba").message("Obteniendo licencia...");
	}
	
	private void menu(LicenseCallback callback) {
		String[] options = { "Introducir licencia", "Obtener una licencia de prueba" };
		JPanel menu = PanelFactory.newPanel();
		menu.add(mustActivate());
		int opt = JOptionPane.showOptionDialog(parent, menu, "Activar producto", JOptionPane.NO_OPTION,
				JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		
		if (opt == 1) fromTrial(callback);
		else callback.onLicense(fromInput(), false);
	}
	
	private void error(String message) {
		ErrorMessage.show(message);
		parent.dispose();
		System.exit(1);
	}
	
	private static JLabel mustActivate() {
		return label("Debes activar este producto para poder utilizarlo.");
	}
	
	private static JLabel label(String message) {
		JLabel label = new JLabel(message);
		label.setBorder(new EmptyBorder(5, 0, 5, 0));
		return label;
	}
	
}
