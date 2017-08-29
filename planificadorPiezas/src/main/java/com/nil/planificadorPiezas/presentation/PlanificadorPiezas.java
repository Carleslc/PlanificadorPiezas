package com.nil.planificadorPiezas.presentation;

import java.awt.EventQueue;

import com.nil.planificadorPiezas.domain.DumpError;
import com.nil.planificadorPiezas.domain.PieceController;
import com.nil.planificadorPiezas.presentation.messages.ErrorMessage;

public class PlanificadorPiezas {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				PieceForm form = new PieceForm(new PieceController());
				form.setVisible(true);
			} catch (Exception uncaught) {
				ErrorMessage.show("Ha ocurrido un error inesperado: " + uncaught.getMessage());
				DumpError.dump(uncaught);
			}
		});
	}

}