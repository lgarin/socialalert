package com.bravson.socialalert.test.repository;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.logging.LogManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.bravson.socialalert.business.file.FileMetadata;
import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;

public class BaseRepositoryTest extends Assertions {

	private static EntityManagerFactory entityManagerFactory;

	private FullTextEntityManager entityManager;
	
    @BeforeClass
    public static void setUpEntityManagerFactory() throws SecurityException, IOException {
    	 final LogManager logManager = LogManager.getLogManager();
         try (final InputStream is = BaseRepositoryTest.class.getResourceAsStream("/logging.properties")) {
             logManager.readConfiguration(is);
         }
    	
        entityManagerFactory = Persistence.createEntityManagerFactory("socialalert-test");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
    	if (entityManagerFactory != null) {
    		entityManagerFactory.close();
    	}
    }
    
    @Before
    public final void startTransaction() {
    	entityManager.getTransaction().begin();
    }

    @After
    public final void closeEntityManager() {
    	if (entityManager != null) {
    		entityManager.close();
    	}
    }
    
    protected final FullTextEntityManager getEntityManager() {
    	if (entityManager == null) {
    		entityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
    	}
    	return entityManager;
    }
    
    protected final PersistenceManager getPersistenceManager() {
    	return new PersistenceManager(getEntityManager());
    }
    
    protected final <T> T persistAndIndex(T entity) {
    	getEntityManager().persist(entity);
    	getEntityManager().index(entity);
    	getEntityManager().flushToIndexes();
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
    	MediaMetadata mediaMetadata = MediaMetadata.builder().height(700).width(1000).build();
		FileEntity file = persistAndIndex(new FileEntity(fileMetadata, mediaMetadata, UserAccess.of("test", "1.2.3.4")));
		return persistAndIndex(new MediaEntity(file, claimParameter, UserAccess.of("test", "1.2.3.4")));
	}
}
