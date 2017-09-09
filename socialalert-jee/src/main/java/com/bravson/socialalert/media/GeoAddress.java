package com.bravson.socialalert.media;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Latitude;
import org.hibernate.search.annotations.Longitude;
import org.hibernate.search.annotations.Spatial;
import org.hibernate.search.annotations.SpatialMode;

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
@Spatial(name="coordinates", spatialMode=SpatialMode.HASH)
public class GeoAddress {

	@Latitude(of="coordinates")
	private Double latitude;
	@Longitude(of="coordinates")
	private Double longitude;
	@Field
	private String formattedAddress;
	@Field
	private String locality;
	@Field
	private String country;
}
