package com.bravson.socialalert.infrastructure.entity;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

import org.apache.lucene.analysis.core.KeywordTokenizerFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.ngram.NGramFilterFactory;
import org.apache.lucene.analysis.pattern.PatternReplaceFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.AnalyzerDefs;
import org.hibernate.search.annotations.IndexedEmbedded;
import org.hibernate.search.annotations.Parameter;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
@MappedSuperclass
@AnalyzerDefs({
	@AnalyzerDef(name = "languageAnalyzer",
	tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
	filters = {
		@TokenFilterDef(factory = LowerCaseFilterFactory.class),
		@TokenFilterDef(factory = SnowballPorterFilterFactory.class)
	}),
	
	@AnalyzerDef(name = "autocompleteEdgeAnalyzer",
	// Split input into tokens according to tokenizer
	tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
	filters = {
	 // Normalize token text to lowercase, as the user is unlikely to
	 // care about casing when searching for matches
	 @TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
	   @Parameter(name = "pattern", value = "([^a-zA-Z0-9\\.])"),
	   @Parameter(name = "replacement", value = " "),
	   @Parameter(name = "replace", value = "all") }),
	 @TokenFilterDef(factory = LowerCaseFilterFactory.class),
	 @TokenFilterDef(factory = StopFilterFactory.class),
	 // Index partial words starting at the front, so we can provide
	 // Autocomplete functionality
	 @TokenFilterDef(factory = EdgeNGramFilterFactory.class, params = {
	   @Parameter(name = "minGramSize", value = "3"),
	   @Parameter(name = "maxGramSize", value = "50") }) }),

	@AnalyzerDef(name = "autocompleteNGramAnalyzer",
	// Split input into tokens according to tokenizer
	tokenizer = @TokenizerDef(factory = StandardTokenizerFactory.class),
	filters = {
	 // Normalize token text to lowercase, as the user is unlikely to
	 // care about casing when searching for matches
	 @TokenFilterDef(factory = WordDelimiterFilterFactory.class),
	 @TokenFilterDef(factory = LowerCaseFilterFactory.class),
	 @TokenFilterDef(factory = NGramFilterFactory.class, params = {
	   @Parameter(name = "minGramSize", value = "3"),
	   @Parameter(name = "maxGramSize", value = "5") }),
	 @TokenFilterDef(factory = PatternReplaceFilterFactory.class, params = {
	   @Parameter(name = "pattern", value = "([^a-zA-Z0-9\\.])"),
	   @Parameter(name = "replacement", value = " "),
	   @Parameter(name = "replace", value = "all") })
	})
})
public abstract class VersionedEntity {

	@Id
	@Getter
	@NonNull
	protected String id;
	
	@Version
	private Integer version;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	protected VersionInfo versionInfo;
}
