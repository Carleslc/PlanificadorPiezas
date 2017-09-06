package com.snowarts.planificadorPiezas.data.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;

import org.simpleyaml.utils.Validate;

public abstract class DateUtils {
	
	public static long getEpochMillis(LocalDateTime date) {
		if (date == null) return 0L;
		return date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	public static LocalDateTime getLocalDateTime(long epochMillis) {
		return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
	}
	
	public static LocalDateTime getLocalDateTime(Date date) {
		if (date == null) return null;
		return getLocalDateTime(date.getTime());
	}
	
	public static Date getDate(LocalDateTime date) {
		if (date == null) return null;
		return new Date(getEpochMillis(date));
	}
	
	public static String format(LocalDate date, FormatStyle style) {
		Validate.notNull(date);
		Validate.notNull(style);
		return date.format(DateTimeFormatter.ofLocalizedDate(style));
	}
	
	public static String format(LocalDateTime date, FormatStyle style) {
		Validate.notNull(date);
		Validate.notNull(style);
		if (style == FormatStyle.LONG) return format(date, FormatStyle.LONG, FormatStyle.SHORT);
		return date.format(DateTimeFormatter.ofLocalizedDateTime(style));
	}
	
	public static String format(LocalDateTime date, FormatStyle dateFormat, FormatStyle timeFormat) {
		Validate.notNull(date);
		Validate.notNull(dateFormat);
		Validate.notNull(timeFormat);
		return date.format(DateTimeFormatter.ofLocalizedDateTime(dateFormat, timeFormat));
	}
	
}
