package com.bravson.socialalert.infrastructure.entity;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ManagedBean
@ApplicationScoped
public class EntityManagerProducer {

	@Produces
    @PersistenceContext(unitName = "socialalert")
    private EntityManager em;
}
