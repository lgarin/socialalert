package com.bravson.socialalert.business.file.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.business.file.media.MediaMetadata;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileFormat;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="File")
@Indexed(index = "File")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class FileEntity extends VersionedEntity {
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;

	@Column(name = "state", nullable = false)
	@Getter
	@NonNull
	@KeywordField
	private FileState state;
	
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "FileVariant", joinColumns = @JoinColumn(name = "file_id", foreignKey = @ForeignKey(name = "FK_FileVariant_File")))
	@IndexedEmbedded
	private List<FileMetadata> fileVariants;
		
	@Getter
	@Embedded
	@IndexedEmbedded
	private MediaMetadata mediaMetadata;

	public FileEntity(@NonNull FileMetadata fileMetadata, @NonNull UserAccess userAccess) {
		if (fileMetadata.getSizeVariant() != MediaSizeVariant.MEDIA) {
			throw new IllegalArgumentException("Size variant must be " + MediaSizeVariant.MEDIA.getVariantName());
		}
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.id = fileMetadata.buildFileUri();
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
		return fileVariants.stream().filter(v -> v.getSizeVariant() == sizeVariant).max(Comparator.comparing(FileMetadata::isVideo));
	}
	
	public Optional<MediaFileFormat> findVariantFormat(@NonNull MediaSizeVariant sizeVariant) {
		return findFileMetadata(sizeVariant).map(FileMetadata::getFileFormat);
	}

	public boolean markProcessed(@NonNull MediaMetadata metadata) {
		if (state != FileState.UPLOADED) {
			return false;
		}
		this.mediaMetadata = metadata;
		changeState(FileState.PROCESSED);
		return true;
	}
	
	public void addVariant(@NonNull FileMetadata metadata) {
		if (fileVariants == null) {
			fileVariants = new ArrayList<>();
		}
		fileVariants.add(metadata);
	}
	
	public List<FileMetadata> getAllVariants() {
		if (fileVariants == null) {
			return Collections.emptyList();
		}
		return List.copyOf(fileVariants);
	}

	public FileMetadata getFileMetadata() {
		return findFileMetadata(MediaSizeVariant.MEDIA).orElseThrow(IllegalStateException::new);
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
		if (getMediaMetadata() != null) {
			info.setLatitude(getMediaMetadata().getLatitude());
			info.setLongitude(getMediaMetadata().getLongitude());
			info.setCameraMaker(getMediaMetadata().getCameraMaker());
			info.setCameraModel(getMediaMetadata().getCameraModel());
			info.setHeight(getMediaMetadata().getHeight());
			info.setWidth(getMediaMetadata().getWidth());
			info.setDuration(getMediaMetadata().getDuration());
			info.setCreation(getMediaMetadata().getTimestamp());
		}
		findVariantFormat(MediaSizeVariant.PREVIEW).ifPresent(info::setPreviewFormat);
		return info;
	}

	public boolean markClaimed(UserAccess userAccess) {
		if (state != FileState.PROCESSED) {
			return false;
		} else if (!getUserId().equals(userAccess.getUserId())) {
			return false;
		}
		changeState(FileState.CLAIMED);
		return true;
	}
	
	public boolean markUploaded(UserAccess userAccess) {
		if (!getUserId().equals(userAccess.getUserId())) {
			return false;
		} else if (state == FileState.CLAIMED) {
			return false;
		}
		changeState(FileState.UPLOADED);
		return true;
	}
	
	private void changeState(@NonNull FileState newState) {
		state = newState;
		versionInfo.touch(versionInfo.getUserId(), versionInfo.getIpAddress());
	}
	
	public boolean isUploaded() {
		return state == FileState.UPLOADED;
	}
	
	public boolean isProcessed() {
		return state == FileState.PROCESSED;
	}
	
	public boolean isTemporary() {
		return state != FileState.CLAIMED;
	}
}
