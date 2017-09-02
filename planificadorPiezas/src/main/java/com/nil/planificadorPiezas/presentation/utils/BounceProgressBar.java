package com.nil.planificadorPiezas.presentation.utils;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class BounceProgressBar extends JProgressBar {

	private static final long serialVersionUID = -5116068681060410011L;

	private JFrame frame;
	private JLabel message;
	
	public BounceProgressBar() {
		setIndeterminate(true);
		frame = new JFrame();
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		JPanel messagePane = PanelFactory.newInnerPanel();
		message = new JLabel();
		messagePane.add(message);
		contentPane.add(messagePane);
		JPanel barPane = PanelFactory.newInnerPanel();
		barPane.add(this);
		contentPane.add(barPane);
		frame.setContentPane(contentPane);
		CenterFrame.center(frame, 0, 0, 20, 20);
	}
	
	public void setTitle(String title) {
		frame.setTitle(title);
	}
	
	public void setMessage(String message) {
		this.message.setText(message);
		frame.pack();
	}
	
	public void start() {
		frame.setVisible(true);
	}
	
	public void finish() {
		frame.dispose();
	}
}
