package com.bravson.socialalert.test.repository;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@QuarkusTest
public class CommentApprovalRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    CommentApprovalRepository repository;

    @Test
    @Transactional
    public void createNewApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", createUserAccess("testUser", "1.2.3.4")));
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.LIKE);
    }

    @Test
    @Transactional
    public void changeExistingApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", createUserAccess("testUser", "1.2.3.4")));
    	repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", ApprovalModifier.DISLIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.DISLIKE);
    }
    
    @Test
    @Transactional
    public void resetExistingApproval() {
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(storeDefaultMedia(), "testComment", createUserAccess("testUser", "1.2.3.4")));
    	repository.changeApproval(comment, "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval(comment, "userId1", null);
    	assertThat(result).isEmpty();
    }
    
    @Test
    @Transactional
    public void listUserMediaApprovals() {
    	String userId = "testUser";
    	MediaEntity media = storeDefaultMedia();
    	MediaCommentEntity comment = persistAndIndex(new MediaCommentEntity(media, "testComment1", createUserAccess("otherUser", "1.2.3.4")));
		CommentApprovalEntity approval = repository.changeApproval(comment, userId, ApprovalModifier.LIKE).get();
		persistAndIndex(approval);
    	List<CommentApprovalEntity> result = repository.findAllByMediaUri(media.getId(), userId);
    	assertThat(result).containsExactly(approval);
    }
}
