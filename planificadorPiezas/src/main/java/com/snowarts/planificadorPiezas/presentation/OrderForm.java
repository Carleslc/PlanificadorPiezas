package com.snowarts.planificadorPiezas.presentation;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.WindowConstants;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;
import com.snowarts.planificadorPiezas.domain.DumpError;
import com.snowarts.planificadorPiezas.domain.OrderCallback;
import com.snowarts.planificadorPiezas.domain.OrderController;
import com.snowarts.planificadorPiezas.domain.OrderDTO;
import com.snowarts.planificadorPiezas.domain.PhaseDTO;
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
	private List<PhaseDTO> phasesList;
	private Map<Integer, String> tags, externalTags;
	private List<PhaseInput> phases = new ArrayList<>();
	private List<PhaseInput> externalPhases = new ArrayList<>();
	
	public OrderForm(OrderController controller) {
		this.controller = controller;
		processing = false;
		tags = controller.getPhaseTags();
		externalTags = controller.getExternalPhaseTags();
		
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
		JPanel phasesPanel = PanelFactory.newBorderPanel();
		int externalPhasesCount = controller.getExternalPhases();
		int phasesCount = controller.getPhases();
		int totalPhases = externalPhasesCount + phasesCount;
		JPanel tagsPanel = PanelFactory.newGridPanel(totalPhases, 1);
		PanelFactory.setMargin(tagsPanel, 2, 10, 2, 0);
		JPanel inputsPanel = PanelFactory.newGridPanel(totalPhases, 1);
		PanelFactory.setMargin(inputsPanel, 2, 0, 2, 0);
		addPhases(tagsPanel, inputsPanel, true, externalTags, externalPhasesCount, externalPhases);
		addPhases(tagsPanel, inputsPanel, false, tags, phasesCount, phases);
		phasesPanel.add(tagsPanel, BorderLayout.WEST);
		phasesPanel.add(inputsPanel, BorderLayout.CENTER);
		addSeparator();
		contentPane.add(phasesPanel);
		addSeparator();
	}
	
	private void addSeparator() {
		contentPane.add(new JSeparator());
	}
	
	private static void addPhases(JPanel tagsPanel, JPanel inputsPanel, boolean external, Map<Integer, String> tags, int count, List<PhaseInput> to) {
		for (int i = 1; i <= count; i++) {
			String tag = tags.get(i);
			String label = tag == null ? (external ? "Proveedor " : "Fase ") + i : tag;
			PhaseInput phase = new PhaseInput(label, tagsPanel, inputsPanel, external);
			to.add(phase);
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
		LocalDateTime now = LocalDateTime.now();
		Date current = DateUtils.getDate(getCurrentStartDate(now));
		Date max = DateUtils.getDate(now.plusYears(100));
		startDate = new JSpinner(new SpinnerDateModel(current, current, max, Calendar.HOUR_OF_DAY));
		startDate.setEditor(new JSpinner.DateEditor(startDate, "HH:mm - dd/MM/yyyy"));
		((JSpinner.DefaultEditor) startDate.getEditor()).getTextField().setColumns(12);
		datePanel.add(startDate);
		contentPane.add(datePanel);
	}
	
	private LocalDateTime getCurrentStartDate(LocalDateTime from) {
		LocalDateTime open = from.toLocalDate().atTime(controller.getOpenTime());
		LocalDateTime close = from.toLocalDate().atTime(controller.getOpenTime());
		if (from.isBefore(open)) return open;
		else if (from.isAfter(close)) return from.toLocalDate().plusDays(1).atTime(controller.getOpenTime());
		return from;
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
		
		phasesList = new LinkedList<>();
		for (int i = 1; i <= externalPhases.size(); i++) {
			PhaseInput externalPhase = externalPhases.get(i - 1);
			double hours = externalPhase.getRawHours();
			if (hours != 0) phasesList.add(new PhaseDTO(-i, hours, true));
		}
		
		double totalHours = 0;
		for (int i = 1; i <= phases.size(); i++) {
			PhaseInput phase = phases.get(i - 1);
			double hours = phase.getRawHours();
			totalHours += hours;
			if (hours != 0) phasesList.add(new PhaseDTO(i, hours, false));
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
				for (PhaseDTO phase : order.getPhases()) {
					if (phase.isExternal()) externalPhases.get(-phase.getId() - 1).setRawHours(phase.getRawHours());
					else phases.get(phase.getId() - 1).setRawHours(phase.getRawHours());
				}
				startDate.setValue(DateUtils.getDate(order.getStartDate()));
				LocalDateTime finishDate = order.getFinishDate();
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
		return new OrderDTO(id, phasesList, DateUtils.getLocalDateTime((Date) startDate.getValue()));
	}
	
	private OrderCallback onProcessed() {
		return new OrderCallback() {
			
			@Override
			public void onProcessed(Result result) {
				Message.show("El pedido con identificador " + result.getId()
					+ " estará listo para el día " + DateUtils.format(result.getFinishDate(), FormatStyle.LONG) + ".");
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