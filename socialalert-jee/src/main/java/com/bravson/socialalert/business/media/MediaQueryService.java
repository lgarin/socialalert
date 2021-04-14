package com.bravson.socialalert.business.media;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.business.media.query.ExecuteQueryEvent;
import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.business.media.query.MediaQueryRepository;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.media.query.MediaQueryInfo;
import com.bravson.socialalert.domain.media.query.MediaQueryParameter;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional
public class MediaQueryService {
	
	@Inject
	@HitEntity
	Event<MediaQueryEntity> queryHitEvent;

	@Inject
	@NonNull
	AsyncRepository asyncRepository;
	
	@Inject
	@NonNull
	MediaQueryRepository queryRepository;
	
	@Inject
	@NonNull
	MediaRepository mediaRepository;
	
	public MediaQueryInfo create(@NonNull MediaQueryParameter parameter, @NonNull UserAccess userAccess) {
		return queryRepository.create(parameter, userAccess).toQueryInfo();
	}
	
	public Optional<MediaQueryInfo> findLastQueryByUserId(@NonNull String userId) {
		return queryRepository.findQueryByUserId(userId).map(MediaQueryEntity::toQueryInfo);
	}
	
	public void queueQueryExecution(@NonNull String userId) {
		asyncRepository.fireAsync(ExecuteQueryEvent.of(userId));
	}
	
	void executeQuery(@Observes ExecuteQueryEvent event) {
		queryRepository.findQueryByUserId(event.getUserId()).ifPresent(this::executeQuery);
	}
	
	private void executeQuery(MediaQueryEntity entity) {
		Instant now = Instant.now();
		PagingParameter paging = new PagingParameter(now, 0, 1);
		Duration maxAge = computeMediaMaxAge(entity, now);
		QueryResult<MediaEntity> result = mediaRepository.searchMedia(entity.toSearchParameter(maxAge), paging);
		entity.updateLastHitCount(result.getPageCount());
		if (result.getPageCount() >= entity.getHitThreshold()) {
			queryHitEvent.fire(entity);
		}
	}

	private static Duration computeMediaMaxAge(MediaQueryEntity entity, Instant now) {
		Duration maxAge = Duration.between(entity.getLastExecution(), now);
		maxAge = maxAge.multipliedBy(3).dividedBy(2);
		return maxAge;
	}
}
