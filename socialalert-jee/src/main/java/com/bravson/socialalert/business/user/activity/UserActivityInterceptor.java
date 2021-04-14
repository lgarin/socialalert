package com.bravson.socialalert.business.user.activity;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.eclipse.microprofile.jwt.JsonWebToken;

import com.bravson.socialalert.business.user.session.UserSessionCache;

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
