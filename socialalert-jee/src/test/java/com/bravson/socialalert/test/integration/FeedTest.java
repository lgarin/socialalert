package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class FeedTest extends BaseIntegrationTest {


	@Test
	public void listFeedForUnknownUser() {
		String token = requestLoginToken("test@test.com", "123");
		String user = "xyz";
		Response response = createAuthRequest("/feed/" + user, MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}
