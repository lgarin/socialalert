package com.bravson.socialalert.test.integration;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

public class MediaCommentTest extends BaseIntegrationTest {

	@Test
	@RunAsClient
	public void createCommentForInvalidMedia() {
		String token = requestLoginToken("test@test.com", "123");
		String mediaUri = "uri1";
		Form form = new Form("comment", "test");
		Response response = createAuthRequest("/media/comment/" + mediaUri, MediaType.APPLICATION_JSON, token).post(Entity.form(form));
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
}
