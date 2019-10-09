package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;

import javax.persistence.Embeddable;

import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
@NoArgsConstructor(access=AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
@Indexed
public class VersionInfo {

	@KeywordField
	private String userId;
	
	@KeywordField
	private String ipAddress;
	
	@GenericField(sortable = Sortable.YES)
	private Instant creation;
	
	private Instant lastUpdate;
	
	public static VersionInfo of(String userId, String ipAddress) {
		VersionInfo result = new VersionInfo();
		result.touch(userId, ipAddress);
		return result;
	}
	
	public void touch(String userId, String ipAddress) {
		this.userId = userId;
		this.ipAddress = ipAddress;
		lastUpdate = Instant.now();
		if (creation == null) {
			this.creation = lastUpdate;
		}
	}
}
