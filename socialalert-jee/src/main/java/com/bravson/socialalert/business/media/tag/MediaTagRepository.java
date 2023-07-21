package com.bravson.socialalert.business.media.tag;

import java.util.List;

import com.bravson.socialalert.infrastructure.entity.PersistenceManager;
import com.bravson.socialalert.infrastructure.layer.Repository;

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
public class MediaTagRepository {

	@Inject
	@NonNull
	PersistenceManager persistenceManager;
	
	public void addTag(String tag) {
		if (!persistenceManager.find(MediaTagEntity.class, tag).isPresent()) {
			persistenceManager.persist(new MediaTagEntity(tag));
		}
	}
	
	public List<String> suggestTags(String searchTerm, int maxHitCount) {
		return persistenceManager.search(MediaTagEntity.class).select(p -> p.field("tag", String.class))
				.where(p -> p.phrase().field("startTag").boost(10).field("endTag").boost(5).field("langTag").boost(3).matching(searchTerm))
				.toQuery().fetchHits(maxHitCount);
	}
}
