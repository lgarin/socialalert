package com.bravson.socialalert.user.activity;

import java.security.Principal;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@UserActivity
@Interceptor
public class UserActivityInterceptor {

	@Inject
	SessionRepository sessionRepository;
	
	@Inject
	Principal principal;
	
	@AroundInvoke
    public Object updateUserActivity(InvocationContext invocationContext) throws Exception {
		if (principal != null) {
			sessionRepository.addActiveUser(principal.getName());
		}
		return invocationContext.proceed();
	}
}
