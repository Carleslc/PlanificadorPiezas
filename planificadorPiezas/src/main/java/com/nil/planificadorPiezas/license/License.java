package com.nil.planificadorPiezas.license;

import java.time.LocalDate;
import java.util.Date;

import com.nil.planificadorPiezas.data.utils.DateUtils;

class License {

	private String key, name, product;
	private Date expiration;
	private int quantity;
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getProduct() {
		return product;
	}
	
	public void setProduct(String product) {
		this.product = product;
	}
	
	public int getQuantity() {
		return quantity;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public LocalDate getExpiration() {
		if (expiration == null) return LocalDate.now().plusYears(100);
		return DateUtils.getLocalDate(expiration);
	}

	public void setExpiration(LocalDate expiration) {
		this.expiration = DateUtils.getDate(expiration);
	}
	
}
