package com.bravson.socialalert.test.repository;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import org.assertj.core.api.Assertions;
import org.hibernate.search.mapper.orm.Search;
import org.junit.jupiter.api.AfterEach;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;

public class BaseRepositoryTest extends Assertions {

	@PersistenceContext
	private EntityManager entityManager;
	
    @AfterEach
    @Transactional(value = TxType.REQUIRES_NEW)
    public void deleteAllData() {
    	String allTables = entityManager.getMetamodel().getEntities().stream().map(EntityType::getName).collect(Collectors.joining(", "));
    	entityManager.createNativeQuery("TRUNCATE TABLE " + allTables + " CASCADE").executeUpdate();
    	Search.session(entityManager).workspace().purge();
    }
    
    @Transactional
    protected <T> T persistAndIndex(T entity) {
    	entityManager.persist(entity);
    	entityManager.flush();
    	Search.session(entityManager).workspace(entity.getClass()).flush();
    	return entity;
    }
    
    protected MediaEntity storeDefaultMedia() {
		UpsertMediaParameter claimParameter = new UpsertMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setDescription("Test desc");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategories(Arrays.asList("cat1", "cat2"));
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		FileMetadata fileMetadata = FileMetadata.builder().md5("test").timestamp(Instant.now()).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity file = persistAndIndex(new FileEntity(fileMetadata, UserAccess.of("test", "1.2.3.4")));
		return persistAndIndex(new MediaEntity(file, claimParameter, UserAccess.of("test", "1.2.3.4")));
	}
    
    protected MediaEntity createDefaultMedia() {
		UpsertMediaParameter claimParameter = new UpsertMediaParameter();
		claimParameter.setTitle("Test title");
		claimParameter.setDescription("Test desc");
		claimParameter.setTags(Arrays.asList("tag1", "tag2"));
		claimParameter.setCategories(Arrays.asList("cat1", "cat2"));
		claimParameter.setLocation(GeoAddress.builder().country("CH").locality("Bern").longitude(7.45).latitude(46.95).build());
		FileMetadata fileMetadata = FileMetadata.builder().md5("test").timestamp(Instant.now()).contentSize(0L).fileFormat(MediaFileFormat.MEDIA_JPG).build();
		FileEntity file = new FileEntity(fileMetadata, UserAccess.of("test", "1.2.3.4"));
		return new MediaEntity(file, claimParameter, UserAccess.of("test", "1.2.3.4"));
	}
}
