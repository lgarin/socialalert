package com.bravson.socialalert.business.media.approval;

import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
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
		 persistenceManager.createQuery("delete from MediaApproval where id.userId = :userId", MediaApprovalEntity.class)
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
}
