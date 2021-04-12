package com.bravson.socialalert.business.media.query;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoArea;
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
	
	public MediaQueryEntity create(@NonNull String label, @NonNull GeoArea location, String keywords, String category, int hitThreshold, @NonNull UserAccess userAccess) {
		MediaQueryEntity entity = new MediaQueryEntity(userAccess.getUserId(), label, location, keywords, category, hitThreshold);
		return persistenceManager.persist(entity);
	}
	
	public Optional<MediaQueryEntity> findQueryByUserId(@NonNull String userId) {
		// TODO temporary
		return persistenceManager.find(MediaQueryEntity.class, userId);
	}
}
