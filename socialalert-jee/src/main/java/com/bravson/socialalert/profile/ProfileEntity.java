package com.bravson.socialalert.profile;

import java.time.Instant;
import java.time.LocalDate;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.annotations.Indexed;

import com.bravson.socialalert.infrastructure.entity.InstantAttributeConverter;
import com.bravson.socialalert.infrastructure.entity.LocalDateAttributeConverter;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity(name="UserProfile")
@ToString(of="userId")
@EqualsAndHashCode(of="userId")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@RequiredArgsConstructor(staticName="of")
@Indexed
@Getter
@Setter
public class ProfileEntity {

	@Setter(AccessLevel.NONE)
	@NonNull
	@Id
	private String userId;
	
	@NonNull
	private String username;
	
	@NonNull
	@Convert(converter=InstantAttributeConverter.class)
	private Instant createdTimestamp;
	
	@Convert(converter=LocalDateAttributeConverter.class)
	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String imageUri;
	
	private String biography;
}
