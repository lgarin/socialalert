package com.bravson.socialalert.test.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.business.user.token.UserAccessToken;

@ExtendWith(MockitoExtension.class)
public abstract class BaseServiceTest extends Assertions {

	protected static UserAccess createUserAccess(String userId, String ipAddress) {
		return UserAccessToken.builder().userId(userId).ipAddress(ipAddress).username(userId).email(userId).build();
	}
}
