package com.bravson.socialalert.infrastructure.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import lombok.SneakyThrows;

public interface ProcessUtil {

	@SneakyThrows(InterruptedException.class)
	public static int execute(String program, List<String> arguments, StringBuilder output) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(program);
		builder.command().addAll(arguments);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		InputStreamReader reader = new InputStreamReader(process.getInputStream());
		readAll(reader, output);
		
		return process.waitFor();
	}

	private static void readAll(InputStreamReader reader, StringBuilder output) throws IOException {
		char[] buffer = new char[8 * 1024];
	    int length;
	    while ((length = reader.read(buffer)) > 0) {
	    	output.append(buffer, 0, length);
	    }
	}
}
