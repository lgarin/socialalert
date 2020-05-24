package com.bravson.socialalert.domain.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserParameter {

	@NotBlank
	@Pattern(regexp = "^[a-zA-Z0-9]+$")
	private String username;
	
	@NotBlank
	@Email
	private String email;
	
	@NotBlank
	private String password;
	
	@Size(max=FieldLength.NAME)
	private String firstName;
	
	@Size(max=FieldLength.NAME)
	private String lastName;
}
