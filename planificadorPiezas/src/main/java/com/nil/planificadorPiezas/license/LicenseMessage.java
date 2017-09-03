package com.nil.planificadorPiezas.license;

import java.time.LocalDate;
import java.util.Date;

import com.nil.planificadorPiezas.data.utils.DateUtils;

class LicenseMessage {

	private String message;
	private Date expiration;
	
	public String getMessage() {
		return message + ".";
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isExpiration() {
		return expiration != null;
	}
	
	public LocalDate getExpiration() {
		return DateUtils.getLocalDate(expiration);
	}

	public void setExpiration(LocalDate expiration) {
		this.expiration = DateUtils.getDate(expiration);
	}
	
}
