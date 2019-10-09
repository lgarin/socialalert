package com.bravson.socialalert.business.media.comment;

import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
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
public class MediaCommentRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	@Inject
	@NewEntity
	Event<MediaCommentEntity> newEntityEvent;
	
	public MediaCommentEntity create(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		MediaEntity media = persistenceManager.find(MediaEntity.class, mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = new MediaCommentEntity(media, comment, userAccess);
		persistenceManager.persist(entity);
		media.getStatistic().increateCommentCount();
		newEntityEvent.fire(entity);
		return entity;
	}
	
	public Optional<MediaCommentEntity> find(@NonNull String commentId) {
		return persistenceManager.find(MediaCommentEntity.class, commentId);
	}
	
	public QueryResult<MediaCommentEntity> listByMediaUri(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		SearchResult<MediaCommentEntity> result = persistenceManager.search(MediaCommentEntity.class)
				.predicate(p -> p.bool()
						.must(p.range().onField("versionInfo.creation").below(paging.getTimestamp()))
						.must(p.match().onField("media.id").matching(mediaUri)))
				.sort(s -> s.byField("versionInfo.creation").desc())
				.fetch(paging.getPageSize(), paging.getOffset());
		return new QueryResult<>(result.getHits(), result.getTotalHitCount(), paging);
	}
}
