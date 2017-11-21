package com.bravson.socialalert.domain.user;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Data
public class LoginParameter {
	@NotEmpty
	private final String username;
	
	@NotEmpty
	private final String password;
}
