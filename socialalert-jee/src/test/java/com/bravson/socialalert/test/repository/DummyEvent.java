package com.bravson.socialalert.test.repository;

import java.lang.annotation.Annotation;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.Priority;
import javax.enterprise.event.Event;
import javax.enterprise.event.NotificationOptions;
import javax.enterprise.inject.Alternative;
import javax.enterprise.util.TypeLiteral;

@Alternative
@Priority(1)
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

	@Override
	public <U extends T> CompletionStage<U> fireAsync(U event) {
		return CompletableFuture.completedFuture(event);
	}

	@Override
	public <U extends T> CompletionStage<U> fireAsync(U event, NotificationOptions options) {
		return CompletableFuture.completedFuture(event);
	}
}
