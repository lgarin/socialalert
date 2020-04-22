package com.bravson.socialalert.domain.user;

import java.time.Instant;
import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.user.statistic.UserStatistic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {

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
	
	private Boolean followed;
}
