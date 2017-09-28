package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.media.comment.MediaCommentEntity;
import com.bravson.socialalert.media.comment.MediaCommentRepository;
import com.bravson.socialalert.user.UserAccess;

public class MediaCommentRepositoryTest extends BaseRepositoryTest {
    
    private MediaCommentRepository repository = new MediaCommentRepository(getEntityManager());
    
    @Test
    public void createNewComment() {
    	String mediaUri = "uri1";
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	String comment = "test comment";
    	MediaCommentEntity result = repository.create(mediaUri, comment, userAccess);
    	assertThat(result.getCommentId()).isNotBlank();
    	assertThat(result.getMediaUri()).isEqualTo(mediaUri);
    	assertThat(result.getUserId()).isEqualTo(userAccess.getUserId());
    	assertThat(result.getComment()).isEqualTo(comment);
    	assertThat(result.getLikeCount()).isEqualTo(0);
    	assertThat(result.getDislikeCount()).isEqualTo(0);
    }

    @Test
    public void findExistingComment() {
    	String mediaUri = "uri1";
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	String comment = "test comment";
    	MediaCommentEntity entity = persistAndIndex(new MediaCommentEntity(mediaUri, comment, userAccess));
    	Optional<MediaCommentEntity> result = repository.find(entity.getCommentId());
    	assertThat(result).hasValue(entity);
    }
    
    @Test
    public void findNonExistingComment() {
    	Optional<MediaCommentEntity> result = repository.find("abc");
    	assertThat(result).isEmpty();
    }
    
    @Test
    public void listByMediaUri() throws InterruptedException {
    	String mediaUri = "uri1";
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	persistAndIndex(new MediaCommentEntity(mediaUri, "comment 1", userAccess));
    	MediaCommentEntity entity2 = persistAndIndex(new MediaCommentEntity(mediaUri, "comment 2", userAccess));
    	Instant instant = Instant.now();
    	Thread.sleep(10);
    	persistAndIndex(new MediaCommentEntity(mediaUri, "comment 3", userAccess));
    	
    	QueryResult<MediaCommentEntity> result = repository.listByMediaUri(mediaUri, new PagingParameter(instant, 0, 1));
    	assertThat(result.getPageCount()).isEqualTo(2);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isEqualTo(new PagingParameter(instant, 1, 1));
    	assertThat(result.getContent()).containsExactly(entity2);
    }
    
    @Test
    public void listByInvalidMediaUri() throws InterruptedException {
    	String mediaUri = "uri1";
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	persistAndIndex(new MediaCommentEntity(mediaUri, "comment 1", userAccess));
    	persistAndIndex(new MediaCommentEntity(mediaUri, "comment 2", userAccess));
    	persistAndIndex(new MediaCommentEntity(mediaUri, "comment 3", userAccess));
    	
    	QueryResult<MediaCommentEntity> result = repository.listByMediaUri("xyz", new PagingParameter(Instant.now(), 0, 10));
    	assertThat(result.getPageCount()).isEqualTo(0);
    	assertThat(result.getPageNumber()).isEqualTo(0);
    	assertThat(result.getNextPage()).isNull();
    	assertThat(result.getContent()).isEmpty();
    }
}
