package com.bravson.socialalert.business.media;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.spatial.DistanceUnit;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;
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
public class MediaRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<MediaEntity> findMedia(@NonNull String mediaUri) {
		return persistenceManager.find(MediaEntity.class, mediaUri);
	}

	public MediaEntity storeMedia(@NonNull FileEntity file, @NonNull UpsertMediaParameter parameter, @NonNull UserAccess userAccess) {
		MediaEntity entity = new MediaEntity(file, parameter, userAccess);
		persistenceManager.persist(entity);
		return entity;
	}
	
	public QueryResult<MediaEntity> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		JsonObject sortSource = new JsonObject();
		sortSource.addProperty("lang", "painless");
		sortSource.addProperty("source", "_score * doc['statistic.boostFactor'].value * doc['versionInfo.creation'].value.toEpochSecond()");
		JsonObject sortScript = new JsonObject();
		sortScript.addProperty("type", "number");
		sortScript.add("script", sortSource);
		sortScript.addProperty("order", "desc");
		JsonObject sortCriteria = new JsonObject();
		sortCriteria.add("_script", sortScript);
		SearchResult<MediaEntity> result = persistenceManager.search(MediaEntity.class)
				.extension( ElasticsearchExtension.get() )
				.where(f -> buildSearchQuery(parameter, paging.getTimestamp(), f))
				.sort(f -> f.fromJson(sortCriteria))
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.hits(), result.total().hitCount(), paging);
	}

	private PredicateFinalStep buildSearchQuery(SearchMediaParameter parameter, Instant timestamp, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.filter(context.range().field("versionInfo.creation").atMost(timestamp).toPredicate());
		if (parameter.getMaxAge() != null) {
			junction = junction.filter(context.range().field("versionInfo.creation").atLeast(timestamp.minus(parameter.getMaxAge())).toPredicate());
		}
		if (parameter.getCreator() != null) {
			junction = junction.filter(context.match().field("versionInfo.userId").matching(parameter.getCreator()).toPredicate());
		}
		if (parameter.getArea() != null) {
			List<String> geoHashList = GeoHashUtil.computeGeoHashList(parameter.getArea(), 8);
			int precision = geoHashList.stream().mapToInt(String::length).max().getAsInt();
			if (precision >= MediaEntity.MIN_GEOHASH_PRECISION && precision <= MediaEntity.MAX_GEOHASH_PRECISION) {
				junction.filter(context.simpleQueryString().field("geoHash" + precision).matching(String.join(" ", geoHashList)).toPredicate());
			}
		}
		if (parameter.getLocation() != null) {
			junction = junction.must(context.spatial().within().field("location.coordinates").circle(parameter.getLocation().getLatitude(), parameter.getLocation().getLongitude(), parameter.getLocation().getRadius(), DistanceUnit.KILOMETERS).boost(8.0f).toPredicate());
		}
		if (parameter.getCategory() != null) {
			junction = junction.filter(context.simpleQueryString().field("category").matching(parameter.getCategory()).toPredicate());
		}
		if (parameter.getKeywords() != null) {
			junction = junction.must(context.match().field("tags").boost(4.0f).field("title").boost(2.0f).field("location.locality").matching(parameter.getKeywords()).fuzzy().toPredicate());
		}
		if (parameter.getMediaKind() != null) {
			junction = junction.filter(context.match().field("kind").matching(parameter.getMediaKind()).toPredicate());
		}
		return junction;
	}

	void handleNewMediaHit(@Observes @HitEntity MediaEntity media) {
		media.increaseHitCount();
	}

	public List<GeoStatistic> groupByGeoHash(@NonNull SearchMediaParameter parameter) {
		int precision;
		if (parameter.getArea() != null) {
			precision = Math.min(GeoHashUtil.computeGeoHashPrecision(parameter.getArea(), 64), MediaEntity.MAX_GEOHASH_PRECISION);
		} else {
			precision = 2;
		}
		
		AggregationKey<JsonObject> feelingByGeoHashKey = AggregationKey.of("feeling");
		JsonObject aggregation = buildAggregationParameters(precision);
		
		SearchResult<MediaEntity> result = persistenceManager.search(MediaEntity.class)
			.where(f -> buildSearchQuery(parameter, Instant.now(), f))
			.aggregation(feelingByGeoHashKey, f -> f.extension(ElasticsearchExtension.get()).fromJson(aggregation))
			.fetch(0);

		JsonObject aggResult = result.aggregation(feelingByGeoHashKey);
		JsonArray buckets = aggResult.get("buckets").getAsJsonArray();
		List<GeoStatistic> resultList = new ArrayList<GeoStatistic>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildGeoStatistic(item));
		}
		return resultList;
	}

	private static JsonObject buildAggregationParameters(int precision) {
		JsonObject sumField = new JsonObject();
		sumField.addProperty("field", "feeling");
		JsonObject sumOperation = new JsonObject();
		sumOperation.add("sum", sumField);
		JsonObject termField = new JsonObject();
		termField.addProperty("field", "geoHash" + precision);
		
		JsonObject fieldFilter = new JsonObject();
		fieldFilter.addProperty("field", "feeling");
		JsonObject existsFilter = new JsonObject();
		existsFilter.add("exists", fieldFilter);
		JsonObject feelingFilter = new JsonObject();
		feelingFilter.add("filter", existsFilter);
		JsonObject feelingSum = new JsonObject();
		feelingSum.add("feelingSum", feelingFilter);
		feelingSum.add("aggs", sumOperation);
		
		JsonObject aggregation = new JsonObject();
		aggregation.add("terms", termField);
		aggregation.add("aggs", feelingSum);
		return aggregation;
	}

	private static GeoStatistic buildGeoStatistic(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		long feelingCount =  bucket.get("feelingSum").getAsJsonObject().get("doc_count").getAsLong();
		String geoHash = bucket.get("key").getAsString();
		long totalCount = bucket.get("doc_count").getAsLong();
		long feelingSum = bucket.get("aggs").getAsJsonObject().get("value").getAsLong();
		
		GeoBox box = GeoHashUtil.computeBoundingBox(geoHash);
		return GeoStatistic.builder().count(totalCount)
				.feelingCount(feelingCount)
				.feelingSum(feelingSum)
				.minLat(box.getMinLat()).maxLat(box.getMaxLat())
				.minLon(box.getMinLon()).maxLon(box.getMaxLon())
				.build();
	}
	
	public List<MediaEntity> listByUserId(@NonNull String userId) {
		return persistenceManager.createQuery("from Media where versionInfo.userId = :userId", MediaEntity.class)
					.setParameter("userId", userId)
					.getResultList();
	}
	
	public void delete(MediaEntity entity) {
		persistenceManager.remove(entity);
	}
}
