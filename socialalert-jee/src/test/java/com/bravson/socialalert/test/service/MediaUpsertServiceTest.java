package com.bravson.socialalert.test.service;

import java.util.Arrays;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.MediaUpsertService;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.infrastructure.rest.ConflictException;

import static org.mockito.Mockito.when;

public class MediaUpsertServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaUpsertService mediaUpsertService;
	
	@Mock
	FileRepository fileRepository;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	MediaEntity mediaEntity;
	
	@Mock
	FileEntity fileEntity;
	
	@Mock
	Event<MediaEntity> newMediaEvent;
	
	@Test
	public void claimUnknownPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void claimAlreadyClaimedPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		
		assertThatExceptionOfType(ConflictException.class).isThrownBy(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
	}
	
	@Test
	public void claimExistingPictureWithWrongUser() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.markClaimed(userAccess)).thenReturn(false);
		
		assertThatExceptionOfType(ForbiddenException.class).isThrownBy(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
	}
	
	@Test
	public void claimExistingPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		MediaInfo mediaInfo = new MediaInfo();
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.markClaimed(userAccess)).thenReturn(true);
		when(mediaRepository.storeMedia(fileEntity, mediaParameter, userAccess)).thenReturn(mediaEntity);
		when(mediaEntity.toMediaInfo()).thenReturn(mediaInfo);
		when(userService.fillUserInfo(mediaInfo)).thenReturn(mediaInfo);
		
		MediaInfo result = mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess);
		assertThat(result).isSameAs(mediaInfo);
	}
	
	@Test
	public void updateExistingMedia() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		MediaInfo mediaInfo = new MediaInfo();
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.getUserId()).thenReturn("test");
		when(mediaEntity.toMediaInfo()).thenReturn(mediaInfo);
		when(userService.fillUserInfo(mediaInfo)).thenReturn(mediaInfo);
		
		MediaInfo result = mediaUpsertService.updateMedia(fileUri, mediaParameter, userAccess);
		assertThat(result).isSameAs(mediaInfo);
	}
	
	@Test
	public void updateNonExistingMedia() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.updateMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void updateMediaWithWrongUser() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategory("cat1");
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.getUserId()).thenReturn("abc");
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.updateMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(ForbiddenException.class);
	}
}
