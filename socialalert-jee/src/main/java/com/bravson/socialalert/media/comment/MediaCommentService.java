package com.bravson.socialalert.media.comment;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;

import lombok.NonNull;

@Service
@Transactional
public class MediaCommentService {

	@Inject
	MediaCommentRepository commentRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	public MediaCommentInfo createComment(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = commentRepository.create(mediaUri, comment, userAccess);
		return userService.fillUserInfo(entity.toMediaCommentInfo());
	}

	@Transactional(TxType.SUPPORTS)
	public QueryResult<MediaCommentInfo> listComments(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		QueryResult<MediaCommentInfo> result = commentRepository.listByMediaUri(mediaUri, paging).map(MediaCommentEntity::toMediaCommentInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
}
