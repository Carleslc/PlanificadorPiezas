package com.snowarts.planificadorPiezas.presentation.utils;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.JSpinner;
import javax.swing.plaf.synth.SynthFormattedTextFieldUI;
import javax.swing.text.JTextComponent;

public final class SpinnerUtils {
	
	private SpinnerUtils() {}

	public static void setBackgroundColor(JSpinner spinner, Color color) {
		JComponent editor = spinner.getEditor();
		int c = editor.getComponentCount();
		for (int i = 0; i < c; i++) {
		    final Component comp = editor.getComponent(i);
		    if (comp instanceof JTextComponent) {
		        ((JTextComponent) comp).setUI(new SynthFormattedTextFieldUI() {
		            protected void paint(javax.swing.plaf.synth.SynthContext context, java.awt.Graphics g) {
		                g.setColor(color);
		                g.fillRect(3, 3, getComponent().getWidth()-3, getComponent().getHeight()-6);
		                super.paint(context, g);
		            };
		        });
		    }
		}
	}
	
}
