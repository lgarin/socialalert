package com.bravson.socialalert.user;

import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import org.mongodb.morphia.annotations.Embedded;

import com.bravson.socialalert.infrastructure.rest.LocalDateDeserializer;
import com.bravson.socialalert.infrastructure.rest.LocalDateSerializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Embedded
public class UserAttributes {

	private Optional<String> birthdate = Optional.empty();
	private Optional<String> gender = Optional.empty();
	private Optional<String> country = Optional.empty();
	private Optional<String> language = Optional.empty();
	private Optional<String> imageUri = Optional.empty();
	private Optional<String> biography = Optional.empty();
	
	@JsonSerialize(using = LocalDateSerializer.class)
	public LocalDate getBirthdate() {
		return birthdate.map(LocalDate::parse).orElse(null);
	}
	
	@JsonDeserialize(using = LocalDateDeserializer.class)
	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = Optional.ofNullable(birthdate).map(LocalDate::toString);
	}
	
	public Gender getGender() {
		return gender.map(Gender::valueOf).orElse(null);
	}
	
	public void setGender(Gender gender) {
		this.gender = Optional.ofNullable(gender).map(Gender::name);
	}

	public String getCountry() {
		return country.orElse(null);
	}
	
	public void setCountry(String country) {
		this.country = Optional.ofNullable(country);
	}

	public String getLanguage() {
		return language.orElse(null);
	}
	
	public void setLanguage(String language) {
		this.language = Optional.ofNullable(language);
	}

	public URI getImageUri() {
		return imageUri.map(URI::create).orElse(null);
	}
	
	public void setImageUri(URI imageUri) {
		this.imageUri = Optional.ofNullable(imageUri).map(URI::toString);
	}

	public String getBiography() {
		return biography.orElse(null);
	}
	
	public void setBiography(String biography) {
		this.biography = Optional.ofNullable(biography);
	}
}
