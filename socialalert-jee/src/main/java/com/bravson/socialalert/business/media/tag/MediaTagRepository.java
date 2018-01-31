package com.bravson.socialalert.business.media.tag;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
import com.bravson.socialalert.infrastructure.layer.Repository;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Repository
@Transactional
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaTagRepository {

	@Inject
	@NonNull
	FullTextEntityManager entityManager;
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		for (String tag : media.getTags()) {
			entityManager.merge(new MediaTagEntity(tag));
		}
	}
	
	private QueryBuilder createQueryBuilder() {
		return entityManager.getSearchFactory().buildQueryBuilder().forEntity(MediaTagEntity.class).get();
	}
	
	public List<String> suggestTags(String searchTerm) {
		QueryBuilder builder = createQueryBuilder();
		Query query = builder.phrase().withSlop(2).onField("nGramTag").andField("edgeTag").boostedTo(5).andField("langTag").boostedTo(3).sentence(searchTerm.toLowerCase()).createQuery();
		@SuppressWarnings("unchecked")
		List<Object[]> result = entityManager.createFullTextQuery(query, MediaTagEntity.class).setProjection("tag").getResultList();
		return result.stream().flatMap(Stream::of).map(Object::toString).collect(Collectors.toList());
	}
}
