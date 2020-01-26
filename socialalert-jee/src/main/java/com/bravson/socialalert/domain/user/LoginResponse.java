package com.bravson.socialalert.domain.user;

import java.time.Instant;
import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.user.statistic.UserStatistic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends LoginTokenResponse {
	
	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@NonNull
	@Schema(description="The creation timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant createdTimestamp;
	
	private boolean online;
	
	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String imageUri;
	
	private String biography;
	
	private UserStatistic statistic;
}
