package com.bravson.socialalert.test.repository;

import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Queue;

import org.apache.activemq.artemis.api.core.TransportConfiguration;
import org.apache.activemq.artemis.core.config.Configuration;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.remoting.impl.invm.InVMAcceptorFactory;
import org.apache.activemq.artemis.jms.server.config.ConnectionFactoryConfiguration;
import org.apache.activemq.artemis.jms.server.config.JMSConfiguration;
import org.apache.activemq.artemis.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.apache.activemq.artemis.jms.server.config.impl.JMSConfigurationImpl;
import org.apache.activemq.artemis.jms.server.embedded.EmbeddedJMS;
import org.junit.Before;
import org.junit.Test;

import com.bravson.socialalert.infrastructure.async.AsyncRepository;

public class AsyncPreviewRepositoryTest {

	private AsyncRepository repository;
	
	@Before
	public void init() throws Exception {
		
		ConnectionFactory cf = createJmsConnectionFactory();
		JMSContext context = cf.createContext();
		Queue queue = context.createTemporaryQueue();
		repository = new AsyncRepository(context, queue);
	}

	private ConnectionFactory createJmsConnectionFactory() throws Exception {
		// Step 1. Create Apache ActiveMQ Artemis core configuration, and set the properties accordingly
		Configuration configuration = new ConfigurationImpl();
		configuration.setPersistenceEnabled(false);
		configuration.setSecurityEnabled(false);
		configuration.addConnectorConfiguration(InVMAcceptorFactory.class.getName(), "vm://0");
		configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

		// Step 2. Create the JMS configuration
		JMSConfiguration jmsConfig = new JMSConfigurationImpl();

		// Step 3. Configure the JMS ConnectionFactory
		ConnectionFactoryConfiguration cfConfig = new ConnectionFactoryConfigurationImpl();
		cfConfig.setName("activemq-ra");
		cfConfig.setBindings("java:/cf");
		cfConfig.setConnectorNames(InVMAcceptorFactory.class.getName());
		jmsConfig.getConnectionFactoryConfigurations().add(cfConfig);

		// Step 5. Start the JMS Server using the Apache ActiveMQ Artemis core server and the JMS configuration
		EmbeddedJMS jmsServer = new EmbeddedJMS();
		jmsServer.setConfiguration(configuration);
		jmsServer.setJmsConfiguration(jmsConfig);
		jmsServer.start();
		
		return (ConnectionFactory) jmsServer.lookup("java:/cf");
	}
	
	@Test
	public void sendMessage() {
		repository.addAsyncProcessing(() -> System.out.print("test"));
		
	}
}
