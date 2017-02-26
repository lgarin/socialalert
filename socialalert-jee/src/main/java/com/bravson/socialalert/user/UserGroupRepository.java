package com.bravson.socialalert.user;

import java.util.Optional;

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
	public Optional<String> findGroupName(String id) {
		UserGroup group = datastore.get(UserGroup.class, id);
		if (group == null) {
			return Optional.empty();
		}
		return Optional.of(group.getName());
	}
}
