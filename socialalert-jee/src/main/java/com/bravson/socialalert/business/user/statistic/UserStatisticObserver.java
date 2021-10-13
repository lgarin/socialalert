package com.bravson.socialalert.business.user.statistic;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserStatisticObserver {

	@Inject
	UserProfileRepository profileRepository;
	
	void handleNewFile(@Observes @NewEntity FileEntity file) {
		profileRepository.findByUserId(file.getUserId()).ifPresent(UserProfileEntity::addFile);
	}
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		profileRepository.findByUserId(media.getUserId()).ifPresent(profile -> profile.addMedia(media));
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		profileRepository.findByUserId(media.getUserId()).ifPresent(profile -> profile.removeMedia(media));
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		profileRepository.findByUserId(comment.getUserId()).ifPresent(UserProfileEntity::addComment);
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		profileRepository.findByUserId(media.getUserId()).ifPresent(UserProfileEntity::addMediaHit);
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		profileRepository.findByUserId(media.getUserId()).ifPresent(UserProfileEntity::addMediaLike);
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		profileRepository.findByUserId(media.getUserId()).ifPresent(UserProfileEntity::addMediaDislike);
	}
	
	void handleNewLink(@Observes @NewEntity UserLinkEntity link) {
		link.getSourceUser().addFollower();
	}
	
	void handleDeleteLink(@Observes @DeleteEntity UserLinkEntity link) {
		link.getSourceUser().removeFollower();
	}
}
