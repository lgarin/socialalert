package com.bravson.socialalert.domain.location;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.GeoPointBinding;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Latitude;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Longitude;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@Embeddable
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
@Indexed
@GeoPointBinding(fieldName = "position")
public class GeoAddress {

	@Latitude
	private Double latitude;
	@Longitude
	private Double longitude;
	@Column(name = "address", length = FieldLength.TEXT)
	@FullTextField(analyzer = "")
	private String formattedAddress;
	@Column(name = "locality", length = FieldLength.NAME)
	@FullTextField(analyzer = "")
	private String locality;
	@Column(name = "country", length = FieldLength.ISO_CODE)
	@KeywordField
	private String country;
}
