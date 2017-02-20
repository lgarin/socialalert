package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.cache.annotation.CacheResult;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.bson.BsonDocument;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@ManagedBean
@ApplicationScoped
public class UserGroupRepository {

	@Inject @NamedMongoCollection(name="groups", db="keycloak")
	MongoCollection<BsonDocument> groups;
	
	@CacheResult
	public String getGroupName(String id) {
		BsonDocument d = groups.find(Filters.eq("_id", id)).first();
		if (d == null) {
			return null;
		}
		return d.getString("name").getValue();
	}
}
