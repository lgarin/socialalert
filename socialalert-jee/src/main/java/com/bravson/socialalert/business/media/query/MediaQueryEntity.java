package com.bravson.socialalert.business.media.query;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.media.MediaQueryInfo;
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
	@Column(name = "category", length = FieldLength.NAME)
	private String category;
	
	@Getter
	@Column(name = "hit_threshold")
	private int hitThreshold;
	
	@Getter
	@Column(name = "last_hit_count")
	private Integer lastHitCount;
	
	@Getter
	@Column(name = "last_execution")
	private Instant lastExecution;
	
	public MediaQueryEntity(String userId, String label, GeoArea location, String keywords, String category,
			int hitThreshold) {
		this.id = userId; // TODO temporary
		this.userId = userId;
		this.label = label;
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.radius = location.getRadius();
		this.keywords = keywords;
		this.category = category;
		this.hitThreshold = hitThreshold;
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
	
}
