package com.snowarts.planificadorPiezas.license;

@FunctionalInterface
public interface LicenseCallback {
	
	void onLicense(String license, boolean isTrial);
	
}
