package com.bravson.socialalert.infrastructure.async;

public interface AsyncConstants {

	String QUEUE_CONNECTION_FACTORY = "java:/JmsXA";
	String ASYNC_PROCESSOR_QUEUE = "java:/jms/queue/asyncProcessorQueue";
}
