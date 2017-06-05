package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.junit.Test;

import com.bravson.socialalert.profile.ProfileEntity;
import com.bravson.socialalert.profile.ProfileRepository;

public class ProfileRepositoryTest extends BaseRepositoryTest {
    
    private ProfileRepository repository = new ProfileRepository(getEntityManager());

    @Test
    public void createNonExistingProfile() {
    	ProfileEntity entity = repository.createProfile("test", "test", Instant.now());
    	assertThat(entity).isNotNull();
    	assertThat(entity.getUserId()).isEqualTo("test");
    }
    
    @Test(expected=EntityExistsException.class)
    public void createExistingProfile() {
    	repository.createProfile("test", "test", Instant.now());
    	repository.createProfile("test", "test2", Instant.now());
    }
    
    @Test
    public void findNonExistingProfile() {
    	Optional<ProfileEntity> entity = repository.findByUserId("test");
    	assertThat(entity).isEmpty();
    }
    
    @Test
    public void findExistingProfile() {
    	ProfileEntity entity = repository.createProfile("test", "test", Instant.now());
    	Optional<ProfileEntity> result = repository.findByUserId("test");
    	assertThat(result).isNotEmpty().hasValue(entity);
    }
}
