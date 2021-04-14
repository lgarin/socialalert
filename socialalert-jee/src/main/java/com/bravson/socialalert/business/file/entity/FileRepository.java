package com.bravson.socialalert.business.file.entity;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
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
	PersistenceManager persistenceManager;
	
	public FileEntity storeMedia(@NonNull FileMetadata fileMetadata, @NonNull UserAccess userAccess) {
		FileEntity entity = new FileEntity(fileMetadata, userAccess);
		persistenceManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(@NonNull String fileUri) {
		return persistenceManager.find(FileEntity.class, fileUri);
	}
	
	public List<FileEntity> findByIpAddressPattern(@NonNull String ipAddressPattern) {
		return persistenceManager.search(FileEntity.class)
				.where(p -> p.wildcard().field("versionInfo.ipAddress").matching(ipAddressPattern))
				.fetchHits(100);
	}
	
	public List<FileEntity> findByUserIdAndState(@NonNull String userId, @NonNull FileState state) {
		return persistenceManager.createQuery("from File where versionInfo.userId = :userId and state = :state", FileEntity.class)
			.setParameter("userId", userId)
			.setParameter("state", state)
			.getResultList();
	}
	
	public List<FileEntity> findByUserId(@NonNull String userId) {
		return persistenceManager.createQuery("from File where versionInfo.userId = :userId", FileEntity.class)
			.setParameter("userId", userId)
			.getResultList();
	}
	
	public void addVariant(@NonNull String fileUri, FileMetadata variant) {
		findFile(fileUri).ifPresent(entity -> entity.addVariant(variant));
	}

	public void delete(FileEntity entity) {
		persistenceManager.remove(entity);
	}
}
