package com.bravson.socialalert.infrastructure.layer;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Stereotype;

import com.bravson.socialalert.infrastructure.log.Logged;

@Stereotype
@Retention(RUNTIME)
@Target(TYPE)
@ManagedBean
@Logged
@ApplicationScoped
public @interface Repository {

}
