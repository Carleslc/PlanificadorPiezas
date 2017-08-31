package com.nil.planificadorPiezas.presentation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.domain.OrderCallback;
import com.nil.planificadorPiezas.domain.OrderController;
import com.nil.planificadorPiezas.domain.OrderDTO;
import com.nil.planificadorPiezas.domain.Result;
import com.nil.planificadorPiezas.presentation.messages.ErrorMessage;
import com.nil.planificadorPiezas.presentation.messages.Message;
import com.nil.planificadorPiezas.presentation.messages.WarningMessage;

public class OrderForm extends JFrame {

	private static final long serialVersionUID = -5045703627293054772L;
	
	private OrderController controller;
	private JPanel contentPane;
	private boolean processing;
	private JTextField identifier;
	private Map<Integer, Double> phasesMap = new HashMap<Integer, Double>();
	private List<JSpinner> phases = new ArrayList<>();
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	
	public OrderForm(OrderController controller) {
		this.controller = controller;
		processing = false;
		
		setStyle();
		addContentPane();
		addOrderIdentifier();
		addPhases();
		addProcessButton();
		setWindowSettings();
	}
	
	private void setWindowSettings() {
		pack();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
	    Rectangle rect = defaultScreen.getDefaultConfiguration().getBounds();
	    final int MARGIN_Y = 100;
	    final int PADDING_X = 50;
	    int x = (int) (rect.getMaxX() - getWidth())/2;
	    int y = (int) ((rect.getMaxY() - MARGIN_Y) - getHeight())/2;
	    setLocation(x, y);
	    int width = (int) Math.min(getWidth() + PADDING_X, rect.getMaxX());
	    int height = (int) Math.min(getHeight() + 10, rect.getMaxY() - MARGIN_Y);
	    Dimension size = new Dimension(width, height);
		setMinimumSize(size);
		setPreferredSize(size);
		setTitle("Planificador de Piezas");
		setIconImage(Icons.MAIN);
		setResizable(true);
		JScrollPane scroll = new JScrollPane(contentPane);
		contentPane.setAutoscrolls(true);
		scroll.setViewportView(contentPane);
		setContentPane(scroll);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (processing) {
					WarningMessage.show("Espera. Hay una pieza calculándose actualmente.");
				} else dispose();
			}
		});
	}
	
	private void addContentPane() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		setContentPane(contentPane);
	}
	
	private void addOrderIdentifier() {
		JPanel topPanel = newInnerPanel();
		identifier = new JTextField(5);
		topPanel.add(new JLabel("ID Pedido"));
		topPanel.add(identifier);
		contentPane.add(topPanel);
	}
	
	private void addPhases() {
		for (int i = 1; i <= controller.getPhases(); i++) {
			JPanel phasePanel = newInnerPanel();
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
		JPanel bottomPanel = newInnerPanel();
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
		
		String id = identifier.getText().trim();
		if (id.isEmpty()) {
			WarningMessage.show("Debes especificar un identificador de pieza.");
			return;
		}
		
		int totalHoras = 0;
		for (int i = 1; i <= phases.size(); i++) {
			int horas = (int) phases.get(i - 1).getValue();
			totalHoras += horas;
			if (horas != 0) {
				phasesMap.put(i, (double) horas);
				System.out.println("Horas en la fase " + i + " : " + horas + " h");
			}
		}
		
		if (totalHoras == 0) {
			WarningMessage.show("Debes especificar como mínimo 1 fase.");
			return;
		}
		
		processing = true;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		controller.process(getOrderDTO(id), onProcessed());
	}
	
	private OrderDTO getOrderDTO(String id) {
		return new OrderDTO(id, phasesMap, LocalDate.now());
	}
	
	private OrderCallback onProcessed() {
		return new OrderCallback() {
			
			@Override
			public void onProcessed(Result result) {
				Message.show("La pieza con el identificador " + result.getId()
					+ " estará lista para el día " + dateFormatter.format(result.getFinishDate()) + ".");
				finishProcessing();
			}
			
			@Override
			public void onError(Exception e) {
				ErrorMessage.show("Ha ocurrido un error al procesar la pieza.");
				DumpError.dump(e);
				finishProcessing();
			}
		};
	}
	
	private void finishProcessing() {
		processing = false;
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		System.exit(0);
	}
	
	private static JPanel newInnerPanel() {
		JPanel panel = new JPanel();
		panel.setBorder(new EmptyBorder(2, 2, 2, 2));
		panel.setLayout(new FlowLayout(FlowLayout.CENTER));
		return panel;
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