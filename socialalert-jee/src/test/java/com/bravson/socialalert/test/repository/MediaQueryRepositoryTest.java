package com.bravson.socialalert.test.repository;

import java.util.Optional;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.business.media.query.MediaQueryRepository;
import com.bravson.socialalert.domain.location.GeoArea;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaQueryRepositoryTest extends BaseRepositoryTest {
    
	@Inject
	MediaQueryRepository repository;
	
	@Test
	void insertQuery() {
		GeoArea location = GeoArea.builder().latitude(7.6).longitude(46.2).radius(20.0).build();
		MediaQueryEntity result = repository.create("Label", location, "keyword", "CATEGORY", 10, createUserAccess("test", "1.2.3.4"));
		assertThat(result.getId()).isEqualTo("test");
		assertThat(result.getLabel()).isEqualTo("Label");
		assertThat(result.getLocation()).isEqualTo(location);
		assertThat(result.getKeywords()).isEqualTo("keyword");
		assertThat(result.getCategory()).isEqualTo("CATEGORY");
	}
	
	@Test
	void findByUserIdWithoutQuery() {
		Optional<MediaQueryEntity> result = repository.findQueryByUserId("test");
		assertThat(result).isEmpty();
	}
	
	@Test
	void findByUserIdWithQuery() {
		GeoArea location = GeoArea.builder().latitude(7.6).longitude(46.2).radius(20.0).build();
		MediaQueryEntity entity = repository.create("Label", location, null, null, 10, createUserAccess("test", "1.2.3.4"));
		Optional<MediaQueryEntity> result = repository.findQueryByUserId("test");
		assertThat(result).contains(entity);
	}

}
