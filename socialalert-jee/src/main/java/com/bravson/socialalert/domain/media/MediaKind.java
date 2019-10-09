package com.bravson.socialalert.domain.media;

import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description="The kind of media.", type = SchemaType.STRING)
public enum MediaKind {

	PICTURE,
	VIDEO;
}
