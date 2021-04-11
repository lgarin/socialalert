package com.bravson.socialalert.business.user;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.activity.UserSessionCache;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.LinkedUserInfo;
import com.bravson.socialalert.domain.user.UserInfo;
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

	public Optional<Instant> findLinkCreationTimetamp(@NonNull UserAccess userAccess, @NonNull String userId) {
		return linkRepository.find(userAccess.getUserId(), userId).map(UserLinkEntity::getCreation);
	}
	
	public Optional<UserInfo> link(@NonNull UserAccess userAccess, @NonNull String userId) {
		UserProfileEntity targetUser = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		if (linkRepository.find(userAccess.getUserId(), userId).isEmpty()) {
			UserProfileEntity sourceUser = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
			UserLinkEntity link = linkRepository.link(sourceUser, targetUser);
			targetUser.addFollower();
			createdLinkEvent.fire(link);
			return Optional.of(getFollowedTargetUserInfo(link));
		}
		return Optional.empty();
	}

	public Optional<UserInfo> unlink(@NonNull UserAccess userAccess, @NonNull String userId) {
		UserProfileEntity targetUser = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		Optional<UserLinkEntity> link = linkRepository.unlink(userAccess.getUserId(), userId);
		if (link.isPresent()) {
			targetUser.removeFollower();
			removedLinkEvent.fire(link.get());
			return Optional.of(getTargetUserInfo(link.get()));
		}
		return Optional.empty();
	}

	public List<LinkedUserInfo> getTargetProfiles(@NonNull String userId) {
		UserProfileEntity profile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		return profile.getFollowedUsers().stream().map(this::getFollowedTargetUserInfo).collect(Collectors.toList());
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
}
