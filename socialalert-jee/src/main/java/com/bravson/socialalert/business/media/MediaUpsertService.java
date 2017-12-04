package com.bravson.socialalert.business.media;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.file.FileEntity;
import com.bravson.socialalert.business.file.FileRepository;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.infrastructure.rest.ConflictException;

import lombok.NonNull;

@Service
@Transactional
public class MediaUpsertService {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	public MediaInfo claimMedia(@NonNull String fileUri, @NonNull UpsertMediaParameter mediaParameter, @NonNull UserAccess userAccess) {
		if (mediaRepository.findMedia(fileUri).isPresent()) {
			throw new ConflictException();
		}
		FileEntity fileEntity = fileRepository.findFile(fileUri).orElseThrow(NotFoundException::new);
		if (!fileEntity.markClaimed(userAccess)) {
			throw new ForbiddenException();
		}
		MediaEntity mediaEntity = mediaRepository.storeMedia(fileEntity, mediaParameter, userAccess);
		return userService.fillUserInfo(mediaEntity.toMediaInfo());
	}
	
	public MediaInfo updateMedia(@NonNull String mediaUri, @NonNull UpsertMediaParameter mediaParameter, @NonNull UserAccess userAccess) {
		MediaEntity mediaEntity = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		if (!mediaEntity.getUserId().equals(userAccess.getUserId())) {
			throw new ForbiddenException();
		}
		mediaEntity.update(mediaParameter, userAccess);
		return userService.fillUserInfo(mediaEntity.toMediaInfo());
	}
}