package com.bravson.socialalert.test.repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseRepositoryTest extends Assertions {

	private static EntityManagerFactory entityManagerFactory;

	private FullTextEntityManager entityManager;
	
    @BeforeClass
    public static void setUpEntityManagerFactory() throws SecurityException, IOException {
    	 final LogManager logManager = LogManager.getLogManager();
         try (final InputStream is = BaseRepositoryTest.class.getResourceAsStream("/logging.properties")) {
             logManager.readConfiguration(is);
         }
    	
        entityManagerFactory = Persistence.createEntityManagerFactory("socialalert");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
    	if (entityManagerFactory != null) {
    		entityManagerFactory.close();
    	}
    }

    @After
    public void closeEntityManager() {
    	if (entityManager != null) {
    		entityManager.close();
    	}
    }
    
    protected FullTextEntityManager getEntityManager() {
    	if (entityManager == null) {
    		entityManager = Search.getFullTextEntityManager(entityManagerFactory.createEntityManager());
    	}
    	return entityManager;
    }
    
    protected <T> T persistAndIndex(T entity) {
    	getEntityManager().persist(entity);
    	getEntityManager().index(entity);
    	getEntityManager().flushToIndexes();
    	return entity;
    }
}
