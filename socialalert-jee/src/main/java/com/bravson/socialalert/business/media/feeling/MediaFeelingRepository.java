package com.bravson.socialalert.business.media.feeling;

import java.util.Optional;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
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
public class MediaFeelingRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<MediaFeelingEntity> changeFeeling(@NonNull MediaEntity media, @NonNull String userId, Integer feeling) {
		find(media.getId(), userId).ifPresent(persistenceManager::remove);
		if (feeling == null) {
			return Optional.empty();
		}
		MediaFeelingEntity entity = new MediaFeelingEntity(media, userId);
		entity.setFeeling(feeling);
		persistenceManager.persist(entity);
		return Optional.of(entity);
	}
	
	public Optional<MediaFeelingEntity> find(@NonNull String mediaUri, @NonNull String userId) {
		return persistenceManager.find(MediaFeelingEntity.class, new MediaFeelingKey(mediaUri, userId));
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 persistenceManager.createUpdate("delete from MediaFeeling where id.userId = :userId")
			.setParameter("userId", user.getId())
			.executeUpdate();
	}
	
	void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		 persistenceManager.createUpdate("delete from MediaFeeling where id.mediaUri = :mediaId")
			.setParameter("mediaId", media.getId())
			.executeUpdate();
	}
}
