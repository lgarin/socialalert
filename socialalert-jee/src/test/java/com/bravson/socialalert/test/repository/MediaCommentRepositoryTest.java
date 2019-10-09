package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentRepository;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;

public class MediaCommentRepositoryTest extends BaseRepositoryTest {
    
    private MediaCommentRepository repository = new MediaCommentRepository(getPersistenceManager(), new DummyEvent<>());
    
    @Test
    public void createNewComment() {
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	String comment = "test comment";
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity result = repository.create(media.getId(), comment, userAccess);
    	assertThat(result.getId()).isNotBlank();
    	assertThat(result.getMediaUri()).isEqualTo(media.getId());
    	assertThat(result.getUserId()).isEqualTo(userAccess.getUserId());
    	assertThat(result.getComment()).isEqualTo(comment);
    	assertThat(result.getLikeCount()).isEqualTo(0);
    	assertThat(result.getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void findExistingComment() {
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	String comment = "test comment";
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity entity = persistAndIndex(new MediaCommentEntity(media, comment, userAccess));
    	Optional<MediaCommentEntity> result = repository.find(entity.getId());
    	assertThat(result).hasValue(entity);
    }
    
    @Test
    public void findNonExistingComment() {
    	Optional<MediaCommentEntity> result = repository.find("abc");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void listByMediaUri() throws InterruptedException {
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	MediaEntity media = storeDefaultMedia();
    	persistAndIndex(new MediaCommentEntity(media, "comment 1", userAccess));
    	Thread.sleep(10);
    	MediaCommentEntity entity2 = persistAndIndex(new MediaCommentEntity(media, "comment 2", userAccess));
    	Instant instant = Instant.now();
    	Thread.sleep(10);
    	persistAndIndex(new MediaCommentEntity(media, "comment 3", userAccess));
    	
    	QueryResult<MediaCommentEntity> result = repository.listByMediaUri(media.getId(), new PagingParameter(instant, 0, 1));
    	assertThat(result.getPageCount()).isEqualTo(2);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getContent()).containsExactly(entity2);
    	assertThat(result.getNextPage()).isEqualTo(new PagingParameter(instant, 1, 1));
    }
    
    @Test
    public void listByInvalidMediaUri() throws InterruptedException {
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	MediaEntity media = storeDefaultMedia();
    	persistAndIndex(new MediaCommentEntity(media, "comment 1", userAccess));
    	persistAndIndex(new MediaCommentEntity(media, "comment 2", userAccess));
    	persistAndIndex(new MediaCommentEntity(media, "comment 3", userAccess));
    	
    	QueryResult<MediaCommentEntity> result = repository.listByMediaUri("xyz", new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getPageCount()).isEqualTo(0);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).isEmpty();
    }
}
