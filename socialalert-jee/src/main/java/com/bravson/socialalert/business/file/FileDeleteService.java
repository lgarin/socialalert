package com.bravson.socialalert.business.file;

import javax.inject.Inject;

import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileDeleteService {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	FileStore fileStore;
	
	public boolean deleteFile(String fileUri, UserAccess userAccess) {
		return fileRepository.findFile(fileUri).map(f -> f.markDelete(userAccess)).orElse(false);
	}

	//@Schedule(minute="*/5", hour="*")
    public void automaticTimeout() {
        System.out.println("Cleanup file timer");
        // TODO find old files which have not been claimed
        // TODO delete files
    }
}
