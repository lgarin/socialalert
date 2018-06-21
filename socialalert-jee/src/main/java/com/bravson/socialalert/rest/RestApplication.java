package com.bravson.socialalert.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.v3.jaxrs2.integration.resources.AcceptHeaderOpenApiResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(title = "Socialalert API", version = "1.0"),
        servers = {@Server(description = "Test server", url = "http://jcla3ndtozbxyghx.myfritz.net:18788/socialalert-jee")}
)
@ApplicationPath("/rest")
public class RestApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> resources = new HashSet<>();
		resources.add(RestApplication.class);
		resources.add(OpenApiResource.class);
		resources.add(AcceptHeaderOpenApiResource.class);
        resources.add(FileFacade.class);
        resources.add(MediaFacade.class);
        resources.add(UserFacade.class);
		return resources;
	}
}
