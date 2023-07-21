package com.bravson.socialalert.business.user.link;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.hibernate.search.engine.search.predicate.dsl.BooleanPredicateClausesStep;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchResult;

import com.bravson.socialalert.business.user.profile.UserProfileEntity;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.infrastructure.entity.DeleteEntity;
import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserLinkRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public Optional<UserLinkEntity> find(@NonNull String sourceUserId, @NonNull String targetUserId) {
		return persistenceManager.find(UserLinkEntity.class, new UserLinkKey(sourceUserId, targetUserId));
	}
	
	public UserLinkEntity link(@NonNull UserProfileEntity sourceUser, @NonNull UserProfileEntity targetUser) {
		return persistenceManager.persist(new UserLinkEntity(sourceUser, targetUser));
	}
	
	public Optional<UserLinkEntity> unlink(@NonNull String sourceUserId, @NonNull String targetUserId) {
		return find(sourceUserId, targetUserId).map(persistenceManager::remove);
	}
	
	public List<UserLinkEntity> findBySource(@NonNull String sourceUserId) {
		return persistenceManager.createQuery("from UserLink where sourceUser.id = :sourceUserId", UserLinkEntity.class)
				.setParameter("sourceUserId", sourceUserId)
				.getResultList();
	}
	
	void handleDeleteUser(@Observes @DeleteEntity UserProfileEntity user) {
		deleteBySource(user.getId());
		deleteByTarget(user.getId());
	}
	
	private int deleteBySource(@NonNull String sourceUserId) {
		return persistenceManager.createUpdate("delete from UserLink where sourceUser.id = :sourceUserId")
				.setParameter("sourceUserId", sourceUserId)
				.executeUpdate();
	}
	
	private int deleteByTarget(@NonNull String targetUserId) {
		return persistenceManager.createUpdate("delete from UserLink where targetUser.id = :targetUserId")
				.setParameter("targetUserId", targetUserId)
				.executeUpdate();
	}
	
	private PredicateFinalStep buildSearchByTargetQuery(String targetUserId, Instant timestamp, SearchPredicateFactory context) {
		BooleanPredicateClausesStep<?> junction = context.bool();
		junction = junction.must(context.range().field("creation").atMost(timestamp));
		junction = junction.filter(context.simpleQueryString().field("targetUser.id").matching(targetUserId));
		return junction;
	}
	
	public QueryResult<UserLinkEntity> searchByTarget(@NonNull String targetUserId, @NonNull PagingParameter paging) {
		SearchResult<UserLinkEntity> result = persistenceManager.search(UserLinkEntity.class)
				.where(p -> buildSearchByTargetQuery(targetUserId, paging.getTimestamp(), p))
				.sort(s -> s.field("creation").desc())
				.fetch(paging.getOffset(), paging.getPageSize());
		return new QueryResult<>(result.hits(), result.total().hitCount(), paging);
	}
}
