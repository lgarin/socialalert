package com.bravson.socialalert.media;

import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.hibernate.search.jpa.FullTextEntityManager;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.log.Logged;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
@Logged
public class MediaRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	public Optional<MediaEntity> findMedia(@NonNull String mediaUri) {
		return Optional.ofNullable(entityManager.find(MediaEntity.class, mediaUri));
	}

	public MediaEntity storeMedia(FileEntity file, ClaimPictureParameter parameter, String userId, String ipAddress) {
		MediaEntity media = MediaEntity.of(file, parameter, new VersionInfo(userId, ipAddress));
		entityManager.persist(media);
		return media;
	}
}
