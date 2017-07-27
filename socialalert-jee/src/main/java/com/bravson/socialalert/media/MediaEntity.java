package com.bravson.socialalert.media;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="Media")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class MediaEntity extends VersionedEntity {

	@Getter
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private FileEntity file;

	@Getter
	@Field
	private MediaType type;
	
	@Getter
	@Field
    private String title;
	
	@Getter
	@Field
	private String description;

	@Getter
	@Embedded
	private GeoAddress location;
	
	@Getter
	@Embedded
	private MediaStatistic statistic;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	private List<String> categories;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	private List<String> tags;

	public static MediaEntity of(FileEntity file, ClaimPictureParameter parameter, VersionInfo versionInfo) {
		MediaEntity entity = new MediaEntity();
		entity.versionInfo = versionInfo;
		entity.id = file.getId();
		entity.type = file.isVideo() ? MediaType.VIDEO : MediaType.PICTURE;
		entity.file = file;
		entity.title = parameter.getTitle();
		entity.description = parameter.getDescription();
		entity.statistic = new MediaStatistic();
		entity.location = parameter.getLocation();
		entity.categories = new ArrayList<>(parameter.getCategories());
		entity.tags = new ArrayList<>(parameter.getTags());
		return entity;
	}
	
	public MediaInfo toMediaInfo() {
		MediaInfo info = new MediaInfo();
		info.setMediaUri(file.getId());
		info.setType(type);
		info.setTitle(title);
		info.setDescription(description);
		info.setTags(new ArrayList<>(tags));
		info.setCategories(new ArrayList<>(categories));
		info.setCreation(versionInfo.getCreation());
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
		info.setCreatorId(file.getMediaFileMetadata().getUserId());
		info.setCameraMaker(file.getMediaMetadata().getCameraMaker());
		info.setCameraModel(file.getMediaMetadata().getCameraModel());
		info.setHeight(file.getMediaMetadata().getHeight());
		info.setWidth(file.getMediaMetadata().getWidth());
		info.setDuration(file.getMediaMetadata().getDuration());
		info.setTimestamp(file.getMediaFileMetadata().getTimestamp());
		return info;
	}
}
