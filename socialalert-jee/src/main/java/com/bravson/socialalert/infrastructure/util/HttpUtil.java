package com.bravson.socialalert.infrastructure.util;

import java.io.UncheckedIOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public interface HttpUtil {

	public static String createBasicAuth(String userId, String password) {
        try {
        	String token = userId + ':' + password;
            return "Basic " + Base64.getEncoder().encodeToString(token.getBytes(StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new UncheckedIOException(e);
        }
	}
}
