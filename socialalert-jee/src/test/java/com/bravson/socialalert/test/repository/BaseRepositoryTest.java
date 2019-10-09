package com.bravson.socialalert.test.repository;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.SynchronizationType;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;

public class BaseRepositoryTest extends Assertions {

	private static EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;
	
    @BeforeAll
    public static void setUpEntityManagerFactory() throws SecurityException, IOException {
    	/*
    	 final LogManager logManager = LogManager.getLogManager();
         try (final InputStream is = BaseRepositoryTest.class.getResourceAsStream("/logging.properties")) {
             logManager.readConfiguration(is);
         }
    	*/
        entityManagerFactory = Persistence.createEntityManagerFactory("socialalert-test");
    }

    @AfterAll
    public static void closeEntityManagerFactory() {
    	if (entityManagerFactory != null) {
    		entityManagerFactory.close();
    	}
    }
    
    private EntityManager getEntityManager() {
    	if (entityManager == null) {
    		entityManager = entityManagerFactory.createEntityManager(SynchronizationType.SYNCHRONIZED);
    	}
    	return entityManager;
    }
    
    @BeforeEach
    public final void startTransaction() {
    	getEntityManager().getTransaction().begin();
    }

    @AfterEach
    public final void closeEntityManager() {
    	if (entityManager != null) {
    		entityManager.close();
    	}
    }
    
    protected final PersistenceManager getPersistenceManager() {
    	return new PersistenceManager(getEntityManager());
    }
    
    protected final <T> T persistAndIndex(T entity) {
    	getEntityManager().persist(entity);
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
}
