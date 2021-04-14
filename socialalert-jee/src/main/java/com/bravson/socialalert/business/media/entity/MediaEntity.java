package com.bravson.socialalert.business.media.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.media.UpsertMediaParameter;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Entity(name="Media")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "file_id", name = "UK_Media_File"))
@Indexed(index = "Media")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaEntity extends VersionedEntity {
	
	public static final int MIN_GEOHASH_PRECISION = 1;
	public static final int MAX_GEOHASH_PRECISION = 8;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;

	@Getter
	@OneToOne(fetch=FetchType.EAGER, optional=false)
	@JoinColumn(name = "file_id", foreignKey = @ForeignKey(name = "FK_Media_File"))
	private FileEntity file;
	
	@NonNull
	@Getter
	@Column(name = "kind", nullable = false)
	@GenericField
	private MediaKind kind;
	
	@Getter
	@Column(name = "title", length = FieldLength.NAME)
	@FullTextField(analyzer="languageAnalyzer")
    private String title;

	@Getter
	@Embedded
	@IndexedEmbedded
	private GeoAddress location;
	
	@Getter
	@Column(name = "camera_maker", length = FieldLength.NAME)
	@KeywordField
	private String cameraMaker;
	
	@Getter
	@Column(name = "camera_model", length = FieldLength.NAME)
	@KeywordField
	private String cameraModel;
	
	@NonNull
	@Getter
	@Embedded
	@IndexedEmbedded
	private MediaStatistic statistic;
	
	@Getter
	@Column(name = "category", length = FieldLength.ID)
	@GenericField
	private String category;
	
	@Getter
	@Column(name = "feeling")
	@GenericField(aggregable = Aggregable.YES)
	private Integer feeling;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@CollectionTable(name = "MediaTag", joinColumns = @JoinColumn(name = "media_id", foreignKey = @ForeignKey(name = "FK_MediaTag_Media")))
	@Column(name = "tag", length = FieldLength.NAME, nullable = false)
	@FullTextField(analyzer="languageAnalyzer")
	private Set<String> tags;
	
	public MediaEntity(FileEntity file, UpsertMediaParameter parameter, UserAccess userAccess) {
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.file = file;
		this.id = file.getId();
		this.kind = file.isVideo() ? MediaKind.VIDEO : MediaKind.PICTURE;
		this.statistic = new MediaStatistic();
		if (file.getMediaMetadata() != null && file.getMediaMetadata().hasLocation()) {
			this.location = file.getMediaMetadata().getLocation();
		}
		if (file.getMediaMetadata() != null) {
			this.cameraMaker = file.getMediaMetadata().getCameraMaker();
			this.cameraModel = file.getMediaMetadata().getCameraModel();
		}
		setMetaInformation(parameter);
	}
	
	public MediaEntity(String mediaUri) {
		this.id = mediaUri;
	}
	
	private void setMetaInformation(UpsertMediaParameter parameter) {
		if (parameter.getTitle() != null) {
			this.title = parameter.getTitle();
		}
		if (parameter.getLocation() != null) {
			this.location = parameter.getLocation();
		}
		if (parameter.getCategory() != null) {
			this.category = parameter.getCategory();
		}
		if (parameter.getFeeling() != null) {
			this.feeling = parameter.getFeeling();
		}
		if (parameter.getTags() != null) {
			this.tags = new HashSet<>(parameter.getTags());
		}
		if (parameter.getCameraMaker() != null) {
			this.cameraMaker = parameter.getCameraMaker();
		}
		if (parameter.getCameraModel() != null) {
			this.cameraModel = parameter.getCameraModel();
		}
	}
	
	public void update(UpsertMediaParameter parameter, UserAccess userAccess) {
		setMetaInformation(parameter);
		versionInfo.touch(userAccess.getUserId(), userAccess.getIpAddress());
	}
	
	public MediaInfo toMediaInfo() {
		return fillMediaInfo(new MediaInfo());
	}
	
	public MediaDetail toMediaDetail() {
		return fillMediaInfo(new MediaDetail());
	}

	private <T extends MediaInfo> T fillMediaInfo(T info) {
		info.setMediaUri(getFile().getId());
		info.setKind(getKind());
		info.setTitle(getTitle());
		info.setTags(new ArrayList<>(getTags()));
		info.setCategory(getCategory());
		info.setFeeling(getFeeling());
		if (getLocation() != null) {
			info.setCountry(getLocation().getCountry());
			info.setLocality(getLocation().getLocality());
			info.setLongitude(getLocation().getLongitude());
			info.setLatitude(getLocation().getLatitude());
		}
		info.setHitCount(getStatistic().getHitCount());
		info.setLikeCount(getStatistic().getLikeCount());
		info.setDislikeCount(getStatistic().getDislikeCount());
		info.setCommentCount(getStatistic().getCommentCount());
		info.setCreatorId(getFile().getUserId());
		info.setTimestamp(getFile().getFileMetadata().getTimestamp());
		info.setCameraMaker(getCameraMaker());
		info.setCameraModel(getCameraModel());
		info.setWidth(getFile().getMediaMetadata().getWidth());
		info.setHeight(getFile().getMediaMetadata().getHeight());
		info.setDuration(getFile().getMediaMetadata().getDuration());
		info.setCreation(getFile().getMediaMetadata().getTimestamp());
		info.setFileFormat(getFile().getFileMetadata().getFileFormat());
		getFile().findVariantFormat(MediaSizeVariant.PREVIEW).ifPresent(info::setPreviewFormat);
		return info;
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}

	public void increaseHitCount() {
		statistic.increaseHitCount();
	}
}
