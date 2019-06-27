package com.bravson.socialalert.business.feed;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional
public class FeedService {

	@Inject
	@NonNull
	FeedItemRepository itemRepository;
	
	@Inject
	@NonNull
	UserLinkRepository linkRepository;
	
	@Inject
	UserInfoService userService;
	
	public QueryResult<FeedItemInfo> getFeed(@NonNull String userId, @NonNull PagingParameter paging) {
		List<String> userIdList = linkRepository.findBySource(userId).stream().map(link -> link.getTargetUser().getId()).collect(Collectors.toList());
		QueryResult<FeedItemInfo> result = itemRepository.getActivitiesByUsers(userIdList, paging).map(FeedItemEntity::toItemInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
}
