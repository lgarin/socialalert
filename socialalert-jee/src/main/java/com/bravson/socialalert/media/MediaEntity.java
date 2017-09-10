package com.bravson.socialalert.media;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import com.bravson.socialalert.file.FileEntity;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;
import com.bravson.socialalert.user.profile.ProfileEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="Media")
@Indexed
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AnalyzerDef(name = "languageAnalyzer",
tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
filters = {
  @TokenFilterDef(factory = LowerCaseFilterFactory.class),
  @TokenFilterDef(factory = SnowballPorterFilterFactory.class)
})
public class MediaEntity extends VersionedEntity {

	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private FileEntity file;
	
	@Getter
	@Setter
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private ProfileEntity userProfile;

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
	@Field
	private List<String> categories;
	
	@Getter
	@ElementCollection(fetch=FetchType.EAGER)
	@IndexedEmbedded
	@Field
	@Analyzer(definition="languageAnalyzer")
	private List<String> tags;

	public MediaEntity(String fileUri, MediaKind kind, ClaimMediaParameter parameter, VersionInfo versionInfo) {
		this.versionInfo = versionInfo;
		this.id = fileUri;
		this.kind = kind;
		this.title = parameter.getTitle();
		this.description = parameter.getDescription();
		this.statistic = new MediaStatistic();
		this.location = parameter.getLocation();
		this.categories = new ArrayList<>(parameter.getCategories());
		this.tags = new ArrayList<>(parameter.getTags());
	}
	
	public MediaInfo toMediaInfo() {
		MediaInfo info = new MediaInfo();
		info.setMediaUri(file.getId());
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
		info.setCreatorId(file.getFileMetadata().getUserId());
		info.setTimestamp(file.getFileMetadata().getTimestamp());
		info.setCameraMaker(file.getMediaMetadata().getCameraMaker());
		info.setCameraModel(file.getMediaMetadata().getCameraModel());
		info.setHeight(file.getMediaMetadata().getHeight());
		info.setWidth(file.getMediaMetadata().getWidth());
		info.setDuration(file.getMediaMetadata().getDuration());
		info.setCreation(file.getMediaMetadata().getTimestamp());
		return info;
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
}
