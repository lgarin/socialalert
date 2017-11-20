package com.bravson.socialalert.test.repository;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Alternative;
import javax.enterprise.util.TypeLiteral;

@Alternative
public class DummyEvent<T> implements Event<T> {

	@Override
	public void fire(T event) {
	}
	
	@Override
	public Event<T> select(Annotation... qualifiers) {
		return new DummyEvent<T>();
	}
	
	@Override
	public <U extends T> Event<U> select(Class<U> subtype, Annotation... qualifiers) {
		return new DummyEvent<U>();
	}
	
	@Override
	public <U extends T> Event<U> select(TypeLiteral<U> subtype, Annotation... qualifiers) {
		return new DummyEvent<U>();
	}
}
