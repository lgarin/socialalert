package com.bravson.socialalert.media.comment;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserAccess;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class MediaCommentRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public MediaCommentEntity create(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		MediaCommentEntity entity = new MediaCommentEntity(mediaUri, comment, userAccess);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<MediaCommentEntity> find(@NonNull String commentId) {
		return Optional.ofNullable(entityManager.find(MediaCommentEntity.class, commentId));
	}
	
	@SuppressWarnings("unchecked")
	public List<MediaCommentEntity> listByMediaUri(@NonNull String mediaUri) {
		return entityManager.createQuery("from MediaComment c where c.mediaUri = :mediaUri")
				.setParameter("mediaUri", mediaUri)
				.getResultList();
	}
}
