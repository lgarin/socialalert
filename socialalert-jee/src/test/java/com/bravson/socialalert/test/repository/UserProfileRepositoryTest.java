package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.Test;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.UserInfo;

public class UserProfileRepositoryTest extends BaseRepositoryTest {
    
    private UserProfileRepository repository = new UserProfileRepository(getEntityManager());

    @Test
    public void createNonExistingProfile() {
    	UserInfo userInfo = new UserInfo("test", "test", "test@test.com", Instant.now(), false);
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	assertThat(entity).isNotNull();
    	assertThat(entity.getId()).isEqualTo("test");
    }
    
    @Test(expected=EntityExistsException.class)
    public void createExistingProfile() {
    	UserInfo userInfo = new UserInfo("test", "test", "test@test.com", Instant.now(), false);
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
    	UserInfo userInfo = new UserInfo("test", "test", "test@test.com", Instant.now(), false);
    	UserProfileEntity entity = repository.createProfile(userInfo, "1.2.3.4");
    	Optional<UserProfileEntity> result = repository.findByUserId("test");
    	assertThat(result).isNotEmpty().hasValue(entity);
    }
}
