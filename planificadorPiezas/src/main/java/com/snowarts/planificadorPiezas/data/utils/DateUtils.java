package com.snowarts.planificadorPiezas.data.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

public abstract class DateUtils {

	public static long getEpochMillis(LocalDate date) {
		return date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static LocalDate getLocalDate(long epochMillis) {
		return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate();
	}
	
	public static LocalDate getLocalDate(Date date) {
		return getLocalDate(date.getTime());
	}
	
	public static Date getDate(LocalDate date) {
		return new Date(getEpochMillis(date));
	}
	
	public static String format(LocalDate date, FormatStyle style) {
		return date.format(DateTimeFormatter.ofLocalizedDate(style));
	}
	
	public static String format(LocalDateTime date, FormatStyle style) {
		return date.format(DateTimeFormatter.ofLocalizedDateTime(style));
	}
	
}