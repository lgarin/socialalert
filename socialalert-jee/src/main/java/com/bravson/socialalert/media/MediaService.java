package com.bravson.socialalert.media;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.user.UserInfoService;
import com.bravson.socialalert.user.session.UserSessionService;

import lombok.NonNull;

@Service
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
		MediaEntity mediaEntity = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		
		ApprovalModifier oldModifier = approvalRepository.find(mediaUri, userId).map(MediaApprovalEntity::getModifier).orElse(null);
		ApprovalModifier newModifier = approvalRepository.changeApproval(mediaEntity, userId, modifier).map(MediaApprovalEntity::getModifier).orElse(null);

		mediaEntity.getStatistic().updateApprovalCount(oldModifier, newModifier);
		
		MediaDetail detail = mediaEntity.toMediaDetail();
		detail.setUserApprovalModifier(newModifier);
		return userService.fillUserInfo(detail);
	}
}
