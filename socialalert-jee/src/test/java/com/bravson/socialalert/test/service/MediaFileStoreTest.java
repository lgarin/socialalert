package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.MediaFileStore;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.picture.PictureFileProcessor;
import com.bravson.socialalert.file.store.FileStore;
import com.bravson.socialalert.file.video.SnapshotVideoFileProcessor;

import static org.mockito.Mockito.*;

public class MediaFileStoreTest extends BaseServiceTest {

	@InjectMocks
	MediaFileStore mediaFileStore;
	
	@Mock
	FileStore fileStore;

	@Mock
	PictureFileProcessor pictureFileProcessor;
	
	@Mock
	SnapshotVideoFileProcessor videoFileProcessor;
	
	@Test
	public void buildFileMetadata() throws IOException {
		String md5 = "1234";
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(fileStore.computeMd5Hex(inputFile)).thenReturn(md5);
		
		FileMetadata result = mediaFileStore.buildFileMetadata(inputFile, fileFormat, userId, ipAddress);
		assertThat(result).isEqualTo(FileMetadata.builder().md5(md5).timestamp(result.getTimestamp()).fileFormat(fileFormat).contentLength(2_100_375L).ipAddress(ipAddress).userId(userId).build());
	}
	
	@Test
	public void buildFileMetadataPropagatesException() throws IOException {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(fileStore.computeMd5Hex(inputFile)).thenThrow(IOException.class);
		
		assertThatThrownBy(() -> mediaFileStore.buildFileMetadata(inputFile, fileFormat, userId, ipAddress)).isInstanceOf(IOException.class);
	}
	
	@Test
	public void buildPictureMetadata() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		MediaMetadata metadata = MediaMetadata.builder().cameraMaker("a").cameraModel("b").width(1200).height(1600).timestamp(Instant.EPOCH).build();
		
		when(pictureFileProcessor.parseMetadata(inputFile)).thenReturn(metadata);
		
		MediaMetadata result = mediaFileStore.buildMediaMetadata(inputFile, fileFormat);
		assertThat(result).isEqualTo(metadata);
		
		verifyNoMoreInteractions(fileStore, videoFileProcessor);
	}
	
	@Test
	public void buildVideoMetadata() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_MP4;
		MediaMetadata metadata = MediaMetadata.builder().cameraMaker("a").cameraModel("b").width(1200).height(1600).duration(Duration.ofMinutes(10)).timestamp(Instant.EPOCH).build();
		
		when(videoFileProcessor.parseMetadata(inputFile)).thenReturn(metadata);
		
		MediaMetadata result = mediaFileStore.buildMediaMetadata(inputFile, fileFormat);
		assertThat(result).isEqualTo(metadata);
		
		verifyNoMoreInteractions(fileStore, pictureFileProcessor);
	}
	
	// TODO add test for storeVariant
}
