package com.bravson.socialalert.infrastructure.db;

import java.util.Collection;
import java.util.Optional;

import org.mongodb.morphia.converters.Converters;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

public class OptionalConverter extends TypeConverter {
	private Converters defaultConverters;

	public OptionalConverter(Converters defaultConverters) {
		super(Optional.class);
		this.defaultConverters = defaultConverters;
	}

	@Override
	public Object encode(Object value, MappedField mappedField) {
		if (value == null) {
			return null;
		}

		Optional<?> optional = (Optional<?>) value;
		return optional.map(defaultConverters::encode).orElse(null);
	}

	@Override
	public Object decode(Class<?> type, Object fromDbObject, MappedField mappedField) {
		if (fromDbObject instanceof Collection) {
			Collection<?> collection = (Collection<?>) fromDbObject;
			return collection.stream().findFirst();
		}
		return Optional.ofNullable(fromDbObject);
	}
}
