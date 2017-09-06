package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.verifyNoMoreInteractions;
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
import com.bravson.socialalert.media.ClaimPictureParameter;
import com.bravson.socialalert.media.GeoAddress;
import com.bravson.socialalert.media.MediaClaimParameter;
import com.bravson.socialalert.media.MediaClaimService;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaInfo;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.user.UserInfoSupplier;

public class MediaClaimServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaClaimService mediaClaimService;
	
	@Mock
	FileRepository fileRepository;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	UserInfoSupplier userRepository;
	
	@Mock
	MediaEntity mediaEntity;
	
	@Mock
	FileEntity fileEntity;
	
	@Test
	public void claimUnknownPicture() {
		String fileUri = "file123.jpg";
		MediaClaimParameter mediaParameter = MediaClaimParameter.builder().fileUri(fileUri).userId("test").ipAddress("1.2.3.4").build();
		ClaimPictureParameter pictureParameter = new ClaimPictureParameter();
		pictureParameter.setTitle("Test title");
		pictureParameter.setDescription("Test desc");
		pictureParameter.setTags(Arrays.asList("tag1", "tag2"));
		pictureParameter.setCategories(Arrays.asList("cat1"));
		pictureParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.empty());
		
		Throwable exception = catchThrowable(() -> mediaClaimService.claimPicture(mediaParameter, pictureParameter));
		assertThat(exception).isInstanceOf(NotFoundException.class);
		
		verifyNoMoreInteractions(userRepository, fileEntity, mediaEntity);
	}
	
	@Test
	public void claimAlreadyClaimedPicture() {
		String fileUri = "file123.jpg";
		MediaClaimParameter mediaParameter = MediaClaimParameter.builder().fileUri(fileUri).userId("test").ipAddress("1.2.3.4").build();
		ClaimPictureParameter pictureParameter = new ClaimPictureParameter();
		pictureParameter.setTitle("Test title");
		pictureParameter.setDescription("Test desc");
		pictureParameter.setTags(Arrays.asList("tag1", "tag2"));
		pictureParameter.setCategories(Arrays.asList("cat1"));
		pictureParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.of(mediaEntity));
		
		Throwable exception = catchThrowable(() -> mediaClaimService.claimPicture(mediaParameter, pictureParameter));
		assertThat(exception).isInstanceOfSatisfying(WebApplicationException.class, e -> assertThat(e.getResponse().getStatus()).isEqualTo(409));
		
		verifyNoMoreInteractions(fileRepository, userRepository, fileEntity, mediaEntity);
	}
	
	@Test
	public void claimExistingPictureWithWrongUser() {
		String fileUri = "file123.jpg";
		MediaClaimParameter mediaParameter = MediaClaimParameter.builder().fileUri(fileUri).userId("test").ipAddress("1.2.3.4").build();
		ClaimPictureParameter pictureParameter = new ClaimPictureParameter();
		pictureParameter.setTitle("Test title");
		pictureParameter.setDescription("Test desc");
		pictureParameter.setTags(Arrays.asList("tag1", "tag2"));
		pictureParameter.setCategories(Arrays.asList("cat1"));
		pictureParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.getUserId()).thenReturn("other");
		when(fileEntity.isPicture()).thenReturn(true);
		
		Throwable exception = catchThrowable(() -> mediaClaimService.claimPicture(mediaParameter, pictureParameter));
		assertThat(exception).isInstanceOf(ForbiddenException.class);
		
		verifyNoMoreInteractions(userRepository, mediaEntity);
	}
	
	@Test
	public void claimExistingPicture() {
		String fileUri = "file123.jpg";
		MediaClaimParameter mediaParameter = MediaClaimParameter.builder().fileUri(fileUri).userId("test").ipAddress("1.2.3.4").build();
		ClaimPictureParameter pictureParameter = new ClaimPictureParameter();
		pictureParameter.setTitle("Test title");
		pictureParameter.setDescription("Test desc");
		pictureParameter.setTags(Arrays.asList("tag1", "tag2"));
		pictureParameter.setCategories(Arrays.asList("cat1"));
		pictureParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		
		MediaInfo mediaInfo = new MediaInfo();
		
		when(mediaRepository.findMedia(fileUri)).thenReturn(Optional.empty());
		when(fileRepository.findFile(fileUri)).thenReturn(Optional.of(fileEntity));
		when(fileEntity.getUserId()).thenReturn("test");
		when(fileEntity.isPicture()).thenReturn(true);
		when(mediaRepository.storeMedia(fileEntity, pictureParameter, "test", "1.2.3.4")).thenReturn(mediaEntity);
		when(mediaEntity.toMediaInfo()).thenReturn(mediaInfo);
		when(userRepository.fillUserInfo(mediaInfo)).thenReturn(mediaInfo);
		
		MediaInfo result = mediaClaimService.claimPicture(mediaParameter, pictureParameter);
		assertThat(result).isSameAs(mediaInfo);
	}
}
