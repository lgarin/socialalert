package com.bravson.socialalert.user;

import javax.annotation.ManagedBean;
import javax.annotation.Resource;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ManagedBean
@Data
@Builder
@AllArgsConstructor(access=AccessLevel.PRIVATE)
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@Setter(AccessLevel.NONE)
public class AuthenticationConfiguration {

	@Resource(name="loginUrl")
	String loginUrl;
	
	@Resource(name="logoutUrl")
	String logoutUrl;
	
	@Resource(name="userInfoUrl")
	String userInfoUrl;
	
	@Resource(name="loginClientId")
	String loginClientId;
	
	@Resource(name="clientSecret")
	String clientSecret;
}
