package com.bravson.socialalert.test.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.business.user.activity.OnlineUserRepository;

public class OnlineUserRepositoryTest extends Assertions {

	private OnlineUserRepository repository = new OnlineUserRepository();
	
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
