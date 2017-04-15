package com.bravson.socialalert.file.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.ws.rs.core.StreamingOutput;

@ManagedBean
public class FileStore {
	
	private static final DateTimeFormatter DATE_FORMATTER = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .parseStrict()
            .appendPattern("yyyyMMdd")
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneOffset.UTC);
	
	private static final String MD5_ALGORITHM = "MD5";
	
	private final Path baseDirectory;

	@Inject
	public FileStore(FileStoreConfiguration config) {
		baseDirectory = config.getBaseDirectory().toPath();
	}
	
	public String computeMd5(File file) throws IOException {
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

	public void storeMedia(File source, String uri) throws IOException {
		try (InputStream is = Files.newInputStream(source.toPath())) {
			Files.copy(is, baseDirectory.resolve(uri));
		}
	}
	
	private Path buildRelativePath(String md5, Instant timestamp, FileFormat format) {
		return Paths.get(format.getSizeVariant(), DATE_FORMATTER.format(timestamp), md5 + format.getExtension());
	}
	
	private Path buildAbsolutePath(String md5, Instant timestamp, FileFormat format) {
		return baseDirectory.resolve(buildRelativePath(md5, timestamp, format));
	}
	
	public String buildFileUri(String md5, Instant timestamp, FileFormat format) {
		return buildRelativePath(md5, timestamp, format).toString();
	}
	
	public File buildFilePath(String md5, Instant timestamp, FileFormat format) {
		return buildAbsolutePath(md5, timestamp, format).toFile();
	}
	
	public StreamingOutput createStreamingOutput(String md5, Instant timestamp, FileFormat format) {
		Path path = buildAbsolutePath(md5, timestamp, format);
		return new StreamingOutput() {
            @Override
            public void write(OutputStream os) throws IOException {
            	Files.copy(path, os);
            }
        };
	}
	
}
