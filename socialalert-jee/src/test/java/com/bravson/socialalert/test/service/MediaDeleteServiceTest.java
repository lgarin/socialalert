package com.bravson.socialalert.test.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.event.Event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaDeleteService;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.media.UpsertMediaParameter;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class MediaDeleteServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaDeleteService deleteService;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	Event<MediaEntity> deleteMediaEvent;
	
	
	private MediaEntity buildMediaEntity() {
		FileEntity file = new FileEntity("123");
		file.addVariant(FileMetadata.builder().md5("xyz").fileFormat(MediaFileFormat.MEDIA_JPG).timestamp(Instant.EPOCH).contentSize(0L).build());
		UpsertMediaParameter param = UpsertMediaParameter.builder().title("test").tags(Collections.emptyList()).build();
		return new MediaEntity(file, param, createUserAccess("test", "1.2.3.4"));
	}
	
	@Test
	public void deleteMediaFromUser() {
		MediaEntity media = buildMediaEntity();
		UserProfileEntity profile = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		Mockito.when(mediaRepository.listByUserId(profile.getId())).thenReturn(Collections.singletonList(media));
		deleteService.handleDeleteUser(profile);
		Mockito.verify(mediaRepository).delete(media);
		Mockito.verify(deleteMediaEvent).fire(media);
	}
	
	@Test
	public void deleteMedia() {
		MediaEntity media = buildMediaEntity();
		Mockito.when(mediaRepository.findMedia(media.getId())).thenReturn(Optional.of(media));
		deleteService.delete(media.getId());
		Mockito.verify(mediaRepository).delete(media);
		Mockito.verify(deleteMediaEvent).fire(media);
	}
}
