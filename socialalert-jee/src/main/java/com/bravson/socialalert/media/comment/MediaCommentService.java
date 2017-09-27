package com.bravson.socialalert.media.comment;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.media.MediaRepository;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;

import lombok.NonNull;

@ManagedBean
@Logged
public class MediaCommentService {

	@Inject
	MediaCommentRepository commentRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	@Transactional
	public MediaCommentInfo createComment(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = commentRepository.create(mediaUri, comment, userAccess);
		return userService.fillUserInfo(entity.toMediaCommentInfo());
	}
}
