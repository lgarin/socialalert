package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import javax.enterprise.event.Event;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.MediaService;
import com.bravson.socialalert.business.media.MediaStatistic;
import com.bravson.socialalert.business.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.business.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.business.media.feeling.MediaFeelingRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.activity.OnlineUserCache;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

public class MediaServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaService mediaService;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	OnlineUserCache onlineUserRepository;
	
	@Mock
	MediaApprovalRepository approvalRepository;
	
	@Mock
	MediaFeelingRepository feelingRepository;
	
	@Mock
	Event<MediaEntity> mediaHitEvent;
	
	@Mock
	Event<MediaEntity> mediaLikedEvent;
	
	@Mock
	Event<MediaEntity> mediaDislikedEvent;
	
	@Test
	public void viewExistingMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		MediaEntity mediaEntity = mock(MediaEntity.class);
		MediaDetail mediaDetail = new MediaDetail();
		when(onlineUserRepository.addViewedMedia(userId, mediaUri)).thenReturn(true);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.find(mediaUri, userId)).thenReturn(Optional.empty());
		when(feelingRepository.find(mediaUri, userId)).thenReturn(Optional.empty());
		
		MediaDetail result = mediaService.viewMediaDetail(mediaUri, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isNull();
	}
	
	@Test
	public void viewLikedMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		MediaEntity mediaEntity = mock(MediaEntity.class);
		when(mediaEntity.getId()).thenReturn(mediaUri);
		MediaDetail mediaDetail = new MediaDetail();
		MediaApprovalEntity approvalEntity = new MediaApprovalEntity(mediaEntity, userId);
		approvalEntity.setModifier(ApprovalModifier.LIKE);
		when(onlineUserRepository.addViewedMedia(userId, mediaUri)).thenReturn(true);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.find(mediaUri, userId)).thenReturn(Optional.of(approvalEntity));
		when(feelingRepository.find(mediaUri, userId)).thenReturn(Optional.empty());
		
		MediaDetail result = mediaService.viewMediaDetail(mediaUri, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(ApprovalModifier.LIKE);
	}
	
	@Test
	public void viewNonExistingMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> mediaService.viewMediaDetail(mediaUri, userId)).isInstanceOf(NotFoundException.class);
		verifyNoMoreInteractions(approvalRepository, userService);
	}
	
	@Test
	public void likeMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		ApprovalModifier modifier = ApprovalModifier.LIKE;
		MediaEntity mediaEntity = mock(MediaEntity.class);
		when(mediaEntity.getId()).thenReturn(mediaUri);
		MediaStatistic mediaStatistic = new MediaStatistic();
		MediaDetail mediaDetail = new MediaDetail();
		MediaApprovalEntity approvalEntity = new MediaApprovalEntity(mediaEntity, userId);
		approvalEntity.setModifier(modifier);
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(mediaEntity.getStatistic()).thenReturn(mediaStatistic);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.changeApproval(mediaEntity, userId, modifier)).thenReturn(Optional.of(approvalEntity));
		
		MediaDetail result = mediaService.setApprovalModifier(mediaUri, modifier, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(modifier);
		
		verify(mediaLikedEvent).fire(mediaEntity);
	}
	
	@Test
	public void resetMediaApproval() {
		String userId = "user1";
		String mediaUri = "uri1";
		ApprovalModifier modifier = null;
		MediaEntity mediaEntity = mock(MediaEntity.class);
		MediaDetail mediaDetail = new MediaDetail();
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(mediaEntity.toMediaDetail()).thenReturn(mediaDetail);
		when(userService.fillUserInfo(mediaDetail)).thenReturn(mediaDetail);
		when(approvalRepository.changeApproval(mediaEntity, userId, modifier)).thenReturn(Optional.empty());
		
		MediaDetail result = mediaService.setApprovalModifier(mediaUri, modifier, userId);
		assertThat(result).isEqualTo(mediaDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(modifier);
		
		verifyNoInteractions(mediaLikedEvent, mediaDislikedEvent);
	}
	
	@Test
	public void dislikeNonExistingMedia() {
		String userId = "user1";
		String mediaUri = "uri1";
		ApprovalModifier modifier = ApprovalModifier.DISLIKE;
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> mediaService.setApprovalModifier(mediaUri, modifier, userId)).isInstanceOf(NotFoundException.class);
		verifyNoMoreInteractions(approvalRepository, userService);
	}
}
