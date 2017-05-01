package com.bravson.socialalert.infrastructure.async;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName  = "connectionFactoryJndiName", propertyValue = AsyncConstants.QUEUE_CONNECTION_FACTORY),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = AsyncConstants.ASYNC_PROCESSOR_QUEUE)
})
public class AsyncProcessor implements MessageListener {

	@Inject
	MessageDrivenContext context;
	
	@Override
	public void onMessage(Message message) {
		try {
			Runnable runnable = message.getBody(Runnable.class);
			runnable.run();
		} catch (JMSException e) {
			context.setRollbackOnly();
		}
		
	}

}
