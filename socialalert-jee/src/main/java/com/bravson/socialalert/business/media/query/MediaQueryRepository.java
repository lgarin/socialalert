package com.bravson.socialalert.business.media.query;

import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.media.query.MediaQueryParameter;
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
public class MediaQueryRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public MediaQueryEntity create(@NonNull MediaQueryParameter parameter, @NonNull UserAccess userAccess) {
		MediaQueryEntity entity = new MediaQueryEntity(parameter, userAccess.getUserId());
		return persistenceManager.persist(entity);
	}
	
	public Optional<MediaQueryEntity> findQueryByUserId(@NonNull String userId) {
		// TODO temporary
		return persistenceManager.find(MediaQueryEntity.class, userId);
	}
}
