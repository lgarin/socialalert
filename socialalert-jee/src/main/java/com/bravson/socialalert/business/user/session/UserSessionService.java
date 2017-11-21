package com.bravson.socialalert.business.user.session;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Service
@Transactional(TxType.SUPPORTS)
@SessionScoped
@NoArgsConstructor(access=AccessLevel.PROTECTED)
public class UserSessionService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Set<String> viewedMedia = new HashSet<>();
	
	public boolean addViewedMedia(String mediaUri) {
		return viewedMedia.add(mediaUri);
	}
}
