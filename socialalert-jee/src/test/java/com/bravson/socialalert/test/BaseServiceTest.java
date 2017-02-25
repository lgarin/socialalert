package com.bravson.socialalert.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class BaseServiceTest extends Assertions {

	protected static final String RESOURCE_PREFIX = "rest";
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		
		File[] libs = Maven.resolver()  
                .loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve().withTransitivity().as(File.class);
		
		return ShrinkWrap.create(WebArchive.class, "socialalert-jee-test.war")
				.addPackage("com/bravson/socialalert")
				.addPackage("com/bravson/socialalert/user")
				.addPackage("com/bravson/socialalert/file")
				.addPackage("com/bravson/socialalert/infrastructure/db")
				.addPackage("com/bravson/socialalert/infrastructure/log")
				.addPackage("com/bravson/socialalert/infrastructure/rest")
				.addAsLibraries(libs)
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/web.xml"), "web.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/keycloak.json"), "keycloak.json")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"), "jboss-deployment-structure.xml");
	}
	
	@ArquillianResource
	protected URL deploymentUrl;
	
	protected WebTarget createTarget() {
		return ClientBuilder.newClient().target(deploymentUrl.toString() + "rest/");
	}
	
	protected Builder createRequest(String path, String mediaType) {
		return createTarget().path(path).request(mediaType);
	}
	
	protected Builder createAuthRequest(String path, String mediaType, String token) {
		return createRequest(path, mediaType).header("Authorization", "bearer " + token);
	}
	
	protected String requestLoginToken(String email, String password) {
		Form form = new Form("email", "test@test.com").param("password", "123");
		return createRequest("user/login", MediaType.TEXT_PLAIN).post(Entity.form(form)).readEntity(String.class);
	}
	
	
}
