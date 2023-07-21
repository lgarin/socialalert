package com.bravson.socialalert.business.user.statistic;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Service;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class LinkStatisticObserver {

	@Inject
	LinkStatisticRepository repository;
	
	@Inject
	UserAccess userAccess;
	
	void handleNewLink(@Observes @NewEntity UserLinkEntity link) {
		repository.insert(link, LinkActivity.CREATE, userAccess);
	}
	
	void handleDeleteLink(@Observes @DeleteEntity UserLinkEntity link) {
		repository.insert(link, LinkActivity.DELETE, userAccess);
	}
}
