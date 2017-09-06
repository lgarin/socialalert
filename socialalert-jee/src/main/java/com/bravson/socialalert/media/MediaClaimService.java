package com.bravson.socialalert.media;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response.Status;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.file.FileRepository;
import com.bravson.socialalert.infrastructure.log.Logged;
import com.bravson.socialalert.user.UserInfoSupplier;

import lombok.NonNull;

@ManagedBean
@Transactional
@Logged
public class MediaClaimService {

	@Inject
	FileRepository fileRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoSupplier userRepository;
	
	public MediaInfo claimPicture(MediaClaimParameter mediaParameter, @NonNull ClaimPictureParameter pictureParameter) {
		if (mediaRepository.findMedia(mediaParameter.getFileUri()).isPresent()) {
			throw new ClientErrorException(Status.CONFLICT);
		}
		FileEntity fileEntity = fileRepository.findFile(mediaParameter.getFileUri()).filter(FileEntity::isPicture).orElseThrow(NotFoundException::new);
		if (!fileEntity.getUserId().equals(mediaParameter.getUserId())) {
			throw new ForbiddenException();
		}
		MediaEntity mediaEntity = mediaRepository.storeMedia(fileEntity, pictureParameter, mediaParameter.getUserId(), mediaParameter.getIpAddress());
		return userRepository.fillUserInfo(mediaEntity.toMediaInfo());
	}
}
