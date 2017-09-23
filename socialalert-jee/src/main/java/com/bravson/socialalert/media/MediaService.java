package com.bravson.socialalert.media;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.user.UserInfoService;
import com.bravson.socialalert.user.session.UserSessionService;

import lombok.NonNull;

@ManagedBean
@Logged
@Transactional
public class MediaService {

	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	@Inject
	UserSessionService sessionService;
	
	@Inject
	MediaApprovalRepository approvalRepository;
	
	public MediaDetail viewMediaDetail(@NonNull String mediaUri, @NonNull String userId) {
		
		if (sessionService.addViewedMedia(mediaUri)) {
			mediaRepository.increaseHitCountAtomicaly(mediaUri);
		}
		
		MediaDetail detail = mediaRepository.findMedia(mediaUri)
			.orElseThrow(NotFoundException::new)
			.toMediaDetail();
		
		approvalRepository.find(mediaUri, userId)
			.map(MediaApprovalEntity::getModifier)
			.ifPresent(detail::setUserApprovalModifier);
		
		return userService.fillUserInfo(detail);
	}

	public MediaDetail setApprovalModifier(@NonNull String mediaUri, ApprovalModifier modifier, @NonNull String userId) {
		MediaDetail detail = mediaRepository.findMedia(mediaUri)
			.orElseThrow(NotFoundException::new)
			.toMediaDetail();
		
		approvalRepository.changeApproval(mediaUri, userId, modifier)
			.map(MediaApprovalEntity::getModifier)
			.ifPresent(detail::setUserApprovalModifier);
	
		return userService.fillUserInfo(detail);
	}
}
