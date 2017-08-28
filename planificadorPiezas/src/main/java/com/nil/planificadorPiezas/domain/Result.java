package com.nil.planificadorPiezas.domain;

import java.time.LocalDate;

public class Result {

	private String id;
	private LocalDate finishDate;
	
	public Result() {}
	
	public Result(String id, LocalDate finishDate) {
		setId(id);
		setFinishDate(finishDate);
	}

	void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}
	
	void setFinishDate(LocalDate finishDate) {
		this.finishDate = finishDate;
	}

	public LocalDate getFinishDate() {
		return finishDate;
	}
	
}
