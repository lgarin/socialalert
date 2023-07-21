package com.bravson.socialalert.test.integration;

import java.io.File;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.business.file.media.AsyncMediaProcessedEvent;
import com.bravson.socialalert.domain.media.format.MediaFileConstants;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@QuarkusTest
public class FileFacadeTest extends BaseIntegrationTest {

	private static Entity<File> getPicture(String filename) {
		return Entity.entity(new File(filename), MediaFileConstants.JPG_MEDIA_TYPE);
	}
	
	private String uploadPicture(String token) throws InterruptedException {
		Response upload = createAuthRequest("/file/upload/picture", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(upload.getStatus()).isEqualTo(Status.CREATED.getStatusCode());
		awaitAsyncEvent(AsyncMediaProcessedEvent.class);
		return getLocationPath(upload);
	}
	
	@Test
	public void downloadNonExistingFile() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/download/20170407/58b28c6b28011a1ad4180419", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void downloadExistingFile() throws Exception  {
		String token = requestLoginToken("test@test.com", "123");
		String path = uploadPicture(token);
		Response response = createAuthRequest(path, MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.getMediaType()).isEqualTo(MediaType.valueOf(MediaFileConstants.JPG_MEDIA_TYPE));
		response.readEntity(InputStream.class).close();
	}
	
	@Test
	public void downloadThumbnailFile() throws Exception  {
		String token = requestLoginToken("test@test.com", "123");
		String path = uploadPicture(token);
		Response response = createAuthRequest(path.replace("/media/", "/thumbnail/"), MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.getMediaType()).isEqualTo(MediaType.valueOf(MediaFileConstants.JPG_MEDIA_TYPE));
		response.readEntity(InputStream.class).close();
	}

	@Test
	public void downloadPreviewFile() throws Exception  {
		String token = requestLoginToken("test@test.com", "123");
		String path = uploadPicture(token);
		Response response = createAuthRequest(path.replace("/media/", "/preview/"), MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.getMediaType()).isEqualTo(MediaType.valueOf(MediaFileConstants.JPG_MEDIA_TYPE));
		response.readEntity(InputStream.class).close();
	}
	
	@Test
	public void findNonExistingFile() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/metadata/20170407/58b28c6b28011a1ad4180419", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void listNewFiles() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/list/new", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void downloadMissingAvatar() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/file/avatar/small/8b99179c-2a6b-4e41-92d3-3edfe3df885b/7e9a5a5bd5e64171c176ac6c7b32d685", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void downloadExistingAvatar() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response upload = createAuthRequest("/file/upload/avatar", MediaType.WILDCARD, token).post(getPicture("src/test/resources/media/IMG_0397.JPG"));
		assertThat(upload.getStatus()).isEqualTo(Status.OK.getStatusCode());
		Response response = createAuthRequest("/file/avatar/small/8b99179c-2a6b-4e41-92d3-3edfe3df885b/7e9a5a5bd5e64171c176ac6c7b32d685", MediaType.WILDCARD, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.getMediaType()).isEqualTo(MediaType.valueOf(MediaFileConstants.JPG_MEDIA_TYPE));
		response.readEntity(InputStream.class).close();
	}
}
