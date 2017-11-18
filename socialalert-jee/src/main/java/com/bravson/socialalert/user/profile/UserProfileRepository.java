package com.bravson.socialalert.user.profile;

import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.comment.MediaCommentEntity;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
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

	void handleNewFile(@Observes FileEntity file) {
		findByUserId(file.getUserId()).ifPresent(profile -> profile.addFile(file));
	}
	
	void handleNewMedia(@Observes MediaEntity media) {
		findByUserId(media.getUserId()).ifPresent(profile -> profile.addMedia(media));
	}
	
	void handleNewComment(@Observes MediaCommentEntity comment) {
		findByUserId(comment.getUserId()).ifPresent(profile -> profile.addComment(comment));
	}
}
