package com.bravson.socialalert.test.integration;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.jupiter.api.Test;

import com.bravson.socialalert.domain.media.MediaInfo;
import com.bravson.socialalert.domain.paging.QueryResult;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MediaSearchTest extends BaseIntegrationTest {

	@Test
	public void searchWithDefaultParamters() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		@SuppressWarnings("unchecked")
		QueryResult<MediaInfo> result = (QueryResult<MediaInfo>) response.readEntity(QueryResult.class);
		assertThat(result.getContent()).isEmpty();
	}

	@Test
	public void searchWithInvalidArea() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?minLatitude=45.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void searchWithInvalidPaging() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?pageSize=0", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
	
	@Test
	public void searchWithMediaKind() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?kind=VIDEO", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void searchWithInvalidMediaKind() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?kind=TEST", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.NOT_FOUND.getStatusCode());
	}
	
	@Test
	public void searchWithMultipleKeywords() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/search?keywords=Test+Test2+Test3", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void mapMediaCount() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/mapCount?kind=PICTURE&minLatitude=45.5&maxLatitude=46.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
	
	@Test
	public void mapMediaCountWithInvalidArea() {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/media/mapCount?minLatitude=45.5&minLongitude=7.5&maxLongitude=7.6", MediaType.APPLICATION_JSON, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.BAD_REQUEST.getStatusCode());
	}
}
