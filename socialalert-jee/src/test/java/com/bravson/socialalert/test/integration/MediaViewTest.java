package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

public class MediaViewTest extends BaseIntegrationTest {

	@Test
	public void viewNonExistingMedia() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/view/uri1", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}

}
