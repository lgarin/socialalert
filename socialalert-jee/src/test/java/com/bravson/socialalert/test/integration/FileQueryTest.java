package com.bravson.socialalert.test.integration;

import java.io.File;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FileQueryTest extends BaseIntegrationTest {
	
	private static Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	@Test
	public void uploadPictureWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(response.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		awaitAsyncEvent(AsyncMediaProcessedEvent.class);
		FileInfo[] result = createAuthRequest("/file/list/new", MediaType.APPLICATION_JSON, token).get(FileInfo[].class);
		assertThat(result).anyMatch(f -> ("/rest/file/download/" + f.getFileUri()).equals(response.getLocation().getPath()));
	}


}
