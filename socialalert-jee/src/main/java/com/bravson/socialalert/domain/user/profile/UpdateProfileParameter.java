package com.bravson.socialalert.domain.user.profile;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.bravson.socialalert.domain.user.Gender;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileParameter {
	
	@Size(max=FieldLength.NAME)
	private String firstname;
	
	@Size(max=FieldLength.NAME)
	private String lastname;

	private LocalDate birthdate;
	
	private Gender gender;
	
	@Size(max=FieldLength.ISO_CODE)
	private String country;
	
	@Size(max=FieldLength.ISO_CODE)
	private String language;
	
	@Size(max=FieldLength.TEXT)
	private String biography;
}
