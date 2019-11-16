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
		info = @Info(title = "Socialalert API", version = "1.2"),
        servers = {@Server(description = "Test server", url = "http://3ft8uk98qmfq79pc.myfritz.net:18788/socialalert-jee"),
        		   @Server(description = "Dev server", url = "http://localhost:7082")},
        components = @Components(securitySchemes = 
        				{@SecurityScheme(securitySchemeName = "JWT", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
        				 @SecurityScheme(securitySchemeName = "Dev", type = SecuritySchemeType.OPENIDCONNECT, openIdConnectUrl = "http://localhost:8081/auth/realms/SocialAlert-Dev/.well-known/openid-configuration"),
        				 @SecurityScheme(securitySchemeName = "Test", type = SecuritySchemeType.OPENIDCONNECT, openIdConnectUrl = "http://3ft8uk98qmfq79pc.myfritz.net:18788/auth/realms/SocialAlert-Dev/.well-known/openid-configuration")})
)
@ApplicationPath("/rest")
public class RestApplication extends Application {

}
