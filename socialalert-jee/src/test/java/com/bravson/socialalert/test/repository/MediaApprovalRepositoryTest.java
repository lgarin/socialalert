package com.bravson.socialalert.test.repository;

import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.media.ApprovalModifier;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.approval.MediaApprovalRepository;

public class MediaApprovalRepositoryTest extends BaseRepositoryTest {
    
    private MediaApprovalRepository repository = new MediaApprovalRepository(getEntityManager());

    @Test
    public void createNewApproval() {
    	Optional<MediaApprovalEntity> result = repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.LIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.LIKE);
    }

    @Test
    public void changeExistingApproval() {
    	repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.LIKE);
    	Optional<MediaApprovalEntity> result = repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.DISLIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.DISLIKE);
    }
    
    @Test
    public void resetExistingApproval() {
    	repository.changeApproval("mediaUri1", "userId1", ApprovalModifier.LIKE);
    	Optional<MediaApprovalEntity> result = repository.changeApproval("mediaUri1", "userId1", null);
    	assertThat(result).isEmpty();
    }
}
