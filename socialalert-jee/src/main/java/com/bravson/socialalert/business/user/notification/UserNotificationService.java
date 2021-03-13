package com.bravson.socialalert.business.user.notification;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.TokenAccess;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.activity.UserSession;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.domain.user.notification.UserNotification;
import com.bravson.socialalert.domain.user.notification.UserNotificationType;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserNotificationService {

	final Map<String, SseEventSink> sinkMap = new ConcurrentHashMap<>();
	
	@Inject
	@TokenAccess
	@NonNull
	Instance<UserAccess> userAccess;
	
	private OutboundSseEvent.Builder sseEventBuilder;
	
	public void init(Sse sse) {
		if (sseEventBuilder == null) {
			sseEventBuilder = sse.newEventBuilder().mediaType(MediaType.valueOf(MediaTypeConstants.JSON));
		}
	}
	
	private void onEvent(String targetUserId, UserNotificationType activity, String mediaUri) {
		String sourceUserId = userAccess.get().getUserId();
		if (!targetUserId.equals(sourceUserId)) {
			SseEventSink sink = sinkMap.get(targetUserId);
			if (sink != null) {
				UserNotification event = new UserNotification(targetUserId, activity, mediaUri, sourceUserId);
				sendEvent(event, sink);
			}
		}
	}

	private void sendEvent(UserNotification event, SseEventSink sink) {
		String id = String.valueOf(Instant.now().toEpochMilli());
		OutboundSseEvent sseEvent = sseEventBuilder.id(id).name(event.getType().name()).data(event).build();
		sink.send(sseEvent);
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		onEvent(comment.getMedia().getUserId(), UserNotificationType.NEW_COMMENT, comment.getMediaUri());
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.WATCH_MEDIA, media.getId());
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.LIKE_MEDIA, media.getId());
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.DISLIKE_MEDIA, media.getId());
	}
	
	void handleCommentLiked(@Observes @LikedEntity MediaCommentEntity comment) {
		onEvent(comment.getUserId(), UserNotificationType.LIKE_COMMENT, comment.getMediaUri());
	}
	
	void handleCommentDisliked(@Observes @DislikedEntity MediaCommentEntity comment) {
		onEvent(comment.getUserId(), UserNotificationType.DISLIKE_COMMENT, comment.getMediaUri());
	}
	
	void handleNewLink(@Observes @NewEntity UserLinkEntity link) {
		onEvent(link.getId().getTargetUserId(), UserNotificationType.JOINED_NETWORK, null);
	}
	
	void handleDeletedLink(@Observes @DeleteEntity UserLinkEntity link) {
		onEvent(link.getId().getTargetUserId(), UserNotificationType.LEFT_NETWORK, null);
	}
	
	public void register(@NonNull String targetUserId, SseEventSink sink) {
		SseEventSink oldSink = sinkMap.replace(targetUserId, sink);
		if (oldSink != null) {
			oldSink.close();
		} else {
			sinkMap.putIfAbsent(targetUserId, sink);
		}
	}
	
	void handleSessionTimeout(@Observes @DeleteEntity UserSession session) {
		SseEventSink oldSink = sinkMap.get(session.getUserId());
		if (oldSink != null) {
			oldSink.close();
		}
	}
}
