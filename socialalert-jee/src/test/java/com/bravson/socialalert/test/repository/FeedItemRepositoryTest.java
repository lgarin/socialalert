package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.feed.item.FeedItemEntity;
import com.bravson.socialalert.business.feed.item.FeedItemRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.PeriodicFeedActivityCount;
import com.bravson.socialalert.domain.histogram.PeriodInterval;
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
    	FeedItemEntity entity = repository.insert(FeedActivity.NEW_MEDIA, media, createUserAccess("test", "1.2.3.4"));
    	assertThat(entity.getUserId()).isEqualTo("test");
    	assertThat(entity.getActivity()).isEqualTo(FeedActivity.NEW_MEDIA);
    	assertThat(entity.getMedia()).isEqualTo(media);
    	assertThat(entity.getComment()).isNull();
    }
    
    @Test
    public void insertFeedItemWithIllegalActivity() {
    	MediaEntity media = storeDefaultMedia();
    	UserAccess userAccess = createUserAccess("test", "1.2.3.4");
    	assertThatThrownBy(() -> repository.insert(FeedActivity.NEW_COMMENT, media, userAccess)).isInstanceOf(IllegalArgumentException.class);
    }
    
    @Test
    public void insertFeedItemWithNewComment() {
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity comment = new MediaCommentEntity(media, "test", createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(comment);
    	FeedItemEntity entity = repository.insert(FeedActivity.NEW_COMMENT, comment, createUserAccess("test", "1.2.3.4"));
    	assertThat(entity.getUserId()).isEqualTo("test");
    	assertThat(entity.getActivity()).isEqualTo(FeedActivity.NEW_COMMENT);
    	assertThat(entity.getMedia()).isEqualTo(media);
    	assertThat(entity.getComment()).isEqualTo(comment);
    }
    
    @Test
    public void searchActivitiesBySingleUser() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(entity);
    	QueryResult<FeedItemEntity> result = repository.searchActivitiesByUsers(Collections.singleton("test"), null, null, new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).containsExactly(entity);
    }
    
    @Test
    public void searchActivitiesByMultipleUsers() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity1 = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(entity1);
    	FeedItemEntity entity2 = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, createUserAccess("test2", "1.2.3.4"));
    	persistAndIndex(entity2);
    	QueryResult<FeedItemEntity> result = repository.searchActivitiesByUsers(Arrays.asList("test", "test2"), null, null, new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getContent()).containsExactly(entity2, entity1);
    }
    
    @Test
    public void groupActivitiesByUser() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(entity);
    	
    	List<PeriodicFeedActivityCount> result = repository.groupUserActivitiesByPeriod(entity.getUserId(), entity.getActivity(), PeriodInterval.DAY);
    	Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
    	assertThat(result).containsExactly(new PeriodicFeedActivityCount(today, 1));
    }
    
    @Test
    public void groupActivitiesByMedia() {
    	MediaEntity media = storeDefaultMedia();
    	FeedItemEntity entity = new FeedItemEntity(media, null, FeedActivity.NEW_MEDIA, createUserAccess("test", "1.2.3.4"));
    	persistAndIndex(entity);
    	
    	List<PeriodicFeedActivityCount> result = repository.groupMediaActivitiesByPeriod(media.getId(), entity.getActivity(), PeriodInterval.DAY);
    	Instant today = Instant.now().truncatedTo(ChronoUnit.DAYS);
    	assertThat(result).containsExactly(new PeriodicFeedActivityCount(today, 1));
    }
}
