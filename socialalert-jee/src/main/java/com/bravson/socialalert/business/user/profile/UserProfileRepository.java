package com.bravson.socialalert.business.user.profile;

import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
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
	FullTextEntityManager entityManager;
	
	public Optional<UserProfileEntity> findByUserId(@NonNull String userId) {
		return Optional.ofNullable(entityManager.find(UserProfileEntity.class, userId));
	}

	public UserProfileEntity createProfile(@NonNull UserInfo userInfo, @NonNull String ipAddress) {
		UserProfileEntity entity = new UserProfileEntity(userInfo.getUsername(), userInfo.getEmail(), UserAccess.of(userInfo.getId(), ipAddress));
		entityManager.persist(entity);
		return entity;
	}

	void handleNewFile(@Observes @NewEntity FileEntity file) {
		findByUserId(file.getUserId()).ifPresent(profile -> profile.addFile(file));
	}
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMedia(media));
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		findByUserId(comment.getUserId()).ifPresent(profile -> profile.addComment(comment));
	}
}
