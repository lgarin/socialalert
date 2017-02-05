package com.bravson.socialalert;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.junit.Test;

public class GreeterServiceTest extends BaseServiceTest {

	@Test
	public void requestWithoutPrincialReturnsHello(@ArquillianResteasyResource WebTarget webTarget) throws Exception {
	 
	 Response response = webTarget.path("/greeter/hello").request(MediaType.TEXT_PLAIN).get();
	 
	 assertEquals(Status.OK.getStatusCode(), response.getStatus());
	 assertEquals("hello", response.readEntity(String.class));
	}
}
