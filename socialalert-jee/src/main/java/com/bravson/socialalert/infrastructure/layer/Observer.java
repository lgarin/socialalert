package com.bravson.socialalert.infrastructure.layer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bravson.socialalert.infrastructure.log.Logged;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Stereotype;

@Stereotype
@Retention(RUNTIME)
@Target(TYPE)
@Logged
@RequestScoped
public @interface Observer {

}
