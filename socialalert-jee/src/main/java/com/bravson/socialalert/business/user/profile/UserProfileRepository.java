package com.bravson.socialalert.business.user.profile;

import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfileRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<UserProfileEntity> findByUserId(@NonNull String userId) {
		return persistenceManager.find(UserProfileEntity.class, userId);
	}

	public UserProfileEntity createProfile(@NonNull AuthenticationInfo authInfo, @NonNull String ipAddress) {
		UserProfileEntity entity = new UserProfileEntity(authInfo.getUsername(), authInfo.getEmail(), UserAccess.of(authInfo.getId(), ipAddress));
		entity.setFirstname(authInfo.getFirstname());
		entity.setLastname(authInfo.getLastname());
		return persistenceManager.persist(entity);
	}
	
	public Optional<UserProfileEntity> deleteByUserId(@NonNull String userId) {
		Optional<UserProfileEntity> result = findByUserId(userId);
		result.ifPresent(profile -> persistenceManager.remove(profile));
		return result;
	}

	void handleNewFile(@Observes @NewEntity FileEntity file) {
		findByUserId(file.getUserId()).ifPresent(profile -> profile.addFile(file));
	}
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMedia(media));
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.removeMedia(media));
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		findByUserId(comment.getUserId()).ifPresent(profile -> profile.addComment(comment));
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMediaHit());
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMediaLike());
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMediaDislike());
	}
}
