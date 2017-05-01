package com.bravson.socialalert.file.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.temporal.Temporal;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.util.DateUtil;

import lombok.NonNull;

@ManagedBean
public class FileStore {

	private static final String MD5_ALGORITHM = "MD5";
	
	private final Path baseDirectory;

	@Inject
	public FileStore(@NonNull FileStoreConfiguration config) {
		baseDirectory = config.getBaseDirectory().toPath();
	}
	
	public String computeMd5Hex(@NonNull File file) throws IOException {
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

	public void storeMedia(@NonNull File source, @NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		try (InputStream is = Files.newInputStream(source.toPath())) {
			Files.copy(is, buildAbsolutePath(md5, timestamp, format));
		}
	}
	
	private Path buildRelativePath(String md5, Temporal timestamp, FileFormat format) {
		return Paths.get(format.getSizeVariant(), DateUtil.COMPACT_DATE_FORMATTER.format(timestamp), md5 + format.getExtension());
	}
	
	private Path buildAbsolutePath(String md5, Temporal timestamp, FileFormat format) throws IOException {
		Path path = baseDirectory.resolve(buildRelativePath(md5, timestamp, format));
		Files.createDirectories(path.getParent());
		return path;
	}

	public File createEmptyFile(@NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, timestamp, format);
		return Files.createFile(path).toFile();
	}
	
	public File getExistingFile(@NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, timestamp, format);
		if (!Files.exists(path)) {
			throw new NoSuchFileException(path.toString());
		}
		return path.toFile();
	}
}
