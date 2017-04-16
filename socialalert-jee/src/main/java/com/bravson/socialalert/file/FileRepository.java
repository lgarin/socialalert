package com.bravson.socialalert.file;

import java.io.IOException;
import java.util.Optional;

import javax.annotation.ManagedBean;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import com.bravson.socialalert.file.media.MediaMetadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;

@ManagedBean
@AllArgsConstructor
@Transactional
public class FileRepository {

	@Getter
	private final EntityManager entityManager;

	public FileEntity storeMedia(FileMetadata fileMetadata, MediaMetadata mediaMetadata) throws IOException {
		val entity = FileEntity.of(fileMetadata, mediaMetadata);
		entityManager.persist(entity);
		return entity;
	}
	
	public Optional<FileEntity> findFile(String fileUri) {
		return Optional.ofNullable(entityManager.find(FileEntity.class, fileUri));
	}
}
