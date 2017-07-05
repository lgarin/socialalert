package com.bravson.socialalert.infrastructure.entity;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
@MappedSuperclass
public abstract class VersionedEntity {

	@Id
	@Getter
	@NonNull
	protected String id;
	
	@Version
	private int version;
	
	@NonNull
	@Embedded
	protected VersionInfo versionInfo;
}
