package com.bravson.socialalert.business.media;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.google.common.annotations.VisibleForTesting;

import lombok.NonNull;

@Service
@Transactional
public class MediaDeleteService {

	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	@DeleteEntity
	Event<MediaEntity> deleteMediaEvent;
	
	@VisibleForTesting
	public void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 mediaRepository.listByUserId(user.getId()).forEach(this::delete);
	}
	
	public void delete(@NonNull String mediaUri) {
		mediaRepository.findMedia(mediaUri).ifPresent(this::delete);
	}
	
	private void delete(MediaEntity entity) {
		deleteMediaEvent.fire(entity);
		mediaRepository.delete(entity);
	}
}
