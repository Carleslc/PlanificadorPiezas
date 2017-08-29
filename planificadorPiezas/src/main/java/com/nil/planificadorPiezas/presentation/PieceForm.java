package com.nil.planificadorPiezas.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
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
	
	private static final int NUM_PHASES = 10;
	
	private PieceController controller;
	private JPanel contentPane;
	private boolean processing;
	
	private List<JSpinner> phases = new ArrayList<>();
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	
	public PieceForm(PieceController controller) {
		this.controller = controller;
		processing = false;
		
		setStyle();
		addContentPane();
		addPhases();
		addProcessButton();
		setWindowSettings();
	}
	
	private void setWindowSettings() {
		pack();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	    int x = (int) (rect.getMaxX() - getWidth())/2;
	    int y = (int) (rect.getMaxY() - getHeight())/2;
	    setLocation(x, y);
		setMinimumSize(new Dimension(getWidth(), getHeight()));
		setTitle("Planificador de Piezas");
		setIconImage(Icons.MAIN);
		setResizable(true);
	}
	
	private void addContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		setContentPane(contentPane);
	}
	
	private void addPhases() {
		for (int i = 1; i <= NUM_PHASES; i++) {
			JPanel phasePanel = new JPanel();
			phasePanel.setBorder(new EmptyBorder(2, 2, 2, 2));
			phasePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			addPhaseInput(phasePanel);
			addPhaseLabel(i, phasePanel);
			contentPane.add(phasePanel);
		}
	}
	
	private void addProcessButton() {
		JButton processButton = new JButton("Procesar");
		processButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				processButtonClicked();
			}
		});
		JPanel bottomPanel = new JPanel();
		bottomPanel.add(processButton);
		contentPane.add(bottomPanel);
	}
	
	private void addPhaseInput(JPanel to) {
		JSpinner phaseSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 365, 1));
		to.add(phaseSpinner);
		phases.add(phaseSpinner);
	}
	
	private void addPhaseLabel(int num, JPanel to) {
		to.add(new JLabel("Horas fase " + num));
	}
	
	private void processButtonClicked() {
		if (processing) {
			WarningMessage.show("Espera. Ya hay una pieza calculándose actualmente.");
			return;
		}
		
		int totalHoras = 0;
		for (int i = 0; i < phases.size(); i++) {
			int horas = (int) phases.get(i).getValue();
			totalHoras += horas;
			System.out.println("Horas en la fase " + i + " : " + horas + " h");
		}
		
		if (totalHoras == 0) {
			WarningMessage.show("Debes especificar como mínimo 1 fase.");
			return;
		}
		
		processing = true;
		controller.process(getPieceDTO(), onProcessed());
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
	
	private static void setStyle() {
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