package com.bravson.socialalert;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@ManagedBean
public class UserRepository {

	@Inject
	MongoClient mongoClient;
	
	@Inject @NamedMongoCollection(name="users", db="keycloak")
	MongoCollection<Document> users;
	
	public UserInfo getUserInfo(String userId) {
		return users.find(Filters.eq("_id", userId)).map(this::mapUserInfo).first();
	}
	
	private UserInfo mapUserInfo(Document d) {
		return new UserInfo(d.getString("firstName"), d.getString("lastName"), d.getString("email"));
	}
}
