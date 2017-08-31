package com.nil.planificadorPiezas.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public abstract class DateUtils {

	public static long getEpochMillis(LocalDate date) {
		return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static LocalDate getLocalDate(long epochMillis) {
		return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
}