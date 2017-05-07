package com.bravson.socialalert.infrastructure.async;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.transaction.Transactional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@ManagedBean
@Transactional
@ApplicationScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class AsyncRepository {

	@Inject
	@JMSConnectionFactory(AsyncConstants.QUEUE_CONNECTION_FACTORY)
    private JMSContext context;

    @Resource(mappedName = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
    private Destination destination;
    
    public AsyncRepository(JMSContext context, Destination destination) {
		this.context = context;
		this.destination = destination;
	}

	public void fireAsync(AsyncEvent event) {
		context.createProducer().send(destination, event);
    }
}
