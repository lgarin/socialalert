package com.bravson.socialalert.business.media.tag;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import com.bravson.socialalert.business.media.MediaEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
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
public class MediaTagRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	void handleNewMedia(@Observes @NewEntity MediaEntity media) {
		for (String tag : media.getTags()) {
			if (!persistenceManager.find(MediaTagEntity.class, tag).isPresent()) {
				persistenceManager.merge(new MediaTagEntity(tag));
			}
		}
	}
	
	public List<String> suggestTags(String searchTerm) {
		return persistenceManager.search(MediaTagEntity.class).asProjection(p -> p.field("tag", String.class))
				.predicate(p -> p.phrase().onField("startTag").boostedTo(10).orField("endTag").boostedTo(5).orField("langTag").boostedTo(3).matching(searchTerm))
				.toQuery().fetchHits();
	}
}
