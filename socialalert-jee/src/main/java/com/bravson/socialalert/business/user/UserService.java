package com.bravson.socialalert.business.user;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.activity.OnlineUserRepository;
import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.authentication.AuthenticationRepository;
import com.bravson.socialalert.business.user.authentication.LoginToken;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.LoginTokenResponse;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserService {
	
	@Inject
	@NonNull
	AuthenticationRepository authenticationRepository;
	
	@Inject
	@NonNull
	UserProfileRepository profileRepository;
	
	@Inject
	@NonNull
	OnlineUserRepository onlineUserRepository;


	private LoginResponse toLoginResponse(LoginToken loginToken, String ipAddress) {
		String accessToken = loginToken.getAccessToken();
		String userId = authenticationRepository.extractUserId(accessToken).get();
		AuthenticationInfo authInfo = authenticationRepository.findAuthenticationInfo(accessToken).orElseThrow(NotFoundException::new);
		UserProfileEntity userProfile = profileRepository.findByUserId(userId).orElseGet(() -> profileRepository.createProfile(authInfo, ipAddress));
		userProfile.login(authInfo);
		return userProfile.toLoginResponse(loginToken);
	}
	
	public Optional<LoginResponse> login(@NonNull LoginParameter param, @NonNull String ipAddress) {
		return authenticationRepository.requestLoginToken(param.getUsername(), param.getPassword())
				.map(token -> toLoginResponse(token, ipAddress));
	}
	
	public Optional<LoginTokenResponse> renewLogin(@NonNull String refreshToken, @NonNull String ipAddress) {
		return authenticationRepository.refreshLoginToken(refreshToken)
				.map(token -> toLoginTokenResponse(token));
	}
	
	private LoginTokenResponse toLoginTokenResponse(LoginToken loginToken) {
		String accessToken = loginToken.getAccessToken();
		String userId = authenticationRepository.extractUserId(accessToken).get();
		UserProfileEntity userProfile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		userProfile.markActive();
		return LoginTokenResponse.builder()
				.accessToken(loginToken.getAccessToken())
				.refreshToken(loginToken.getRefreshToken())
				.expiration(loginToken.getExpiration())
				.build();
	}

	public boolean logout(@NonNull String authorization) {
		return authenticationRepository.invalidateAccessToken(authorization);
	}

	public Optional<UserInfo> findUserInfo(@NonNull String userId) {
		return profileRepository.findByUserId(userId).map(this::getUserInfo);
	}
	
	private UserInfo getUserInfo(UserProfileEntity entity) {
		if (onlineUserRepository.isUserActive(entity.getId())) {
			return entity.toOnlineUserInfo();
		} else {
			return entity.toOfflineUserInfo();
		}
	}
}
