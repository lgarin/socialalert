package com.bravson.socialalert.test.architecture;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.bravson.socialalert")
public class ArchitectureTest {

	@ArchTest
    public static final ArchRule PACKAGE_DEPENDENCIES = slices().matching("com.bravson.socialalert.(**)").should().beFreeOfCycles();
	
	@ArchTest
    public static final ArchRule LAYER_DEPENDENCIES = layeredArchitecture().consideringAllDependencies()
    		.layer("Test").definedBy("com.bravson.socialalert.test..")
    		.layer("Infratstructure").definedBy("com.bravson.socialalert.infrastructure..")
    		.layer("Domain").definedBy("com.bravson.socialalert.domain..")
    	    .layer("Facade").definedBy("com.bravson.socialalert.rest..")
    	    .layer("Business").definedBy("com.bravson.socialalert.business..")
    	    .whereLayer("Test").mayNotBeAccessedByAnyLayer()
    	    .whereLayer("Domain").mayOnlyBeAccessedByLayers("Facade", "Business", "Test")
    	    .whereLayer("Business").mayOnlyBeAccessedByLayers("Facade", "Test")
    	    .whereLayer("Facade").mayNotBeAccessedByAnyLayer();
}
