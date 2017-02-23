package com.bravson.socialalert.user;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.mongodb.morphia.Datastore;

import com.bravson.socialalert.infrastructure.db.NamedMongoDatastore;

@ManagedBean
public class UserRepository {
	
	@Inject
	UserGroupRepository userGroupRepository;

	@Inject @NamedMongoDatastore(db="keycloak")
	Datastore datastore;
	
	public UserInfo findUserInfo(String userId) {
		UserInfo info = datastore.get(UserInfo.class, userId);
		if (info != null) {
			populateGroupNames(info);
		}
		return info;
	}

	private void populateGroupNames(UserInfo info) {
		for (String groupId : info.getGroupIds()) {
			info.getGroupNames().add(userGroupRepository.getGroupName(groupId));
		}
	}
	
}
