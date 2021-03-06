package com.bravson.socialalert.infrastructure.entity;

import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

public class AnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
    	
    	context.tokenFilter("autocompleteFilter").type("edge_ngram")
	    	.param("min_gram", 3)
			.param("max_gram", 5);
    	
    	context.analyzer("languageAnalyzer").custom() 
        	.tokenizer("standard")
        	.tokenFilters("asciifolding", "lowercase", "porter_stem");
    	
    	context.analyzer("autocompleteReverseAnalyzer").custom()
    		.tokenizer("standard")
    		.tokenFilters("asciifolding", "lowercase", "reverse", "autocompleteFilter", "reverse");
    	
    	context.analyzer("autocompleteAnalyzer").custom()
    		.tokenizer("standard")
    		.tokenFilters("asciifolding", "lowercase", "autocompleteFilter");
    }

}
