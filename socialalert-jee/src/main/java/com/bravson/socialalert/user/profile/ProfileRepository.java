package com.bravson.socialalert.user.profile;

import java.util.Optional;

import javax.inject.Inject;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class ProfileRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<ProfileEntity> findByUserId(String userId) {
		return Optional.ofNullable(entityManager.find(ProfileEntity.class, userId));
	}

	public ProfileEntity createProfile(UserInfo userInfo, String ipAddress) {
		ProfileEntity entity = new ProfileEntity(userInfo.getUsername(), userInfo.getEmail(), UserAccess.of(userInfo.getId(), ipAddress));
		entityManager.persist(entity);
		return entity;
	}

}
