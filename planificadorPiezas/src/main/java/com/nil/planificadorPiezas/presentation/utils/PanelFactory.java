package com.nil.planificadorPiezas.presentation.utils;

import java.awt.FlowLayout;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class PanelFactory {

	private PanelFactory() {}
	
	public static JPanel newInnerPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		return panel;
	}
	
}
