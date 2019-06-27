package com.bravson.socialalert.business.feed;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
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
public class FeedItemRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public FeedItemEntity insert(@NonNull FeedActivity activity, @NonNull MediaEntity media, @NonNull UserAccess userAccess) {
		if (!FeedActivity.MEDIA_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for media");
		}
		FeedItemEntity entity = new FeedItemEntity(media, null, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	public FeedItemEntity insert(@NonNull FeedActivity activity, @NonNull MediaCommentEntity comment, @NonNull UserAccess userAccess) {
		if (!FeedActivity.COMMENT_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for comment");
		}
		FeedItemEntity entity = new FeedItemEntity(comment.getMedia(), comment, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	@SuppressWarnings("unchecked")
	public QueryResult<FeedItemEntity> getActivitiesByUsers(Collection<String> userIdList, @NonNull PagingParameter paging) {
		QueryBuilder builder = persistenceManager.createQueryBuilder(FeedItemEntity.class);
	    BooleanJunction<?> junction = builder.bool();
		junction = junction.must(builder.range().onField("versionInfo.creation").below(paging.getTimestamp()).createQuery()).disableScoring();
		junction = junction.must(builder.keyword().onField("versionInfo.userId").matching(userIdList.stream().collect(Collectors.joining(" "))).createQuery()).disableScoring();
		FullTextQuery query = persistenceManager.createFullTextQuery(junction.createQuery(), FeedItemEntity.class);
		List<FeedItemEntity> list = query
				.setSort(builder.sort().byField("versionInfo.creation").desc().createSort())
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize())
				.setHint(QueryHints.READ_ONLY, true)
				.getResultList();
		return new QueryResult<>(list, query.getResultSize(), paging);
	}
}
