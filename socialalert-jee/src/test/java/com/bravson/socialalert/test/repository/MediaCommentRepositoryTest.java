package com.bravson.socialalert.test.repository;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

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
    public void listByMediaUri() {
    	String mediaUri1 = "uri1";
    	String mediaUri2 = "uri2";
    	UserAccess userAccess = UserAccess.of("usr1", "1.2.3.4");
    	MediaCommentEntity entity1 = persistAndIndex(new MediaCommentEntity(mediaUri1, "comment 1", userAccess));
    	MediaCommentEntity entity2 = persistAndIndex(new MediaCommentEntity(mediaUri1, "comment 2", userAccess));
    	MediaCommentEntity entity3 = persistAndIndex(new MediaCommentEntity(mediaUri2, "comment 3", userAccess));
    	
    	List<MediaCommentEntity> result = repository.listByMediaUri(mediaUri1);
    	assertThat(result).containsExactlyInAnyOrder(entity1, entity2);
    }
}
