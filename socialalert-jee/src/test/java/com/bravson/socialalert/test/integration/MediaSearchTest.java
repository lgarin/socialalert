package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.jboss.arquillian.container.test.api.RunAsClient;
import org.junit.Test;

import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.media.MediaInfo;

public class MediaSearchTest extends BaseIntegrationTest {

	@Test
	@RunAsClient
	public void searchWithDefaultParamters() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		@SuppressWarnings("unchecked")
		QueryResult<MediaInfo> result = (QueryResult<MediaInfo>) response.readEntity(QueryResult.class);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	@RunAsClient
	public void searchWithInvalidArea() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?minLatitude=45.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void searchWithInvalidPaging() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?pageSize=0", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void searchWithMediaKind() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?kind=VIDEO", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void searchWithInvalidMediaKind() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?kind=TEST", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void searchWithMultipleKeywords() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?keywords=Test+Test2+Test3", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void mapMediaCount() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/mapCount?kind=PICTURE&minLatitude=45.5&maxLatitude=46.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	@RunAsClient
	public void mapMediaCountWithInvalidArea() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/mapCount?minLatitude=45.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
}
