package com.bravson.socialalert.infrastructure.log;

import java.io.Serializable;
import java.util.Arrays;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@Priority(1)
@Logged
public class LoggedInterceptor implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		Logger logger = LoggerFactory.getLogger(invocationContext.getMethod().getDeclaringClass());
		logger.info("Calling method {}.{} with {}", invocationContext.getMethod().getDeclaringClass().getSimpleName(), invocationContext.getMethod().getName(), Arrays.toString(invocationContext.getParameters()));
		try {
			Object result = invocationContext.proceed();
			if (invocationContext.getMethod().getReturnType() == Void.TYPE) {
				logger.info("Returning from method {}.{}", invocationContext.getMethod().getDeclaringClass().getSimpleName(), invocationContext.getMethod().getName());
			} else {
				logger.info("Returning from method {}.{} with {}", invocationContext.getMethod().getDeclaringClass().getSimpleName(), invocationContext.getMethod().getName(), mapResult(result));
			}
			return result;
		} catch (Exception e) {
			logger.error("Failed method {}.{} with {}", invocationContext.getMethod().getDeclaringClass().getSimpleName(), invocationContext.getMethod().getName(), e);
			throw e;
		}
    }

	private static Object mapResult(Object result) {
		if (result instanceof Response) {
			Response response = (Response) result;
			return response.getStatusInfo();
		}
		return result;
	}
}
