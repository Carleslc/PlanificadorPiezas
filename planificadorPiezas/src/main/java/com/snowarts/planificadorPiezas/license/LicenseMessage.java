package com.snowarts.planificadorPiezas.license;

import java.time.LocalDateTime;
import java.util.Date;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;

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
	
	public LocalDateTime getExpiration() {
		return DateUtils.getLocalDateTime(expiration);
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = DateUtils.getDate(expiration);
	}
	
}
