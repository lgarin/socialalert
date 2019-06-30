package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.feed.FeedItemEntity;
import com.bravson.socialalert.business.feed.FeedItemRepository;
import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

public class FeedServiceTest extends BaseServiceTest {

	@InjectMocks
	FeedService feedService;
	
	@Mock
	FeedItemRepository itemRepository;

	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	UserInfoService userService;
	
	@Test
	public void getFeedWithNoLinks() {
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		QueryResult<FeedItemEntity> items = new QueryResult<>(Collections.emptyList(), 0, paging);
		UserProfileEntity profile = new UserProfileEntity("test", "test@test.com", UserAccess.of("test", "1.2.3.4"));
		when(profileRepository.findByUserId("test")).thenReturn(Optional.of(profile));
		when(itemRepository.getActivitiesByUsers(Collections.emptyList(), paging)).thenReturn(items);
		when(userService.fillUserInfo(Collections.emptyList())).thenReturn(Collections.emptyList());
		
		QueryResult<FeedItemInfo> result = feedService.getFeed("test", paging);
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getPageCount()).isEqualTo(0);
	}
	
	@Test(expected = NotFoundException.class)
	public void getFeedForUnknownUser() {
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		when(profileRepository.findByUserId("test")).thenReturn(Optional.empty());
		
		feedService.getFeed("test", paging);
	}
}
