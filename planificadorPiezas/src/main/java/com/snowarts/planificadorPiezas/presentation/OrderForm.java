package com.snowarts.planificadorPiezas.presentation;

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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.DumpError;
import com.snowarts.planificadorPiezas.domain.OrderCallback;
import com.snowarts.planificadorPiezas.domain.OrderController;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.Result;
import com.snowarts.planificadorPiezas.presentation.utils.BounceProgressBar;
import com.snowarts.planificadorPiezas.presentation.utils.CenterFrame;
import com.snowarts.planificadorPiezas.presentation.utils.ErrorMessage;
import com.snowarts.planificadorPiezas.presentation.utils.Icons;
import com.snowarts.planificadorPiezas.presentation.utils.Message;
import com.snowarts.planificadorPiezas.presentation.utils.OptionMessage;
import com.snowarts.planificadorPiezas.presentation.utils.PanelFactory;
import com.snowarts.planificadorPiezas.presentation.utils.WarningMessage;

public class OrderForm extends JFrame {

	private static final long serialVersionUID = -5045703627293054772L;
	
	private OrderController controller;
	private JPanel contentPane;
	private boolean processing;
	private JTextField identifier;
	private JSpinner startDate;
	private BounceProgressBar progress;
	private Map<Integer, Double> phasesMap;
	private Map<Integer, String> tags;
	private List<PhaseInput> phases = new ArrayList<>();
	
	private DateTimeFormatter dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG);
	
	public OrderForm(OrderController controller) {
		this.controller = controller;
		processing = false;
		tags = controller.getPhaseTags();
		
		addContentPane();
		addOrderIdentifier();
		addEditButtons();
		addPhases();
		addStartDate();
		addProcessButton();
		setWindowSettings();
	}
	
	private void setWindowSettings() {
		CenterFrame.center(this, 0, 100, 50, 10);
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
		contentPane = PanelFactory.newPanel();
		setContentPane(contentPane);
	}
	
	private void addOrderIdentifier() {
		JPanel topPanel = PanelFactory.newInnerPanel();
		topPanel.add(new JLabel("ID Pedido"));
		identifier = new JTextField(6);
		topPanel.add(identifier);
		contentPane.add(topPanel);
	}
	
	private void addEditButtons() {
		JPanel editPanel = PanelFactory.newInnerPanel();
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
		int indent = tags.values().stream().mapToInt(String::length).max().getAsInt();
		for (int i = 1; i <= controller.getPhases(); i++) {
			PhaseInput phase = new PhaseInput(i, tags.get(i), indent);
			phases.add(phase);
			contentPane.add(phase);
		}
	}
	
	private void addProcessButton() {
		JPanel bottomPanel = PanelFactory.newInnerPanel();
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
		JPanel datePanel = PanelFactory.newInnerPanel();
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
		double totalHours = 0;
		for (int i = 1; i <= phases.size(); i++) {
			PhaseInput phase = phases.get(i - 1);
			double hours = phase.getRawHours();
			totalHours += hours;
			if (hours != 0) phasesMap.put(i, hours);
		}
		
		if (totalHours == 0) {
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
				LocalDate finishDate = order.getFinishDate();
				Message.show("Se ha autorellenado el pedido con identificador " + id + "."
						+ (finishDate != null ? "\nFecha estimada de finalización: " + DateUtils.format(finishDate, FormatStyle.LONG) : ""));
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
		progress = new BounceProgressBar().title("Procesando Pedido").message("Procesando pedido...");
		progress.start();
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	}
	
	private void finishProcessing() {
		processing = false;
		if (progress != null) progress.finish();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		System.exit(0);
	}

}