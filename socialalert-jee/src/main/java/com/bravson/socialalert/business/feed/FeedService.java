package com.bravson.socialalert.business.feed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.feed.item.FeedItemEntity;
import com.bravson.socialalert.business.feed.item.FeedItemRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
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
	
	public QueryResult<FeedItemInfo> getFeed(@NonNull String userId, String category, String keywords, @NonNull PagingParameter paging) {
		List<String> userIdList = buildRelatedUserIdList(userId);
		
		QueryResult<FeedItemInfo> result = itemRepository.searchActivitiesByUsers(userIdList, category, keywords, paging).map(FeedItemEntity::toItemInfo);
		Collection<MediaInfo> mediaCollection = result.getContent().stream().map(FeedItemInfo::getMedia).filter(Objects::nonNull).collect(Collectors.toList());
		Collection<MediaCommentInfo> commentCollection = result.getContent().stream().map(FeedItemInfo::getComment).filter(Objects::nonNull).collect(Collectors.toList());

		userService.fillUserInfo(mediaCollection);
		userService.fillUserInfo(commentCollection);
		userService.fillUserInfo(result.getContent());
		return result;
	}

	private List<String> buildRelatedUserIdList(String userId) {
		UserProfileEntity profile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		List<String> userIdList = new ArrayList<>(profile.getFollowedUsers().size() + 1);
		profile.getFollowedUsers().stream().map(link -> link.getId().getTargetUserId()).forEach(userIdList::add);
		userIdList.add(userId);
		return userIdList;
	}
}
