package com.bravson.socialalert.test.service;

import java.io.File;
import java.time.Instant;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.ws.rs.NotSupportedException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import com.bravson.socialalert.business.file.FileUploadService;
import com.bravson.socialalert.business.file.MediaFileStore;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.entity.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileRepository;
import com.bravson.socialalert.business.file.exchange.FileUploadParameter;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.rest.ConflictException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class FileUploadServiceTest extends BaseServiceTest {

	@InjectMocks
	FileUploadService fileUploadService;
	
	@Mock
	FileRepository mediaRepository;
	
	@Mock
	MediaFileStore mediaFileStore;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	Logger logger;
	
	@Mock
	Event<FileEntity> newFileEvent;
	
	@Test
	public void uploadExistingPictureWithSameUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess(userId, "4.3.2.1"));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, createUserAccess(userId, ipAddress));
		
		assertThat(result).isEqualTo(fileEntity.toFileInfo());
		verifyNoInteractions(logger, newFileEvent);
	}
	
	@Test
	public void uploadExistingPictureWithDifferentUser() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess("test2", "4.3.2.1"));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		UserAccess userAccess = createUserAccess(userId, ipAddress);
		assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, userAccess));
		
		verifyNoInteractions(logger, newFileEvent);
	}
	
	@Test
	public void uploadExistingPictureWithClaimedState() throws Exception {
		String userId = "test";
		String ipAddress = "1.2.3.4";
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		MediaFileFormat fileFormat = MediaFileFormat.MEDIA_JPG;
		
		FileMetadata fileMetadata = FileMetadata.builder().md5("123").timestamp(Instant.EPOCH).contentSize(0L).fileFormat(fileFormat).build();
		when(mediaFileStore.buildFileMetadata(inputFile, fileFormat)).thenReturn(fileMetadata);
		
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess(userId, ipAddress));
		fileEntity.markProcessed(MediaMetadata.builder().width(1600).height(800).build());
		fileEntity.markClaimed(createUserAccess(userId, ipAddress));
		when(mediaRepository.findFile(fileMetadata.buildFileUri())).thenReturn(Optional.of(fileEntity));
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, createUserAccess(userId, ipAddress)));
		
		verifyNoInteractions(logger, newFileEvent);
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
		
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, createUserAccess(userId, ipAddress))).thenReturn(fileEntity);
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.JPG_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, createUserAccess(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verifyNoInteractions(logger);
		verify(newFileEvent).fire(fileEntity);
	}
	
	@Test
	public void uploadInvalidPicture() throws Exception {
		File inputFile = new File("src/test/resources/media/IMG_0397.JPG");
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType("image/bmp").build();
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		assertThatExceptionOfType(NotSupportedException.class).isThrownBy(() -> fileUploadService.uploadMedia(param, userAccess));
		verifyNoInteractions(logger, userService, newFileEvent);
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
		
		FileEntity fileEntity = new FileEntity(fileMetadata, createUserAccess(userId, ipAddress));
		when(mediaRepository.storeMedia(fileMetadata, createUserAccess(userId, ipAddress))).thenReturn(fileEntity);
		
		when(userService.fillUserInfo(fileEntity.toFileInfo())).thenReturn(fileEntity.toFileInfo());
		
		FileUploadParameter param = FileUploadParameter.builder().inputFile(inputFile).contentType(MediaFileConstants.MOV_MEDIA_TYPE).build();
		FileInfo result = fileUploadService.uploadMedia(param, createUserAccess(userId, ipAddress));
		
		assertThat(result.getFileUri()).isEqualTo(fileMetadata.buildFileUri());
		verifyNoInteractions(logger);
		verify(newFileEvent).fire(fileEntity);
	}
}
