package com.nil.planificadorPiezas.domain;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class DumpError {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_YYYY__HH_mm_ss");
	
	public static void dump(Exception e) {
		try (PrintWriter writer = new PrintWriter("dump_" + formatter.format(LocalDateTime.now()) + ".txt")) {
			e.printStackTrace(writer);
		} catch (FileNotFoundException e1) {
			e.printStackTrace();
			e1.printStackTrace();
		}
	}
	
}
