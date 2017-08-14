package com.bravson.socialalert.user;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

import lombok.NonNull;

@ManagedBean
@Transactional
@Logged
public class UserService {
	
	@Inject
	AuthenticationRepository authenticationRepository;
	
	@Inject
	ProfileRepository profileRepository;

	private ProfileEntity getOrCreateProfile(String accessToken, String userId, String ipAddress) {
		Optional<ProfileEntity> profile = profileRepository.findByUserId(userId);
		if (profile.isPresent()) {
			return profile.get();
		}
		return authenticationRepository.findUserInfo(accessToken)
				.map(userInfo -> profileRepository.createProfile(userInfo, ipAddress))
				.orElseThrow(NotFoundException::new);
	}
	
	private LoginResponse toLoginResponse(String accessToken, String userId, String ipAddress) {
		ProfileEntity profile = getOrCreateProfile(accessToken, userId, ipAddress);
		return LoginResponse.builder()
				.accessToken(accessToken)
				.username(profile.getUsername())
				.email(profile.getEmail())
				.country(profile.getCountry())
				.language(profile.getLanguage())
				.imageUri(profile.getImageUri())
				.creation(profile.getCreation())
				.build();
	}
	
	public Optional<LoginResponse> login(@NonNull LoginParameter param, @NonNull String ipAddress) {
		return authenticationRepository.requestAccessToken(param.getUserId(), param.getPassword())
				.map(token -> toLoginResponse(token, param.getUserId(), ipAddress));
	}
	
	public boolean logout(@NonNull  String authorization) {
		return authenticationRepository.invalidateAccessToken(authorization);
	}

	public Optional<UserInfo> findUserInfo(@NonNull  String authorization) {
		return authenticationRepository.findUserInfo(authorization);
	}
}
