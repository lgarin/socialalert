package com.bravson.socialalert.test.repository;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.business.media.approval.MediaApprovalRepository;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaApprovalRepositoryTest extends BaseRepositoryTest {
    
	@Inject
    private MediaApprovalRepository repository;

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
