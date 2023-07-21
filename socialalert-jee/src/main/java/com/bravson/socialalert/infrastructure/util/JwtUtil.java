package com.bravson.socialalert.infrastructure.util;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.json.Json;
import jakarta.json.JsonReader;

import lombok.NonNull;

public interface JwtUtil {

	Pattern JWT_TOKEN_PATTERN = Pattern.compile("^Bearer [A-Z0-9_\\-]+\\.([A-Z0-9_\\-]+)\\.[A-Z0-9_\\-]+$", Pattern.CASE_INSENSITIVE); 
	
	static Optional<String> extractUserId(@NonNull String accessToken) {
		Matcher matcher = JWT_TOKEN_PATTERN.matcher(accessToken);
		if (matcher.matches()) {
			byte[] body = Base64.getDecoder().decode(matcher.group(1));
			try (JsonReader reader = Json.createReader(new ByteArrayInputStream(body))) {
				return Optional.ofNullable(reader.readObject().getString("sub"));
			} catch (Exception e) {
				return Optional.empty();
			}
		}
		return Optional.empty();
	}

}
