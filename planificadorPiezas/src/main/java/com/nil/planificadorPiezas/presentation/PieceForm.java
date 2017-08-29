package com.nil.planificadorPiezas.presentation;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
	private static JTextField input1;
	private JLabel text1;
	private List<JTextField> textFields = new ArrayList<JTextField>();
	private static final int num_fases = 10;
	private JTextField[] fields;
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
		
		for (int i = 1; i < num_fases; i++) {
			getFase1();
			getLabel1(i);
		}
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
	
	private void getFase1() {
		JTextField jf = new JTextField (20);
		contentPane.add(jf);
		textFields.add(jf);
	}
	
	private void getLabel1(int num) {
		text1 = new JLabel("Fase " + (int)num);
		contentPane.add(text1);
	}	
	private PieceDTO getPieceDTO() {

		for (int i = 0; i < textFields.size();i++){
			String horas_s =  textFields.get(i).getText().trim();
			if(horas_s.isEmpty())
				
				horas_s = "0";
			
		  System.out.println("Horas en la fase " + i + " : " + horas_s + " h.");
		}
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