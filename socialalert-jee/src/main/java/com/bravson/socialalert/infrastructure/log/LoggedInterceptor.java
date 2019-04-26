package com.bravson.socialalert.infrastructure.log;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InterceptionType;
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
public class LoggedInterceptor implements javax.enterprise.inject.spi.Interceptor<Object>, Serializable {
	
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

	@Override
	public Class<?> getBeanClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isNullable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object create(CreationalContext<Object> creationalContext) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void destroy(Object instance, CreationalContext<Object> creationalContext) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Type> getTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isAlternative() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<Annotation> getInterceptorBindings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean intercepts(InterceptionType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object intercept(InterceptionType type, Object instance, InvocationContext ctx) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
