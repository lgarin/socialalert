package com.bravson.socialalert.domain.user;

import java.time.Instant;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class LinkedUserInfo extends UserInfo {

	@Schema(description="The link creation timestamp in milliseconds since the epoch.", implementation=Long.class)
	private Instant followedSince;
	
	public LinkedUserInfo(UserInfo user, Instant followedSince) {
		setId(user.getId());
		setUsername(user.getUsername());
		setEmail(user.getEmail());
		setCreatedTimestamp(user.getCreatedTimestamp());
		setOnline(user.isOnline());
		setFirstname(user.getFirstname());
		setLastname(user.getLastname());
		setBirthdate(user.getBirthdate());
		setGender(user.getGender());
		setCountry(user.getCountry());
		setLanguage(user.getLanguage());
		setImageUri(user.getImageUri());
		setBiography(user.getBiography());
		setStatistic(user.getStatistic());
		setCreatorPrivacy(user.getCreatorPrivacy());
		setFollowedSince(followedSince);
	}
}
