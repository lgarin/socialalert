package com.bravson.socialalert.business.media.approval;

import java.util.Optional;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaApprovalRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<MediaApprovalEntity> changeApproval(@NonNull MediaEntity media, @NonNull String userId, ApprovalModifier modifier) {
		find(media.getId(), userId).ifPresent(persistenceManager::remove);
		if (modifier == null) {
			return Optional.empty();
		}
		MediaApprovalEntity entity = new MediaApprovalEntity(media, userId);
		entity.setModifier(modifier);
		persistenceManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<MediaApprovalEntity> find(@NonNull String mediaUri, @NonNull String userId) {
		return persistenceManager.find(MediaApprovalEntity.class, new MediaApprovalKey(mediaUri, userId));
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 persistenceManager.createUpdate("delete from MediaApproval where id.userId = :userId")
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		 persistenceManager.createUpdate("delete from MediaApproval where id.mediaUri = :mediaId")
			.setParameter("mediaId", media.getId())
			.executeUpdate();
	}
}
