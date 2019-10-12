package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

import javax.ws.rs.NotSupportedException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.media.AsyncMediaEnrichEvent;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
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
	public void uploadExistingPictureWithSameUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, UserAccess.of(userId, "4.3.2.1"));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result).isEqualTo(fileEntity.toFileInfo());
		verifyZeroInteractions(asyncRepository, logger);
	}
	
	@Test
	public void uploadExistingPictureWithDifferentUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, UserAccess.of("test2", "4.3.2.1"));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		
		verifyZeroInteractions(asyncRepository, logger);
	}
	
	@Test
	public void uploadExistingPictureWithClaimedState() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, UserAccess.of(userId, ipAddress));
		fileEntity.markProcessed(MediaMetadata.builder().width(1600).height(800).build());
		fileEntity.markClaimed(UserAccess.of(userId, ipAddress));
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
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = new FileEntity(fileMetadata, UserAccess.of(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verify(asyncRepository).fireAsync(AsyncMediaEnrichEvent.of(fileEntity.getId()));
		verifyZeroInteractions(logger);
	}
	
	@Test
	public void uploadInvalidPicture() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType("image/bmp").build();
		assertThatExceptionOfType(NotSupportedException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress)));
		verifyZeroInteractions(asyncRepository, logger, userService);
	}
	
	@Test
	public void uploadNewVideo() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0236.MOV");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_MOV;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(1000L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.empty());
		
		FileEntity fileEntity = new FileEntity(fileMetadata, UserAccess.of(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, UserAccess.of(userId, ipAddress))).thenReturn(fileEntity);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.MOV_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, UserAccess.of(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verify(asyncRepository).fireAsync(AsyncMediaEnrichEvent.of(fileEntity.getId()));
		verifyZeroInteractions(logger);
	}
}
