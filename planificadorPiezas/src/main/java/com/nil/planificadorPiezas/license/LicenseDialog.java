package com.nil.planificadorPiezas.license;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

abstract class LicenseDialog {

	private LicenseDialog() {}
	
	static String show(JFrame parent) {
		String[] options = { "Aceptar" };
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		JTextField license = new JTextField(10);
		panel.add(label("Debes activar este producto para poder utilizarlo."));
		panel.add(label("Introduce una licencia válida:"));
		panel.add(license);
		int opt = JOptionPane.showOptionDialog(parent, panel, "Introducir Licencia", JOptionPane.NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options , options[0]);

		return opt == JOptionPane.OK_OPTION ? license.getText().trim() : null;
	}
	
	private static JLabel label(String message) {
		JLabel label = new JLabel(message);
		label.setBorder(new EmptyBorder(5, 0, 5, 0));
		return label;
	}
	
}
