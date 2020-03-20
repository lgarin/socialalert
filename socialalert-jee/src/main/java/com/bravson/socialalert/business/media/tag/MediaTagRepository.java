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
	
	public List<String> suggestTags(String searchTerm, int maxHitCount) {
		return persistenceManager.search(MediaTagEntity.class).select(p -> p.field("tag", String.class))
				.where(p -> p.phrase().field("startTag").boost(10).field("endTag").boost(5).field("langTag").boost(3).matching(searchTerm))
				.toQuery().fetchHits(maxHitCount);
	}
}
