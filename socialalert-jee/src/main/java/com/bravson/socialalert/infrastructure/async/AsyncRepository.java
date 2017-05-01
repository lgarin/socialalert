package com.bravson.socialalert.infrastructure.async;

import java.io.Serializable;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.transaction.Transactional;

@ManagedBean
@Transactional
@ApplicationScoped
public class AsyncRepository {

	@Inject
	@JMSConnectionFactory(AsyncConstants.QUEUE_CONNECTION_FACTORY)
    JMSContext context;

    @Resource(lookup = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
    Destination destination;
    
    public AsyncRepository() {
	}
    
    public AsyncRepository(JMSContext context, Destination destination) {
		this.context = context;
		this.destination = destination;
	}

	public <T extends Serializable & Runnable> void addAsyncProcessing(T processing) {
    	context.createProducer().send(destination, processing);
    }
}
