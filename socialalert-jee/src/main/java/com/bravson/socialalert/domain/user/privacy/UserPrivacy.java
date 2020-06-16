package com.bravson.socialalert.domain.user.privacy;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Embeddable
public class UserPrivacy {

	@Column(name = "name_masked", nullable = false)
	private boolean nameMasked;
	
	@Column(name = "birthdate_masked", nullable = false)
	private boolean birthdateMasked;
	
	@Column(name = "location_privacy")
	private LocationPrivacy location;
}
