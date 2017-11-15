package com.bravson.socialalert.test.repository;

import java.util.Optional;

import org.junit.Test;

import com.bravson.socialalert.domain.approval.ApprovalModifier;
import com.bravson.socialalert.media.MediaEntity;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.approval.MediaApprovalRepository;

public class MediaApprovalRepositoryTest extends BaseRepositoryTest {
    
    private MediaApprovalRepository repository = new MediaApprovalRepository(getEntityManager());

    @Test
    public void createNewApproval() {
    	MediaEntity media = storeDefaultMedia();
    	Optional<MediaApprovalEntity> result = repository.changeApproval(media, "userId1", ApprovalModifier.LIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.LIKE);
    }

    @Test
    public void changeExistingApproval() {
    	MediaEntity media = storeDefaultMedia();
    	repository.changeApproval(media, "userId1", ApprovalModifier.LIKE);
    	Optional<MediaApprovalEntity> result = repository.changeApproval(media, "userId1", ApprovalModifier.DISLIKE);
    	assertThat(result).isPresent();
    	assertThat(result.get().getModifier()).isEqualTo(ApprovalModifier.DISLIKE);
    }
    
    @Test
    public void resetExistingApproval() {
    	MediaEntity media = storeDefaultMedia();
    	repository.changeApproval(media, "userId1", ApprovalModifier.LIKE);
    	Optional<MediaApprovalEntity> result = repository.changeApproval(media, "userId1", null);
    	assertThat(result).isEmpty();
    }
}
