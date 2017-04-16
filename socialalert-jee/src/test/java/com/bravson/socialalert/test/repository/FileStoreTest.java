package com.bravson.socialalert.test.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.NoSuchFileException;
import java.time.LocalDate;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.store.FileStoreConfiguration;

public class FileStoreTest extends Assertions {

	private static FileStoreConfiguration config = FileStoreConfiguration.builder().baseDirectory(new File("C:/Temp/test")).build();
	
	private FileStore store = new FileStore(config);
	
	@Before
	public void checkNoStore() throws IOException {
		if (config.getBaseDirectory().exists()) {
			throw new IOException("Base directory " + config.getBaseDirectory() + " must not exist");
		}
	}
	
	@After
	public void cleanupStore() throws IOException {
		FileUtils.deleteDirectory(config.getBaseDirectory());
	}
	
	@Test
	public void computeMd5() throws IOException {
		String result = store.computeMd5Hex(new File("src/main/resources/logo.jpg"));
		assertThat(result).isEqualTo("38c4297b9099b466eab20fea521ee2f6");
	}
	
	@Test
	public void storeExistingFile() throws IOException {
		store.storeMedia(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.MEDIA_JPG);
		assertThat(new File(config.getBaseDirectory(), "media/20170415/38c4297b9099b466eab20fea521ee2f6.jpg")).exists();
	}
	
	@Test(expected=NoSuchFileException.class)
	public void storeNonExistingFile() throws IOException {
		store.storeMedia(new File("src/main/resources/xyz.jpg"), "38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.MEDIA_JPG);
	}
	
	@Test(expected=FileAlreadyExistsException.class)
	public void storeFileTwice() throws IOException {
		store.storeMedia(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.MEDIA_JPG);
		store.storeMedia(new File("src/main/resources/logo.jpg"), "38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.MEDIA_JPG);
	}
	
	@Test
	public void storeNewMp4File() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2016, 12, 4), MediaFileFormat.PREVIEW_MP4);
		assertThat(new File(config.getBaseDirectory(), "preview/20161204/38c4297b9099b466eab20fea521ee2f6.mp4")).exists();
	}
	
	@Test(expected=FileAlreadyExistsException.class)
	public void storeNewFileTwice() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.PREVIEW_MP4);
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2017, 4, 15), MediaFileFormat.PREVIEW_MP4);
	}
	
	@Test(expected=NoSuchFileException.class)
	public void getNonExistingMp4File() throws IOException {
		store.getExistingFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2016, 12, 4), MediaFileFormat.PREVIEW_MP4);
	}
	
	@Test
	public void getExistingMp4File() throws IOException {
		store.createEmptyFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2016, 12, 4), MediaFileFormat.PREVIEW_MP4);
		File file = store.getExistingFile("38c4297b9099b466eab20fea521ee2f6", LocalDate.of(2016, 12, 4), MediaFileFormat.PREVIEW_MP4);
		assertThat(file).exists();
	}
}
