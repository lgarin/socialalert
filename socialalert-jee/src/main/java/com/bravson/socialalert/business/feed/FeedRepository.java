package com.bravson.socialalert.business.feed;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class FeedRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public FeedEntity insert(FeedActivity activity, MediaEntity media, UserAccess userAccess) {
		if (FeedActivity.MEDIA_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for media");
		}
		FeedEntity entity = new FeedEntity(media, null, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	public FeedEntity insert(FeedActivity activity, MediaCommentEntity comment, UserAccess userAccess) {
		if (FeedActivity.COMMENT_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for comment");
		}
		FeedEntity entity = new FeedEntity(comment.getMedia(), comment, activity, userAccess);
		return persistenceManager.persist(entity);
	}
}
