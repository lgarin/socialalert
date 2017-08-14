package com.bravson.socialalert.test.repository;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.bravson.socialalert.user.AuthenticationConfiguration;
import com.bravson.socialalert.user.activity.OnlineUserRepository;

public class OnlineUserRepositoryTest extends Assertions {

	private static AuthenticationConfiguration authConfig = AuthenticationConfiguration.builder().sessionTimeout(1).build();
	
	private static OnlineUserRepository repository = new OnlineUserRepository(authConfig);
	
	@Test
	public void addNewUser() {
		repository.addActiveUser("test");
		assertThat(repository.isUserActive("test")).isTrue();
	}
	
	@Test
	public void unknownUserIsInactive() {
		assertThat(repository.isUserActive("xyz")).isFalse();
	}
	
	@Test
	public void expiredUserIsInactive() throws InterruptedException {
		repository.addActiveUser("expired");
		Thread.sleep(authConfig.getSessionTimeout() * 1000L + 10L);
		assertThat(repository.isUserActive("expired")).isFalse();
	}
}
