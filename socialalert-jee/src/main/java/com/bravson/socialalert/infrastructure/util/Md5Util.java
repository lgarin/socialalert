package com.bravson.socialalert.infrastructure.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.NonNull;

public interface Md5Util {
	
	String MD5_ALGORITHM = "MD5";

	public static String computeMd5Hex(@NonNull File file) throws IOException {
		try {
			return digest(file, MD5_ALGORITHM);
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Algorithm not supported: " + MD5_ALGORITHM, e);
		}
	}
	
	private static String digest(File file, String algorithm) throws NoSuchAlgorithmException, IOException {
		MessageDigest md5 = MessageDigest.getInstance(algorithm);
		try (InputStream is = Files.newInputStream(file.toPath())) {
			
			byte[] buffer = new byte[8192];
			int read;
			while ((read = is.read(buffer)) > 0) {
                md5.update(buffer, 0, read);
            }
		}
		return toHex(md5.digest());
	}

	private static String toHex(byte[] data) {
		return new BigInteger(1, data).toString(16);
	}

}
