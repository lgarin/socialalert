package com.bravson.socialalert.test.integration;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaFeelingTest extends BaseIntegrationTest {

	@Test
	public void setFeelingForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/feeling/" + mediaUri + "/2", MediaTypeConstants.JSON, token).post(Entity.text("test"));
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void setInvalidFeeling() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/feeling/" + mediaUri + "/999", MediaTypeConstants.JSON, token).post(Entity.text(""));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
}
