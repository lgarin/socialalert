package com.bravson.socialalert.business.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.business.user.session.UserSessionCache;
import com.bravson.socialalert.business.user.statistic.LinkStatisticRepository;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.media.statistic.PeriodInterval;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.LinkedUserInfo;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.domain.user.statistic.PeriodicLinkActivityCount;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLinkService {

	@Inject
	@NonNull
	UserLinkRepository linkRepository;
	
	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
	@Inject
	@NonNull
	UserSessionCache userSessionCache;
	
	@Inject
	@NewEntity
	Event<UserLinkEntity> createdLinkEvent;
	
	@Inject
	@DeleteEntity
	Event<UserLinkEntity> removedLinkEvent;
	
	@Inject
	@NonNull
	LinkStatisticRepository statisticRepository;

	public Optional<Instant> findLinkCreationTimetamp(@NonNull UserAccess userAccess, @NonNull String userId) {
		return linkRepository.find(userAccess.getUserId(), userId).map(UserLinkEntity::getCreation);
	}
	
	public Optional<UserInfo> link(@NonNull UserAccess userAccess, @NonNull String userId) {
		UserProfileEntity targetUser = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		if (linkRepository.find(userAccess.getUserId(), userId).isEmpty()) {
			UserProfileEntity sourceUser = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
			UserLinkEntity link = linkRepository.link(sourceUser, targetUser);
			createdLinkEvent.fire(link);
			return Optional.of(getFollowedTargetUserInfo(link));
		}
		return Optional.empty();
	}

	public Optional<UserInfo> unlink(@NonNull UserAccess userAccess, @NonNull String userId) {
		UserProfileEntity targetUser = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		Optional<UserLinkEntity> link = linkRepository.unlink(userAccess.getUserId(), targetUser.getId());
		if (link.isPresent()) {
			removedLinkEvent.fire(link.get());
			return Optional.of(getTargetUserInfo(link.get()));
		}
		return Optional.empty();
	}

	public List<LinkedUserInfo> getTargetProfiles(@NonNull String userId) {
		UserProfileEntity sourceUser = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		List<UserLinkEntity> followedUserLinks = linkRepository.findBySource(sourceUser.getId());
		return followedUserLinks.stream().map(this::getFollowedTargetUserInfo).collect(Collectors.toList());
	}
	
	private UserInfo getTargetUserInfo(UserLinkEntity entity) {
		if (userSessionCache.isUserActive(entity.getId().getTargetUserId())) {
			return entity.getTargetUser().toOnlineUserInfo();
		} else {
			return entity.getTargetUser().toOfflineUserInfo();
		}
	}
	
	private LinkedUserInfo getFollowedTargetUserInfo(UserLinkEntity entity) {
		UserInfo userInfo = getTargetUserInfo(entity);
		return new LinkedUserInfo(userInfo, entity.getCreation());
	}
	
	public QueryResult<LinkedUserInfo> listSourceProfiles(@NonNull String userId, @NonNull PagingParameter paging) {
		return linkRepository.searchByTarget(userId, paging).map(this::getFollowedSourceUserInfo);	
	}
	
	private UserInfo getSourceUserInfo(UserLinkEntity entity) {
		if (userSessionCache.isUserActive(entity.getId().getSourceUserId())) {
			return entity.getSourceUser().toOnlineUserInfo();
		} else {
			return entity.getSourceUser().toOfflineUserInfo();
		}
	}
	
	private LinkedUserInfo getFollowedSourceUserInfo(UserLinkEntity entity) {
		UserInfo userInfo = getSourceUserInfo(entity);
		return new LinkedUserInfo(userInfo, entity.getCreation());
	}
	
	public List<PeriodicLinkActivityCount> groupLinkCountsByPeriod(@NonNull String targetUserId, @NonNull PeriodInterval interval) {
		List<PeriodicLinkActivityCount> creationList = statisticRepository.groupLinkActivitiesByPeriod(targetUserId, LinkActivity.CREATE, interval);
		List<PeriodicLinkActivityCount> deletionList = statisticRepository.groupLinkActivitiesByPeriod(targetUserId, LinkActivity.DELETE, interval);
		List<PeriodicLinkActivityCount> resultList = new ArrayList<>(creationList.size() + deletionList.size());
		ListIterator<PeriodicLinkActivityCount> creationIterator = creationList.listIterator();
		ListIterator<PeriodicLinkActivityCount> deletionIterator = deletionList.listIterator();
		long currentCount = 0;
		
		while (creationIterator.hasNext() && deletionIterator.hasNext()) {
			PeriodicLinkActivityCount creation = creationIterator.next();
			PeriodicLinkActivityCount deletion = deletionIterator.next();
			if (creation.getPeriod().isBefore(deletion.getPeriod())) {
				currentCount += creation.getCount();
				deletionIterator.previous();
				resultList.add(new PeriodicLinkActivityCount(creation.getPeriod(), currentCount));
			} else if (deletion.getPeriod().isBefore(creation.getPeriod())) {
				currentCount -= deletion.getCount();
				creationIterator.previous();
				resultList.add(new PeriodicLinkActivityCount(deletion.getPeriod(), currentCount));
			} else {
				currentCount += creation.getCount();
				currentCount -= deletion.getCount();
				resultList.add(new PeriodicLinkActivityCount(creation.getPeriod(), currentCount));
			}
		}
		
		while (creationIterator.hasNext()) {
			PeriodicLinkActivityCount creation = creationIterator.next();
			currentCount += creation.getCount();
			resultList.add(new PeriodicLinkActivityCount(creation.getPeriod(), currentCount));
		}
		
		while (deletionIterator.hasNext()) {
			PeriodicLinkActivityCount deletion = deletionIterator.next();
			currentCount -= deletion.getCount();
			resultList.add(new PeriodicLinkActivityCount(deletion.getPeriod(), currentCount));
		}
		
		return resultList;
	}
}
