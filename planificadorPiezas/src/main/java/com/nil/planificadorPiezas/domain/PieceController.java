package com.nil.planificadorPiezas.domain;

import com.nil.planificadorPiezas.presentation.PieceDTO;

public class PieceController {

	public void process(PieceDTO dto, PieceCallback callback) {
		PieceProcessor processor = new PieceProcessor(getPiece(dto));
		processor.setCallback(callback);
		processor.process();
	}
	
	/** Convert PieceDTO to Piece */
	private Piece getPiece(PieceDTO dto) {
		return new Piece("ID");
	}
	
}