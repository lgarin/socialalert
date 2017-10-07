package com.bravson.socialalert.user;

import java.util.Collection;
import java.util.function.Function;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.media.UserContent;
import com.bravson.socialalert.user.activity.OnlineUserRepository;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

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
	ProfileRepository profileRepository;
	
	private Function<ProfileEntity, UserInfo> getUserInfoMapper(UserContent content) {
		if (onlineUserRepository.isUserActive(content.getCreatorId())) {
			return ProfileEntity::toOnlineUserInfo;
		} else {
			return ProfileEntity::toOfflineUserInfo;
		}
	}
	
	public <T extends UserContent> T fillUserInfo(@NonNull T content) {
		profileRepository.findByUserId(content.getCreatorId())
			.map(getUserInfoMapper(content))
			.ifPresent(content::setCreator);
		return content;
	}
	
	public <T extends Collection<? extends UserContent>> T fillUserInfo(T collection) {
		for (UserContent content : collection) {
			fillUserInfo(content);
		}
		return collection;
	}
}
