package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class UserLinkRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    UserLinkRepository repository;

    @Test
    public void findByNonExistingSource() {
    	List<UserLinkEntity> result = repository.findBySource("abc");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void findByExistingSource() {
    	UserProfileEntity sourceUser = new UserProfileEntity(createUserAccess("xyz", "1.2.3.4"));
    	UserProfileEntity targetUser = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
    	UserLinkEntity entity = new UserLinkEntity(sourceUser, targetUser);
    	persistAndIndex(entity);
    	
    	List<UserLinkEntity> result = repository.findBySource(sourceUser.getId());
    	assertThat(result).containsExactly(entity);
    }
    
    @Test
    public void searchByNonExistingTarget() {
    	QueryResult<UserLinkEntity> result = repository.searchByTarget("abc", new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).isEmpty();
    }
    
    @Test
    public void searchByExistingTarget() {
    	UserProfileEntity sourceUser = new UserProfileEntity(createUserAccess("xyz", "1.2.3.4"));
    	UserProfileEntity targetUser = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
    	UserLinkEntity entity = new UserLinkEntity(sourceUser, targetUser);
    	persistAndIndex(entity);
    	
    	QueryResult<UserLinkEntity> result = repository.searchByTarget(targetUser.getId(), new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).containsExactly(entity);
    }
}
