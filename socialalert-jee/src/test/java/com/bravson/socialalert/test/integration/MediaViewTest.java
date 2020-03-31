package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.infrastructure.rest.MediaTypeConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaViewTest extends BaseIntegrationTest {

	@Test
	public void viewNonExistingMedia() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/view/uri1", MediaTypeConstants.JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}

}
