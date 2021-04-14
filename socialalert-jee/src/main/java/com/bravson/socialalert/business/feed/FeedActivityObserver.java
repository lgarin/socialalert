package com.bravson.socialalert.business.feed;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.feed.item.FeedItemRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
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
	@NonNull
	FeedItemRepository itemRepository;
	
	@Inject
	@NonNull
	Instance<UserAccess> userAccess;

	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		itemRepository.insert(FeedActivity.NEW_MEDIA, media, userAccess.get());
	}
	
	void handleMediaHit(@Observes @HitEntity MediaEntity media) {
		itemRepository.insert(FeedActivity.WATCH_MEDIA, media, userAccess.get());
	}
	
	void handleMediaLiked(@Observes @LikedEntity MediaEntity media) {
		itemRepository.insert(FeedActivity.LIKE_MEDIA, media, userAccess.get());
	}
	
	void handleMediaDisliked(@Observes @DislikedEntity MediaEntity media) {
		itemRepository.insert(FeedActivity.DISLIKE_MEDIA, media, userAccess.get());
	}
	
	void handleNewComment(@Observes @NewEntity MediaCommentEntity comment) {
		itemRepository.insert(FeedActivity.NEW_COMMENT, comment, userAccess.get());
	}
	
	void handleCommentLiked(@Observes @LikedEntity MediaCommentEntity comment) {
		itemRepository.insert(FeedActivity.LIKE_COMMENT, comment, userAccess.get());
	}
	
	void handleCommentDisliked(@Observes @DislikedEntity MediaCommentEntity comment) {
		itemRepository.insert(FeedActivity.DISLIKE_COMMENT, comment, userAccess.get());
	}
}
