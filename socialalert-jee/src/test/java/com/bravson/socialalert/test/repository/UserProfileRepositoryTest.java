package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.Test;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.UserInfo;

public class UserProfileRepositoryTest extends BaseRepositoryTest {
    
    private UserProfileRepository repository = new UserProfileRepository(getPersistenceManager());

    private UserInfo createTestUserInfo() {
		return UserInfo.builder()
				.id("test")
				.username("test")
				.email("test@test.com")
				.createdTimestamp(Instant.now())
				.online(false)
				.build();
	}
    
    @Test
    public void createNonExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	assertThat(entity).isNotNull();
    	assertThat(entity.getId()).isEqualTo("test");
    }
    
    @Test(expected=EntityExistsException.class)
    public void createExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	repository.createProfile(userInfo, "1.2.3.4");
    	repository.createProfile(userInfo, "1.2.3.4");
    }
    
    @Test
    public void findNonExistingProfile() {
    	Optional<UserProfileEntity> entity = repository.findByUserId("test");
    	assertThat(entity).isEmpty();
    }
    
    @Test
    public void findExistingProfile() {
    	UserInfo userInfo = createTestUserInfo();
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	Optional<UserProfileEntity> result = repository.findByUserId("test");
    	assertThat(result).isNotEmpty().hasValue(entity);
    }
}
