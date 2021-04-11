package com.bravson.socialalert.business.user;

import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.user.activity.UserSessionCache;
import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.authentication.AuthenticationRepository;
import com.bravson.socialalert.business.user.authentication.LoginToken;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.user.CreateUserParameter;
import com.bravson.socialalert.domain.user.LoginResponse;
import com.bravson.socialalert.domain.user.LoginTokenResponse;
import com.bravson.socialalert.domain.user.UserCredential;
import com.bravson.socialalert.domain.user.UserDetail;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.util.JwtUtil;

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
	UserSessionCache userSessionCache;

	@Inject
	@DeleteEntity
	Event<UserProfileEntity> deleteUserEvent;

	private LoginResponse toLoginResponse(LoginToken loginToken, String ipAddress) {
		String accessToken = loginToken.getAccessToken();
		String userId = JwtUtil.extractUserId(accessToken).get();
		AuthenticationInfo authInfo = authenticationRepository.findAuthenticationInfo(accessToken).orElseThrow(NotFoundException::new);
		UserProfileEntity userProfile = profileRepository.findByUserId(userId).orElseGet(() -> profileRepository.createProfile(authInfo, ipAddress));
		userProfile.login(authInfo);
		return userProfile.toLoginResponse(loginToken);
	}
	
	@Transactional
	public Optional<LoginResponse> login(@NonNull UserCredential param, @NonNull String ipAddress) {
		return authenticationRepository.requestLoginToken(param.getUsername(), param.getPassword())
				.map(token -> toLoginResponse(token, ipAddress));
	}
	
	@Transactional
	public Optional<LoginTokenResponse> renewLogin(@NonNull String refreshToken, @NonNull String ipAddress) {
		return authenticationRepository.refreshLoginToken(refreshToken)
				.map(token -> toLoginTokenResponse(token));
	}
	
	private LoginTokenResponse toLoginTokenResponse(LoginToken loginToken) {
		String accessToken = loginToken.getAccessToken();
		String userId = JwtUtil.extractUserId(accessToken).get();
		UserProfileEntity userProfile = profileRepository.findByUserId(userId).orElseThrow(NotFoundException::new);
		userProfile.markActive();
		return LoginTokenResponse.builder()
				.accessToken(loginToken.getAccessToken())
				.refreshToken(loginToken.getRefreshToken())
				.expiration(loginToken.getExpiration())
				.build();
	}

	public boolean logout(@NonNull String authorization) {
		JwtUtil.extractUserId(authorization).ifPresent(userSessionCache::removeUser); 
		return authenticationRepository.invalidateAccessToken(authorization);
	}

	public Optional<UserInfo> findUserInfo(@NonNull String userId) {
		return profileRepository.findByUserId(userId).map(this::getUserInfo);
	}
	
	public Optional<UserDetail> findOwnUserInfo(@NonNull String userId) {
		return profileRepository.findByUserId(userId).map(UserProfileEntity::toOwnUserDetail);
	}
	
	private UserInfo getUserInfo(UserProfileEntity entity) {
		if (userSessionCache.isUserActive(entity.getId())) {
			return entity.toOnlineUserInfo();
		} else {
			return entity.toOfflineUserInfo();
		}
	}
	
	public boolean createUser(@NonNull CreateUserParameter param) {
		return authenticationRepository.createUser(param);
	}

	public void changePassword(@NonNull String userId, @NotNull String newPassword) {
		authenticationRepository.changePassword(userId, newPassword);		
	}
	
	@Transactional
	public void deleteUser(@NonNull String userId) {
		UserProfileEntity userProfile = profileRepository.deleteByUserId(userId).orElseThrow(NotFoundException::new);
		deleteUserEvent.fire(userProfile);
		userSessionCache.removeUser(userId);
		authenticationRepository.deleteUser(userId);
	}
}
