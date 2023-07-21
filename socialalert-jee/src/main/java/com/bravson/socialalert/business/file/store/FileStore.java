package com.bravson.socialalert.business.file.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

import com.bravson.socialalert.domain.media.format.FileFormat;

import jakarta.annotation.PostConstruct;
import com.bravson.socialalert.infrastructure.layer.Service;
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
		baseDirectory = config.baseDirectory().toPath();
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

	public File storeFile(@NonNull File source, @NonNull String md5, @NonNull String folder, @NonNull FileFormat format) throws IOException {
		Path targetPath = buildAbsolutePath(md5, folder, format);
		try (InputStream is = Files.newInputStream(source.toPath())) {
			Files.copy(is, targetPath);
		}
		return targetPath.toFile();
	}
	
	private Path buildRelativePath(String md5, String folder, FileFormat format) {
		return Paths.get(format.getSizeVariant(), folder, md5 + format.getExtension());
	}
	
	private Path buildAbsolutePath(String md5, String folder, FileFormat format) throws IOException {
		Path path = baseDirectory.resolve(buildRelativePath(md5, folder, format));
		Files.createDirectories(path.getParent());
		return path;
	}

	public File createEmptyFile(@NonNull String md5, @NonNull String folder, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, folder, format);
		return Files.createFile(path).toFile();
	}
	
	public File changeFileFormat(@NonNull String md5, @NonNull String folder, @NonNull FileFormat oldFormat, @NonNull FileFormat newFormat) throws IOException {
		Path oldPath = buildAbsolutePath(md5, folder, oldFormat);
		Path newPath = buildAbsolutePath(md5, folder, newFormat);
		return Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE).toFile();
	}
	
	public File getExistingFile(@NonNull String md5, @NonNull String folder, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, folder, format);
		if (!Files.exists(path)) {
			throw new NoSuchFileException(path.toString());
		}
		return path.toFile();
	}
	
	public Optional<File> findExistingFile(@NonNull String md5, @NonNull String folder, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, folder, format);
		if (!Files.exists(path)) {
			return Optional.empty();
		}
		return Optional.of(path.toFile());
	}
	
	public boolean deleteFile(@NonNull String md5, @NonNull String folder, @NonNull FileFormat format) throws IOException {
		Path path = buildAbsolutePath(md5, folder, format);
		return Files.deleteIfExists(path);
	}
	
	public boolean deleteFolder(@NonNull String folder) throws IOException {
		Path path = baseDirectory.resolve(folder);
		if (Files.isDirectory(path)) {
			Files.walk(baseDirectory.resolve(folder))
			  .filter(Files::isRegularFile)
		      .map(Path::toFile)
		      .forEach(File::delete);
			
			return baseDirectory.resolve(folder).toFile().delete();
		}
		return false;
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
