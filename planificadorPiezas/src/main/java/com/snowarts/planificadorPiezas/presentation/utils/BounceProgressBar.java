package com.snowarts.planificadorPiezas.presentation.utils;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

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
		message.setBorder(new EmptyBorder(5, 5, 5, 5));
		messagePane.add(message);
		contentPane.add(messagePane);
		JPanel barPane = PanelFactory.newInnerPanel();
		barPane.add(this);
		contentPane.add(barPane);
		frame.setContentPane(contentPane);
		CenterFrame.center(frame, 0, 0, 20, 20);
	}
	
	public BounceProgressBar title(String title) {
		frame.setTitle(title);
		return this;
	}
	
	public BounceProgressBar message(String message) {
		this.message.setText(message);
		frame.pack();
		return this;
	}
	
	public void start() {
		frame.setVisible(true);
	}
	
	public void finish() {
		frame.dispose();
	}
}
