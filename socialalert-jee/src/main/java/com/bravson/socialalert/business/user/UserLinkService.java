package com.bravson.socialalert.business.user;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.activity.OnlineUserRepository;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.link.UserLinkRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
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
	OnlineUserRepository onlineUserRepository;

	public boolean link(UserAccess userAccess, String userId) {
		profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		if (!linkRepository.find(userAccess.getUserId(), userId).isPresent()) {
			linkRepository.link(userAccess.getUserId(), userId);
			return true;
		}
		return false;
	}

	public boolean unlink(UserAccess userAccess, String userId) {
		profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		if (linkRepository.find(userAccess.getUserId(), userId).isPresent()) {
			linkRepository.unlink(userAccess.getUserId(), userId);
			return true;
		}
		return false;
	}

	@Transactional
	public List<UserInfo> getTargetProfiles(String userId) {
		UserProfileEntity profile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		return profile.getFollowedUsers().stream().map(this::getTargetUserInfo).collect(Collectors.toList());
	}
	
	private UserInfo getTargetUserInfo(UserLinkEntity entity) {
		if (onlineUserRepository.isUserActive(entity.getId().getTargetUserId())) {
			return entity.getTargetUser().toOnlineUserInfo();
		} else {
			return entity.getTargetUser().toOfflineUserInfo();
		}
	}
}
