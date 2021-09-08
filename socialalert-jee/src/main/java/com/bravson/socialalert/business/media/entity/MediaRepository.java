package com.bravson.socialalert.business.media.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.UpsertMediaParameter;
import com.bravson.socialalert.domain.media.statistic.LocationMediaCount;
import com.bravson.socialalert.domain.media.statistic.MediaCount;
import com.bravson.socialalert.domain.media.statistic.PeriodInterval;
import com.bravson.socialalert.domain.media.statistic.PeriodicMediaCount;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
			junction = junction.must(context.spatial().within().field("location.position").boundingBox(parameter.getArea().getMaxLat(), parameter.getArea().getMinLon(), parameter.getArea().getMinLat(), parameter.getArea().getMaxLon()).toPredicate());
		}
		if (parameter.getLocation() != null) {
			junction = junction.must(context.spatial().within().field("location.position").circle(parameter.getLocation().getLatitude(), parameter.getLocation().getLongitude(), parameter.getLocation().getRadius(), DistanceUnit.KILOMETERS).boost(8.0f).toPredicate());
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
		if (parameter.getLocality() != null) {
			junction = junction.filter(context.match().field("location.locality").matching(parameter.getLocality()).toPredicate());
		}
		if (parameter.getCountry() != null) {
			junction = junction.filter(context.simpleQueryString().field("location.country").matching(parameter.getCountry()).toPredicate());
		}
		return junction;
	}

	void handleNewMediaHit(@Observes @HitEntity MediaEntity media) {
		media.increaseHitCount();
	}

	public List<GeoStatistic> groupByGeoHash(@NonNull SearchMediaParameter parameter) {
		int precision;
		if (parameter.getArea() != null) {
			precision = Math.min(GeoHashUtil.computeGeoHashPrecision(parameter.getArea().toBoundingBox(), 64), MediaEntity.MAX_GEOHASH_PRECISION);
		} else {
			precision = 2;
		}
		
		JsonObject aggregation = buildGeoHashAggParam(precision);
		JsonArray buckets = aggregateMedia(parameter, aggregation);
		List<GeoStatistic> resultList = new ArrayList<>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildGeoStatistic(item));
		}
		return resultList;
	}

	private JsonArray aggregateMedia(SearchMediaParameter parameter, JsonObject aggregation) {
		AggregationKey<JsonObject> aggKey = AggregationKey.of("aggKey");
		SearchResult<MediaEntity> result = persistenceManager.search(MediaEntity.class)
			.where(f -> buildSearchQuery(parameter, Instant.now(), f))
			.aggregation(aggKey, f -> f.extension(ElasticsearchExtension.get()).fromJson(aggregation))
			.fetch(0);

		JsonObject aggResult = result.aggregation(aggKey);
		return aggResult.get("buckets").getAsJsonArray();
	}

	private static JsonObject buildGeoHashAggParam(int precision) {
		JsonObject sumField = new JsonObject();
		sumField.addProperty("field", "feeling");
		JsonObject sumOperation = new JsonObject();
		sumOperation.add("sum", sumField);
		
		JsonObject fieldFilter = new JsonObject();
		fieldFilter.addProperty("field", "feeling");
		JsonObject existsFilter = new JsonObject();
		existsFilter.add("exists", fieldFilter);
		JsonObject feelingFilter = new JsonObject();
		feelingFilter.add("filter", existsFilter);
		JsonObject feelingSum = new JsonObject();
		feelingSum.add("feelingSum", feelingFilter);
		feelingSum.add("aggs", sumOperation);
		
		JsonObject geohashGrid = new JsonObject();
		geohashGrid.addProperty("field", "location.position");
		geohashGrid.addProperty("precision", precision);
		
		JsonObject aggregation = new JsonObject();
		aggregation.add("geohash_grid", geohashGrid);
		aggregation.add("aggs", feelingSum);
		return aggregation;
	}

	private static GeoStatistic buildGeoStatistic(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		long feelingCount =  bucket.getAsJsonObject("feelingSum").getAsJsonPrimitive("doc_count").getAsLong();
		String geoHash = bucket.getAsJsonPrimitive("key").getAsString();
		long totalCount = bucket.getAsJsonPrimitive("doc_count").getAsLong();
		long feelingSum = bucket.getAsJsonObject("aggs").getAsJsonPrimitive("value").getAsLong();
		
		GeoBox box = GeoBox.fromBoundingBox(GeoHashUtil.computeBoundingBox(geoHash));
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
	
	private List<MediaCount> groupByField(@NonNull SearchMediaParameter parameter, int maxCount, String field) {
		AggregationKey<Map<String, Long>> countsKey = AggregationKey.of("counts");
		SearchResult<MediaEntity> result = persistenceManager.search(MediaEntity.class)
			.where(f -> buildSearchQuery(parameter, Instant.now(), f))
			.aggregation(countsKey, f -> f.terms().field(field, String.class).maxTermCount(maxCount).orderByCountDescending())
			.fetch(0);
		Map<String, Long> countsByCreator = result.aggregation(countsKey);
		return countsByCreator.entrySet().stream()
				.map(e -> new MediaCount(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
	}
	
	public List<MediaCount> groupByCreator(@NonNull SearchMediaParameter parameter, int maxCount) {
		return groupByField(parameter, maxCount, "versionInfo.userId");
	}
	
	public List<LocationMediaCount> groupByLocation(@NonNull SearchMediaParameter parameter, int maxCount) {
		JsonArray buckets = aggregateMedia(parameter, buildCentroidAggParam(maxCount));
		List<LocationMediaCount> resultList = new ArrayList<>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildLocationCount(item));
		}
		return resultList;
	}
	
	private static JsonObject buildCentroidAggParam(int maxCount) {
		JsonObject terms = new JsonObject();
		terms.addProperty("field", "location.fullLocality");
		terms.addProperty("size", maxCount);
		
		JsonObject geoCentroid = new JsonObject();
		geoCentroid.addProperty("field", "location.position");
		
		JsonObject centroid = new JsonObject();
		centroid.add("geo_centroid", geoCentroid);
		
		JsonObject aggs = new JsonObject();
		aggs.add("centroid", centroid);
		
		JsonObject aggregation = new JsonObject();
		aggregation.add("terms", terms);
		aggregation.add("aggs", aggs);
		return aggregation;
	}
	
	private static LocationMediaCount buildLocationCount(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		String key = bucket.getAsJsonPrimitive("key").getAsString();
		long count = bucket.getAsJsonPrimitive("doc_count").getAsLong();
		JsonObject location = bucket.getAsJsonObject("centroid").getAsJsonObject("location");
		JsonPrimitive latitude = location.getAsJsonPrimitive("lat");
		JsonPrimitive longitude = location.getAsJsonPrimitive("lon");
		
		return new LocationMediaCount(key, latitude.getAsDouble(), longitude.getAsDouble(), count);
	}
	
	public List<PeriodicMediaCount> groupByPeriod(@NonNull SearchMediaParameter parameter, @NonNull PeriodInterval interval) {
		JsonArray buckets = aggregateMedia(parameter, buildHistogramAggParam(interval));
		List<PeriodicMediaCount> resultList = new ArrayList<>(buckets.size());
		for (JsonElement item : buckets) {
			resultList.add(buildPeriodCount(item));
		}
		return resultList;
	}
	
	private static PeriodicMediaCount buildPeriodCount(JsonElement item) {
		JsonObject bucket = item.getAsJsonObject();
		long key = bucket.getAsJsonPrimitive("key").getAsLong();
		long count = bucket.getAsJsonPrimitive("doc_count").getAsLong();
		return new PeriodicMediaCount(Instant.ofEpochMilli(key), count);
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
