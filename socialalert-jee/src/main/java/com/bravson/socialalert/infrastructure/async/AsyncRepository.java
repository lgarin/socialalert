package com.bravson.socialalert.infrastructure.async;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Destination;
import javax.jms.JMSConnectionFactory;
import javax.jms.JMSContext;

import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
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
