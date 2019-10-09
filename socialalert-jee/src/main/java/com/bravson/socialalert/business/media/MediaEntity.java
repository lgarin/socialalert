package com.bravson.socialalert.business.media;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import com.bravson.socialalert.business.file.entity.FileEntity;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.media.MediaKind;
import com.bravson.socialalert.domain.media.format.MediaSizeVariant;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;
import com.bravson.socialalert.infrastructure.util.GeoHashUtil;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="Media")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaEntity extends VersionedEntity {
	
	public static final int MIN_GEOHASH_PRECISION = 1;
	public static final int MAX_GEOHASH_PRECISION = 8;

	@Getter
	@OneToOne(fetch=FetchType.LAZY, optional=false)
	private FileEntity file;
	
	@Getter
	@GenericField
	private MediaKind kind;
	
	@Getter
	@FullTextField(analyzer="languageAnalyzer")
    private String title;
	
	@Getter
	@FullTextField(analyzer="languageAnalyzer")
	private String description;

	@Getter
	@Embedded
	@IndexedEmbedded
	private GeoAddress location;
	
	@Getter
	@Embedded
	@IndexedEmbedded
	private MediaStatistic statistic;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@GenericField
	private List<String> categories;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@FullTextField(analyzer="languageAnalyzer")
	private List<String> tags;
	
	@Transient
	@GenericField
	private String geoHash1;
	@Transient
	@GenericField
	private String geoHash2;
	@Transient
	@GenericField
	private String geoHash3;
	@Transient
	@GenericField
	private String geoHash4;
	@Transient
	@GenericField
	private String geoHash5;
	@Transient
	@GenericField
	private String geoHash6;
	@Transient
	@GenericField
	private String geoHash7;
	@Transient
	@GenericField
	private String geoHash8;
	
	@PrePersist
	@PreUpdate
	private void updateGeoHashes() {
		if (location != null && location.getLatitude() != null && location.getLongitude() != null) {
			geoHash1 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 1);
			geoHash2 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 2);
			geoHash3 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 3);
			geoHash4 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 4);
			geoHash5 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 5);
			geoHash6 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 6);
			geoHash7 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 7);
			geoHash8 = GeoHashUtil.computeGeoHash(location.getLatitude(), location.getLongitude(), 8);
		} else {
			geoHash1 = null;
			geoHash2 = null;
			geoHash3 = null;
			geoHash4 = null;
			geoHash5 = null;
			geoHash6 = null;
			geoHash7 = null;
			geoHash8 = null;
		}
	}
	
	public MediaEntity(FileEntity file, UpsertMediaParameter parameter, UserAccess userAccess) {
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.file = file;
		this.id = file.getId();
		this.kind = file.isVideo() ? MediaKind.VIDEO : MediaKind.PICTURE;
		this.statistic = new MediaStatistic();
		setMetaInformation(parameter);
	}
	
	public MediaEntity(String mediaUri) {
		this.id = mediaUri;
	}
	
	private void setMetaInformation(UpsertMediaParameter parameter) {
		if (parameter.getTitle() != null) {
			this.title = parameter.getTitle();
		}
		if (parameter.getDescription() != null) {
			this.description = parameter.getDescription();
		}
		if (parameter.getLocation() != null) {
			this.location = parameter.getLocation();
		}
		if (parameter.getCategories() != null) {
			this.categories = new ArrayList<>(parameter.getCategories());
		}
		if (parameter.getTags() != null) {
			this.tags = new ArrayList<>(parameter.getTags());
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
		info.setKind(kind);
		info.setTitle(title);
		info.setDescription(description);
		info.setTags(new ArrayList<>(tags));
		info.setCategories(new ArrayList<>(categories));
		if (location != null) {
			info.setCountry(location.getCountry());
			info.setLocality(location.getLocality());
			info.setLongitude(location.getLongitude());
			info.setLatitude(location.getLatitude());
		}
		info.setHitCount(statistic.getHitCount());
		info.setLikeCount(statistic.getLikeCount());
		info.setDislikeCount(statistic.getDislikeCount());
		info.setCommentCount(statistic.getCommentCount());
		info.setCreatorId(getFile().getUserId());
		info.setTimestamp(getFile().getFileMetadata().getTimestamp());
		info.setCameraMaker(getFile().getMediaMetadata().getCameraMaker());
		info.setCameraModel(getFile().getMediaMetadata().getCameraModel());
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
