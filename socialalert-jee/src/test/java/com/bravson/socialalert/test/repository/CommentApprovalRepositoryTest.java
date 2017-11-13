package com.bravson.socialalert.test.repository;

import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.media.approval.CommentApprovalRepository;

public class CommentApprovalRepositoryTest extends BaseRepositoryTest {
    
    private CommentApprovalRepository repository = new CommentApprovalRepository(getEntityManager());

    @Test
    public void createNewApproval() {
    	Optional<CommentApprovalEntity> result = repository.changeApproval("commentId1", "userId1", ApprovalModifier.LIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.LIKE);
    }

    @Test
    public void changeExistingApproval() {
    	repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval("commentId1", "userId1", ApprovalModifier.DISLIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.DISLIKE);
    }
    
    @Test
    public void resetExistingApproval() {
    	repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.LIKE);
    	Optional<CommentApprovalEntity> result = repository.changeApproval("commentId1", "userId1", null);
    	assertThat(result).isEmpty();
    }
    
}
