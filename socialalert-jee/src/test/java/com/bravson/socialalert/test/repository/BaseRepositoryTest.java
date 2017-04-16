package com.bravson.socialalert.test.repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class BaseRepositoryTest extends Assertions {

	private static EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;
	
    @BeforeClass
    public static void setUpEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory("socialalert");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        entityManagerFactory.close();
    }
    
    @After
    public void closeEntityManager() {
    	if (entityManager != null) {
    		entityManager.getTransaction().rollback();
    		entityManager.close();
    	}
    }
    
    protected EntityManager getEntityManager() {
    	if (entityManager == null) {
    		entityManager = entityManagerFactory.createEntityManager();	
    	}
    	entityManager.getTransaction().begin();
    	return entityManager;
    }
}
