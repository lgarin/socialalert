package com.bravson.socialalert.domain.location;

import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.GeoPointBinding;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Latitude;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Longitude;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

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
	@FullTextField(analyzer = "")
	private String formattedAddress;
	@FullTextField(analyzer = "")
	private String locality;
	@KeywordField
	private String country;
}
