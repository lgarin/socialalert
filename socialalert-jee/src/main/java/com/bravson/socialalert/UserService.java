package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/user")
@ManagedBean
public class UserService {

	@Resource(name="loginUrl")
	String loginUrl;
	
	@Resource(name="loginClientId")
	String loginClientId;
	
	Client httpClient;
	
	@PostConstruct
	void init() {
		httpClient = ClientBuilder.newClient();
	}
	
	@PreDestroy
	void cleanup() {
		if (httpClient != null) {
			httpClient.close();
		}
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/login")
	public Response login(@HeaderParam("email") String email, @HeaderParam("password") String password) {
		Form form = new Form().param("username", email).param("password", password).param("grant_type", "password").param("client_id", loginClientId);
		Response response = httpClient.target(loginUrl).request().buildPost(Entity.form(form)).invoke();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		JsonObject payload = response.readEntity(JsonObject.class);
		return Response.status(Status.OK).entity(payload).build();
	}
}
