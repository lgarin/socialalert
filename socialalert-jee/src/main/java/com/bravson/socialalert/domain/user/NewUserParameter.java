package com.bravson.socialalert.domain.user;

import javax.validation.constraints.Size;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

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
	@Size(max=FieldLength.NAME, min=1)
	private String username;
	
	@NonNull
	@Size(max=FieldLength.NAME, min=1)
	private String email;
	
	@NonNull
	private String password;
	
	@Size(max=FieldLength.NAME)
	private String firstName;
	
	@Size(max=FieldLength.NAME)
	private String lastName;
}
