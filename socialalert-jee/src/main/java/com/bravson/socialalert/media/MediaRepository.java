package com.bravson.socialalert.media;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class MediaRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<MediaEntity> findMedia(@NonNull String mediaUri) {
		return Optional.ofNullable(entityManager.find(MediaEntity.class, mediaUri));
	}

	public MediaEntity storeMedia(@NonNull FileEntity file, @NonNull ClaimMediaParameter parameter, @NonNull  String userId, @NonNull  String ipAddress) {
		MediaEntity media = MediaEntity.of(file, parameter, VersionInfo.of(userId, ipAddress));
		entityManager.persist(media);
		return media;
	}
	
	@SuppressWarnings("unchecked")
	public QueryResult<MediaEntity> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryBuilder builder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaEntity.class).get();
		BooleanJunction<?> junction = builder.bool();
		junction = junction.must(builder.range().onField("versionInfo.creation").below(paging.getTimestamp()).createQuery()).disableScoring();
		if (parameter.getMaxAge() != null) {
			junction = junction.must(builder.range().onField("versionInfo.creation").above(paging.getTimestamp().minus(parameter.getMaxAge())).createQuery());
		}
		if (parameter.getArea() != null) {
			junction = junction.must(builder.spatial().onField("location.coordinates").within(parameter.getArea().getRadius(), Unit.KM).ofLatitude(parameter.getArea().getLatitude()).andLongitude(parameter.getArea().getLongitude()).createQuery());
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
		FullTextQuery query = entityManager.createFullTextQuery(junction.createQuery(), MediaEntity.class);
		List<MediaEntity> list = query
				.setSort(builder.sort().byScore().createSort())
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize())
				.getResultList();
		return new QueryResult<>(list, query.getResultSize(), paging);
	}
}
