package com.bravson.socialalert.domain.location;

import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.GeoPointBinding;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Latitude;
import org.hibernate.search.mapper.pojo.bridge.builtin.annotation.Longitude;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ObjectPath;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.PropertyValue;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
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
@NoArgsConstructor(access=AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
@GeoPointBinding(fieldName = "position", projectable = Projectable.YES)
public class GeoAddress {

	@Latitude
	private Double latitude;
	@Longitude
	private Double longitude;
	@Column(name = "address", length = FieldLength.TEXT)
	@FullTextField(analyzer = "standard")
	private String formattedAddress;
	@Column(name = "locality", length = FieldLength.NAME)
	@FullTextField(analyzer = "standard")
	private String locality;
	@Column(name = "country", length = FieldLength.ISO_CODE)
	@KeywordField(aggregable = Aggregable.YES)
	private String country;
	
	@KeywordField(aggregable = Aggregable.YES)
	@Transient
	@IndexingDependency(derivedFrom = {
			@ObjectPath(@PropertyValue(propertyName = "locality")),
			@ObjectPath(@PropertyValue(propertyName = "country"))
	})
	public String getFullLocality() {
		if (locality == null || country == null) {
			return null;
		}
		return locality + " [" + country + "]";
	}
}
