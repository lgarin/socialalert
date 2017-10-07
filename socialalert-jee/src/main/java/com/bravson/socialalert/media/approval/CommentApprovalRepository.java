package com.bravson.socialalert.media.approval;

import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentApprovalRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<CommentApprovalEntity> changeApproval(@NonNull String commentId, @NonNull String userId, ApprovalModifier modifier) {
		find(commentId, userId).ifPresent(entityManager::remove);
		if (modifier == null) {
			return Optional.empty();
		}
		CommentApprovalEntity entity = new CommentApprovalEntity(commentId, userId);
		entity.setModifier(modifier);
		entityManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<CommentApprovalEntity> find(@NonNull String commentId, @NonNull String userId) {
		return Optional.ofNullable(entityManager.find(CommentApprovalEntity.class, new CommentApprovalKey(commentId, userId)));
	}
}
