package com.bravson.socialalert.infrastructure.entity;

import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeFromDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeToDocumentIdentifierContext;

public class DefaultStringIdentifierBridge implements IdentifierBridge<String> {

	@Override
	public String toDocumentIdentifier(String propertyValue, IdentifierBridgeToDocumentIdentifierContext context) {
		return propertyValue;
	}

	@Override
	public String fromDocumentIdentifier(String documentIdentifier, IdentifierBridgeFromDocumentIdentifierContext context) {
		return documentIdentifier;
	}

	@Override
	public boolean isCompatibleWith(IdentifierBridge<?> other) {
		return getClass().equals( other.getClass() );
	}
}

