package com.bravson.socialalert.test.service;

import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Collections;

import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import com.bravson.socialalert.business.feed.FeedItemEntity;
import com.bravson.socialalert.business.feed.FeedItemRepository;
import com.bravson.socialalert.business.feed.FeedService;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

public class FeedServiceTest extends BaseServiceTest {

	@InjectMocks
	FeedService feedService;
	
	@Mock
	FeedItemRepository itemRepository;

	@Mock
	UserLinkRepository linkRepository;
	
	@Mock
	UserInfoService userService;
	
	@Test
	public void getFeedWithNoLinks() {
		PagingParameter paging = new PagingParameter(Instant.now(), 0, 10);
		QueryResult<FeedItemEntity> items = new QueryResult<>(Collections.emptyList(), 0, paging);
		when(linkRepository.findBySource("test")).thenReturn(Collections.emptyList());
		when(itemRepository.getActivitiesByUsers(Collections.emptyList(), paging)).thenReturn(items);
		when(userService.fillUserInfo(Collections.emptyList())).thenReturn(Collections.emptyList());
		
		QueryResult<FeedItemInfo> result = feedService.getFeed("test", paging);
		assertThat(result.getContent()).isEmpty();
		assertThat(result.getPageCount()).isEqualTo(0);
	}
	
}
