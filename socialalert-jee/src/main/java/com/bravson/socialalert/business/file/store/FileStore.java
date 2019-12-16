package com.bravson.socialalert.business.file.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.temporal.Temporal;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.domain.media.format.FileFormat;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.util.DateUtil;
import com.bravson.socialalert.infrastructure.util.Md5Util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileStore {

	private Path baseDirectory;

	@Inject
	public FileStore(@NonNull FileStoreConfiguration config) {
		baseDirectory = config.getBaseDirectory().toPath();
	}
	
	@PostConstruct
	protected void checkBaseDirectory() throws IOException {
		if (!Files.isDirectory(baseDirectory) || !Files.exists(baseDirectory)) {
			throw new IOException("Base directory " + baseDirectory + " must exist");
		}
	}
	
	public String computeMd5Hex(@NonNull File file) throws IOException {
		return Md5Util.computeMd5Hex(file);
	}

	public File storeFile(@NonNull File source, @NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		Path targetPath = buildAbsolutePath(md5, timestamp, format);
		try (InputStream is = Files.newInputStream(source.toPath())) {
			Files.copy(is, targetPath);
		}
		return targetPath.toFile();
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
	
	public File changeFileFormat(@NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat oldFormat, @NonNull FileFormat newFormat) throws IOException {
		Path oldPath = buildAbsolutePath(md5, timestamp, oldFormat);
		Path newPath = buildAbsolutePath(md5, timestamp, newFormat);
		return Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE).toFile();
	}
	
	public File getExistingFile(@NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, timestamp, format);
		if (!Files.exists(path)) {
			throw new NoSuchFileException(path.toString());
		}
		return path.toFile();
	}
	
	public boolean deleteFile(@NonNull String md5, @NonNull Temporal timestamp, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, timestamp, format);
		return Files.deleteIfExists(path);
	}
	
	public void deleteAllFiles() throws IOException {
		Files.walk(baseDirectory)
		  .filter(Files::isRegularFile)
	      .map(Path::toFile)
	      .forEach(File::delete);
		
		Files.walk(baseDirectory)
			.filter(Files::isDirectory)
			.filter(p -> !p.equals(baseDirectory))
			.map(Path::toFile)
		    .forEach(File::delete);
	}
}
