package com.bravson.socialalert.infrastructure.entity;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

@ManagedBean
@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "socialalert")
    private EntityManager em;
	
	@Produces
	public FullTextEntityManager getFullTextEntityManager() {
		return Search.getFullTextEntityManager(em);
	}
}
