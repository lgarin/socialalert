package com.bravson.socialalert.infrastructure.async;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName  = "connectionFactoryJndiName", propertyValue = AsyncConstants.QUEUE_CONNECTION_FACTORY),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), 
        @ActivationConfigProperty(propertyName = "destination", propertyValue = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
})
public class AsyncProcessor implements MessageListener {

	@Resource
	MessageDrivenContext context;
	
	@Inject 
	@Any Event<AsyncEvent> eventTrigger;
	
	@Override
	public void onMessage(Message message) {
		try {
			AsyncEvent event = message.getBody(AsyncEvent.class);
			eventTrigger.fire(event);
		} catch (JMSException e) {
			context.setRollbackOnly();
		}
		
	}

}
