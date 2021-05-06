package com.bravson.socialalert.business.media.query;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.media.SearchMediaParameter;
import com.bravson.socialalert.domain.media.query.MediaQueryInfo;
import com.bravson.socialalert.domain.media.query.MediaQueryParameter;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionedEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name="MediaQuery")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MediaQueryEntity extends VersionedEntity {

	@Getter
	@Column(name = "label", length = FieldLength.NAME, nullable = false)
	private String label;
	
	@Getter
	@Column(name = "user_id", length = FieldLength.ID, nullable = false)
	private String userId;
	
	private double latitude;
	
	private double longitude;
	
	private double radius;
	
	@Getter
	@Column(name = "keywords", length = FieldLength.TEXT)
	private String keywords;
	
	@Getter
	@Column(name = "category", length = FieldLength.ID)
	private String category;
	
	@Getter
	@Column(name = "hit_threshold", nullable = false)
	private int hitThreshold;
	
	@Getter
	@Column(name = "last_hit_count")
	private Integer lastHitCount;
	
	@Getter
	@Column(name = "last_execution", nullable = false)
	private Instant lastExecution;
	
	public MediaQueryEntity(MediaQueryParameter param, String userId) {
		this.id = userId; // TODO temporary
		this.userId = userId;
		this.label = param.getLabel();
		this.latitude = param.getLatitude();
		this.longitude = param.getLongitude();
		this.radius = param.getRadius();
		this.keywords = param.getKeywords();
		this.category = param.getCategory();
		this.hitThreshold = param.getHitThreshold();
		this.lastExecution = Instant.now();
	}
	
	public GeoArea getLocation() {
		return GeoArea.builder().longitude(longitude).latitude(latitude).radius(radius).build();
	}
	
	public MediaQueryInfo toQueryInfo() {
		MediaQueryInfo info = new MediaQueryInfo();
		info.setId(id);
		info.setLabel(label);
		info.setCategory(category);
		info.setKeywords(keywords);
		info.setLocation(getLocation());
		info.setUserId(userId);
		info.setHitThreshold(hitThreshold);
		info.setLastHitCount(lastHitCount);
		if (lastHitCount != null) {
			info.setLastExecution(lastExecution);
		}
		return info;
	}
	
	public SearchMediaParameter toSearchParameter(Duration maxAge) {
		SearchMediaParameter parameter = new SearchMediaParameter();
		parameter.setCategory(category);
		parameter.setKeywords(keywords);
		parameter.setLocation(getLocation());
		parameter.setMaxAge(maxAge);
		return parameter;
	}
	
	public void updateLastHitCount(int hitCount) {
		lastHitCount = hitCount;
		lastExecution = Instant.now();
	}

	public void updateParameter(MediaQueryParameter parameter) {
		label = parameter.getLabel();
		category = parameter.getCategory();
		keywords = parameter.getKeywords();
		latitude = parameter.getLatitude();
		longitude = parameter.getLongitude();
		radius = parameter.getRadius();
		hitThreshold = parameter.getHitThreshold();
		lastExecution = Instant.now();
		lastHitCount = null;
	}
	
}
