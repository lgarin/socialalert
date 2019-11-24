package com.bravson.socialalert.business.media;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.business.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.activity.OnlineUserRepository;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
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
	OnlineUserRepository onlineUserRepository;
	
	@Inject
	MediaApprovalRepository approvalRepository;
	
	@Inject
	@HitEntity
	Event<MediaEntity> mediaHitEvent;
	
	@Inject
	@LikedEntity
	Event<MediaEntity> mediaLikedEvent;
	
	@Inject
	@DislikedEntity
	Event<MediaEntity> mediaDislikedEvent;
	
	public MediaDetail viewMediaDetail(@NonNull String mediaUri, @NonNull String userId) {
		MediaEntity media = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		if (onlineUserRepository.addViewedMedia(userId, mediaUri)) {
			mediaHitEvent.fire(media);
		}
		
		MediaDetail detail = media.toMediaDetail();
		approvalRepository.find(mediaUri, userId)
			.map(MediaApprovalEntity::getModifier)
			.ifPresent(detail::setUserApprovalModifier);
		
		return userService.fillUserInfo(detail);
	}

	public MediaDetail setApprovalModifier(@NonNull String mediaUri, ApprovalModifier modifier, @NonNull String userId) {
		MediaEntity mediaEntity = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		
		ApprovalModifier oldModifier = approvalRepository.find(mediaUri, userId).map(MediaApprovalEntity::getModifier).orElse(null);
		ApprovalModifier newModifier = approvalRepository.changeApproval(mediaEntity, userId, modifier).map(MediaApprovalEntity::getModifier).orElse(null);

		if (oldModifier != newModifier) {
			mediaEntity.getStatistic().updateApprovalCount(oldModifier, newModifier);
			if (newModifier == ApprovalModifier.LIKE) {
				mediaLikedEvent.fire(mediaEntity);
			} else if (newModifier == ApprovalModifier.DISLIKE) {
				mediaDislikedEvent.fire(mediaEntity);
			}
		}
		
		MediaDetail detail = mediaEntity.toMediaDetail();
		detail.setUserApprovalModifier(newModifier);
		return userService.fillUserInfo(detail);
	}
}
