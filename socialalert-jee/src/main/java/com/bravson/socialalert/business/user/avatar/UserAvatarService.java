package com.bravson.socialalert.business.user.avatar;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;

import com.bravson.socialalert.business.file.FileResponse;
import com.bravson.socialalert.business.file.FileUploadParameter;
import com.bravson.socialalert.business.file.store.FileStore;
import com.bravson.socialalert.business.file.store.TempFileFormat;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.business.user.profile.UserProfileRepository;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.domain.user.UserDetail;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserAvatarService {

	@Inject
	UserProfileRepository profileRepository;
	
	@Inject
	FileStore fileStore;
	
	@Inject
	AvatarFileProcessor processor;
	
	private static void checkFileFormat(FileUploadParameter parameter) {
		if (!MediaFileFormat.MEDIA_JPG.getContentType().equals(parameter.getContentType())) {
			throw new NotSupportedException();
		}
	}
	
	public UserDetail storeAvatar(@NonNull FileUploadParameter upload, @NonNull UserAccess userAccess) throws IOException {
		checkFileFormat(upload);
	
		UserProfileEntity profile = profileRepository.findByUserId(userAccess.getUserId()).orElseThrow(NotFoundException::new);
		String md5 = storeMedia(upload.getInputFile(), profile.getId());
		
		storeVariant(md5, profile.getId(), MediaSizeVariant.THUMBNAIL);
		storeVariant(md5, profile.getId(), MediaSizeVariant.PREVIEW);
		
		profile.changeAvatar(profile.getId() + "/" + md5, userAccess);

		return profile.toOwnUserDetail();
	}

	private String storeMedia(File inputFile, String userId) throws IOException {
		String md5 = fileStore.computeMd5Hex(inputFile);
		fileStore.deleteFile(md5, userId, MediaFileFormat.MEDIA_JPG);
		fileStore.storeFile(inputFile, md5, userId, MediaFileFormat.MEDIA_JPG);
		return md5;
	}

	private File storeVariant(String md5, String userId, MediaSizeVariant sizeVariant) throws IOException {
		File sourceFile = fileStore.getExistingFile(md5, userId, MediaFileFormat.MEDIA_JPG);
		TempFileFormat tempFormat = new TempFileFormat(processor.getFormat(sizeVariant));
		File thumbnailFile = fileStore.createEmptyFile(md5, userId, tempFormat);
		MediaFileFormat fileFormat = processor.createVariant(sourceFile, thumbnailFile, sizeVariant);
		fileStore.deleteFile(md5, userId, fileFormat);
		return fileStore.changeFileFormat(md5, userId, tempFormat, fileFormat);
	}

	public FileResponse getSmallImage(@NonNull String imageUri) throws IOException {
		return getImage(imageUri, MediaSizeVariant.THUMBNAIL);
	}
	
	public FileResponse getLargeImage(@NonNull String imageUri) throws IOException {
		return getImage(imageUri, MediaSizeVariant.PREVIEW);
	}

	private FileResponse getImage(String imageUri, MediaSizeVariant sizeVariant) throws IOException {
		File inputFile = new File(imageUri);
		MediaFileFormat fileFormat = processor.getFormat(sizeVariant);
		File outputFile = fileStore.findExistingFile(inputFile.getName(), inputFile.getParent(), fileFormat).orElseThrow(NotFoundException::new);
		return FileResponse.builder().file(outputFile).format(fileFormat).build();
	}
}
