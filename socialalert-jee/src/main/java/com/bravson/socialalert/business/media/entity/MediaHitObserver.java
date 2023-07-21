package com.bravson.socialalert.business.media.entity;

import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

@Service
@Transactional
public class MediaHitObserver {

	void handleNewMediaHit(@Observes @HitEntity MediaEntity media) {
		media.increaseHitCount();
	}
}
