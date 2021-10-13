package com.bravson.socialalert.business.user.statistic;

import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.user.link.UserLinkEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.user.statistic.LinkActivity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class LinkStatisticRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public LinkStatisticEntity insert(@NonNull UserLinkEntity link, @NonNull LinkActivity activity, @NonNull UserAccess userAccess) {
		LinkStatisticEntity entity = new LinkStatisticEntity(link, activity, userAccess);
		return persistenceManager.persist(entity);
	}
}
