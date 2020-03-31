package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FeedFacadeTest extends BaseIntegrationTest {

	@Test
	public void listEmptyFeedForKnownUser() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/feed/current", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
}
