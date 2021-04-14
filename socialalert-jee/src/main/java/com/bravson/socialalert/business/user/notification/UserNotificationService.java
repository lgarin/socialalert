package com.bravson.socialalert.business.user.notification;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.domain.user.notification.UserNotification;
import com.bravson.socialalert.domain.user.notification.UserNotificationType;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserNotificationService {

	@Inject
	@NonNull
	Instance<UserAccess> userAccess;
	
	@Inject
	@NonNull
	UserEventSink eventSink;
	
	private void onEvent(String targetUserId, UserNotificationType type, MediaEntity media, MediaCommentEntity comment) {
		String sourceUserId = userAccess.get().getUserId();
		if (!targetUserId.equals(sourceUserId)) {
			UserNotification event = buildUserNotification(targetUserId, type, media, comment);
			eventSink.sendEvent("userNotification", event, targetUserId);
		}
	}

	private UserNotification buildUserNotification(String targetUserId, UserNotificationType type, MediaEntity media, MediaCommentEntity comment) {
		if (media == null && comment != null) {
			media = comment.getMedia();
		}
		return UserNotification.builder()
				.sourceUserId(userAccess.get().getUserId())
				.sourceUsername(userAccess.get().getUsername())
				.type(type)
				.targetUserId(targetUserId)
				.mediaUri(media != null ? media.getId() : null)
				.mediaTitle(media != null ? media.getTitle() : null)
				.commentId(comment != null ? comment.getId() : null)
				.commentText(comment != null ? comment.getComment() : null)
				.build();
	}

	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		onEvent(comment.getMedia().getUserId(), UserNotificationType.NEW_COMMENT, null, comment);
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.WATCH_MEDIA, media, null);
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.LIKE_MEDIA, media, null);
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		onEvent(media.getUserId(), UserNotificationType.DISLIKE_MEDIA, media, null);
	}
	
	void handleCommentLiked(@Observes @LikedEntity MediaCommentEntity comment) {
		onEvent(comment.getUserId(), UserNotificationType.LIKE_COMMENT, null, comment);
	}
	
	void handleCommentDisliked(@Observes @DislikedEntity MediaCommentEntity comment) {
		onEvent(comment.getUserId(), UserNotificationType.DISLIKE_COMMENT, null, comment);
	}
	
	void handleNewLink(@Observes @NewEntity UserLinkEntity link) {
		onEvent(link.getId().getTargetUserId(), UserNotificationType.JOINED_NETWORK, null, null);
	}
	
	void handleDeletedLink(@Observes @DeleteEntity UserLinkEntity link) {
		onEvent(link.getId().getTargetUserId(), UserNotificationType.LEFT_NETWORK, null, null);
	}
}
