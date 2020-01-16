package com.bravson.socialalert.domain.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserParameter {

	@NonNull
	private String username;
	
	@NonNull
	private String email;
	
	@NonNull
	private String password;
	
	private String firstName;
	
	private String lastName;
	
}
