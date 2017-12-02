package com.bravson.socialalert.business.user;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.business.user.activity.OnlineUserRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.media.UserContent;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserInfoService {

	@Inject
	@NonNull
	OnlineUserRepository onlineUserRepository;
	
	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
	private Function<UserProfileEntity, UserInfo> getUserInfoMapper(String userId) {
		if (onlineUserRepository.isUserActive(userId)) {
			return UserProfileEntity::toOnlineUserInfo;
		} else {
			return UserProfileEntity::toOfflineUserInfo;
		}
	}
	
	public Optional<UserInfo> findUserInfo(@NonNull String userId) {
		return profileRepository.findByUserId(userId).map(getUserInfoMapper(userId));
	}
	
	public <T extends UserContent> T fillUserInfo(@NonNull T content) {
		findUserInfo(content.getCreatorId()).ifPresent(content::setCreator);
		return content;
	}
	
	public <T extends UserContent> Optional<T> fillUserInfo(@NonNull Optional<T> content) {
		content.ifPresent(this::fillUserInfo);
		return content;
	}
	
	public <T extends Collection<? extends UserContent>> T fillUserInfo(@NonNull T collection) {
		for (UserContent content : collection) {
			fillUserInfo(content);
		}
		return collection;
	}
}
