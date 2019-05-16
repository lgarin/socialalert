package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class AsyncMediaFileEnricherTest extends BaseServiceTest {

	// TODO
	
	/*
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
	*/
}
