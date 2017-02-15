package com.bravson.socialalert;

import java.security.Principal;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.mongodb.MongoClient;

@Path("/user")
@ManagedBean
public class UserService {

	@Resource(name="loginUrl")
	String loginUrl;
	
	@Resource(name="logoutUrl")
	String logoutUrl;
	
	@Resource(name="loginClientId")
	String loginClientId;
	
	@Resource(name="clientSecret")
	String clientSecret;
	
	@Inject
	Client httpClient;
	
	@Inject
	Principal principal;
	
	@Inject
	UserRepository userRepository;
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/login")
	public Response login(@Email @FormParam("email") String email, @NotEmpty @FormParam("password") String password) {
		Form form = new Form().param("username", email).param("password", password).param("grant_type", "password").param("client_id", loginClientId).param("client_secret", clientSecret);
		Response response = httpClient.target(loginUrl).request().post(Entity.form(form));
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Response.status(Status.UNAUTHORIZED).build();
		}
		JsonObject payload = response.readEntity(JsonObject.class);
		return Response.status(Status.OK).entity(payload.getString("access_token")).build();
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/logout")
	public Response logout(@NotEmpty @HeaderParam("Authorization") String authorization, @Context HttpServletRequest httpRequest) {
		Response response = httpClient.target(logoutUrl).request().header("Authorization", authorization).get();
		if (response.getStatus() != Status.OK.getStatusCode()) {
			return Response.status(response.getStatus()).build();
		}
		httpRequest.getSession().invalidate();
		try {
			httpRequest.logout();
		} catch (ServletException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/current")
	public Response current() {
		UserInfo userInfo = userRepository.getUserInfo(principal.getName());
		if (userInfo == null) {
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.status(Status.OK).entity(userInfo).build();
	}
}
