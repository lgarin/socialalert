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

import org.slf4j.Logger;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName  = "connectionFactoryLookup", propertyValue = AsyncConstants.QUEUE_CONNECTION_FACTORY),
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"), 
        @ActivationConfigProperty(propertyName = "destination", propertyValue = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
})
public class AsyncProcessor implements MessageListener {

	@Resource
	MessageDrivenContext context;
	
	@Inject 
	@Any Event<AsyncEvent> eventTrigger;
	
	@Inject
	Logger logger;
	
	@Override
	public void onMessage(Message message) {
		String messageId = getMessageId(message);
		try {
			logger.info("Processing message {}", messageId);
			AsyncEvent event = message.getBody(AsyncEvent.class);
			eventTrigger.fire(event);
		} catch (Exception e) {
			context.setRollbackOnly();
			logger.error("Failed async processing for message " + messageId, e);
		}
		
	}

	private static String getMessageId(Message message) {
		try {
			return message.getJMSMessageID();
		} catch (JMSException e1) {
			return "<unknown>";
		}
	}

}
