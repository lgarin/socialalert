package com.bravson.socialalert.file;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;

import com.bravson.socialalert.file.media.MediaFileFormat;
import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@ManagedBean
@AllArgsConstructor
public class MediaRepository {

	@Getter
	private final EntityManager entityManager;

	public FileEntity storeMedia(String fileUri, MediaFileFormat format, FileMetadata fileMetadata, MediaMetadata mediaMetadata) throws IOException {
		val entity = new FileEntity(fileUri, Collections.singleton(format), fileMetadata, mediaMetadata);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
}
