package com.bravson.socialalert.domain.user;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class ChangePasswordParameter extends UserCredential {

	@NotEmpty
	private String newPassword;

	public ChangePasswordParameter(String username, String password, String newPassword) {
		super(username, password);
		this.newPassword = newPassword;
	}
}
