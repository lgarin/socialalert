package com.bravson.socialalert.media.approval;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class MediaApprovalRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<MediaApprovalEntity> changeApproval(@NonNull String mediaUri, @NonNull String userId, ApprovalModifier modifier) {
		find(mediaUri, userId).ifPresent(entityManager::remove);
		if (modifier == null) {
			return Optional.empty();
		}
		MediaApprovalEntity entity = new MediaApprovalEntity(mediaUri, userId);
		entity.setModifier(modifier);
		entityManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<MediaApprovalEntity> find(@NonNull String mediaUri, @NonNull String userId) {
		return Optional.ofNullable(entityManager.find(MediaApprovalEntity.class, new MediaApprovalKey(mediaUri, userId)));
	}
}
