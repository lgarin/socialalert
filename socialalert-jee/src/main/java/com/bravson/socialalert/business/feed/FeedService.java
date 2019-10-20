package com.bravson.socialalert.business.feed;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
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
	UserProfileRepository profileRepository;
	
	@Inject
	UserInfoService userService;
	
	public QueryResult<FeedItemInfo> getFeed(@NonNull String userId, @NonNull PagingParameter paging) {
		UserProfileEntity profile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		List<String> userIdList = profile.getFollowedUsers().stream().map(link -> link.getId().getTargetUserId()).collect(Collectors.toList());
		QueryResult<FeedItemInfo> result = itemRepository.searchActivitiesByUsers(userIdList, paging).map(FeedItemEntity::toItemInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
}