package com.bravson.socialalert.business.media.tag;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.infrastructure.entity.DefaultStringIdentifierBridge;
import com.bravson.socialalert.infrastructure.entity.FieldLength;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="TagIndex")
@Indexed
@ToString(of="tag")
@EqualsAndHashCode(of="tag")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaTagEntity {

	@Id
	@Column(name = "tag", length = FieldLength.NAME)
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type=DefaultStringIdentifierBridge.class))
	@GenericField
	@Getter
	@KeywordField(name="id")
	@FullTextField(name = "edgeTag", analyzer = "autocompleteEdgeAnalyzer")
	@FullTextField(name = "nGramTag", analyzer = "autocompleteNGramAnalyzer")
	@FullTextField(name = "langTag", analyzer = "languageAnalyzer")
	private String tag;
}
