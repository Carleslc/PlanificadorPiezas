package com.snowarts.planificadorPiezas.presentation;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;

class PhaseInput extends JPanel {

	private static final long serialVersionUID = 6662166871891572448L;
	
	private int id;
	private JSpinner hours, minutes;
	
	PhaseInput(int id) {
		this.id = id;
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(new JLabel("Fase " + id));
		hours = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		((JSpinner.DefaultEditor) hours.getEditor()).getTextField().setColumns(5);
		add(hours);
		add(new JLabel("h"));
		minutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		add(minutes);
		add(new JLabel("m"));
	}
	
	public int getHours() {
		return (int) hours.getValue();
	}
	
	public int getMinutes() {
		return (int) minutes.getValue();
	}
	
	public void setRawHours(double rawHours) {
		int hoursValue = (int) rawHours;
		hours.setValue(hoursValue);
		double minutesValue = (rawHours - hoursValue)*60;
		minutes.setValue((int) Math.round(minutesValue));
	}
	
	public void clear() {
		setRawHours(0);
	}
	
	public double getRawHours() {
		return getHours() + getMinutes()/60.0;
	}

	public int getId() {
		return id;
	}

}