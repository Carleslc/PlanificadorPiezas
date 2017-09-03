package com.snowarts.planificadorPiezas.presentation.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Message {

	private static String defaultTitle = "Información";
	
	protected Component parentComponent;
	protected String message, title;
	protected int messageType;
	
	public Message(String message) {
		this(message, defaultTitle);
	}
	
	public Message(String message, String title) {
		this(null, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public Message(Component parentComponent, String message) {
		this(parentComponent, message, defaultTitle);
	}
	
	public Message(Component parentComponent, String message, String title) {
		this(parentComponent, message, title, JOptionPane.INFORMATION_MESSAGE);
	}
	
	public Message(Component parentComponent, String message, String title, int messageType) {
		this.parentComponent = parentComponent;
		this.message = message;
		this.title = title;
		this.messageType = messageType;
	}
	
	public Component getParentComponent() {
		return parentComponent;
	}

	public void setParentComponent(Component parentComponent) {
		this.parentComponent = parentComponent;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void show() {
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}
	
	public static void show(String message) {
		new Message(message).show();
	}
	
}