package com.bravson.socialalert.test.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

public class CommentApprovalRepositoryTest extends BaseRepositoryTest {
    
    private CommentApprovalRepository repository = new CommentApprovalRepository(getPersistenceManager());

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
    
    @Test
    public void listUserMediaApprovals() {
    	String userId = "testUser";
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(media, "testComment1", UserAccess.of("otherUser", "1.2.3.4")));
		CommentApprovalEntity approval = repository.changeApproval(comment, userId, ApprovalModifier.LIKE).get();
		persistAndIndex(approval);
    	List<CommentApprovalEntity> result = repository.findAllByMediaUri(media.getId(), userId);
    	assertThat(result).containsExactly(approval);
    }
}
