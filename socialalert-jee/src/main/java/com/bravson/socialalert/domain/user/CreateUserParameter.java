package com.bravson.socialalert.domain.user;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
