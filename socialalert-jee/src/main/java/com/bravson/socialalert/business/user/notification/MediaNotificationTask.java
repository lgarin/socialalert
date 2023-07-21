package com.bravson.socialalert.business.user.notification;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.bravson.socialalert.business.media.MediaQueryService;
import com.bravson.socialalert.business.media.query.MediaQueryEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import lombok.NonNull;

@Service
public class MediaNotificationTask implements Runnable {

	ScheduledExecutorService scheduler;
	
	@ConfigProperty(name = "media.notificationRate", defaultValue = "PT1m")
	Duration notificationRate;

	@Inject
	@NonNull
	UserEventSink eventSink;
	
	@Inject
	@NonNull
	MediaQueryService queryService;
	
	void onStart(@Observes StartupEvent ev) {
		scheduler = Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(this, notificationRate.toMillis(), notificationRate.toMillis(), TimeUnit.MILLISECONDS);
	}

	void onStop(@Observes ShutdownEvent ev) {
		scheduler.shutdown();
	}
	
	@Override
	public void run() {
		for (String userId : eventSink.getRegisteredUserIdSet()) {
			queryService.queueQueryExecution(userId);
		}
	}
	
	void handleQueryHit(@Observes @HitEntity MediaQueryEntity entity) {
		eventSink.sendEvent("liveQuery", entity.toQueryInfo(), entity.getUserId());
	}
}
