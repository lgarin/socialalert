package com.bravson.socialalert.infrastructure.log;

import java.io.Serializable;
import java.util.Arrays;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Interceptor
@Logged
@Transactional(TxType.SUPPORTS)
public class LoggedInterceptor implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {
		Logger logger = LoggerFactory.getLogger(invocationContext.getMethod().getDeclaringClass());
		logger.info("Calling method {} with {}", invocationContext.getMethod().getName(), Arrays.toString(invocationContext.getParameters()));
		try {
			Object result = invocationContext.proceed();
			logger.info("Returning from method {} with {}", invocationContext.getMethod().getName(), mapResult(result));
			return result;
		} catch (Exception e) {
			logger.error("Failed method {} with {}", invocationContext.getMethod().getName(), e);
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
