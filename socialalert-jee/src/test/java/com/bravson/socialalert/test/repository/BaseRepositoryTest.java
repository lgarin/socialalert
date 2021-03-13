package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Arrays;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.assertj.core.api.Assertions;
import org.hibernate.search.mapper.orm.Search;
import org.junit.jupiter.api.BeforeEach;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserAccessToken;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;

public class BaseRepositoryTest extends Assertions {

	@PersistenceContext
	EntityManager entityManager;
	
	@Inject
	PersistenceManager persistenceManager;
	
    @BeforeEach
    @Transactional(value = TxType.REQUIRES_NEW)
    public void deleteAllData() {
    	persistenceManager.deleteAll();
    }
    
    @Transactional
    protected <T> T persistAndIndex(T entity) {
    	entityManager.persist(entity);
    	entityManager.flush();
    	Search.session(entityManager).workspace(entity.getClass()).flush();
    	return entity;
    }
    
    protected static UserAccess createUserAccess(String userId, String ipAddress) {
		return UserAccessToken.builder().userId(userId).ipAddress(ipAddress).username(userId).email(userId).build();
	}
    
    protected MediaEntity storeDefaultMedia() {
		UpsertMediaParameter claimParameter = buildDefaultClaimParameter();
		FileMetadata fileMetadata = FileMetadata.builder().md5("test").timestamp(Instant.now()).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity file = persistAndIndex(new FileEntity(fileMetadata, createUserAccess("test", "1.2.3.4")));
		return persistAndIndex(new MediaEntity(file, claimParameter, createUserAccess("test", "1.2.3.4")));
	}

	private UpsertMediaParameter buildDefaultClaimParameter() {
		UpsertMediaParameter claimParameter = new UpsertMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategory("cat1");
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		return claimParameter;
	}
    
    protected MediaEntity createDefaultMedia() {
		UpsertMediaParameter claimParameter = buildDefaultClaimParameter();
		FileMetadata fileMetadata = FileMetadata.builder().md5("test").timestamp(Instant.now()).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity file = new FileEntity(fileMetadata, createUserAccess("test", "1.2.3.4"));
		return new MediaEntity(file, claimParameter, createUserAccess("test", "1.2.3.4"));
	}
}
