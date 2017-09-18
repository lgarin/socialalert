package com.bravson.socialalert.user;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.profile.ProfileEntity;
import com.bravson.socialalert.user.profile.ProfileRepository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@Logged
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserService {
	
	@Inject
	@NonNull
	AuthenticationRepository authenticationRepository;
	
	@Inject
	@NonNull
	ProfileRepository profileRepository;

	private ProfileEntity createProfile(String accessToken, String ipAddress) {
		return authenticationRepository.findUserInfo(accessToken)
				.map(userInfo -> profileRepository.createProfile(userInfo, ipAddress))
				.orElseThrow(NotFoundException::new);
	}
	
	private ProfileEntity getOrCreateProfile(String accessToken, String userId, String ipAddress) {
		return profileRepository.findByUserId(userId).orElseGet(() -> createProfile(accessToken, ipAddress));
	}
	
	private LoginResponse toLoginResponse(String accessToken, String ipAddress) {
		ProfileEntity profile = getOrCreateProfile(accessToken, authenticationRepository.extractUserId(accessToken).get(), ipAddress);
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
		return authenticationRepository.requestAccessToken(param.getUsername(), param.getPassword())
				.map(token -> toLoginResponse(token, ipAddress));
	}
	
	public boolean logout(@NonNull String authorization) {
		return authenticationRepository.invalidateAccessToken(authorization);
	}

	public Optional<UserInfo> findUserInfo(@NonNull String authorization) {
		return authenticationRepository.findUserInfo(authorization);
	}
}
