package com.bravson.socialalert.test.service;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.media.ApprovalModifier;
import com.bravson.socialalert.media.MediaDetail;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.media.MediaService;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.user.UserInfoService;
import com.bravson.socialalert.user.session.UserSessionService;

import static org.mockito.Mockito.*;

import java.util.Optional;

import javax.ws.rs.NotFoundException;

public class MediaServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaService mediaService;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	UserSessionService sessionService;
	
	@Mock
	MediaApprovalRepository approvalRepository;
	
	@Test
	public void viewExistingMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		MediaEntity mediaEntity = mock(MediaEntity.class);
		MediaDetail mediaDetail = new MediaDetail();
		when(sessionService.addViewedMedia(mediaUri)).thenReturn(true);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.find(mediaUri, userId)).thenReturn(Optional.empty());
		
		MediaDetail result = mediaService.viewMediaDetail(mediaUri, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isNull();
	}
	
	@Test
	public void viewLikedMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		MediaEntity mediaEntity = mock(MediaEntity.class);
		MediaDetail mediaDetail = new MediaDetail();
		MediaApprovalEntity approvalEntity = new MediaApprovalEntity(mediaUri, userId);
		approvalEntity.setModifier(ApprovalModifier.LIKE);
		when(sessionService.addViewedMedia(mediaUri)).thenReturn(true);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.find(mediaUri, userId)).thenReturn(Optional.of(approvalEntity));
		
		MediaDetail result = mediaService.viewMediaDetail(mediaUri, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(ApprovalModifier.LIKE);
	}
	
	@Test
	public void viewNonExistingMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		when(sessionService.addViewedMedia(mediaUri)).thenReturn(true);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> mediaService.viewMediaDetail(mediaUri, userId)).isInstanceOf(NotFoundException.class);
		verifyNoMoreInteractions(approvalRepository, userService);
	}
}
