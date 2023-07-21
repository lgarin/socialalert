package com.bravson.socialalert.business.media.tag;

import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.infrastructure.entity.DefaultStringIdentifierBridge;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="TagIndex")
@Cacheable
@Indexed(index = "MediaTag")
@ToString(of="tag")
@EqualsAndHashCode(of="tag")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaTagEntity {

	@Getter
	@Id
	@Column(name = "tag", length = FieldLength.NAME)
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type=DefaultStringIdentifierBridge.class))
	@KeywordField(name = "tag", projectable = Projectable.YES)
	@FullTextField(name = "startTag", analyzer = "autocompleteAnalyzer")
	@FullTextField(name = "endTag", analyzer = "autocompleteReverseAnalyzer")
	@FullTextField(name = "langTag", analyzer = "languageAnalyzer")
	private String tag;
}
