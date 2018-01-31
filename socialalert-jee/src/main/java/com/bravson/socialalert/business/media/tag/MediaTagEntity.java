package com.bravson.socialalert.business.media.tag;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Fields;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity(name="MediaTag")
@Indexed
@ToString(of="tag")
@EqualsAndHashCode(of="tag")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor
public class MediaTagEntity {

	@Id
	@Getter
	@Fields({
		  @Field(name = "id", analyze = Analyze.NO, store = Store.YES),
		  @Field(name = "edgeTag", index = Index.YES, store = Store.NO, analyzer = @Analyzer(definition = "autocompleteEdgeAnalyzer")),
		  @Field(name = "nGramTag", index = Index.YES, store = Store.NO, analyzer = @Analyzer(definition = "autocompleteNGramAnalyzer")),
		  @Field(name = "langTag", index = Index.YES, store = Store.NO, analyzer = @Analyzer(definition = "languageAnalyzer"))
	})
	private String tag;
}
