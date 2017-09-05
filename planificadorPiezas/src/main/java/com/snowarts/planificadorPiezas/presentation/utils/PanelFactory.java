package com.snowarts.planificadorPiezas.presentation.utils;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

public final class PanelFactory {

	private PanelFactory() {}
	
	public static JPanel newPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		return panel;
	}
	
	public static JPanel newInnerPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(getBorder());
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		return panel;
	}
	
	public static JPanel newInnerBoxPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(getBorder());
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		return panel;
	}
	
	public static JPanel newGridPanel(int rows, int columns) {
		JPanel panel = new JPanel(new GridLayout(rows, columns));
		panel.setBorder(getBorder());
		return panel;
	}
	
	public static void setMargin(JComponent component, int top, int left, int bottom, int right) {
		component.setBorder(new EmptyBorder(top, left, bottom, right));
	}
	
	public static JPanel newBorderPanel() {
		return new JPanel(new BorderLayout());
	}
	
	private static Border getBorder() {
		return new EmptyBorder(2, 2, 2, 2);
	}
	
}
