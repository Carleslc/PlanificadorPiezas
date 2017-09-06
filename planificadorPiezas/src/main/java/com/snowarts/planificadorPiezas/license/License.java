package com.snowarts.planificadorPiezas.license;

import java.time.LocalDateTime;
import java.util.Date;

import com.snowarts.planificadorPiezas.data.utils.DateUtils;

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

	public LocalDateTime getExpiration() {
		if (expiration == null) return LocalDateTime.now().plusYears(100);
		return DateUtils.getLocalDateTime(expiration);
	}

	public void setExpiration(LocalDateTime expiration) {
		this.expiration = DateUtils.getDate(expiration);
	}
	
}
