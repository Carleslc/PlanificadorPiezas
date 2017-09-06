package com.snowarts.planificadorPiezas.license;

import java.time.LocalDateTime;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.snowarts.planificadorPiezas.data.utils.DateUtils;

class LicenseActivation {
	
	private String fingerprint;
	
	@SerializedName("activated_at")
	private Date activatedAt;
	
	private License license;

	public String getFingerprint() {
		return fingerprint;
	}

	public void setFingerprint(String fingerprint) {
		this.fingerprint = fingerprint;
	}

	public LocalDateTime getActivatedAt() {
		return DateUtils.getLocalDateTime(activatedAt);
	}

	public void setActivatedAt(LocalDateTime activatedAt) {
		this.activatedAt = DateUtils.getDate(activatedAt);
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}
	
}
