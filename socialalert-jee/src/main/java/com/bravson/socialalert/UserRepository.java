package com.bravson.socialalert;

import java.util.ArrayList;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.bson.BsonDocument;
import org.bson.BsonValue;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@ManagedBean
public class UserRepository {
	
	@Inject
	UserGroupRepository userGroupRepository;

	@Inject @NamedMongoCollection(name="users", db="keycloak")
	MongoCollection<BsonDocument> users;
	
	public UserInfo getUserInfo(String userId) {
		return users.find(Filters.eq("_id", userId)).map(this::mapUserInfo).first();
	}
	
	private UserInfo mapUserInfo(BsonDocument d) {
		UserInfo info = new UserInfo();
		info.firstName = d.getString("firstName").getValue();
		info.lastName = d.getString("lastName").getValue();
		info.email = d.getString("email").getValue();
		info.createdTimestamp = d.getInt64("createdTimestamp").getValue();
		info.groupNames = new ArrayList<String>();
		for (BsonValue groupId : d.getArray("groupIds").getValues()) {
			info.groupNames.add(userGroupRepository.getGroupName(groupId.asString().getValue()));
		}
		for (BsonValue groupId : d.getArray("groupIds").getValues()) {
			info.groupNames.add(userGroupRepository.getGroupName(groupId.asString().getValue()));
		}
		return info;
	}
}
