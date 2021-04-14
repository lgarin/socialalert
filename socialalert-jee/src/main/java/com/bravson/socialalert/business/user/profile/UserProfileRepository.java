package com.bravson.socialalert.business.user.profile;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.authentication.AuthenticationInfo;
import com.bravson.socialalert.business.user.token.UserAccessToken;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserProfileRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<UserProfileEntity> findByUserId(@NonNull String userId) {
		return persistenceManager.find(UserProfileEntity.class, userId);
	}

	public UserProfileEntity createProfile(@NonNull AuthenticationInfo authInfo, @NonNull String ipAddress) {
		UserAccessToken userAccess = UserAccessToken.builder()
				.userId(authInfo.getId())
				.ipAddress(ipAddress)
				.username(authInfo.getUsername())
				.email(authInfo.getEmail())
				.build();
		
		UserProfileEntity entity = new UserProfileEntity(userAccess);
		entity.setFirstname(authInfo.getFirstname());
		entity.setLastname(authInfo.getLastname());
		return persistenceManager.persist(entity);
	}
	
	public Optional<UserProfileEntity> deleteByUserId(@NonNull String userId) {
		Optional<UserProfileEntity> result = findByUserId(userId);
		result.ifPresent(profile -> persistenceManager.remove(profile));
		return result;
	}

}
