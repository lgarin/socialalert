package com.bravson.socialalert.business.file;

import java.io.IOException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileDeleteService {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Inject
	FileRepository fileRepository;
	
	@Inject
	FileStore fileStore;

	//@VisibleForTesting
	public void handleDeleteMedia(@Observes @DeleteEntity MediaEntity media) {
		delete(media.getFile());
	}

	private void delete(FileEntity file) {
		for (FileMetadata variant : file.getAllVariants()) {
			try {
				fileStore.deleteFile(variant.getMd5(), variant.getFormattedDate(), variant.getFileFormat());
			} catch (IOException e) {
				logger.error("Cannot delete variant " + variant, e);
			}
		}
		fileRepository.delete(file);
	}

	//@VisibleForTesting
	public void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) throws IOException {
		fileRepository.findByUserId(user.getId()).stream().filter(FileEntity::isTemporary).forEach(this::delete);
		fileStore.deleteFolder(user.getId());
	}
	
	
	//@Schedule(minute="*/5", hour="*")
    public void automaticTimeout() {
        // TODO find old files which have not been claimed
    	// TODO delete old profile pictures
    }
}
