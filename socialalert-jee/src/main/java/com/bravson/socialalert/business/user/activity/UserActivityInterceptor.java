package com.bravson.socialalert.business.user.activity;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

@UserActivity
@Interceptor
@Priority(2)
public class UserActivityInterceptor {

	@Inject
	OnlineUserRepository onlineUserRepository;
	
	@Inject
	JsonWebToken principal;
	
	@AroundInvoke
    public Object updateUserActivity(InvocationContext invocationContext) throws Exception {
		if (principal != null) {
			onlineUserRepository.addActiveUser(principal.getSubject());
		}
		return invocationContext.proceed();
	}
}
