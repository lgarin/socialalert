package com.bravson.socialalert;

import java.io.IOException;
import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public abstract class BaseServiceTest {

	protected static final String RESOURCE_PREFIX = "rest";
	
	@Deployment(testable = false)
	public static WebArchive createDeployment() throws IOException {
		WebArchive war = ShrinkWrap.create(WebArchive.class, "socialalert-jee.war")
				.addPackage("com/bravson/socialalert")
				.addAsWebInfResource("web.xml", "web.xml");
		return war;
	}
	
	@ArquillianResource
	protected URL deploymentUrl;
}
