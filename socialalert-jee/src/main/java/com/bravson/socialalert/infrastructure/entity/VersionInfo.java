package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;

import javax.persistence.Convert;
import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class VersionInfo {

	@NonNull
	@Convert(converter=InstantAttributeConverter.class)
	@Field
	private Instant creation;
	
	@NonNull
	@Convert(converter=InstantAttributeConverter.class)
	@Field
	private Instant lastUpdate;
	
	@Field
	private String userId;
	
	@Field
	private String ipAddress;
	
	public VersionInfo(String userId, String ipAddress) {
		touch(userId, ipAddress);
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
