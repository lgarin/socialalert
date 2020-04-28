package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.feed.FeedItemEntity;
import com.bravson.socialalert.business.feed.FeedItemRepository;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FeedItemRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    FeedItemRepository repository;

    @Test
    public void insertFeedItemWithNewMedia() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity = repository.insert(FeedActivity.NEW_MEDIA, media, UserAccess.of("test", "1.2.3.4"));
    	assertThat(entity.getUserId()).isEqualTo("test");
    	assertThat(entity.getActivity()).isEqualTo(FeedActivity.NEW_MEDIA);
    	assertThat(entity.getMedia()).isEqualTo(media);
    	assertThat(entity.getComment()).isNull();
    }
    
    @Test
    public void insertFeedItemWithIllegalActivity() {
    	MediaEntity media = storeDefaultMedia();
    	assertThatThrownBy(() -> repository.insert(FeedActivity.NEW_COMMENT, media, UserAccess.of("test", "1.2.3.4"))).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void insertFeedItemWithNewComment() {
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity comment = new MediaCommentEntity(media, "test", UserAccess.of("test", "1.2.3.4"));
    	persistAndIndex(comment);
    	FeedItemEntity entity = repository.insert(FeedActivity.NEW_COMMENT, comment, UserAccess.of("test", "1.2.3.4"));
    	assertThat(entity.getUserId()).isEqualTo("test");
    	assertThat(entity.getActivity()).isEqualTo(FeedActivity.NEW_COMMENT);
    	assertThat(entity.getMedia()).isEqualTo(media);
    	assertThat(entity.getComment()).isEqualTo(comment);
    }
    
    @Test
    public void searchActivitiesBySingleUser() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, UserAccess.of("test", "1.2.3.4"));
    	persistAndIndex(entity);
    	QueryResult<FeedItemEntity> result = repository.searchActivitiesByUsers(Collections.singleton("test"), null, null, new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).containsExactly(entity);
    }
    
    @Test
    public void searchActivitiesByMultipleUsers() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity1 = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, UserAccess.of("test", "1.2.3.4"));
    	persistAndIndex(entity1);
    	FeedItemEntity entity2 = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, UserAccess.of("test2", "1.2.3.4"));
    	persistAndIndex(entity2);
    	QueryResult<FeedItemEntity> result = repository.searchActivitiesByUsers(Arrays.asList("test", "test2"), null, null, new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).containsExactly(entity2, entity1);
    }
}
