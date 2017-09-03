package com.bravson.socialalert.infrastructure.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.bravson.socialalert.file.FileFacade;
import com.bravson.socialalert.media.MediaFacade;
import com.bravson.socialalert.user.UserFacade;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;

@ApplicationPath("/rest")
public class RestApplication extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> resources = new HashSet<>();
		resources.add(ApiListingResource.class);
        resources.add(SwaggerSerializers.class);
        resources.add(FileFacade.class);
        resources.add(MediaFacade.class);
        resources.add(UserFacade.class);
		return resources;
	}
}
