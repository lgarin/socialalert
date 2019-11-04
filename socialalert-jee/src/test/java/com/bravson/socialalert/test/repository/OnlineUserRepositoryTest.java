package com.bravson.socialalert.test.repository;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.activity.OnlineUserRepository;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class OnlineUserRepositoryTest extends Assertions {

	@Inject
	private OnlineUserRepository repository;
	
	@Test
	public void addNewUser() {
		repository.addActiveUser("test");
		assertThat(repository.isUserActive("test")).isTrue();
	}
	
	@Test
	public void unknownUserIsInactive() {
		assertThat(repository.isUserActive("xyz")).isFalse();
	}
}
