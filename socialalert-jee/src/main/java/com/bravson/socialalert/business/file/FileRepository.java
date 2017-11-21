package com.bravson.socialalert.business.file;

import java.util.List;
import java.util.Optional;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.annotations.QueryHints;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class FileRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	@Inject
	@NewEntity
	Event<FileEntity> newEntityEvent;

	public FileEntity storeMedia(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata, @NonNull UserAccess userAccess) {
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, userAccess);
		entityManager.persist(entity);
		newEntityEvent.fire(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(@NonNull String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
	
	@SuppressWarnings("unchecked")
	public List<FileEntity> findByIpAddressPattern(@NonNull String ipAddressPattern) {
		QueryBuilder queryBuilder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(FileEntity.class).get();
		Query query = queryBuilder.keyword().wildcard().onField("versionInfo.ipAddress").matching(ipAddressPattern).createQuery();
		return entityManager.createFullTextQuery(query, FileEntity.class).setHint(QueryHints.READ_ONLY, true).getResultList();
	}
}
