package com.bravson.socialalert.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import javax.ws.rs.NotSupportedException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileMetadata;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.file.FileUploadParameter;
import com.bravson.socialalert.file.FileUploadService;
import com.bravson.socialalert.file.MediaFileStore;
import com.bravson.socialalert.file.media.MediaFileConstants;
import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.file.media.MediaSizeVariant;
import com.bravson.socialalert.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

public class FileUploadServiceTest extends BaseServiceTest {

	@InjectMocks
	FileUploadService fileUploadService;
	
	@Mock
	FileRepository mediaRepository;
	
	@Mock
	MediaFileStore mediaFileStore;
	
	@Mock
	AsyncRepository asyncRepository;
	
	@Mock
	Logger logger;
	
	@Test
	public void uploadExistingPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(0L).fileFormat(fileFormat).userId(userId).ipAddress(ipAddress).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat, userId, ipAddress)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = FileEntity.of(fileMetadata, mediaMetadata);
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).userId(userId).ipAddress(ipAddress).build();
		FileMetadata result = fileUploadService.uploadMedia(param);
		
		assertThat(result).isEqualTo(fileMetadata);
		verifyZeroInteractions(asyncRepository, logger);
	}
	
	@Test
	public void uploadNewPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).timestamp(Instant.EPOCH).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(0L).fileFormat(fileFormat).userId(userId).ipAddress(ipAddress).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat, userId, ipAddress)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = FileEntity.of(fileMetadata, mediaMetadata);
		when(mediaRepository.storeMedia(fileMetadata, mediaMetadata)).thenReturn(fileEntity);
		
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA)).thenReturn(fileMetadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW)).thenReturn(fileMetadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL)).thenReturn(fileMetadata);
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).userId(userId).ipAddress(ipAddress).build();
		FileMetadata result = fileUploadService.uploadMedia(param);
		
		assertThat(result).isEqualTo(fileMetadata);
		verifyZeroInteractions(asyncRepository, logger);
	}
	
	@Test
	public void uploadInvalidPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenThrow(Exception.class);
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).userId(userId).ipAddress(ipAddress).build();
		assertThatExceptionOfType(NotSupportedException.class).isThrownBy(() -> fileUploadService.uploadMedia(param));
		verify(logger).info(eq("Cannot extract metadata"), any(Exception.class));
		verifyZeroInteractions(asyncRepository);
	}
	
	@Test
	public void uploadNewVideo() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0236.MOV");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_MOV;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).duration(Duration.ofHours(2L)).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentLength(1000L).fileFormat(fileFormat).userId(userId).ipAddress(ipAddress).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat, userId, ipAddress)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = FileEntity.of(fileMetadata, mediaMetadata);
		when(mediaRepository.storeMedia(fileMetadata, mediaMetadata)).thenReturn(fileEntity);
		
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA)).thenReturn(fileMetadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW)).thenReturn(fileMetadata);
		when(mediaFileStore.storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL)).thenReturn(fileMetadata);
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.MOV_MEDIA_TYPE).userId(userId).ipAddress(ipAddress).build();
		FileMetadata result = fileUploadService.uploadMedia(param);
		
		assertThat(result).isEqualTo(fileMetadata);
		verify(asyncRepository).fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		verifyZeroInteractions(logger);
	}
}
