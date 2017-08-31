package com.nil.planificadorPiezas.presentation.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class ErrorMessage extends Message {
	
	private static String defaultTitle = "Error";
	
	public ErrorMessage(String message) {
		this(message, defaultTitle);
	}
	
	public ErrorMessage(String message, String title) {
		this(null, message, title);
	}
	
	public ErrorMessage(Component parentComponent, String message) {
		this(parentComponent, message, defaultTitle);
	}
	
	public ErrorMessage(Component parentComponent, String message, String title) {
		super(parentComponent, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	public static void show(String message) {
		new ErrorMessage(message).show();
	}
	
}