package com.bravson.socialalert.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.Components;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(title = "Socialalert API", version = "2.7"),
        servers = {@Server(description = "Test server", url = "http://3ft8uk98qmfq79pc.myfritz.net:18774"),
        		   @Server(description = "Dev server", url = "http://localhost:7080")},
        components = @Components(securitySchemes = @SecurityScheme(securitySchemeName = "JWT", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"))
)
@ApplicationPath("/rest")
public class RestApplication extends Application {

}
