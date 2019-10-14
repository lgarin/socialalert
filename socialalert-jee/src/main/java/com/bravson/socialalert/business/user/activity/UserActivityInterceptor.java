package com.bravson.socialalert.business.user.activity;

import java.security.Principal;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@UserActivity
@Interceptor
@Priority(2)
public class UserActivityInterceptor {

	@Inject
	OnlineUserRepository onlineUserRepository;
	
	@Inject
	Principal principal;
	
	@AroundInvoke
    public Object updateUserActivity(InvocationContext invocationContext) throws Exception {
		if (principal != null) {
			onlineUserRepository.addActiveUser(principal.getName());
		}
		return invocationContext.proceed();
	}
}
