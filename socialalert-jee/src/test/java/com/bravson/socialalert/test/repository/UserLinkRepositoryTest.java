package com.bravson.socialalert.test.repository;

import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserLinkRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    private UserLinkRepository repository;

    @Test
    public void findByNonExistingSource() {
    	List<UserLinkEntity> result = repository.findBySource("abc");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void findByExistingSource() {
    	UserProfileEntity sourceUser = new UserProfileEntity("xyz", "xyz@test.com", UserAccess.of("xyz", "1.2.3.4"));
    	UserProfileEntity targetUser = new UserProfileEntity("test", "test@test.com", UserAccess.of("test", "1.2.3.4"));
    	UserLinkEntity entity = new UserLinkEntity(sourceUser, targetUser);
    	persistAndIndex(entity);
    	
    	List<UserLinkEntity> result = repository.findBySource(sourceUser.getId());
    	assertThat(result).containsExactly(entity);
    }

}
