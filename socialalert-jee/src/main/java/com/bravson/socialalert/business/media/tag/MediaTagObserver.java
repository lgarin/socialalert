package com.bravson.socialalert.business.media.tag;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaTagObserver {

	@Inject
	MediaTagRepository repository;
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		for (String tag : media.getTags()) {
			repository.addTag(tag);
		}
	}
}
