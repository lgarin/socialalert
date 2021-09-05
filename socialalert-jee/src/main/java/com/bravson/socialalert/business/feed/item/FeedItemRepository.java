package com.bravson.socialalert.business.feed.item;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.PeriodicFeedActivityCount;
import com.bravson.socialalert.domain.media.statistic.PeriodInterval;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

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
	
	private PredicateFinalStep buildSearchByUsersQuery(Collection<String> userIdList, Instant timestamp, String category, String keywords, SearchPredicateFactory context) {
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
				.where(p -> buildSearchByUsersQuery(userIdList, paging.getTimestamp(), category, keywords, p))
				.sort(s -> s.field("versionInfo.creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.hits(), result.total().hitCount(), paging);
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
	
	public List<PeriodicFeedActivityCount> groupUserActivitiesByPeriod(@NonNull String userId, @NonNull FeedActivity activity, @NonNull PeriodInterval interval) {
		return aggregateActivities(f -> buildUserActivitiesQuery(userId, activity, f), interval);
	}
	
	private PredicateFinalStep buildUserActivitiesQuery(@NonNull String userId, @NonNull FeedActivity activity, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.filter(context.simpleQueryString().field("versionInfo.userId").matching(userId));
		junction = junction.filter(context.simpleQueryString().field("activity").matching(activity.name()));
		return junction;
	}
	
	public List<PeriodicFeedActivityCount> groupMediaActivitiesByPeriod(@NonNull String mediaId, @NonNull FeedActivity activity, @NonNull PeriodInterval interval) {
		return aggregateActivities(f -> buildMediaActivitiesQuery(mediaId, activity, f), interval);
	}
	
	private PredicateFinalStep buildMediaActivitiesQuery(@NonNull String mediaId, @NonNull FeedActivity activity, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.filter(context.simpleQueryString().field("media.id").matching(mediaId));
		junction = junction.filter(context.simpleQueryString().field("activity").matching(activity.name()));
		return junction;
	}
	
	private List<PeriodicFeedActivityCount> aggregateActivities(Function<? super SearchPredicateFactory, ? extends PredicateFinalStep> predicateContributor, PeriodInterval interval) {
		AggregationKey<JsonObject> aggKey = AggregationKey.of("aggKey");
		JsonObject aggregation = buildHistogramAggParam(interval);
		SearchResult<FeedItemEntity> result = persistenceManager.search(FeedItemEntity.class)
			.where(predicateContributor)
			.aggregation(aggKey, f -> f.extension(ElasticsearchExtension.get()).fromJson(aggregation))
			.fetch(0);

		JsonObject aggResult = result.aggregation(aggKey);
		JsonArray buckets = aggResult.get("buckets").getAsJsonArray();
		return buildPeriodList(buckets);
	}
	
	private static List<PeriodicFeedActivityCount> buildPeriodList(JsonArray buckets) {
		List<PeriodicFeedActivityCount> resultList = new ArrayList<>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildPeriodCount(item));
		}
		return resultList;
	}
	
	private static PeriodicFeedActivityCount buildPeriodCount(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		long key = bucket.getAsJsonPrimitive("key").getAsLong();
		long count = bucket.getAsJsonPrimitive("doc_count").getAsLong();
		return new PeriodicFeedActivityCount(Instant.ofEpochMilli(key), count);
	}
	
	private static JsonObject buildHistogramAggParam(PeriodInterval interval) {
		JsonObject histogram = new JsonObject();
		histogram.addProperty("field", "versionInfo.creation");
		histogram.addProperty("calendar_interval", interval.name().toLowerCase());
		histogram.addProperty("min_doc_count", 1);
		
		JsonObject aggs = new JsonObject();
		aggs.add("date_histogram", histogram);
		return aggs;
	}
}
