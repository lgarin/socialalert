package com.bravson.socialalert.infrastructure.entity;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;

@ManagedBean
@RequestScoped
public class EntityManagerProducer {

    @PersistenceContext(unitName = "socialalert")
    private EntityManager em;
	
	@Produces @RequestScoped
	public FullTextEntityManager getFullTextEntityManager() {
		return Search.getFullTextEntityManager(em);
	}
	
	public void destroyEntityManager(@Disposes FullTextEntityManager em) {
		em.close();
	}
}
