package com.bravson.socialalert.business.media.entity;

import javax.enterprise.event.Observes;
import javax.transaction.Transactional;

import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

@Service
@Transactional
public class MediaHitObserver {

	void handleNewMediaHit(@Observes @HitEntity MediaEntity media) {
		media.increaseHitCount();
	}
}
