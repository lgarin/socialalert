package com.bravson.socialalert.test.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

public class MediaCommentServiceTest {

	@InjectMocks
	MediaCommentService commentService;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	MediaCommentRepository commentRepository;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Mock
	MediaCommentEntity commentEntity;
	
	@Mock
	CommentApprovalRepository approvalRepository;
	
	@Mock
	Event<MediaCommentEntity> commentLikedEvent;
	
	@Test
	public void createCommentForExitingMedia() {
		String mediaUri = "uri1";
		String comment = "test";
		UserAccess userAccess = UserAccess.of("user1", "1.2.3.4");
		MediaEntity mediaEntity = mock(MediaEntity.class);
		MediaCommentInfo commentInfo = new MediaCommentInfo();
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(commentRepository.create(mediaUri, comment, userAccess)).thenReturn(commentEntity);
		when(commentEntity.toMediaCommentInfo()).thenReturn(commentInfo);
		when(userService.fillUserInfo(commentInfo)).thenReturn(commentInfo);
		
		MediaCommentInfo result = commentService.createComment(mediaUri, comment, userAccess);
		assertThat(result).isSameAs(commentInfo);
	}
	
	@Test
	public void createCommentForNonExitingMedia() {
		String mediaUri = "uri1";
		String comment = "test";
		UserAccess userAccess = UserAccess.of("user1", "1.2.3.4");
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> commentService.createComment(mediaUri, comment, userAccess)).isInstanceOf(NotFoundException.class);
		verifyZeroInteractions(commentRepository, userService);
	}
	
	@Test
	public void listCommentsForExitingMedia() {
		String mediaUri = "uri1";
		String userId = "user1";
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		MediaEntity mediaEntity = mock(MediaEntity.class);
		QueryResult<MediaCommentEntity> entityResult = new QueryResult<>(Collections.emptyList(), 0, paging);
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(commentRepository.listByMediaUri(mediaUri, paging)).thenReturn(entityResult);
		
		QueryResult<MediaCommentDetail> result = commentService.listComments(mediaUri, userId, paging);
		assertThat(result.getContent()).isEmpty();
	}
	
	@Test
	public void listCommentsForNonExitingMedia() {
		String mediaUri = "uri1";
		String userId = "user1";
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> commentService.listComments(mediaUri, userId, new PagingParameter(Instant.now(), 0, 10))).isInstanceOf(NotFoundException.class);
		verifyZeroInteractions(commentRepository, userService);
	}
	
	@Test
	public void likeComment() {
		String userId = "user1";
		String commentId = "id";
		ApprovalModifier modifier = ApprovalModifier.LIKE;
		MediaCommentEntity commentEntity = mock(MediaCommentEntity.class);
		MediaCommentDetail commentDetail = new MediaCommentDetail();
		when(commentEntity.getId()).thenReturn(commentId);
		CommentApprovalEntity approvalEntity = new CommentApprovalEntity(commentEntity, userId);
		approvalEntity.setModifier(modifier);
		when(commentRepository.find(commentId)).thenReturn(Optional.of(commentEntity));
		when(approvalRepository.changeApproval(commentEntity, userId, modifier)).thenReturn(Optional.of(approvalEntity));
		when(commentEntity.toMediaCommentDetail()).thenReturn(commentDetail);
		when(userService.fillUserInfo(commentDetail)).thenReturn(commentDetail);
		
		MediaCommentDetail result = commentService.setCommentModifier(commentId, modifier, userId);
		assertThat(result).isEqualTo(commentDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(modifier);
		
		verify(commentLikedEvent).fire(commentEntity);
	}
	
	@Test
	public void resetCommentApproval() {
		String userId = "user1";
		String commentId = "id";
		ApprovalModifier modifier = null;
		MediaCommentEntity commentEntity = mock(MediaCommentEntity.class);
		when(commentEntity.getId()).thenReturn(commentId);
		MediaCommentDetail commentDetail = new MediaCommentDetail();
		CommentApprovalEntity approvalEntity = new CommentApprovalEntity(commentEntity, userId);
		approvalEntity.setModifier(modifier);
		when(commentRepository.find(commentId)).thenReturn(Optional.of(commentEntity));
		when(approvalRepository.find(commentId, userId)).thenReturn(Optional.of(approvalEntity));
		when(approvalRepository.changeApproval(commentEntity, userId, modifier)).thenReturn(Optional.empty());
		when(commentEntity.toMediaCommentDetail()).thenReturn(commentDetail);
		when(userService.fillUserInfo(commentDetail)).thenReturn(commentDetail);
		
		MediaCommentDetail result = commentService.setCommentModifier(commentId, modifier, userId);
		assertThat(result).isEqualTo(commentDetail);
		assertThat(result.getUserApprovalModifier()).isEqualTo(modifier);
	}
	
	@Test
	public void dislikeNonExistingComment() {
		String userId = "user1";
		String commentId = "id";
		ApprovalModifier modifier = ApprovalModifier.DISLIKE;
		when(commentRepository.find(commentId)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> commentService.setCommentModifier(commentId, modifier, userId)).isInstanceOf(NotFoundException.class);
		verifyNoMoreInteractions(approvalRepository, userService);
	}
}
