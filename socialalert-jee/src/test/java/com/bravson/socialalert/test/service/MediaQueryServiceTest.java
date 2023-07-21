package com.bravson.socialalert.test.service;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.media.MediaQueryService;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.business.media.query.ExecuteQueryEvent;
import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.business.media.query.MediaQueryRepository;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.media.query.MediaQueryInfo;
import com.bravson.socialalert.domain.media.query.MediaQueryParameter;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.enterprise.event.Event;

public class MediaQueryServiceTest extends BaseServiceTest {

	@InjectMocks
	MediaQueryService queryService;
	
	@Mock
	Event<MediaQueryEntity> queryHitEvent;
	
	@Mock
	AsyncRepository asyncRepository;
	
	@Mock
	MediaQueryRepository queryRepository;
	
	@Mock
	MediaRepository mediaRepository;
	
	@Test
	public void upsertQuery() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		MediaQueryParameter param = MediaQueryParameter.builder()
				.label("Label").hitThreshold(10)
				.latitude(7.6).longitude(46.2).radius(20.0)
				.build();
		MediaQueryEntity entity = new MediaQueryEntity(param, userAccess.getUserId());
		when(queryRepository.create(param, userAccess)).thenReturn(entity);
		MediaQueryInfo result = queryService.upsert(param, userAccess);
		assertThat(result).isEqualTo(entity.toQueryInfo());
	}
	
	@Test
	public void findQueryByUserId() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		MediaQueryParameter param = MediaQueryParameter.builder()
			.label("Label").hitThreshold(10)
			.latitude(7.6).longitude(46.2).radius(20.0)
			.build();
		MediaQueryEntity entity = new MediaQueryEntity(param, userAccess.getUserId());
		when(queryRepository.findQueryByUserId(userAccess.getUserId())).thenReturn(Optional.of(entity));
		Optional<MediaQueryInfo> result = queryService.findLastQueryByUserId(userAccess.getUserId());
		assertThat(result).contains(entity.toQueryInfo());
	}
	
	@Test
	public void queueQueryExecution() {
		queryService.queueQueryExecution("test");
		verify(asyncRepository).fireAsync(ExecuteQueryEvent.of("test"));
	}
}
