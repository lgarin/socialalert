package com.bravson.socialalert.business.file;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.business.file.media.MediaFileFormat;
import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.file.media.MediaSizeVariant;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="File")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileEntity extends VersionedEntity {

	@Getter
	@NonNull
	@Field
	private FileState state;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	private List<FileMetadata> fileVariants;
		
	@Getter
	@NonNull
	@Embedded
	@IndexedEmbedded
	private MediaMetadata mediaMetadata;

	public FileEntity(@NonNull FileMetadata fileMetadata, @NonNull MediaMetadata mediaMetadata, @NonNull UserAccess userAccess) {
		if (fileMetadata.getSizeVariant() != MediaSizeVariant.MEDIA) {
			throw new IllegalArgumentException("Size variant must be " + MediaSizeVariant.MEDIA.getVariantName());
		}
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.id = fileMetadata.buildFileUri();
		this.mediaMetadata = mediaMetadata;
		this.state = FileState.UPLOADED;
		addVariant(fileMetadata);
	}
	
	public FileEntity(@NonNull String id) {
		this.id = id;
	}
	
	private Optional<FileMetadata> findFileMetadata(MediaSizeVariant sizeVariant) {
		if (fileVariants == null) {
			return Optional.empty();
		}
		return fileVariants.stream().filter(v -> v.getSizeVariant() == sizeVariant).findAny();
	}
	
	public Optional<MediaFileFormat> findVariantFormat(@NonNull MediaSizeVariant sizeVariant) {
		return findFileMetadata(sizeVariant).map(FileMetadata::getFileFormat);
	}

	public boolean addVariant(@NonNull FileMetadata metadata) {
		if (findFileMetadata(metadata.getSizeVariant()).isPresent()) {
			return false;
		}
		if (fileVariants == null) {
			fileVariants = new ArrayList<>();
		}
		fileVariants.add(metadata);
		return true;
	}

	public FileMetadata getFileMetadata() {
		return findFileMetadata(MediaSizeVariant.MEDIA).orElseThrow(IllegalStateException::new);
	}

	public boolean isTemporary(MediaFileFormat format) {
		return getFileMetadata().isVideo() && format == MediaFileFormat.PREVIEW_JPG;
	}

	public boolean isVideo() {
		return getFileMetadata().isVideo();
	}
	
	public boolean isPicture() {
		return getFileMetadata().isPicture();
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
	
	public FileInfo toFileInfo() {
		FileInfo info = new FileInfo();
		info.setFileUri(getId());
		info.setFileFormat(getFileMetadata().getFileFormat());
		info.setContentSize(getFileMetadata().getContentSize());
		info.setCreatorId(getUserId());
		info.setTimestamp(getFileMetadata().getTimestamp());
		info.setCameraMaker(getMediaMetadata().getCameraMaker());
		info.setCameraModel(getMediaMetadata().getCameraModel());
		info.setHeight(getMediaMetadata().getHeight());
		info.setWidth(getMediaMetadata().getWidth());
		info.setDuration(getMediaMetadata().getDuration());
		info.setCreation(getMediaMetadata().getTimestamp());
		return info;
	}

	public void markClaimed() {
		state = FileState.CLAIMED;
		versionInfo.touch(versionInfo.getUserId(), versionInfo.getIpAddress());
	}
	
	public void markDeleted() {
		state = FileState.DELETED;
		versionInfo.touch(versionInfo.getUserId(), versionInfo.getIpAddress());
	}
}
