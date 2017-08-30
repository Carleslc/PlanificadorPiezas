package com.nil.planificadorPiezas.domain;

import java.io.IOException;

import org.simpleyaml.exceptions.InvalidConfigurationException;

import com.nil.planificadorPiezas.data.DataController;

public class PieceController {

	private DataController data;
	
	public PieceController() throws IOException, InvalidConfigurationException {
		data = new DataController();
	}
	
	public void process(PieceDTO dto, PieceCallback callback) {
		PieceProcessor processor = new PieceProcessor(getPiece(dto), data);
		processor.setCallback(callback);
		processor.process();
	}
	
	/** Convert PieceDTO to Piece */
	private Piece getPiece(PieceDTO dto) {
		return new Piece("ID",dto.getPhases());
	}
	
}