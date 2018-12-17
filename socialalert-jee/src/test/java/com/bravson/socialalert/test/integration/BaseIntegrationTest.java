package com.bravson.socialalert.test.integration;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.assertj.core.api.Assertions;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.bravson.socialalert.domain.user.LoginParameter;
import com.bravson.socialalert.domain.user.LoginResponse;

@RunWith(Arquillian.class)
@RunAsClient
public abstract class BaseIntegrationTest extends Assertions {

	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		
		File[] libs = Maven.resolver()  
                .loadPomFromFile("pom.xml").importCompileAndRuntimeDependencies().resolve().withTransitivity().as(File.class);
		
		return ShrinkWrap.create(WebArchive.class, "socialalert-jee-test.war")
				.addPackages(true, "com/bravson/socialalert")
				.addAsResource("logo.jpg")
				.addAsResource("neo4j.properties")
				.addAsLibraries(libs)
				.addAsResource(new File("src/main/resources/META-INF/jboss-logging.properties"), "META-INF/jboss-logging.properties")
				.addAsResource(new File("src/main/resources/META-INF/persistence.xml"), "META-INF/persistence.xml")
				.setWebXML(new File("src/main/webapp/WEB-INF/web.xml"))
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/beans.xml"), "beans.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/keycloak.json"), "keycloak.json")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-deployment-structure.xml"), "jboss-deployment-structure.xml")
				.addAsWebInfResource(new File("src/main/webapp/WEB-INF/jboss-jms.xml"), "jboss-jms.xml");
	}
	
	@ArquillianResource
	protected URL deploymentUrl;
	
	protected Builder createRequest(String path, String mediaType) {
		return ClientBuilder.newClient().target(deploymentUrl.toString() + "rest" + path).request(mediaType);
	}
	
	protected Builder createAuthRequest(String path, String mediaType, String token) {
		return createRequest(path, mediaType).header("Authorization", token);
	}
	
	protected String requestLoginToken(String userId, String password) {
		LoginParameter param = new LoginParameter(userId,password);
		LoginResponse response = createRequest("/user/login", MediaType.APPLICATION_JSON).post(Entity.json(param)).readEntity(LoginResponse.class);
		return response.getAccessToken();
	}
	
	protected String getLocationPath(Response response) {
		return response.getLocation().toString().replaceFirst("^(http://.*/rest)", "");
	}
	
	@RunAsClient
	@Before
	public void cleanAllData() {
		Response response = ClientBuilder.newClient().target(deploymentUrl.toString() + "unitTest/deleteData").request().delete();
		assertThat(response.getStatus()).isEqualTo(Status.OK.getStatusCode());
	}
}
