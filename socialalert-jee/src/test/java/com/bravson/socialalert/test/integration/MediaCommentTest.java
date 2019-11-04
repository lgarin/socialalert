package com.bravson.socialalert.test.integration;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaCommentTest extends BaseIntegrationTest {

	@Test
	public void createCommentForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comment/" + mediaUri, MediaType.APPLICATION_JSON, token).post(Entity.text("test"));
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void createEmptyComment() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comment/" + mediaUri, MediaType.APPLICATION_JSON, token).post(Entity.text(""));
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void listCommentsForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Response response = createAuthRequest("/media/comments/" + mediaUri, MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}
