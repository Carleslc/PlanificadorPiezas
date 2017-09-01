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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.nil.planificadorPiezas.data.utils.DateUtils;
import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.domain.OrderCallback;
import com.nil.planificadorPiezas.domain.OrderController;
import com.nil.planificadorPiezas.domain.OrderDTO;
import com.nil.planificadorPiezas.domain.Result;
import com.nil.planificadorPiezas.presentation.utils.ErrorMessage;
import com.nil.planificadorPiezas.presentation.utils.Icons;
import com.nil.planificadorPiezas.presentation.utils.Message;
import com.nil.planificadorPiezas.presentation.utils.OptionMessage;
import com.nil.planificadorPiezas.presentation.utils.WarningMessage;

public class OrderForm extends JFrame {

	private static final long serialVersionUID = -5045703627293054772L;
	
	private OrderController controller;
	private JPanel contentPane;
	private boolean processing;
	private JTextField identifier;
	private JSpinner startDate;
	private Map<Integer, Double> phasesMap;
	private List<PhaseInput> phases = new ArrayList<>();
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	
	public OrderForm(OrderController controller) {
		this.controller = controller;
		processing = false;
		
		setStyle();
		addContentPane();
		addOrderIdentifier();
		addEditButtons();
		addPhases();
		addStartDate();
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
					WarningMessage.show("Espera. Hay un pedido procesándose actualmente.");
				} else dispose();
			}
		});
	}
	
	private void addContentPane() {
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		setContentPane(contentPane);
	}
	
	private void addOrderIdentifier() {
		JPanel topPanel = newInnerPanel();
		topPanel.add(new JLabel("ID Pedido"));
		identifier = new JTextField(6);
		topPanel.add(identifier);
		contentPane.add(topPanel);
	}
	
	private void addEditButtons() {
		JPanel editPanel = newInnerPanel();
		JButton loadButton = new JButton("Cargar");
		loadButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				loadButtonClicked();
			}
		});
		editPanel.add(loadButton);
		JButton deleteButton = new JButton("Eliminar");
		deleteButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				deleteButtonClicked();
			}
		});
		editPanel.add(deleteButton);
		contentPane.add(editPanel);
	}
	
	private void addPhases() {
		for (int i = 1; i <= controller.getPhases(); i++) {
			PhaseInput phase = new PhaseInput(i);
			phases.add(phase);
			contentPane.add(phase);
		}
	}
	
	private void addProcessButton() {
		JPanel bottomPanel = newInnerPanel();
		JButton processButton = new JButton("Añadir / Modificar");
		processButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				addButtonClicked();
			}
		});
		bottomPanel.add(processButton);
		JButton clearButton = new JButton("Restablecer");
		clearButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				clear();
			}
		});
		bottomPanel.add(clearButton);
		contentPane.add(bottomPanel);
	}
	
	private void addStartDate() {
		JPanel datePanel = newInnerPanel();
		datePanel.add(new JLabel("Fecha de inicio"));
		LocalDate localNow = LocalDate.now();
		Date now = DateUtils.getDate(localNow);
		Date max = DateUtils.getDate(localNow.plusYears(100));
		startDate = new JSpinner(new SpinnerDateModel(now, now, max, Calendar.DAY_OF_MONTH));
		startDate.setEditor(new JSpinner.DateEditor(startDate, "dd/MM/yyyy"));
		((JSpinner.DefaultEditor) startDate.getEditor()).getTextField().setColumns(7);
		datePanel.add(startDate);
		contentPane.add(datePanel);
	}
	
	private void clear() {
		phases.forEach(PhaseInput::clear);
		startDate.setValue(((SpinnerDateModel)startDate.getModel()).getStart());
	}
	
	private boolean checkOrder(String id) {
		if (processing) {
			WarningMessage.show("Espera. Ya hay un pedido procesándose actualmente.");
			return true;
		}
		if (id.isEmpty()) {
			WarningMessage.show("Debes especificar un identificador de pedido.");
			return true;
		}
		return false;
	}
	
	private void addButtonClicked() {
		String id = identifier.getText().trim();
		if (checkOrder(id)) return;
		
		phasesMap = new HashMap<>();
		double totalHours = 0D;
		for (int i = 1; i <= phases.size(); i++) {
			PhaseInput phase = phases.get(i - 1);
			double hours = phase.getRawHours();
			totalHours += hours;
			if (hours != 0D) {
				phasesMap.put(i, hours);
				System.out.println("Fase " + i + " : " + phase.getHours() + "h " + phase.getMinutes() + "m");
			}
		}
		
		if (totalHours == 0D) {
			WarningMessage.show("Debes especificar como mínimo 1 fase.");
			return;
		}
		
		try {
			if (controller.exists(id)) {
				OptionMessage
					.build("El pedido con identificador " + id + " ya existe. ¿Quieres modificarlo con los valores introducidos?")
					.title("Modificar Pedido")
					.no(() -> { throw new RuntimeException("NO"); })
					.useNoAsCancel()
					.show();
			}
		} catch (RuntimeException e) {
			if (e.getMessage().equals("NO")) return;
			errorProcessing(e);
		} catch (Exception e) {
			errorProcessing(e);
		}
		
		startProcessing();
		controller.process(getOrderDTO(id), onProcessed());
	}
	
	private void loadButtonClicked() {
		String id = identifier.getText().trim();
		if (checkOrder(id)) return;
		try {
			if (controller.exists(id)) {
				OrderDTO order = controller.getOrder(id);
				clear();
				for (Entry<Integer, Double> phase : order.getPhases().entrySet()) {
					phases.get(phase.getKey() - 1).setRawHours(phase.getValue());
				}
				startDate.setValue(DateUtils.getDate(order.getStartDate()));
				Message.show("Se ha autorellenado el pedido con identificador " + id + ".");
			} else notExists(id);
		} catch (Exception e) {
			errorProcessing(e);
		}
	}
	
	private void deleteButtonClicked() {
		String id = identifier.getText().trim();
		if (checkOrder(id)) return;
		try {
			if (controller.exists(id)) {
				OptionMessage
					.build("¿Estás seguro de eliminar el pedido con identificador " + id + "?")
					.title("Eliminar Pedido")
					.yes(() -> {
						try {
							controller.deleteOrder(id);
							Message.show("El pedido con identificador " + id + " ha sido eliminado.");
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					})
					.no(() -> { throw new RuntimeException("NO"); })
					.useNoAsCancel()
					.show();
			} else notExists(id);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("NO")) return;
			errorProcessing(e);
		} catch (Exception e) {
			errorProcessing(e);
		}
	}
	
	private void notExists(String id) {
		WarningMessage.show("El pedido con identificador " + id + " no existe.");
	}
	
	private OrderDTO getOrderDTO(String id) {
		return new OrderDTO(id, phasesMap, DateUtils.getLocalDate((Date) startDate.getValue()));
	}
	
	private OrderCallback onProcessed() {
		return new OrderCallback() {
			
			@Override
			public void onProcessed(Result result) {
				Message.show("El pedido con identificador " + result.getId()
					+ " estará listo para el día " + dateFormatter.format(result.getFinishDate()) + ".");
				finishProcessing();
			}
			
			@Override
			public void onError(Exception e) {
				errorProcessing(e);
			}
		};
	}
	
	private void errorProcessing(Exception e) {
		finishProcessing();
		ErrorMessage.show("Ha ocurrido un error al procesar el pedido.");
		DumpError.dump(e);
	}
	
	private void startProcessing() {
		processing = true;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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