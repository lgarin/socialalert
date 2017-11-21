package com.bravson.socialalert.business.media;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.business.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.session.UserSessionService;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
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
		MediaEntity mediaEntity = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		
		ApprovalModifier oldModifier = approvalRepository.find(mediaUri, userId).map(MediaApprovalEntity::getModifier).orElse(null);
		ApprovalModifier newModifier = approvalRepository.changeApproval(mediaEntity, userId, modifier).map(MediaApprovalEntity::getModifier).orElse(null);

		mediaEntity.getStatistic().updateApprovalCount(oldModifier, newModifier);
		
		MediaDetail detail = mediaEntity.toMediaDetail();
		detail.setUserApprovalModifier(newModifier);
		return userService.fillUserInfo(detail);
	}
}
