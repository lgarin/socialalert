package com.bravson.socialalert.infrastructure.layer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;

import com.bravson.socialalert.infrastructure.log.Logged;

@Stereotype
@Retention(RUNTIME)
@Target(TYPE)
@Logged
@RequestScoped
public @interface Observer {

}
