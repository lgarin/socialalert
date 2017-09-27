package com.bravson.socialalert.media;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.BooleanJunction;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.hibernate.search.query.dsl.Unit;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.file.FileEntity;
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
public class MediaRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<MediaEntity> findMedia(@NonNull String mediaUri) {
		return Optional.ofNullable(entityManager.find(MediaEntity.class, mediaUri));
	}

	public MediaEntity storeMedia(@NonNull FileEntity file, @NonNull UpsertMediaParameter parameter, @NonNull UserAccess userAccess) {
		MediaEntity media = new MediaEntity(file.getId(), file.isVideo() ? MediaKind.VIDEO : MediaKind.PICTURE, parameter, userAccess);
		media.setFile(file);
		media.setUserProfile(file.getUserProfile());
		entityManager.persist(media);
		return media;
	}
	
	@SuppressWarnings("unchecked")
	public QueryResult<MediaEntity> searchMedia(@NonNull SearchMediaParameter parameter, @NonNull PagingParameter paging) {
		QueryBuilder builder = createQueryBuilder();
		FullTextQuery query = createSearchQuery(parameter, paging, builder);
		List<MediaEntity> list = query
				.setSort(builder.sort().byScore().createSort())
				.setFirstResult(paging.getPageNumber() * paging.getPageSize())
				.setMaxResults(paging.getPageSize())
				.getResultList();
		return new QueryResult<>(list, query.getResultSize(), paging);
	}

	private QueryBuilder createQueryBuilder() {
		return entityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaEntity.class).get();
	}

	private FullTextQuery createSearchQuery(SearchMediaParameter parameter, PagingParameter paging, QueryBuilder builder) {
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
		return entityManager.createFullTextQuery(junction.createQuery(), MediaEntity.class);
	}

	@Transactional(value=TxType.REQUIRES_NEW)
	public void increaseHitCountAtomicaly(@NonNull String mediaUri) {
		// TODO improve performance + handle OptimisticLockException
		findMedia(mediaUri).ifPresent(MediaEntity::increaseHitCount);
	}
}
