package com.bravson.socialalert.business.media.approval;

import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
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
public class CommentApprovalRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<CommentApprovalEntity> changeApproval(@NonNull MediaCommentEntity comment, @NonNull String userId, ApprovalModifier modifier) {
		find(comment.getId(), userId).ifPresent(persistenceManager::remove);
		if (modifier == null) {
			return Optional.empty();
		}
		CommentApprovalEntity entity = new CommentApprovalEntity(comment, userId);
		entity.setModifier(modifier);
		persistenceManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<CommentApprovalEntity> find(@NonNull String commentId, @NonNull String userId) {
		return persistenceManager.find(CommentApprovalEntity.class, new CommentApprovalKey(commentId, userId));
	}
	
	public List<CommentApprovalEntity> findAllByMediaUri(@NonNull String mediaUri, @NonNull String userId) {
		return persistenceManager.createQuery("from CommentApproval where id.userId = :userId and comment.media.id = :mediaUri", CommentApprovalEntity.class)
				.setParameter("userId", userId)
				.setParameter("mediaUri", mediaUri)
				.getResultList();
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 persistenceManager.createQuery("delete from CommentApproval where id.userId = :userId", CommentApprovalEntity.class)
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
}
