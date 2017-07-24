package com.bravson.socialalert.user.profile;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class ProfileRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<ProfileEntity> findByUserId(String userId) {
		return Optional.ofNullable(entityManager.find(ProfileEntity.class, userId));
	}

	public ProfileEntity createProfile(UserInfo userInfo, String ipAddress) {
		ProfileEntity entity = new ProfileEntity(userInfo.getUsername(), userInfo.getEmail(), new VersionInfo(userInfo.getId(), ipAddress));
		entityManager.persist(entity);
		return entity;
	}

}
