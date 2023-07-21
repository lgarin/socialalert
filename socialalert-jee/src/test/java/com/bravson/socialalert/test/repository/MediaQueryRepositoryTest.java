package com.bravson.socialalert.test.repository;

import java.util.Optional;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.business.media.query.MediaQueryRepository;
import com.bravson.socialalert.domain.media.query.MediaQueryParameter;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaQueryRepositoryTest extends BaseRepositoryTest {
    
	@Inject
	MediaQueryRepository repository;
	
	@Test
	void insertQuery() {
		MediaQueryParameter param = MediaQueryParameter.builder()
				.label("Label").category("CATEGORY").keywords("keyword").hitThreshold(10)
				.latitude(7.6).longitude(46.2).radius(20.0)
				.build();
		MediaQueryEntity result = repository.create(param, createUserAccess("test", "1.2.3.4"));
		assertThat(result.getId()).isEqualTo("test");
		assertThat(result.getLabel()).isEqualTo(param.getLabel());
		assertThat(result.getLocation()).isEqualTo(param.getLocation());
		assertThat(result.getKeywords()).isEqualTo(param.getKeywords());
		assertThat(result.getCategory()).isEqualTo(param.getCategory());
	}
	
	@Test
	void findByUserIdWithoutQuery() {
		Optional<MediaQueryEntity> result = repository.findQueryByUserId("test");
		assertThat(result).isEmpty();
	}
	
	@Test
	void findByUserIdWithQuery() {
		MediaQueryParameter param = MediaQueryParameter.builder()
				.label("Label").hitThreshold(10)
				.latitude(7.6).longitude(46.2).radius(20.0)
				.build();
		MediaQueryEntity entity = repository.create(param, createUserAccess("test", "1.2.3.4"));
		Optional<MediaQueryEntity> result = repository.findQueryByUserId("test");
		assertThat(result).contains(entity);
	}

}
