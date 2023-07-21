package com.bravson.socialalert.business.media;

import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional
public class MediaDeleteService {

	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	@DeleteEntity
	Event<MediaEntity> deleteMediaEvent;
	
	//@VisibleForTesting
	public void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		 mediaRepository.listByUserId(user.getId()).forEach(this::delete);
	}
	
	public void delete(@NonNull String mediaUri) {
		// TODO add check for owner
		mediaRepository.findMedia(mediaUri).ifPresent(this::delete);
	}
	
	private void delete(MediaEntity entity) {
		deleteMediaEvent.fire(entity);
		mediaRepository.delete(entity);
	}
}
