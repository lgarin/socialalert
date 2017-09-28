package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.media.comment.MediaCommentEntity;
import com.bravson.socialalert.media.comment.MediaCommentInfo;
import com.bravson.socialalert.media.comment.MediaCommentRepository;
import com.bravson.socialalert.media.comment.MediaCommentService;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;

public class MediaCommentServiceTest extends BaseServiceTest {

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
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		MediaEntity mediaEntity = mock(MediaEntity.class);
		QueryResult<MediaCommentEntity> entityResult = new QueryResult<>(Collections.emptyList(), 0, paging);
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.of(mediaEntity));
		when(commentRepository.listByMediaUri(mediaUri, paging)).thenReturn(entityResult);
		
		QueryResult<MediaCommentInfo> result = commentService.listComments(mediaUri, paging);
		assertThat(result.getContent()).isEmpty();
	}
	
	@Test
	public void listCommentsForNonExitingMedia() {
		String mediaUri = "uri1";
		
		when(mediaRepository.findMedia(mediaUri)).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> commentService.listComments(mediaUri, new PagingParameter(Instant.now(), 0, 10))).isInstanceOf(NotFoundException.class);
		verifyZeroInteractions(commentRepository, userService);
	}
}
