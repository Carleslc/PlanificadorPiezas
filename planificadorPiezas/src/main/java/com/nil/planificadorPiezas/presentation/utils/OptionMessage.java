package com.nil.planificadorPiezas.presentation.utils;

import java.awt.Component;

import javax.swing.JOptionPane;

public class OptionMessage extends WarningMessage {

	private Runnable yes, no, cancel;
	private boolean noCancel = false;
	
	public OptionMessage(String message) {
		super(message);
	}
	
	public OptionMessage(String message, String title) {
		super(message, title);
	}
	
	public OptionMessage(Component parentComponent, String message) {
		super(parentComponent, message);
	}
	
	public OptionMessage(Component parentComponent, String message, String title) {
		super(parentComponent, message, title);
	}
	
	public OptionMessage yes(Runnable yes) {
		this.yes = yes;
		return this;
	}
	
	public OptionMessage no(Runnable no) {
		this.no = no;
		return this;
	}
	
	public OptionMessage cancel(Runnable cancel) {
		this.cancel = cancel;
		return this;
	}
	
	public OptionMessage useNoAsCancel() {
		noCancel = true;
		return this;
	}
	
	public OptionMessage message(String message) {
		this.message = message;
		return this;
	}

	public OptionMessage title(String title) {
		this.title = title;
		return this;
	}
	
	@Override
	public void show() {
		int opt = JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_CANCEL_OPTION);
		switch (opt) {
			case JOptionPane.YES_OPTION:
				if (yes != null) yes.run();
				break;
			case JOptionPane.NO_OPTION:
				if (no != null) no.run();
				break;
			case JOptionPane.CANCEL_OPTION:
				if (cancel != null) cancel.run();
				else if (noCancel && no != null) no.run();
				break;
		}
	}
	
	public static OptionMessage build(String message) {
		return new OptionMessage(message);
	}
	
	public static void show(String message) {
		new OptionMessage(message).show();
	}
}