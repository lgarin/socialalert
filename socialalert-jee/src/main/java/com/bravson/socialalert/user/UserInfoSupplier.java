package com.bravson.socialalert.user;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.media.UserContent;
import com.bravson.socialalert.user.activity.SessionRepository;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@ApplicationScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class UserInfoSupplier {

	@Inject
	@NonNull
	SessionRepository sessionRepository;
	
	@Inject
	@NonNull
	ProfileRepository profileRepository;
	
	public <T extends UserContent> T fillUserInfo(@NonNull T content) {
		Optional<ProfileEntity> profile = profileRepository.findByUserId(content.getCreatorId());
		profile.map(getUserInfoMapper(content)).ifPresent(content::setCreator);
		return content;
	}
	
	public <T extends Collection<? extends UserContent>> T fillUserInfo(T collection) {
		for (UserContent content : collection) {
			fillUserInfo(content);
		}
		return collection;
	}
	
	private Function<ProfileEntity, UserInfo> getUserInfoMapper(UserContent content) {
		if (sessionRepository.isUserActive(content.getCreatorId())) {
			return ProfileEntity::toOnlineUserInfo;
		} else {
			return ProfileEntity::toOfflineUserInfo;
		}
	}
}
