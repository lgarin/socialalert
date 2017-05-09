package com.bravson.socialalert.infrastructure.util;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public interface HttpUtil {

	public static String createBasicAuth(String userId, String password) {
        try {
        	String token = userId + ':' + password;
            return "Basic " + Base64.getEncoder().encodeToString(token.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
	}
}
