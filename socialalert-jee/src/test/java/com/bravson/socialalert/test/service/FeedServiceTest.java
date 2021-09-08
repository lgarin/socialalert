package com.bravson.socialalert.test.service;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.feed.item.FeedItemEntity;
import com.bravson.socialalert.business.feed.item.FeedItemRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

import static org.mockito.Mockito.when;

public class FeedServiceTest extends BaseServiceTest {

	@InjectMocks
	FeedService feedService;
	
	@Mock
	FeedItemRepository itemRepository;

	@Mock
	UserProfileRepository profileRepository;
	
	@Mock
	UserInfoService userService;
	
	@Mock
	UserLinkRepository linkRepository;
	
	@Test
	public void getFeedWithNoLinks() {
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		QueryResult<FeedItemEntity> items = new QueryResult<>(Collections.emptyList(), 0, paging);
		UserProfileEntity profile = new UserProfileEntity(createUserAccess("test", "1.2.3.4"));
		when(profileRepository.findByUserId("test")).thenReturn(Optional.of(profile));
		when(itemRepository.searchActivitiesByUsers(Collections.singletonList("test"), null, null, paging)).thenReturn(items);
		when(linkRepository.findBySource(profile.getId())).thenReturn(Collections.emptyList());
		
		QueryResult<FeedItemInfo> result = feedService.getFeed("test", null, null, paging);
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getPageCount()).isZero();
	}
	
	@Test
	public void getFeedForUnknownUser() {
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		when(profileRepository.findByUserId("test")).thenReturn(Optional.empty());
		
		assertThatThrownBy(() -> feedService.getFeed("test", null, null, paging)).isInstanceOf(NotFoundException.class);
	}
}
