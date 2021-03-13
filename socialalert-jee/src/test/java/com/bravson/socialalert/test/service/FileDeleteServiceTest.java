package com.bravson.socialalert.test.service;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.bravson.socialalert.business.file.FileDeleteService;
import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class FileDeleteServiceTest extends BaseServiceTest {

	@InjectMocks
	FileDeleteService deleteService;
	
	@Mock
	FileRepository fileRepository;

	@Mock
	FileStore fileStore;
	
	private MediaEntity buildMediaEntity() {
		FileEntity file = new FileEntity("123");
		file.addVariant(FileMetadata.builder().md5("xyz").fileFormat(MediaFileFormat.MEDIA_JPG).timestamp(Instant.EPOCH).contentSize(0L).build());
		file.addVariant(FileMetadata.builder().md5("xyz").fileFormat(MediaFileFormat.PREVIEW_JPG).timestamp(Instant.EPOCH).contentSize(0L).build());
		file.addVariant(FileMetadata.builder().md5("xyz").fileFormat(MediaFileFormat.THUMBNAIL_JPG).timestamp(Instant.EPOCH).contentSize(0L).build());
		UpsertMediaParameter param = UpsertMediaParameter.builder().title("test").tags(Collections.emptyList()).build();
		return new MediaEntity(file, param, createUserAccess("test", "1.2.3.4"));
	}
	
	@Test
	public void deleteFileFromMedia() throws IOException {
		MediaEntity media = buildMediaEntity();
		deleteService.handleDeleteMedia(media);
		String md5 = media.getFile().getFileMetadata().getMd5();
		String folder = media.getFile().getFileMetadata().getFormattedDate();
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.MEDIA_JPG);
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.PREVIEW_JPG);
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.THUMBNAIL_JPG);
		Mockito.verify(fileRepository).delete(media.getFile());
	}
	
	@Test
	public void deleteFilesFromUser() throws IOException {
		MediaEntity media = buildMediaEntity();
		UserProfileEntity profile = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		Mockito.when(fileRepository.findByUserId(profile.getId())).thenReturn(Collections.singletonList(media.getFile()));
		deleteService.handleDeleteUser(profile);
		String md5 = media.getFile().getFileMetadata().getMd5();
		String folder = media.getFile().getFileMetadata().getFormattedDate();
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.MEDIA_JPG);
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.PREVIEW_JPG);
		Mockito.verify(fileStore).deleteFile(md5, folder, MediaFileFormat.THUMBNAIL_JPG);
		Mockito.verify(fileRepository).delete(media.getFile());
		Mockito.verify(fileStore).deleteFolder(profile.getId());
	}
}
