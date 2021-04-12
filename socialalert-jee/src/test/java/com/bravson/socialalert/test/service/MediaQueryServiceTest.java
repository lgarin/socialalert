package com.bravson.socialalert.test.service;

import java.util.Optional;

import javax.enterprise.event.Event;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.query.ExecuteQueryEvent;
import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.business.media.query.MediaQueryRepository;
import com.bravson.socialalert.business.media.query.MediaQueryService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.media.MediaQueryInfo;
import com.bravson.socialalert.infrastructure.async.AsyncRepository;

import static org.mockito.Mockito.*;

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
	public void createQuery() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		GeoArea location = GeoArea.builder().latitude(7.6).longitude(46.2).radius(20.0).build();
		MediaQueryEntity entity = new MediaQueryEntity(userAccess.getUserId(), "Label", location, "keyword", "CATEGORY", 10);
		when(queryRepository.create(entity.getLabel(), location, entity.getKeywords(), entity.getCategory(), entity.getHitThreshold(), userAccess)).thenReturn(entity);
		MediaQueryInfo result = queryService.create(entity.getLabel(), location, entity.getKeywords(), entity.getCategory(), entity.getHitThreshold(), userAccess);
		assertThat(result).isEqualTo(entity.toQueryInfo());
	}
	
	@Test
	public void findQueryByUserId() {
		UserAccess userAccess = createUserAccess("test", "1.2.3.4");
		GeoArea location = GeoArea.builder().latitude(7.6).longitude(46.2).radius(20.0).build();
		MediaQueryEntity entity = new MediaQueryEntity(userAccess.getUserId(), "Label", location, "keyword", "CATEGORY", 10);
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
