package com.bravson.socialalert.business.user;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.profile.UpdateProfileParameter;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfileService {

	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
	public UserInfo updateProfile(@NonNull UpdateProfileParameter param, @NonNull UserAccess userAccess) {
		UserProfileEntity entity = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
		entity.updateProfile(param, userAccess);
		return entity.toOnlineUserInfo();
	}
}
