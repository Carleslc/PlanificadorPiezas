package com.snowarts.planificadorPiezas.domain;

import java.time.LocalDate;
import java.time.format.FormatStyle;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;

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

	@Override
	public String toString() {
		return "El pedido #" + id + " finalizará el día " + DateUtils.format(finishDate, FormatStyle.LONG) + ".";
	}
	
}