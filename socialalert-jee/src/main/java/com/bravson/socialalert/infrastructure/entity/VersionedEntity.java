package com.bravson.socialalert.infrastructure.entity;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

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
	@GenericField
	@Getter
	@NonNull
	protected String id;
	
	@Version
	protected Integer version;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	public VersionInfo versionInfo; // TODO should be protected
}
