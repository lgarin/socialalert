package com.bravson.socialalert.infrastructure.entity;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

public class AnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
    	context.analyzer("languageAnalyzer").custom() 
        	.withTokenizer("standard")
        	.withTokenFilters("asciifolding", "lowercase", "porter_stem");
    	
    	context.analyzer("autocompleteEdgeAnalyzer").type("edge_ngram")
    		.param("min_gram", 3)
    		.param("max_gram", 5)
    		.param("token_chars", "letter", "digit");
    	
    	context.analyzer("autocompleteNGramAnalyzer").type("ngram")
			.param("min_gram", 3)
			.param("max_gram", 3)
			.param("token_chars", "letter", "digit");
    }

}
