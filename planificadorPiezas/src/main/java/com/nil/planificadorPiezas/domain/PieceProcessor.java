package com.nil.planificadorPiezas.domain;

import java.time.LocalDate;

class PieceProcessor {

	private Piece piece;
	private Result result;
	private PieceCallback callback;
	
	PieceProcessor(Piece piece) {
		this.piece = piece;
	}
	
	void setCallback(PieceCallback callback) {
		this.callback = callback;
	}
	
	/** Process the piece asynchronously */
	void process() {
		new Thread(() -> {
			compute();
			if (callback != null) callback.onProcessed(result);
		}).start();
	}
	
	private void compute() {
		 // Simulation of heavy process
		for (int i = 1; i <= 5; ++i) {
			try { Thread.sleep(1000); } catch (Exception e) {}
			System.out.println((int) (100 * i/5.0) + "%");
		}
		
		// Set the result
		result = new Result(piece.getId(), LocalDate.now());
	}
	
}