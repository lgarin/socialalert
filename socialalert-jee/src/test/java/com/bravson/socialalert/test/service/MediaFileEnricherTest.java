package com.bravson.socialalert.test.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.business.file.media.MediaFileEnricher;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaMetadataExtractor;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class MediaFileEnricherTest extends BaseServiceTest {

	@InjectMocks
	MediaFileEnricher enricher;
	
	@Mock
	FileRepository fileRepository;
	
	@Mock
	FileStore fileStore;
	
	@Mock
	MediaFileStore mediaFileStore;
	
	@Mock
	MediaMetadataExtractor metadataExtractor;
	
	@Mock
	AsyncRepository asyncRepository;
	
	@Test
	public void buildPictureMetadata() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess("test", "1.2.3.4"));
		MediaMetadata metadata = MediaMetadata.builder().cameraMaker("a").cameraModel("b").width(1200).height(1600).timestamp(Instant.EPOCH).build();
		
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat())).thenReturn(inputFile);
		when(metadataExtractor.parseMetadata(inputFile)).thenReturn(metadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW)).thenReturn(fileMetadata.withFileFormat(MediaFileFormat.PREVIEW_JPG));
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL)).thenReturn(fileMetadata.withFileFormat(MediaFileFormat.THUMBNAIL_JPG));
		
		enricher.handleNewMedia(fileEntity);
		
		assertThat(fileEntity.getMediaMetadata()).isEqualTo(metadata);
		assertThat(fileEntity.isProcessed()).isTrue();
		assertThat(fileEntity.findVariantFormat(MediaSizeVariant.PREVIEW)).isPresent();
		assertThat(fileEntity.findVariantFormat(MediaSizeVariant.THUMBNAIL)).isPresent();
		
		verify(asyncRepository).fireAsync(AsyncMediaProcessedEvent.of(fileEntity.getId()));
	}
	
	@Test
	public void buildVideoMetadata() throws Exception {
		File inputFile = new File("src/test/resources/media/VID_0397.MP4");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_MP4).build();
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess("test", "1.2.3.4"));
		MediaMetadata metadata = MediaMetadata.builder().cameraMaker("a").cameraModel("b").width(1200).height(1600).timestamp(Instant.EPOCH).build();
		
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat())).thenReturn(inputFile);
		when(metadataExtractor.parseMetadata(inputFile)).thenReturn(metadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW)).thenReturn(fileMetadata.withFileFormat(MediaFileFormat.PREVIEW_JPG));
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL)).thenReturn(fileMetadata.withFileFormat(MediaFileFormat.THUMBNAIL_JPG));
		
		enricher.handleNewMedia(fileEntity);
		
		assertThat(fileEntity.getMediaMetadata()).isEqualTo(metadata);
		assertThat(fileEntity.isProcessed()).isTrue();
		assertThat(fileEntity.findVariantFormat(MediaSizeVariant.PREVIEW)).isPresent();
		assertThat(fileEntity.findVariantFormat(MediaSizeVariant.THUMBNAIL)).isPresent();
		
		verify(asyncRepository).fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		verifyNoMoreInteractions(asyncRepository);
	}
	
	@Test
	public void buildInvalidVideoMetadata() throws Exception {
		File inputFile = new File("src/test/resources/media/VID_0397.MP4");
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_MP4).build();
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess("test", "1.2.3.4"));
		IOException exception = new IOException();
		
		when(fileStore.getExistingFile(fileMetadata.getMd5(), fileMetadata.getFormattedDate(), fileMetadata.getFileFormat())).thenReturn(inputFile);
		when(metadataExtractor.parseMetadata(inputFile)).thenThrow(exception);
		
		assertThrows(IOException.class, () -> enricher.handleNewMedia(fileEntity));
		
		assertThat(fileEntity.getMediaMetadata()).isNull();;
		assertThat(fileEntity.isProcessed()).isFalse();

		verifyNoMoreInteractions(asyncRepository);
	}
}
