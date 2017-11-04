package com.bravson.socialalert.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Transient;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.DynamicBoost;
import org.hibernate.search.annotations.Facet;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import com.bravson.socialalert.domain.location.GeoAddress;
import com.bravson.socialalert.domain.location.GeoHashHelper;
import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;
import com.bravson.socialalert.media.approval.MediaApprovalEntity;
import com.bravson.socialalert.media.comment.MediaCommentEntity;
import com.bravson.socialalert.user.UserAccess;
import com.bravson.socialalert.user.profile.ProfileEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="Media")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@DynamicBoost(impl=MediaBoostStrategy.class)
public class MediaEntity extends VersionedEntity {

	@Getter
	@Setter
	@OneToOne(fetch=FetchType.LAZY, optional=false)
	private FileEntity file;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private ProfileEntity userProfile;
	
	@Getter
	@Setter
	@OneToMany(fetch=FetchType.LAZY, mappedBy="media")
	private Set<MediaApprovalEntity> approvalSet;
	
	@Getter
	@Setter
	@OneToMany(fetch=FetchType.LAZY, mappedBy="media")
	private Set<MediaCommentEntity> commentSet;
	
	@Getter
	@Field
	private MediaKind kind;
	
	@Getter
	@Field
	@Analyzer(definition="languageAnalyzer")
    private String title;
	
	@Getter
	@Field
	@Analyzer(definition="languageAnalyzer")
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
	@IndexedEmbedded
	@Fields({@Field, @Field(name="categories_facet", analyze=Analyze.NO)})
	@Facet(forField="categories_facet")
	private List<String> categories;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	@Field
	@Analyzer(definition="languageAnalyzer")
	private List<String> tags;
	
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash1;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash2;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash3;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash4;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash5;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash6;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash7;
	@Transient
	@Field(analyze=Analyze.NO)
	@Facet
	private String geoHash8;
	
	@PrePersist
	private void updateGeoHashes() {
		if (location != null && location.getLatitude() != null && location.getLongitude() != null) {
			geoHash1 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 1);
			geoHash2 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 2);
			geoHash3 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 3);
			geoHash4 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 4);
			geoHash5 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 5);
			geoHash6 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 6);
			geoHash7 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 7);
			geoHash8 = GeoHashHelper.computeGeoHash(location.getLatitude(), location.getLongitude(), 8);
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
	
	public MediaEntity(String fileUri, MediaKind kind, UpsertMediaParameter parameter, UserAccess userAccess) {
		this.versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
		this.id = fileUri;
		this.kind = kind;
		this.statistic = new MediaStatistic();
		setMetaInformation(parameter);
	}
	
	public MediaEntity(String mediaUri) {
		this.id = mediaUri;
	}
	
	private void setMetaInformation(UpsertMediaParameter parameter) {
		this.title = parameter.getTitle();
		this.description = parameter.getDescription();
		this.location = parameter.getLocation();
		this.categories = new ArrayList<>(parameter.getCategories());
		this.tags = new ArrayList<>(parameter.getTags());
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
		info.setHeight(getFile().getMediaMetadata().getHeight());
		info.setWidth(getFile().getMediaMetadata().getWidth());
		info.setDuration(getFile().getMediaMetadata().getDuration());
		info.setCreation(getFile().getMediaMetadata().getTimestamp());
		return info;
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}

	public void increaseHitCount() {
		statistic.increaseHitCount();
	}
}
