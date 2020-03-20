package com.bravson.socialalert.business.feed;

import java.util.Collection;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
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
public class FeedItemRepository {
	
	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public FeedItemEntity insert(@NonNull FeedActivity activity, @NonNull MediaEntity media, @NonNull UserAccess userAccess) {
		if (!FeedActivity.MEDIA_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for media");
		}
		FeedItemEntity entity = new FeedItemEntity(media, null, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	public FeedItemEntity insert(@NonNull FeedActivity activity, @NonNull MediaCommentEntity comment, @NonNull UserAccess userAccess) {
		if (!FeedActivity.COMMENT_SET.contains(activity)) {
			throw new IllegalArgumentException("Activity " + activity + " is not allowed for comment");
		}
		FeedItemEntity entity = new FeedItemEntity(comment.getMedia(), comment, activity, userAccess);
		return persistenceManager.persist(entity);
	}
	
	public QueryResult<FeedItemEntity> searchActivitiesByUsers(@NonNull Collection<String> userIdList, @NonNull PagingParameter paging) {
		SearchResult<FeedItemEntity> result = persistenceManager.search(FeedItemEntity.class)
				.where(p -> p.bool()
						.must(p.range().field("versionInfo.creation").atMost(paging.getTimestamp()))
						.must(p.simpleQueryString().field("versionInfo.userId").matching(String.join(" ", userIdList))))
				.sort(s -> s.field("versionInfo.creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.getHits(), result.getTotalHitCount(), paging);
	}
}
