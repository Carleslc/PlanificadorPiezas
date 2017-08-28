package com.nil.planificadorPiezas.presentation.messages;

import java.awt.Component;

import javax.swing.JOptionPane;

public class WarningMessage extends Message {
	
	private static String  defaultTitle = "Advertencia";
	
	public WarningMessage(String message) {
		this(message, defaultTitle);
	}
	
	public WarningMessage(String message, String title) {
		this(null, message, title);
	}
	
	public WarningMessage(Component parentComponent, String message) {
		this(parentComponent, message, defaultTitle);
	}
	
	public WarningMessage(Component parentComponent, String message, String title) {
		super(parentComponent, message, title, JOptionPane.WARNING_MESSAGE);
	}
	
}