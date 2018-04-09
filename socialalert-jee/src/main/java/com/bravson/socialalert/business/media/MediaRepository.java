package com.bravson.socialalert.business.media;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.facet.Facet;
import org.hibernate.search.query.facet.FacetSortOrder;
import org.hibernate.search.query.facet.FacetingRequest;
import org.hibernate.search.spatial.impl.SpatialHashQuery;

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoBox;
import com.bravson.socialalert.domain.location.GeoStatistic;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;

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
	
	@Inject
	@NewEntity
	Event<MediaEntity> newEntityEvent;
	
	public Optional<MediaEntity> findMedia(@NonNull String mediaUri) {
		return persistenceManager.find(MediaEntity.class, mediaUri);
	}

	public MediaEntity storeMedia(@NonNull FileEntity file, @NonNull UpsertMediaParameter parameter, @NonNull UserAccess userAccess) {
		MediaEntity entity = new MediaEntity(file, parameter, userAccess);
		persistenceManager.persist(entity);
		newEntityEvent.fire(entity);
		return entity;
	}
	
	public void updateMedia(@NonNull MediaEntity entity) {
		persistenceManager.merge(entity);
		newEntityEvent.fire(entity);
	}
	
	@SuppressWarnings("unchecked")
	public QueryResult<MediaEntity> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryBuilder builder = persistenceManager.createQueryBuilder(MediaEntity.class);
		FullTextQuery query = createSearchQuery(parameter, paging.getTimestamp(), builder);
		List<MediaEntity> list = query
				.setSort(builder.sort().byScore().createSort())
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize())
				.setHint(QueryHints.READ_ONLY, true)
				.getResultList();
		return new QueryResult<>(list, query.getResultSize(), paging);
	}

	private FullTextQuery createSearchQuery(SearchMediaParameter parameter, Instant pagingTimestamp, QueryBuilder builder) {
		BooleanJunction<?> junction = builder.bool();
		junction = junction.must(builder.range().onField("versionInfo.creation").below(pagingTimestamp).createQuery()).disableScoring();
		if (parameter.getMaxAge() != null) {
			junction = junction.must(builder.range().onField("versionInfo.creation").above(pagingTimestamp.minus(parameter.getMaxAge())).createQuery());
		}
		if (parameter.getArea() != null) {
			List<String> geoHashList = GeoHashUtil.computeGeoHashList(parameter.getArea());
			int precision = geoHashList.stream().mapToInt(String::length).max().getAsInt();
			if (precision >= MediaEntity.MIN_GEOHASH_PRECISION && precision <= MediaEntity.MAX_GEOHASH_PRECISION) {
				junction.filteredBy(new SpatialHashQuery(geoHashList, "geoHash" + precision));
			}
		}
		if (parameter.getCategory() != null) {
			junction = junction.must(builder.keyword().onField("categories").matching(parameter.getCategory()).createQuery());
		}
		if (parameter.getKeywords() != null) {
			junction = junction.must(builder.keyword().fuzzy().onField("tags").boostedTo(4.0f).andField("title").boostedTo(2.0f).andField("description").matching(parameter.getKeywords()).createQuery());
		}
		if (parameter.getMediaKind() != null) {
			junction = junction.must(builder.keyword().onField("kind").matching(parameter.getMediaKind()).createQuery());
		}
		return persistenceManager.createFullTextQuery(junction.createQuery(), MediaEntity.class);
	}

	@Transactional(value=TxType.REQUIRES_NEW)
	public void increaseHitCountAtomicaly(@NonNull String mediaUri) {
		// TODO improve performance + handle OptimisticLockException
		findMedia(mediaUri).ifPresent(MediaEntity::increaseHitCount);
	}

	public List<GeoStatistic> groupByGeoHash(@NonNull SearchMediaParameter parameter) {
		int precision = 2;
		if (parameter.getArea() != null) {
			precision = Math.min(GeoHashUtil.computeGeoHashPrecision(parameter.getArea(), 64), MediaEntity.MAX_GEOHASH_PRECISION);
		}
		QueryBuilder builder = persistenceManager.createQueryBuilder(MediaEntity.class);
		FullTextQuery query = createSearchQuery(parameter, Instant.now(), builder);
		
		FacetingRequest geoHashFacet = builder.facet()
			    .name("geoHashFacet")
			    .onField("geoHash" + precision)
			    .discrete()
			    .orderedBy(FacetSortOrder.COUNT_DESC)
			    .includeZeroCounts(false)
			    .maxFacetCount(-1)
			    .createFacetingRequest();
		
		return query.getFacetManager().enableFaceting(geoHashFacet).getFacets("geoHashFacet").stream()
				.map(MediaRepository::toGeoStatistic).filter(s -> s.intersect(parameter.getArea())).collect(Collectors.toList());
	}

	private static GeoStatistic toGeoStatistic(Facet geoHashFacet) {
		GeoBox box = GeoHashUtil.computeBoundingBox(geoHashFacet.getValue());
		return GeoStatistic.builder().count(geoHashFacet.getCount()).minLat(box.getMinLat()).maxLat(box.getMaxLat()).minLon(box.getMinLon()).maxLon(box.getMaxLon()).build();
	}
}
