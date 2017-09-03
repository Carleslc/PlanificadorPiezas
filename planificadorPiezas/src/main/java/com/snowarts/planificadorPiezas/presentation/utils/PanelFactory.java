package com.snowarts.planificadorPiezas.presentation.utils;

import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public abstract class PanelFactory {

	private PanelFactory() {}
	
	public static JPanel newPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		return panel;
	}
	
	public static JPanel newInnerPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		return panel;
	}
	
}
