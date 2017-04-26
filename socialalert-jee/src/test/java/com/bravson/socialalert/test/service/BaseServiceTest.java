package com.bravson.socialalert.test.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
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

	private static File baseDirectory = new File("C:/Temp/xtra");
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		try {
			FileUtils.deleteDirectory(baseDirectory);
		} catch (IOException e) {
			// ignore
		}
		
		File[] libs = Maven.resolver()  
                .loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve().withTransitivity().as(File.class);
		
		return ShrinkWrap.create(WebArchive.class, "socialalert-jee-test.war")
				.addPackages(true, "com/bravson/socialalert")
				.addAsResource("logo.jpg")
				.addAsLibraries(libs)
				.addAsResource(new File("src/main/webapp/META-INF/jboss-logging.properties"), "META-INF/jboss-logging.properties")
				.addAsResource(new File("src/main/webapp/META-INF/persistence.xml"), "META-INF/persistence.xml")
				.addAsWebInfResource(new File("src/main/webapp/META-INF/persistence.xml"), "persistence.xml")
				//.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
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
		return createRequest(path, mediaType).header("Authorization", token);
	}
	
	protected String requestLoginToken(String email, String password) {
		Form form = new Form("userId", "test@test.com").param("password", "123");
		return createRequest("user/login", MediaType.TEXT_PLAIN).post(Entity.form(form)).readEntity(String.class);
	}
	
	protected String getLocationPath(Response response) {
		return response.getLocation().toString().replaceFirst("^(http://.*/rest)", "");
	}
}
