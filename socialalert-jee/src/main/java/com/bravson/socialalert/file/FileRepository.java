package com.bravson.socialalert.file;

import java.util.List;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.file.media.MediaMetadata;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.profile.ProfileEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class FileRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;

	public FileEntity storeMedia(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata, @NonNull ProfileEntity userProfile, @NonNull UserAccess userAccess) {
		FileEntity entity = new FileEntity(fileMetadata, mediaMetadata, userAccess);
		entity.setUserProfile(userProfile);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(@NonNull String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
	
	@SuppressWarnings("unchecked")
	public List<FileEntity> findByIpAddressPattern(@NonNull String ipAddressPattern) {
		QueryBuilder queryBuilder = entityManager.getSearchFactory().buildQueryBuilder().forEntity(FileEntity.class).get();
		Query query = queryBuilder.keyword().wildcard().onField("versionInfo.ipAddress").matching(ipAddressPattern).createQuery();
		return entityManager.createFullTextQuery(query, FileEntity.class).getResultList();
	}
}
