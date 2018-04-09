package com.bravson.socialalert.infrastructure.entity;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ManagedBean
@ApplicationScoped
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceManager {

	@PersistenceContext(unitName = "socialalert")
    EntityManager entityManager;
	
	public QueryBuilder createQueryBuilder(Class<?> entityClass) {
		return Search.getFullTextEntityManager(entityManager).getSearchFactory().buildQueryBuilder().forEntity(entityClass).get();
	}
	
	public <T> T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}
	
	public <T> T merge(T entity) {
		entityManager.merge(entity);
		return entity;
	}
	
	public <T> T remove(T entity) {
		entityManager.remove(entity);
		return entity;
	}
	
	public <T> Optional<T> find(Class<T> entityClass, Object key) {
		return Optional.ofNullable(entityManager.find(entityClass, key));
	}
	
	public FullTextQuery createFullTextQuery(Query query, Class<?> entityClass) {
		 return Search.getFullTextEntityManager(entityManager).createFullTextQuery(query, entityClass);
	}
}
