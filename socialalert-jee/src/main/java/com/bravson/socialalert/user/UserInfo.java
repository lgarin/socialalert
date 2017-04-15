package com.bravson.socialalert.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Getter;

public class UserInfo {

	private String id;
	
	private List<String> groupIds;
	
	@Getter
	private String firstName;
	
	@Getter
	private String lastName;
	
	@Getter
	private String email;
	
	private Long createdTimestamp;
	
	private List<String> groupNames;
	
	private UserAttributes attributes;

	@JsonIgnore
	public List<String> getGroupIds() {
		return Collections.unmodifiableList(groupIds);
	}
	
	@JsonUnwrapped
	public UserAttributes getAttributes() {
		if (attributes == null) {
			attributes = new UserAttributes();
		}
		return attributes;
	}

	@JsonSerialize(using = InstantSerializer.class)
	public Instant getCreatedTimestamp() {
		if (createdTimestamp == null) {
			return null;
		}
		return Instant.ofEpochMilli(createdTimestamp);
	}

	public List<String> getGroupNames() {
		if (groupNames == null) {
			groupNames = new ArrayList<String>();
		}
		return groupNames;
	}
}
