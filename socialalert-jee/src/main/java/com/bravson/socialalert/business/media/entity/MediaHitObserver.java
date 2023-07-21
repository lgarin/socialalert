package com.bravson.socialalert.business.media.entity;

import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class MediaHitObserver {

	void handleNewMediaHit(@Observes @HitEntity MediaEntity media) {
		media.increaseHitCount();
	}
}
