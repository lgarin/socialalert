package com.bravson.socialalert.business.media.comment;

import java.util.Optional;

import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaCommentRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public MediaCommentEntity create(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		MediaEntity media = persistenceManager.find(MediaEntity.class, mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = new MediaCommentEntity(media, comment, userAccess);
		persistenceManager.persist(entity);
		media.getStatistic().increateCommentCount();
		return entity;
	}
	
	public Optional<MediaCommentEntity> find(@NonNull String commentId) {
		return persistenceManager.find(MediaCommentEntity.class, commentId);
	}
	
	public QueryResult<MediaCommentEntity> searchByMediaUri(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		SearchResult<MediaCommentEntity> result = persistenceManager.search(MediaCommentEntity.class)
				.where(p -> p.bool()
						.must(p.range().field("versionInfo.creation").atMost(paging.getTimestamp()))
						.must(p.match().field("media.id").matching(mediaUri)))
				.sort(s -> s.field("versionInfo.creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.hits(), result.total().hitCount(), paging);
	}

	public QueryResult<MediaCommentEntity> searchByUserId(@NonNull String userId, @NonNull PagingParameter paging) {
		SearchResult<MediaCommentEntity> result = persistenceManager.search(MediaCommentEntity.class)
				.where(p -> p.bool()
						.must(p.range().field("versionInfo.creation").atMost(paging.getTimestamp()))
						.must(p.match().field("versionInfo.userId").matching(userId)))
				.sort(s -> s.field("versionInfo.creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.hits(), result.total().hitCount(), paging);
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 persistenceManager.createUpdate("delete from MediaComment where versionInfo.userId = :userId")
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		 persistenceManager.createUpdate("delete from MediaComment where media.id = :mediaId")
			.setParameter("mediaId", media.getId())
			.executeUpdate();
	}
}
