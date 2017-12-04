package com.bravson.socialalert.business.media.approval;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
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
	FullTextEntityManager entityManager;
	
	public Optional<MediaApprovalEntity> changeApproval(@NonNull MediaEntity media, @NonNull String userId, ApprovalModifier modifier) {
		find(media.getId(), userId).ifPresent(entityManager::remove);
		if (modifier == null) {
			return Optional.empty();
		}
		MediaApprovalEntity entity = new MediaApprovalEntity(media, userId);
		entity.setModifier(modifier);
		entityManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<MediaApprovalEntity> find(@NonNull String mediaUri, @NonNull String userId) {
		return Optional.ofNullable(entityManager.find(MediaApprovalEntity.class, new MediaApprovalKey(mediaUri, userId)));
	}
}