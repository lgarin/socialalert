package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class VersionInfo {

	@Field
	private String userId;
	
	@Field
	private String ipAddress;
	
	@Convert(converter=InstantAttributeConverter.class)
	private Instant creation;
	
	@Convert(converter=InstantAttributeConverter.class)
	private Instant lastUpdate;
	
	public static VersionInfo of(String userId, String ipAddress) {
		VersionInfo result = new VersionInfo();
		result.touch(userId, ipAddress);
		return result;
	}
	
	private void touch(String userId, String ipAddress) {
		this.userId = userId;
		this.ipAddress = ipAddress;
		lastUpdate = Instant.now();
		if (creation == null) {
			this.creation = lastUpdate;
		}
	}
}
