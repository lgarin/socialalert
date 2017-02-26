package com.bravson.socialalert.user;

import java.util.Optional;
import java.util.stream.Stream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.mongodb.morphia.Datastore;

import com.bravson.socialalert.infrastructure.db.NamedMongoDatastore;

@ManagedBean
public class UserRepository {

	@Inject
	UserGroupRepository userGroupRepository;

	@Inject
	@NamedMongoDatastore(db = "keycloak")
	Datastore datastore;

	public Optional<UserInfo> findUserInfo(String userId) {
		UserInfo info = datastore.get(UserInfo.class, userId);
		if (info != null) {
			populateGroupNames(info);
		}
		return Optional.ofNullable(info);
	}

	private void populateGroupNames(UserInfo info) {
		info.getGroupIds().stream().flatMap(this::findGroupName).forEach(info.getGroupNames()::add);
	}

	private Stream<String> findGroupName(String groupId) {
		return userGroupRepository.findGroupName(groupId).map(Stream::of).orElse(Stream.empty());
	}
}
