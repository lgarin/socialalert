package com.bravson.socialalert.domain.user.profile;

import java.time.LocalDate;

import com.bravson.socialalert.domain.user.Gender;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProfileParameter {

	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String biography;
}
