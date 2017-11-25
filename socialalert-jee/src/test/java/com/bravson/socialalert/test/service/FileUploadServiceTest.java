package com.bravson.socialalert.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
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

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.media.MediaFileConstants;
import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaSizeVariant;
import com.bravson.socialalert.business.file.video.AsyncVideoPreviewEvent;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.rest.ConflictException;

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
	UserInfoService userService;
	
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
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of(userId, ipAddress));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		
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
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, mediaMetadata, UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verifyZeroInteractions(asyncRepository, logger);
	}
	
	@Test
	public void uploadInvalidPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenThrow(Exception.class);
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(NotSupportedException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		verify(logger).info(eq("Cannot extract metadata"), any(Exception.class));
		verifyZeroInteractions(asyncRepository, userService);
	}
	
	@Test
	public void uploadNewVideo() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0236.MOV");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_MOV;
		
		MediaMetadata mediaMetadata = MediaMetadata.builder().width(100).height(100).duration(Duration.ofHours(2L)).build();
		when(mediaFileStore.buildMediaMetadata(inputFile, fileFormat)).thenReturn(mediaMetadata);
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = new FileEntity(fileMetadata, mediaMetadata, UserAccess.of(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, mediaMetadata, UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.MEDIA);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.THUMBNAIL);
		doReturn(fileMetadata).when(mediaFileStore).storeVariant(inputFile, fileMetadata, MediaSizeVariant.PREVIEW);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.MOV_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verify(asyncRepository).fireAsync(AsyncVideoPreviewEvent.of(fileEntity.getId()));
		verifyZeroInteractions(logger);
	}
}
