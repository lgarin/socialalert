package com.bravson.socialalert.business.user.authentication;

import java.time.Instant;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Data
@Builder
@Setter(AccessLevel.NONE)
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationInfo {

	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@JsonbTypeSerializer(InstantSerializer.class)
	@JsonbTypeDeserializer(InstantDeserializer.class)
	@NonNull
	private Instant createdTimestamp;
	
	@JsonbProperty("email_verified")
	private boolean emailVerified;
}
