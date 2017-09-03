package com.nil.planificadorPiezas.license;

import java.time.LocalDate;
import java.util.Date;

import com.google.gson.annotations.SerializedName;
import com.nil.planificadorPiezas.data.utils.DateUtils;

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

	public LocalDate getActivatedAt() {
		return DateUtils.getLocalDate(activatedAt);
	}

	public void setActivatedAt(LocalDate activatedAt) {
		this.activatedAt = DateUtils.getDate(activatedAt);
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}
	
}
