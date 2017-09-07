package com.snowarts.planificadorPiezas.presentation;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;

import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.text.DefaultEditorKit;

import com.snowarts.planificadorPiezas.domain.DumpError;
import com.snowarts.planificadorPiezas.domain.OrderController;
import com.snowarts.planificadorPiezas.license.LicenseValidator;
import com.snowarts.planificadorPiezas.presentation.utils.ErrorMessage;

import me.carleslc.serialnumber.OS;

public class Rem {

	public static String PROGRAM_NAME = "rem";
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				setStyle();
				OrderController controller = new OrderController();
				OrderForm form = new OrderForm(controller);
				LicenseValidator.validate("planificadorPiezas", form, () -> {
					form.setVisible(true);
				});
			} catch (Exception uncaught) {
				ErrorMessage.show("Ha ocurrido un error inesperado: " + uncaught.getMessage());
				DumpError.dump(uncaught);
				System.exit(1);
			}
		});
	}
	
	private static void setStyle() {
		try {
			// Nimbus L&F style
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// Base Background
			UIManager.put("control", Color.getHSBColor(200/360f, 0.05f, 1f));
			// ToolTip Background
			UIManager.put("info", Color.getHSBColor(185/360f, 0.15f, 0.97f));
			// Buttons Background
			UIManager.put("nimbusBase", Color.getHSBColor(200/360f, 0.15f, 0.65f));
			// ComboBox Highlight Background
			UIManager.put("ComboBox:\"ComboBox.listRenderer\"[Selected].background", Color.LIGHT_GRAY);
			// Default colors
			UIManager.getLookAndFeelDefaults().put("nimbusOrange", UIManager.getColor("nimbusBase"));
			UIManager.getLookAndFeelDefaults().put("nimbusGreen", Color.getHSBColor(100/360f, 0.65f, 0.85f));
		} catch (Exception notFoundThenUseDefault) {}
		if (OS.get() == OS.MAC_OS) {
			InputMap im = (InputMap) UIManager.get("TextField.focusInputMap");
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.META_DOWN_MASK), DefaultEditorKit.copyAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.META_DOWN_MASK), DefaultEditorKit.pasteAction);
			im.put(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.META_DOWN_MASK), DefaultEditorKit.cutAction);
		}
	}

}