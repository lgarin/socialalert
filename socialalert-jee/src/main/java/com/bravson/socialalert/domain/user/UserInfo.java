package com.bravson.socialalert.domain.user;

import java.time.Instant;
import java.time.LocalDate;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.bravson.socialalert.domain.user.privacy.LocationPrivacy;
import com.bravson.socialalert.domain.user.privacy.UserPrivacy;
import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.With;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@With
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
	
	private String firstname;
	
	private String lastname;
	
	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String imageUri;
	
	private String biography;
	
	private UserStatistic statistic;
	
	private UserPrivacy creatorPrivacy;
	
	@Schema(description="The link creation timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant followedSince;

	@JsonIgnore
	private UserPrivacy getCreatorPrivacy() {
		return creatorPrivacy;
	}
	
	public LocationPrivacy getLocationPrivacy() {
		return creatorPrivacy != null ? creatorPrivacy.getLocation() : null;
	}
	
	public boolean hasFeelingPrivacy() {
		return creatorPrivacy == null || creatorPrivacy.isFeelingMasked();
	}
}
