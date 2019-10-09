package com.bravson.socialalert.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
		info = @Info(title = "Socialalert API", version = "1.2"),
        servers = {@Server(description = "Test server", url = "http://3ft8uk98qmfq79pc.myfritz.net:18788/socialalert-jee")}
)
@ApplicationPath("/rest")
public class RestApplication extends Application {

}
