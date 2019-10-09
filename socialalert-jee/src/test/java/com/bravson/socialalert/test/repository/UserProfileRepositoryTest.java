package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;

public class UserProfileRepositoryTest extends BaseRepositoryTest {
    
    private UserProfileRepository repository = new UserProfileRepository(getPersistenceManager());

    private AuthenticationInfo createTestAuthInfo() {
		return AuthenticationInfo.builder()
				.id("test")
				.username("test")
				.email("test@test.com")
				.createdTimestamp(Instant.now())
				.build();
	}
    
    @Test
    public void createNonExistingProfile() {
    	AuthenticationInfo authInfo = createTestAuthInfo();
    	UserProfileEntity entity = repository.createProfile(authInfo, "1.2.3.4");
    	assertThat(entity).isNotNull();
    	assertThat(entity.getId()).isEqualTo("test");
    }
    
    @Test
    public void createExistingProfile() {
    	AuthenticationInfo authInfo = createTestAuthInfo();
    	repository.createProfile(authInfo, "1.2.3.4");
    	assertThatThrownBy(() -> repository.createProfile(authInfo, "1.2.3.4")).isInstanceOf(EntityExistsException.class);
    }
    
    @Test
    public void findNonExistingProfile() {
    	Optional<UserProfileEntity> entity = repository.findByUserId("test");
    	assertThat(entity).isEmpty();
    }
    
    @Test
    public void findExistingProfile() {
    	AuthenticationInfo authInfo = createTestAuthInfo();
    	UserProfileEntity entity = repository.createProfile(authInfo, "1.2.3.4");
    	Optional<UserProfileEntity> result = repository.findByUserId("test");
    	assertThat(result).isNotEmpty().hasValue(entity);
    }
}
