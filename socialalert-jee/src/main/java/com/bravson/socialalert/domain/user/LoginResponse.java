package com.bravson.socialalert.domain.user;

import java.time.Instant;
import java.time.LocalDate;

import javax.json.bind.annotation.JsonbTypeDeserializer;
import javax.json.bind.annotation.JsonbTypeSerializer;

import com.bravson.socialalert.domain.user.statistic.UserStatistic;
import com.bravson.socialalert.infrastructure.rest.InstantDeserializer;
import com.bravson.socialalert.infrastructure.rest.InstantSerializer;
import com.bravson.socialalert.infrastructure.rest.LocalDateDeserializer;
import com.bravson.socialalert.infrastructure.rest.LocalDateSerializer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

	@NonNull
	private String accessToken;
	
	@NonNull
	private String id;
	
	@NonNull
	private String username;
	
	private String email;
	
	@JsonbTypeSerializer(InstantSerializer.class)
	@JsonbTypeDeserializer(InstantDeserializer.class)
	@NonNull
	private Instant createdTimestamp;
	
	private boolean online;
	
	@JsonbTypeSerializer(LocalDateSerializer.class)
	@JsonbTypeDeserializer(LocalDateDeserializer.class)
	private LocalDate birthdate;
	
	private Gender gender;
	
	private String country;
	
	private String language;
	
	private String imageUri;
	
	private String biography;
	
	private UserStatistic statistic;
}
