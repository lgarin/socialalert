package com.bravson.socialalert.business.media.approval;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
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
	
	@SuppressWarnings("unchecked")
	public List<CommentApprovalEntity> findAllByMediaUri(@NonNull String mediaUri, @NonNull String userId) {
		QueryBuilder builder = createQueryBuilder();
		BooleanJunction<?> criteria = builder.bool()
			.must(builder.keyword().onField("comment.media.id").matching(mediaUri).createQuery())
			.must(builder.keyword().onField("id.userId").matching(userId).createQuery());
		FullTextQuery query = persistenceManager.createFullTextQuery(criteria.createQuery(), CommentApprovalEntity.class);
		return query.setHint(QueryHints.READ_ONLY, true).getResultList();
	}
	
	private QueryBuilder createQueryBuilder() {
		return persistenceManager.createQueryBuilder(CommentApprovalEntity.class);
	}
}
