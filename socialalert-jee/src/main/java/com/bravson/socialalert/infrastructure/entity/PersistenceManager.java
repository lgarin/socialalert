package com.bravson.socialalert.infrastructure.entity;

import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.search.engine.search.query.dsl.SearchQuerySelectStep;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.common.EntityReference;
import org.hibernate.search.mapper.orm.search.loading.dsl.SearchLoadingOptionsStep;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ApplicationScoped
@Transactional
@NoArgsConstructor
@AllArgsConstructor
public class PersistenceManager {

	@PersistenceContext
    EntityManager entityManager;
	
	public <T> SearchQuerySelectStep<?, EntityReference, T, SearchLoadingOptionsStep, ?, ?> search(Class<T> entityClass) {
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
	
	public Query createUpdate(String qlString) {
		return entityManager.createQuery(qlString);
	}
	
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return entityManager.createQuery(qlString, resultClass);
	}
	
	public void deleteAll() {
		String allTables = entityManager.getMetamodel().getEntities().stream().map(EntityType::getName).collect(Collectors.joining(", "));
    	entityManager.createNativeQuery("TRUNCATE TABLE " + allTables + " CASCADE").executeUpdate();
    	Search.session(entityManager).workspace().purge();
	}
	
	void onStart(@Observes StartupEvent ev) throws InterruptedException {
		Search.session(entityManager).massIndexer().startAndWait();
	}
}
