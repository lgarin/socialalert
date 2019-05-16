package com.bravson.socialalert.infrastructure.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.google.common.io.CharStreams;

import lombok.SneakyThrows;

public class ProcessUtil {

	@SneakyThrows(InterruptedException.class)
	public static int execute(String program, List<String> arguments, StringBuilder output) throws IOException {
		ProcessBuilder builder = new ProcessBuilder(program);
		builder.command().addAll(arguments);
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		InputStreamReader reader = new InputStreamReader(process.getInputStream());
		CharStreams.copy(reader, output);
		
		return process.waitFor();
	}
}
