package com.bravson.socialalert;

import javax.inject.Inject;

import org.junit.Test;

public class UserRepositoryTest extends BaseBeanTest {

	@Inject
	public UserRepository userRepository;
	
	@Test
	public void getExistingUser() {
		UserInfo user = userRepository.getUserInfo("4b09beae-9187-4566-b15a-b26f50dd840c");
		assertThat(user).isNotNull();
		assertThat(user.firstName).isEqualTo("Test");
		assertThat(user.lastName).isEqualTo("Hello");
		
	}
}
