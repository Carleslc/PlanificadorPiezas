package com.nil.planificadorPiezas.domain;

import java.sql.SQLException;
import java.time.LocalDate;

import com.nil.planificadorPiezas.data.DataController;

class PieceProcessor {

	private Piece piece;
	private Result result;
	private PieceCallback callback;
	private DataController data;
	
	PieceProcessor(Piece piece, DataController data) {
		this.piece = piece;
		this.data = data;
	}
	
	void setCallback(PieceCallback callback) {
		this.callback = callback;
	}
	
	/** Process the piece asynchronously */
	void process() {
		new Thread(() -> {
			try {
				compute();
				if (callback != null) callback.onProcessed(result);
			} catch (Exception e) {
				callback.onError(e);
			}
		}).start();
	}
	
	private void compute() throws ClassNotFoundException, SQLException {
		// Simulation of heavy process
		// TODO data.connect();
		for (int i = 1; i <= 5; ++i) {
			try { Thread.sleep(1000); } catch (Exception e) {}
			System.out.println((int) (100 * i/5.0) + "%");
		}
		
		// Set the result
		result = new Result(piece.getId(), LocalDate.now());
	}
	
}