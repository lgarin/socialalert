package com.bravson.socialalert.business.media.query;

import java.time.Duration;
import java.time.Instant;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.bravson.socialalert.business.media.SearchMediaParameter;
import com.bravson.socialalert.domain.location.GeoArea;
import com.bravson.socialalert.domain.media.MediaQueryInfo;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="MediaQuery")
@EqualsAndHashCode(of="id")
@ToString(of="id")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MediaQueryEntity {

	@Getter
	@Id
	@Column(name = "id", length = FieldLength.ID)
	private String id;
	
	@Getter
	@Column(name = "label", length = FieldLength.NAME, nullable = false)
	private String label;
	
	@Getter
	@Column(name = "user_id", length = FieldLength.ID, nullable = false)
	private String userId;
	
	@Getter
	@Embedded
	private GeoArea location;
	
	@Getter
	@Column(name = "keywords", length = FieldLength.TEXT)
	private String keywords;
	
	@Getter
	@Column(name = "category", length = FieldLength.NAME)
	private String category;
	
	@Getter
	@Column(name = "hit_threshold", nullable = false)
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
		this.location = location;
		this.keywords = keywords;
		this.category = category;
		this.hitThreshold = hitThreshold;
		this.lastExecution = Instant.now();
	}
	
	public MediaQueryInfo toQueryInfo() {
		MediaQueryInfo info = new MediaQueryInfo();
		info.setId(id);
		info.setLabel(label);
		info.setCategory(category);
		info.setKeywords(keywords);
		info.setLocation(location);
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
		parameter.setLocation(location);
		parameter.setMaxAge(maxAge);
		return parameter;
	}
	
	public void updateLastHitCount(int hitCount) {
		lastHitCount = hitCount;
		lastExecution = Instant.now();
	}
	
}
