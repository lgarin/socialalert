package com.bravson.socialalert.domain.user;

import com.bravson.socialalert.domain.user.privacy.UserPrivacy;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserDetail extends UserInfo {

	private UserPrivacy privacy;
}
