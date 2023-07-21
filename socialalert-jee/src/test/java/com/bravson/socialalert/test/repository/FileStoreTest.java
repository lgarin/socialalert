package com.bravson.socialalert.test.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.store.FileStoreConfiguration;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class FileStoreTest extends Assertions {

	@Inject
	FileStoreConfiguration config;
	
	@Inject
	FileStore store;
	
	
	@BeforeEach
	@AfterEach
	public void cleanupStore() throws IOException {
		store.deleteAllFiles();
	}
	
	@Test
	public void computeMd5() throws IOException {
		String result = store.computeMd5Hex(new File("src/main/resources/logo.jpg"));
		assertThat(result).isEqualTo("38c4297b9099b466eab20fea521ee2f6");
	}
	
	@Test
	public void storeExistingFile() throws IOException {
		store.storeFile(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.MEDIA_JPG);
		assertThat(new File(config.baseDirectory(), "media/20170415/38c4297b9099b466eab20fea521ee2f6.jpg")).exists();
	}
	
	@Test
	public void storeNonExistingFile() throws IOException {
		assertThatThrownBy(() -> store.storeFile(new File("src/main/resources/xyz.jpg"), "38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.MEDIA_JPG)).isInstanceOf(NoSuchFileException.class);
	}
	
	@Test
	public void storeFileTwice() throws IOException {
		store.storeFile(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.MEDIA_JPG);
		assertThatThrownBy(() -> store.storeFile(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.MEDIA_JPG)).isInstanceOf(FileAlreadyExistsException.class);
	}
	
	@Test
	public void storeNewMp4File() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", "20161204", MediaFileFormat.PREVIEW_MP4);
		assertThat(new File(config.baseDirectory(), "preview/20161204/38c4297b9099b466eab20fea521ee2f6.mp4")).exists();
	}
	
	@Test
	public void storeNewFileTwice() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.PREVIEW_MP4);
		assertThatThrownBy(() -> store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.PREVIEW_MP4)).isInstanceOf(FileAlreadyExistsException.class);
	}
	
	@Test
	public void getNonExistingMp4File() throws IOException {
		assertThatThrownBy(() -> store.getExistingFile("38c4297b9099b466eab20fea521ee2f6", "20161204", MediaFileFormat.PREVIEW_MP4)).isInstanceOf(NoSuchFileException.class);
	}
	
	@Test
	public void getExistingMp4File() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", "20161204", MediaFileFormat.PREVIEW_MP4);
		File file = store.getExistingFile("38c4297b9099b466eab20fea521ee2f6", "20161204", MediaFileFormat.PREVIEW_MP4);
		assertThat(file).exists();
	}
	
	@Test
	public void deleteNonExistingFile() throws IOException {
		boolean result = store.deleteFile("xyz", "20200423", MediaFileFormat.MEDIA_JPG);
		assertThat(result).isFalse();
	}
	
	@Test
	public void deleteNonExistingFolder() throws IOException {
		boolean result = store.deleteFolder("xyz");
		assertThat(result).isFalse();
	}
	
	@Test
	public void deleteNonEmptyFolder() throws IOException {
		store.storeFile(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", "20170415", MediaFileFormat.MEDIA_JPG);
		boolean result = store.deleteFolder("media/20170415");
		assertThat(result).isTrue();
	}
}
