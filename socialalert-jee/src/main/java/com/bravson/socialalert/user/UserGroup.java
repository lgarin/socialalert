package com.bravson.socialalert.user;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Getter;

@Entity("groups")
public class UserGroup {

	@Id
	private String id;
	
	@Getter
	private String name;
}
