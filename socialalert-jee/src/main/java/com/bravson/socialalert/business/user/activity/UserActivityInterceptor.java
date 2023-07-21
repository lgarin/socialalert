package com.bravson.socialalert.business.user.activity;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.bravson.socialalert.business.user.session.UserSessionCache;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@UserActivity
@Interceptor
@Priority(2)
public class UserActivityInterceptor {

	@Inject
	UserSessionCache userSessionCache;
	
	@Inject
	JsonWebToken principal;
	
	@AroundInvoke
    public Object updateUserActivity(InvocationContext invocationContext) throws Exception {
		if (principal != null) {
			userSessionCache.addActiveUser(principal.getSubject());
		}
		return invocationContext.proceed();
	}
}
