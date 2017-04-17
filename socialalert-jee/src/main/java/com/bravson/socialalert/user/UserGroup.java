package com.bravson.socialalert.user;

import javax.persistence.Id;

import lombok.Getter;

public class UserGroup {

	@Id
	private String id;
	
	@Getter
	private String name;
}
