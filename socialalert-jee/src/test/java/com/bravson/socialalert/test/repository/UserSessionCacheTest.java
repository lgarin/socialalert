package com.bravson.socialalert.test.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.session.UserSessionCache;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class UserSessionCacheTest extends Assertions {

	@Inject
	UserSessionCache repository;
	
	@Test
	public void addNewUser() {
		repository.addActiveUser("test");
		assertThat(repository.isUserActive("test")).isTrue();
	}
	
	@Test
	public void unknownUserIsInactive() {
		assertThat(repository.isUserActive("abc")).isFalse();
	}
	
	@Test
	public void addNewViewedMedia() {
		boolean result = repository.addViewedMedia("xyz", "media1");
		assertThat(result).isTrue();
	}
	
	@Test
	public void addAlreadyViewedMedia() {
		repository.addViewedMedia("test", "media1");
		boolean result = repository.addViewedMedia("test", "media1");
		assertThat(result).isFalse();
	}
}
