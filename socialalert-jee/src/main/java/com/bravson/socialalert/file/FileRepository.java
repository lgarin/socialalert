package com.bravson.socialalert.file;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.file.media.MediaMetadata;

@ManagedBean
@Transactional
public class FileRepository {

	private final FullTextEntityManager entityManager;
	
	@Inject
	public FileRepository(EntityManager entityManager) {
		this.entityManager = Search.getFullTextEntityManager(entityManager);
	}

	public FileEntity storeMedia(FileMetadata fileMetadata, MediaMetadata mediaMetadata) {
		FileEntity entity = FileEntity.of(fileMetadata, mediaMetadata);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
	
	@SuppressWarnings("unchecked")
	public List<FileEntity> findByIpAddressPattern(String ipAddressPattern) {
		QueryBuilder queryBuilder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(FileEntity.class).get();
		Query query = queryBuilder.keyword().wildcard().onField("fileVariants.ipAddress").matching(ipAddressPattern).createQuery();
		return entityManager.createFullTextQuery(query, FileEntity.class).getResultList();
	}
}
