package com.bravson.socialalert.test.repository;

import javax.annotation.ManagedBean;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import com.bravson.socialalert.business.user.UserAccess;

@ManagedBean
@Alternative
@Priority(value = 99)
public class DummyUserAccessProducer {

	@Produces
	public UserAccess createUserAccess() {
		return UserAccess.of("test", "1.2.3.4");
	}
}
