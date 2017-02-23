package com.bravson.socialalert.user;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Entity("users")
public class UserInfo {

	@Id
	private String id;
	private List<String> groupIds;
	
	private String firstName;
	private String lastName;
	private String email;
	private Long createdTimestamp;
	private List<String> groupNames;
	
	@Embedded
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

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
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
