package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FeedFacadeTest extends BaseIntegrationTest {

	@Test
	public void listEmptyFeedForKnownUser() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/feed/current", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
}
