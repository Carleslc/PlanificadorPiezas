package com.nil.planificadorPiezas.presentation.messages;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Message {

	private static String defaultTitle = "Información";
	
	private Component parentComponent;
	private String message, title;
	private int messageType;
	
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
	
	public void show() {
		JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
	}
	
}