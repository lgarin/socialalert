package com.bravson.socialalert.infrastructure.entity;

import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.EntityType;
import javax.transaction.Transactional;

import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.search.query.dsl.HibernateOrmSearchQuerySelectStep;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ApplicationScoped
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceManager {

	@PersistenceContext
    EntityManager entityManager;
	
	public <T> HibernateOrmSearchQuerySelectStep<T> search(Class<T> entityClass) {
		return Search.session(entityManager).search(entityClass);
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
	
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return entityManager.createQuery(qlString, resultClass);
	}
	
	public void deleteAll() {
		String allTables = entityManager.getMetamodel().getEntities().stream().map(EntityType::getName).collect(Collectors.joining(", "));
    	entityManager.createNativeQuery("TRUNCATE TABLE " + allTables + " CASCADE").executeUpdate();
    	Search.session(entityManager).workspace().purge();
	}
}
