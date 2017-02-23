package com.bravson.socialalert.user;

import javax.annotation.ManagedBean;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.mongodb.morphia.Datastore;

import com.bravson.socialalert.infrastructure.db.NamedMongoDatastore;

@ManagedBean
@ApplicationScoped
public class UserGroupRepository {

	@Inject @NamedMongoDatastore(db="keycloak")
	Datastore datastore;
	
	@CacheResult
	public String getGroupName(String id) {
		UserGroup group = datastore.get(UserGroup.class, id);
		if (group == null) {
			return null;
		}
		return group.name;
	}
}
