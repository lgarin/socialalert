package com.bravson.socialalert.business.media.comment;

import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

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
	
	@SuppressWarnings("unchecked")
	public QueryResult<MediaCommentEntity> listByMediaUri(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		QueryBuilder builder = persistenceManager.createQueryBuilder(MediaCommentEntity.class);
		BooleanJunction<?> junction = builder.bool();
		junction = junction.must(builder.range().onField("versionInfo.creation").below(paging.getTimestamp()).createQuery()).disableScoring();
		junction = junction.must(builder.keyword().onField("media.id").matching(mediaUri).createQuery()).disableScoring();
		FullTextQuery query = persistenceManager.createFullTextQuery(junction.createQuery(), MediaCommentEntity.class)
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize());
		List<MediaCommentEntity> list = query
				.setSort(builder.sort().byField("versionInfo.creation").desc().createSort())
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize())
				.setHint(QueryHints.READ_ONLY, true)
				.getResultList();
		return new QueryResult<>(list, query.getResultSize(), paging);
	}
}
