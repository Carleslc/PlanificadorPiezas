package com.nil.planificadorPiezas.presentation;

import java.awt.EventQueue;

import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.domain.OrderController;
import com.nil.planificadorPiezas.presentation.utils.ErrorMessage;

public class PlanificadorPiezas {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				OrderForm form = new OrderForm(new OrderController());
				form.setVisible(true);
			} catch (Exception uncaught) {
				ErrorMessage.show("Ha ocurrido un error inesperado: " + uncaught.getMessage());
				DumpError.dump(uncaught);
			}
		});
	}

}