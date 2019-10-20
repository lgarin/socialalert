package com.bravson.socialalert.infrastructure.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Embeddable
@NoArgsConstructor(access=AccessLevel.PUBLIC)
@Setter(AccessLevel.NONE)
public class VersionInfo {

	@Column(name = "user_id", length = FieldLength.ID, nullable = false)
	@KeywordField
	private String userId;
	
	@Column(name = "ip_address", length = FieldLength.IP_ADDRESS, nullable = false)
	@KeywordField
	private String ipAddress;
	
	@Column(name = "creation", nullable = false)
	@GenericField(sortable = Sortable.YES)
	private Instant creation;
	
	@Column(name = "last_update", nullable = false)
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
