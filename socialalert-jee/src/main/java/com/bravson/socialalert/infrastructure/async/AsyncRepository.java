package com.bravson.socialalert.infrastructure.async;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;
import javax.transaction.Transactional;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@ManagedBean
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class AsyncRepository {

	@Inject
	@JMSConnectionFactory(AsyncConstants.QUEUE_CONNECTION_FACTORY)
    JMSContext context;

    @Resource(mappedName = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
    Destination destination;
    
	public void fireAsync(AsyncEvent event) {
		context.createProducer().send(destination, event);
    }
}
