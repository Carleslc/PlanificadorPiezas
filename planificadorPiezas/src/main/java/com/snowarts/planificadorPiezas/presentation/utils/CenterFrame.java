package com.snowarts.planificadorPiezas.presentation.utils;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

public abstract class CenterFrame {

	private CenterFrame() {}
	
	public static void center(JFrame frame) {
		center(frame, 0, 0, 0, 0);
	}
	
	public static void center(JFrame frame, int marginX, int marginY, int paddingX, int paddingY) {
		frame.pack();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	    final int frameWidth = frame.getWidth();
	    final int frameHeight = frame.getHeight();
	    int x = (int) ((rect.getMaxX() - marginX) - frameWidth)/2;
	    int y = (int) ((rect.getMaxY() - marginY) - frameHeight)/2;
	    frame.setLocation(x, y);
	    int width = (int) Math.min(frameWidth + paddingX, rect.getMaxX());
	    int height = (int) Math.min(frameHeight + paddingY, rect.getMaxY() - marginY);
	    Dimension size = new Dimension(width, height);
		frame.setMinimumSize(size);
		frame.setPreferredSize(size);
	}
	
}
