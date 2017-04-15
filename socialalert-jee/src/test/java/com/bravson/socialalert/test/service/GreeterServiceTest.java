package com.bravson.socialalert.test.service;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class GreeterServiceTest extends BaseServiceTest {

	@Test
	public void requestWithoutPrincial() throws Exception {
		Response response = createRequest("/greeter/hello", MediaType.TEXT_PLAIN).get();
		assertThat(response.getStatus()).isEqualTo(Status.FORBIDDEN.getStatusCode());
	}

	@Test
	public void requestWithLogin() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		Response response = createAuthRequest("/greeter/hello", MediaType.TEXT_PLAIN, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.readEntity(String.class)).isEqualTo("hello 4b09beae-9187-4566-b15a-b26f50dd840c");
	}
	
	@Test
	public void requestWithOldToken() throws Exception {
		String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJ4NEgwcFNlU0NSa1Z3WklXQ3pVN25PVUtSOHNvMlM5dUJseGxfWUxkM3RJIn0.eyJqdGkiOiIwODNhODNhMi0wNzViLTQxN2MtOTMxZi05NTgwYjAxYTlhODQiLCJleHAiOjE0ODY2NjQwNDYsIm5iZiI6MCwiaWF0IjoxNDg2NjYzNzQ2LCJpc3MiOiJodHRwOi8vbG9jYWxob3N0OjgwODAvYXV0aC9yZWFsbXMvU29jaWFsQWxlcnQtRGV2IiwiYXVkIjoic29jaWFsYWxlcnQtamVlIiwic3ViIjoiNGIwOWJlYWUtOTE4Ny00NTY2LWIxNWEtYjI2ZjUwZGQ4NDBjIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoic29jaWFsYWxlcnQtamVlIiwiYXV0aF90aW1lIjowLCJzZXNzaW9uX3N0YXRlIjoiYTI2MzdhY2QtZDViNi00ODFkLWJmM2ItMTdiMTEwN2I1N2FjIiwiYWNyIjoiMSIsImNsaWVudF9zZXNzaW9uIjoiNTcxODY4MjYtZDNiNy00MzNmLWJmZjgtNWE0NWVjYmNlZTFmIiwiYWxsb3dlZC1vcmlnaW5zIjpbXSwicmVhbG1fYWNjZXNzIjp7InJvbGVzIjpbInVtYV9hdXRob3JpemF0aW9uIiwidXNlciJdfSwicmVzb3VyY2VfYWNjZXNzIjp7InNvY2lhbGFsZXJ0LWplZSI6eyJyb2xlcyI6WyJ1c2VyIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50Iiwidmlldy1wcm9maWxlIl19fSwibmFtZSI6IlRlc3QgSGVsbG8iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0QHRlc3QuY29tIiwiZ2l2ZW5fbmFtZSI6IlRlc3QiLCJmYW1pbHlfbmFtZSI6IkhlbGxvIiwiZW1haWwiOiJ0ZXN0QHRlc3QuY29tIn0.GcuV77KQJiEltQso2a1anL6C1NcvL5eriLRGiZGsO5vjKRZyi2sPorAKCzBDcBJYYH4en2yoeJzLDEEHX5IiGEcJP8o-fZnZUxxAG6w8KeVn3lVY_pECqHtWvgnLPYJlUTdpO1qODFT0fioZqY-CnljF0y-HNeyXrX2qBGamS0ATW1MoWp80UGIaz0myLJ96yXFcjb4XrTj-dIqaAabsM5YZcQSaMA6qX63_3EPcX8o47rnYb5pxXFbwx5rKNtCeu9-F4m1va58Wzn8snKKiJQIT4fmEll4hX2tCHH3GIEL_5-5W7bPnJ4043KBKI6DvtaS1VeTcl0BZbu_YIVQlhg";
		Response response = createAuthRequest("/greeter/hello", MediaType.TEXT_PLAIN, token).get();
		assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void requestAfterLogout() throws Exception {
		String token = requestLoginToken("test@test.com", "123");
		createAuthRequest("/user/logout", MediaType.WILDCARD, token).post(null);
		
		Response response = createAuthRequest("/greeter/hello", MediaType.TEXT_PLAIN, token).get();
		//assertThat(response.getStatus()).isEqualTo(Status.UNAUTHORIZED.getStatusCode());
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
		assertThat(response.readEntity(String.class)).isEqualTo("hello 4b09beae-9187-4566-b15a-b26f50dd840c");
	}
}
