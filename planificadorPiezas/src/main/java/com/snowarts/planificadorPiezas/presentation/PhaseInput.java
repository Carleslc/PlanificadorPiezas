package com.snowarts.planificadorPiezas.presentation;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.snowarts.planificadorPiezas.presentation.utils.PanelFactory;
import com.snowarts.planificadorPiezas.presentation.utils.SpinnerUtils;

class PhaseInput {

	private JSpinner hours, minutes;
	
	PhaseInput(String label, JPanel tagsPanel, JPanel inputsPanel, boolean external) {
		tagsPanel.add(new JLabel(label, JLabel.RIGHT));
		JPanel inputPanel = PanelFactory.newInnerBoxPanel();
		PanelFactory.setMargin(inputPanel, 10, 10, 10, 10);
		hours = new JSpinner(new SpinnerNumberModel(0, 0, null, 1));
		((JSpinner.DefaultEditor) hours.getEditor()).getTextField().setColumns(5);
		if (external) SpinnerUtils.setBackgroundColor(hours, Color.decode("0xffe4e1"));
		inputPanel.add(hours);
		JLabel h = new JLabel("h");
		PanelFactory.setMargin(h, 0, 2, 0, 5);
		inputPanel.add(h);
		minutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		if (external) SpinnerUtils.setBackgroundColor(minutes, Color.decode("0xffe4e1"));
		inputPanel.add(minutes);
		JLabel m = new JLabel("m");
		PanelFactory.setMargin(m, 0, 2, 0, 2);
		inputPanel.add(m);
		inputsPanel.add(inputPanel);
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

}