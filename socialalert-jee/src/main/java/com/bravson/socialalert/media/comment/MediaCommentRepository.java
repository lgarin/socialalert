package com.bravson.socialalert.media.comment;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
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
	
	private QueryBuilder createQueryBuilder() {
		return entityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaCommentEntity.class).get();
	}
	
	public MediaCommentEntity create(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		MediaCommentEntity entity = new MediaCommentEntity(mediaUri, comment, userAccess);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<MediaCommentEntity> find(@NonNull String commentId) {
		return Optional.ofNullable(entityManager.find(MediaCommentEntity.class, commentId));
	}
	
	@SuppressWarnings("unchecked")
	public QueryResult<MediaCommentEntity> listByMediaUri(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		QueryBuilder builder = createQueryBuilder();
		BooleanJunction<?> junction = builder.bool();
		junction = junction.must(builder.range().onField("versionInfo.creation").below(paging.getTimestamp()).createQuery()).disableScoring();
		junction = junction.must(builder.keyword().onField("mediaUri").matching(mediaUri).createQuery()).disableScoring();
		FullTextQuery query = entityManager.createFullTextQuery(junction.createQuery(), MediaCommentEntity.class)
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
