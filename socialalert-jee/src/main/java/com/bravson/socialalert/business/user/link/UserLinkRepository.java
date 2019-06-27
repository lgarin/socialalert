package com.bravson.socialalert.business.user.link;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
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
public class UserLinkRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<UserLinkEntity> find(@NonNull String sourceUserId, @NonNull String targetUserId) {
		return persistenceManager.find(UserLinkEntity.class, new UserLinkKey(sourceUserId, targetUserId));
	}
	
	public UserLinkEntity link(@NonNull UserProfileEntity sourceUser, @NonNull UserProfileEntity targetUser) {
		return persistenceManager.persist(new UserLinkEntity(sourceUser, targetUser));
	}
	
	public Optional<UserLinkEntity> unlink(@NonNull String sourceUserId, @NonNull String targetUserId) {
		return find(sourceUserId, targetUserId).map(persistenceManager::remove);
	}
	
	public List<UserLinkEntity> findBySource(@NonNull String sourceUserId) {
		return persistenceManager.createQuery("from UserLink ul where ul.sourceUser.id = :sourceUserId", UserLinkEntity.class)
				.setParameter("sourceUserId", sourceUserId)
				.getResultList();
	}
}
