package com.nil.planificadorPiezas.license;

@FunctionalInterface
public interface LicenseCallback {
	
	void onLicense(String license, boolean isTrial);
	
}
