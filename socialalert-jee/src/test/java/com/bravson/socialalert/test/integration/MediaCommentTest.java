package com.bravson.socialalert.test.integration;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaCommentTest extends BaseIntegrationTest {

	@Test
	public void createCommentForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comment/" + mediaUri, MediaTypeConstants.JSON, token).post(Entity.text("test"));
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void createEmptyComment() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comment/" + mediaUri, MediaTypeConstants.JSON, token).post(Entity.text(""));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void listCommentsForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comments/" + mediaUri, MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}
