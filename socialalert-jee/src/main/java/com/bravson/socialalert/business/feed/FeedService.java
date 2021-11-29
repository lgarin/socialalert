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
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.domain.feed.PeriodicFeedActivityCount;
import com.bravson.socialalert.domain.histogram.HistogramParameter;
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
	@NonNull
	UserInfoService userService;
	
	@Inject
	@NonNull
	UserLinkRepository linkRepository;
	
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
		UserProfileEntity sourceProfile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		List<UserLinkEntity> followedUserLinks = linkRepository.findBySource(sourceProfile.getId());
		List<String> userIdList = new ArrayList<>(followedUserLinks.size() + 1);
		followedUserLinks.stream().map(link -> link.getId().getTargetUserId()).forEach(userIdList::add);
		userIdList.add(userId);
		return userIdList;
	}
	
	public List<PeriodicFeedActivityCount> groupUserActivitiesByPeriod(@NonNull String userId, @NonNull FeedActivity activity, @NonNull HistogramParameter histogram) {
		List<PeriodicFeedActivityCount> activityCountList = itemRepository.groupUserActivitiesByPeriod(userId, activity, histogram.getInterval());
		return histogram.filter(activityCountList);
	}
	
	public List<PeriodicFeedActivityCount> groupMediaActivitiesByPeriod(@NonNull String mediaUri, @NonNull FeedActivity activity, @NonNull HistogramParameter histogram) {
		List<PeriodicFeedActivityCount> activityCountList = itemRepository.groupMediaActivitiesByPeriod(mediaUri, activity, histogram.getInterval());
		return histogram.filter(activityCountList);
	}
	
}
