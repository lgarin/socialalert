package com.bravson.socialalert.test.service;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.user.UserLinkService;
import com.bravson.socialalert.business.user.activity.OnlineUserCache;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.LinkedUserInfo;

import static org.mockito.Mockito.when;

public class UserLinkServiceTest extends BaseServiceTest {

	@Mock
	UserLinkRepository linkRepository;
	
	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	OnlineUserCache onlineUserCache;
	
	@InjectMocks
	UserLinkService linkService;
	
	@Test
	public void listSourceProfilesForUserWithoutLink() {
		String userId = "abc";
		PagingParameter paging = PagingParameter.of(0L, 0, 20); 
		when(linkRepository.searchByTarget(userId, paging)).thenReturn(new QueryResult<>(Collections.emptyList(), 0, paging));
		
		QueryResult<LinkedUserInfo> result = linkService.listSourceProfiles(userId, paging);
		assertThat(result.getContent()).isEmpty();
	}
	
	@Test
	public void listSourceProfilesForOfflineUserWithLink() {
		PagingParameter paging = PagingParameter.of(0L, 0, 20);
		UserProfileEntity sourceUser = new UserProfileEntity(createUserAccess("xyz", "1.2.3.4"));
    	UserProfileEntity targetUser = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		UserLinkEntity linkEntity = new UserLinkEntity(sourceUser, targetUser);
		when(linkRepository.searchByTarget(targetUser.getId(), paging)).thenReturn(new QueryResult<>(Collections.singletonList(linkEntity), 0, paging));
		when(onlineUserCache.isUserActive(sourceUser.getId())).thenReturn(false);
		QueryResult<LinkedUserInfo> result = linkService.listSourceProfiles(targetUser.getId(), paging);
		assertThat(result.getContent()).containsExactly(new LinkedUserInfo(sourceUser.toOfflineUserInfo(), linkEntity.getCreation()));
	}
	
	@Test
	public void listSourceProfilesForOnlineUserWithLink() {
		PagingParameter paging = PagingParameter.of(0L, 0, 20);
		UserProfileEntity sourceUser = new UserProfileEntity(createUserAccess("xyz", "1.2.3.4"));
    	UserProfileEntity targetUser = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		UserLinkEntity linkEntity = new UserLinkEntity(sourceUser, targetUser);
		when(linkRepository.searchByTarget(targetUser.getId(), paging)).thenReturn(new QueryResult<>(Collections.singletonList(linkEntity), 0, paging));
		when(onlineUserCache.isUserActive(sourceUser.getId())).thenReturn(true);
		QueryResult<LinkedUserInfo> result = linkService.listSourceProfiles(targetUser.getId(), paging);
		assertThat(result.getContent()).containsExactly(new LinkedUserInfo(sourceUser.toOnlineUserInfo(), linkEntity.getCreation()));
	}
}
