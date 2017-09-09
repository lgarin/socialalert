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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
	@ManyToOne(fetch=FetchType.LAZY, optional=false)
	private FileEntity file;

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

	public static MediaEntity of(FileEntity file, ClaimMediaParameter parameter, VersionInfo versionInfo) {
		MediaEntity entity = new MediaEntity();
		entity.versionInfo = versionInfo;
		entity.id = file.getId();
		entity.kind = file.isVideo() ? MediaKind.VIDEO : MediaKind.PICTURE;
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
}
