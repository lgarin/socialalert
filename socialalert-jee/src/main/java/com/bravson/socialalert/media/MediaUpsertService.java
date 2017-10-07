package com.bravson.socialalert.media;

import javax.inject.Inject;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.infrastructure.layer.Service;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.UserInfoService;

import lombok.NonNull;

@Service
public class MediaUpsertService {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	public MediaInfo claimMedia(@NonNull String fileUri, @NonNull UpsertMediaParameter mediaParameter, @NonNull UserAccess userAccess) {
		if (mediaRepository.findMedia(fileUri).isPresent()) {
			throw new ClientErrorException(Status.CONFLICT);
		}
		FileEntity fileEntity = fileRepository.findFile(fileUri).orElseThrow(NotFoundException::new);
		if (!fileEntity.getUserId().equals(userAccess.getUserId())) {
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
