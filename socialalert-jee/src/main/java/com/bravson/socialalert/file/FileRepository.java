package com.bravson.socialalert.file;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.NonNull;

@ManagedBean
@Transactional
@ApplicationScoped
public class FileRepository {

	private final FullTextEntityManager entityManager;
	
	@Inject
	public FileRepository(FullTextEntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public FileEntity storeMedia(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata) {
		FileEntity entity = FileEntity.of(fileMetadata, mediaMetadata);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(@NonNull String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
	
	@SuppressWarnings("unchecked")
	public List<FileEntity> findByIpAddressPattern(@NonNull String ipAddressPattern) {
		QueryBuilder queryBuilder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(FileEntity.class).get();
		Query query = queryBuilder.keyword().wildcard().onField("fileVariants.ipAddress").matching(ipAddressPattern).createQuery();
		return entityManager.createFullTextQuery(query, FileEntity.class).getResultList();
	}
}
