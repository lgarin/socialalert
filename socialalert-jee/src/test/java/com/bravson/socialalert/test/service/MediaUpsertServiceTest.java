package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.media.GeoAddress;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaInfo;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.media.MediaUpsertService;
import com.bravson.socialalert.media.UpsertMediaParameter;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;

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
	
	@Test
	public void claimUnknownPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void claimAlreadyClaimedPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOfSatisfying(WebApplicationException.class, e -> assertThat(e.getResponse().getStatus()).isEqualTo(409));
	}
	
	@Test
	public void claimExistingPictureWithWrongUser() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.getUserId()).thenReturn("other");
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(ForbiddenException.class);
	}
	
	@Test
	public void claimExistingPicture() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		MediaInfo mediaInfo = new MediaInfo();
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.getUserId()).thenReturn("test");
		when(mediaRepository.storeMedia(fileEntity, mediaParameter, userAccess)).thenReturn(mediaEntity);
		when(mediaEntity.toMediaInfo()).thenReturn(mediaInfo);
		when(userService.fillUserInfo(mediaInfo)).thenReturn(mediaInfo);
		
		MediaInfo result = mediaUpsertService.claimMedia(fileUri, mediaParameter, userAccess);
		assertThat(result).isSameAs(mediaInfo);
	}
	
	@Test
	public void updateExistingMedia() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
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
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.updateMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(NotFoundException.class);
	}
	
	@Test
	public void updateMediaWithWrongUser() {
		String fileUri = "file123.jpg";
		UserAccess userAccess = UserAccess.of("test", "1.2.3.4");
		UpsertMediaParameter mediaParameter = new UpsertMediaParameter();
		mediaParameter.setTitle("Test title");
		mediaParameter.setDescription("Test desc");
		mediaParameter.setTags(Arrays.asList("tag1", "tag2"));
		mediaParameter.setCategories(Arrays.asList("cat1"));
		mediaParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.getUserId()).thenReturn("abc");
		
		Throwable exception = catchThrowable(() -> mediaUpsertService.updateMedia(fileUri, mediaParameter, userAccess));
		assertThat(exception).isInstanceOf(ForbiddenException.class);
	}
}
