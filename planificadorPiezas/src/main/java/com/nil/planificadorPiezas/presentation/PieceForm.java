package com.nil.planificadorPiezas.presentation;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.domain.PieceCallback;
import com.nil.planificadorPiezas.domain.PieceController;
import com.nil.planificadorPiezas.domain.PieceDTO;
import com.nil.planificadorPiezas.domain.Result;
import com.nil.planificadorPiezas.presentation.messages.ErrorMessage;
import com.nil.planificadorPiezas.presentation.messages.Message;
import com.nil.planificadorPiezas.presentation.messages.WarningMessage;

public class PieceForm extends JFrame {

	private static final long serialVersionUID = -5045703627293054772L;
	
	private PieceController controller;
	private JPanel contentPane;
	private boolean processing;
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	
	public PieceForm(PieceController controller) {
		this.controller = controller;
		processing = false;
		
		setDefaultStyle();
		setWindowSettings();
		addContentPane();
		addProcessButton();
	}
	
	private void setWindowSettings() {
		setBounds(200, 50, 512, 768);
		setTitle("Planificador de Piezas");
		setResizable(true);
	}
	
	private void addContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
	}
	
	private void addProcessButton() {
		JButton processButton = new JButton("Procesar");
		processButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (processing) {
					WarningMessage.show("Espera. Ya hay una pieza calculándose actualmente.");
					return;
				}
				processing = true;
				controller.process(getPieceDTO(), onProcessed());
			}
		});
		contentPane.add(processButton);
	}
	
	private PieceDTO getPieceDTO() {
		return new PieceDTO(/* inputs */);
	}
	
	private PieceCallback onProcessed() {
		return new PieceCallback() {
			
			@Override
			public void onProcessed(Result result) {
				Message.show("La pieza con el identificador " + result.getId()
					+ " estará lista para el día " + dateFormatter.format(result.getFinishDate()) + ".");
				processing = false;
			}
			
			@Override
			public void onError(Exception e) {
				ErrorMessage.show("Ha ocurrido un error al procesar la pieza.");
				DumpError.dump(e);
				processing = false;
			}
		};
	}
	
	private static void setDefaultStyle() {
		try {
			// Nimbus L&F style
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
			// Base Background
			UIManager.put("control", Color.getHSBColor(200/360f, 0.05f, 1f));
			// ToolTip Background
			UIManager.put("info", Color.getHSBColor(185/360f, 0.15f, 0.97f));
			// Buttons Background
			UIManager.put("nimbusBase", Color.getHSBColor(200/360f, 0.15f, 0.65f));
			// ComboBox Highlight Background
			UIManager.put("ComboBox:\"ComboBox.listRenderer\"[Selected].background", Color.LIGHT_GRAY);
			// Default colors
			UIManager.getLookAndFeelDefaults().put("nimbusOrange", UIManager.getColor("nimbusBase"));
			UIManager.getLookAndFeelDefaults().put("nimbusGreen", Color.getHSBColor(100/360f, 0.65f, 0.85f));
		} catch (Exception notFoundThenUseDefault) {}
	}

}