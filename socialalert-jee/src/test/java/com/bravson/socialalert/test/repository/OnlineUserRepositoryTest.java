package com.bravson.socialalert.test.repository;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.activity.OnlineUserCache;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OnlineUserRepositoryTest extends Assertions {

	@Inject
	OnlineUserCache repository;
	
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
