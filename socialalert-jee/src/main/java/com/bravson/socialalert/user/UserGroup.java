package com.bravson.socialalert.user;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("groups")
public class UserGroup {

	@Id
	private String id;
	
	public String name;
}
