package com.bravson.socialalert.profile;

import java.time.Instant;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.infrastructure.log.Logged;

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

	public ProfileEntity createProfile(String userId, String username, Instant createdTimestamp) {
		ProfileEntity entity = ProfileEntity.of(userId, username, createdTimestamp);
		entityManager.persist(entity);
		return entity;
	}

}
