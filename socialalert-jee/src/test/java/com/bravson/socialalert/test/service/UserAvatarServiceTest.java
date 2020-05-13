package com.bravson.socialalert.test.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Optional;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.store.TempFileFormat;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.avatar.AvatarFileProcessor;
import com.bravson.socialalert.business.user.avatar.UserAvatarService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.domain.user.UserInfo;

public class UserAvatarServiceTest extends BaseServiceTest {

	@InjectMocks
	UserAvatarService avatarService;
	
	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	FileStore fileStore;
	
	@Mock
	AvatarFileProcessor processor;

	@Test
	public void storeInvalidFile() {
		FileUploadParameter param = new FileUploadParameter(new File("test.jpg"), "text/plain");
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		assertThrows(NotSupportedException.class, () -> avatarService.storeAvatar(param, userAccess));
	}
	
	@Test
	public void storePictureForNonExistingUser() throws IOException {
		FileUploadParameter param = new FileUploadParameter(new File("test.jpg"), "image/jpeg");
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> avatarService.storeAvatar(param, userAccess));
	}
	
	@Test
	public void storeNonExistingFile() throws IOException {
		FileUploadParameter param = new FileUploadParameter(new File("test.jpg"), "image/jpeg");
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		UserProfileEntity profileEntity = new UserProfileEntity(userAccess.getUserId());
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		when(fileStore.computeMd5Hex(param.getInputFile())).thenThrow(FileNotFoundException.class);
		assertThrows(FileNotFoundException.class, () -> avatarService.storeAvatar(param, userAccess));
	}
	
	@Test
	public void storeValidFile() throws IOException {
		FileUploadParameter param = new FileUploadParameter(new File("test.jpg"), "image/jpeg");
		UserAccess userAccess = UserAccess.of("test", "1.1.1.1");
		String md5 = "abcd";
		UserProfileEntity profileEntity = new UserProfileEntity("test", "test@test.com", userAccess);
		
		when(profileRepository.findByUserId(userAccess.getUserId())).thenReturn(Optional.of(profileEntity));
		when(fileStore.computeMd5Hex(param.getInputFile())).thenReturn(md5);
		File outputFile = new File("output.jpg");
		when(fileStore.storeFile(param.getInputFile(), md5, userAccess.getUserId(), MediaFileFormat.MEDIA_JPG)).thenReturn(outputFile);
		when(fileStore.getExistingFile(md5, userAccess.getUserId(), MediaFileFormat.MEDIA_JPG)).thenReturn(outputFile);

		File tempFile = new File("output.tmp");
		when(fileStore.createEmptyFile(eq(md5), eq(userAccess.getUserId()), any(TempFileFormat.class))).thenReturn(tempFile);
		
		when(processor.getFormat(MediaSizeVariant.THUMBNAIL)).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		File thumbnailFile = new File("thumbnail.jpg");
		when(processor.createVariant(eq(outputFile), eq(tempFile), eq(MediaSizeVariant.THUMBNAIL))).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		when(fileStore.changeFileFormat(eq(md5), eq(userAccess.getUserId()), any(TempFileFormat.class), eq(MediaFileFormat.THUMBNAIL_JPG))).thenReturn(thumbnailFile);
		
		when(processor.getFormat(MediaSizeVariant.PREVIEW)).thenReturn(MediaFileFormat.PREVIEW_JPG);
		File previewFile = new File("preview.jpg");
		when(processor.createVariant(eq(outputFile), eq(tempFile), eq(MediaSizeVariant.PREVIEW))).thenReturn(MediaFileFormat.PREVIEW_JPG);
		when(fileStore.changeFileFormat(eq(md5), eq(userAccess.getUserId()), any(TempFileFormat.class), eq(MediaFileFormat.PREVIEW_JPG))).thenReturn(previewFile);
		
		UserInfo userInfo = avatarService.storeAvatar(param, userAccess);
		
		assertThat(userInfo.getImageUri()).isEqualTo(userAccess.getUserId() + "/" + md5);
	}
	
	@Test
	public void getMissingSmallImage() throws IOException {
		when(processor.getFormat(MediaSizeVariant.THUMBNAIL)).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		when(fileStore.findExistingFile("abcd", "test", MediaFileFormat.THUMBNAIL_JPG)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> avatarService.getSmallImage("test/abcd"));
	}
	
	@Test
	public void getExistingSmallImage() throws IOException {
		File outputFile = new File("thumbnail.jpg");
		when(processor.getFormat(MediaSizeVariant.THUMBNAIL)).thenReturn(MediaFileFormat.THUMBNAIL_JPG);
		when(fileStore.findExistingFile("abcd", "test", MediaFileFormat.THUMBNAIL_JPG)).thenReturn(Optional.of(outputFile));
		FileResponse response = avatarService.getSmallImage("test/abcd");
		assertThat(response.getFile()).isEqualTo(outputFile);
		assertThat(response.getFormat()).isEqualTo(MediaFileFormat.THUMBNAIL_JPG);
	}
	
	@Test
	public void getMissingLargeImage() throws IOException {
		when(processor.getFormat(MediaSizeVariant.PREVIEW)).thenReturn(MediaFileFormat.PREVIEW_JPG);
		when(fileStore.findExistingFile("abcd", "test", MediaFileFormat.PREVIEW_JPG)).thenReturn(Optional.empty());
		assertThrows(NotFoundException.class, () -> avatarService.getLargeImage("test/abcd"));
	}
	
	@Test
	public void getExistingLargeImage() throws IOException {
		File outputFile = new File("preview.jpg");
		when(processor.getFormat(MediaSizeVariant.PREVIEW)).thenReturn(MediaFileFormat.PREVIEW_JPG);
		when(fileStore.findExistingFile("abcd", "test", MediaFileFormat.PREVIEW_JPG)).thenReturn(Optional.of(outputFile));
		FileResponse response = avatarService.getLargeImage("test/abcd");
		assertThat(response.getFile()).isEqualTo(outputFile);
		assertThat(response.getFormat()).isEqualTo(MediaFileFormat.PREVIEW_JPG);
	}
}
