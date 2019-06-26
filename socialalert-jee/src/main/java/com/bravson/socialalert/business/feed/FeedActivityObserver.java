package com.bravson.socialalert.business.feed;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.HitEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Observer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Observer
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedActivityObserver {

	@Inject
	FeedRepository feedRepository;
	
	@Inject
	@NonNull
	UserAccess userAccess;

	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		feedRepository.insert(FeedActivity.NEW_MEDIA, media, userAccess);
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		feedRepository.insert(FeedActivity.WATCH_MEDIA, media, userAccess);
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		feedRepository.insert(FeedActivity.LIKE_MEDIA, media, userAccess);
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		feedRepository.insert(FeedActivity.DISLIKE_MEDIA, media, userAccess);
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		feedRepository.insert(FeedActivity.NEW_COMMENT, comment, userAccess);
	}
	
	void handleCommentLiked(@Observes @LikedEntity MediaCommentEntity comment) {
		feedRepository.insert(FeedActivity.LIKE_COMMENT, comment, userAccess);
	}
	
	void handleCommentDisliked(@Observes @DislikedEntity MediaCommentEntity comment) {
		feedRepository.insert(FeedActivity.DISLIKE_COMMENT, comment, userAccess);
	}
}
