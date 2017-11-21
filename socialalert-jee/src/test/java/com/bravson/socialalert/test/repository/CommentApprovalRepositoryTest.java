package com.bravson.socialalert.test.repository;

import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

public class CommentApprovalRepositoryTest extends BaseRepositoryTest {
    
    private CommentApprovalRepository repository = new CommentApprovalRepository(getEntityManager());

    @Test
    public void createNewApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", UserAccess.of("testUser", "1.2.3.4")));
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.LIKE);
    }

    @Test
    public void changeExistingApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", UserAccess.of("testUser", "1.2.3.4")));
    	repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", ApprovalModifier.DISLIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.DISLIKE);
    }
    
    @Test
    public void resetExistingApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", UserAccess.of("testUser", "1.2.3.4")));
    	repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", null);
    	assertThat(result).isEmpty();
    }
    
}
