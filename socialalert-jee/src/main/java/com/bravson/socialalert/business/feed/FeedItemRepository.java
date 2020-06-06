package com.bravson.socialalert.business.feed;

import java.time.Instant;
import java.util.Collection;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
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
	
	private PredicateFinalStep createSearchQuery(Collection<String> userIdList, Instant timestamp, String category, String keywords, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.mustNot(context.simpleQueryString().field("activity").matching(FeedActivity.WATCH_MEDIA.name()));
		junction = junction.must(context.range().field("versionInfo.creation").atMost(timestamp));
		junction = junction.filter(context.simpleQueryString().field("versionInfo.userId").matching(String.join(" ", userIdList)));
		if (category != null) {
			junction = junction.filter(context.simpleQueryString().field("category").matching(category).toPredicate());
		}
		if (keywords != null) {
			junction = junction.must(context.match().field("tags").boost(4.0f).field("text").matching(keywords).fuzzy().toPredicate());
		}
		return junction;
	}
	
	public QueryResult<FeedItemEntity> searchActivitiesByUsers(@NonNull Collection<String> userIdList, String category, String keywords, @NonNull PagingParameter paging) {
		SearchResult<FeedItemEntity> result = persistenceManager.search(FeedItemEntity.class)
				.where(p -> createSearchQuery(userIdList, paging.getTimestamp(), category, keywords, p))
				.sort(s -> s.field("versionInfo.creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.getHits(), result.getTotalHitCount(), paging);
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 persistenceManager.createUpdate("delete from FeedItem where versionInfo.userId = :userId")
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		 persistenceManager.createUpdate("delete from FeedItem where media.id = :mediaId")
			.setParameter("mediaId", media.getId())
			.executeUpdate();
	}
}
