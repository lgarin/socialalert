package com.bravson.socialalert.business.user.statistic;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.histogram.PeriodInterval;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.domain.user.statistic.PeriodicLinkActivityCount;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class LinkStatisticRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public LinkStatisticEntity insert(@NonNull UserLinkEntity link, @NonNull LinkActivity activity, @NonNull UserAccess userAccess) {
		LinkStatisticEntity entity = new LinkStatisticEntity(link, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	public List<PeriodicLinkActivityCount> groupLinkActivitiesByPeriod(@NonNull String targetUserId, @NonNull LinkActivity activity, @NonNull PeriodInterval interval) {
		return aggregateActivities(f -> buildLinkActivitiesQuery(targetUserId, activity, f), interval);
	}
	
	private PredicateFinalStep buildLinkActivitiesQuery(@NonNull String targetUserId, @NonNull LinkActivity activity, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.filter(context.simpleQueryString().field("targetUserId").matching(targetUserId));
		junction = junction.filter(context.simpleQueryString().field("activity").matching(activity.name()));
		return junction;
	}
	
	private List<PeriodicLinkActivityCount> aggregateActivities(Function<? super SearchPredicateFactory, ? extends PredicateFinalStep> predicateContributor, PeriodInterval interval) {
		AggregationKey<JsonObject> aggKey = AggregationKey.of("aggKey");
		JsonObject aggregation = buildHistogramAggParam(interval);
		SearchResult<LinkStatisticEntity> result = persistenceManager.search(LinkStatisticEntity.class)
			.where(predicateContributor)
			.aggregation(aggKey, f -> f.extension(ElasticsearchExtension.get()).fromJson(aggregation))
			.fetch(0);

		JsonObject aggResult = result.aggregation(aggKey);
		JsonArray buckets = aggResult.get("buckets").getAsJsonArray();
		return buildPeriodList(buckets);
	}
	
	private static List<PeriodicLinkActivityCount> buildPeriodList(JsonArray buckets) {
		List<PeriodicLinkActivityCount> resultList = new ArrayList<>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildPeriodCount(item));
		}
		return resultList;
	}
	
	private static PeriodicLinkActivityCount buildPeriodCount(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		long key = bucket.getAsJsonPrimitive("key").getAsLong();
		long count = bucket.getAsJsonPrimitive("doc_count").getAsLong();
		return new PeriodicLinkActivityCount(Instant.ofEpochMilli(key), count);
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
