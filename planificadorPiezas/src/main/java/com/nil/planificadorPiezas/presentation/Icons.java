package com.nil.planificadorPiezas.presentation;

import java.awt.Image;
import java.awt.Toolkit;

/**
 * Images for icons.
 * <p>
 * Convert to an Icon with <b><code>new ImageIcon(Image)</code></b>
 */
public abstract class Icons {
	
	private Icons() {}
	
	public static final Image MAIN = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/computer.gif"));
	
	public static final Image ADD = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/upFolder.gif"));
	
	public static final Image DISK = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/hardDrive.gif"));
	
	public static final Image SAVE = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/floppy.gif"));
	
	public static final Image QUESTION = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/Question.gif"));
	
	public static final Image WARNING = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/warning.gif"));
	
	public static final Image INFO = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/info.gif"));
	
	public static final Image ERROR = Toolkit.getDefaultToolkit()
			.createImage(ClassLoader.getSystemResource(
					"javax/swing/plaf/metal/icons/ocean/error.gif"));
	
}