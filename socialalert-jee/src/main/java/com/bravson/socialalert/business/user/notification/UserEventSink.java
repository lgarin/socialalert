package com.bravson.socialalert.business.user.notification;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bravson.socialalert.business.user.session.UserSession;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional(TxType.SUPPORTS)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserEventSink {

	private final Map<String, SseEventSink> sinkMap = new ConcurrentHashMap<>();
	
	private OutboundSseEvent.Builder sseEventBuilder;
	
	public void init(Sse sse) {
		if (sseEventBuilder == null) {
			sseEventBuilder = sse.newEventBuilder().mediaType(MediaType.valueOf(MediaTypeConstants.JSON));
		}
	}
	
	public void register(@NonNull String targetUserId, SseEventSink sink) {
		SseEventSink oldSink = sinkMap.put(targetUserId, sink);
		if (oldSink != null) {
			oldSink.close();
		}
	}
	
	private void unregister(@NonNull String userId) {
		SseEventSink oldSink = sinkMap.remove(userId);
		if (oldSink != null) {
			oldSink.close();
		}
	}
	
	void handleSessionTimeout(@Observes @DeleteEntity UserSession session) {
		unregister(session.getUserId());
	}
	
	public void sendEvent(String type, Object event, String userId) {
		SseEventSink sink = sinkMap.get(userId);
		if (sink != null) {
			String id = String.valueOf(Instant.now().toEpochMilli());
			OutboundSseEvent sseEvent = sseEventBuilder.id(id).name(type).data(event).build();
			sink.send(sseEvent);
		}
	}
	
	public Set<String> getRegisteredUserIdSet() {
		return Collections.unmodifiableSet(sinkMap.keySet());
	}
}
