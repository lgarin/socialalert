package com.bravson.socialalert.business.user.link;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeFromDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeToDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter(AccessLevel.PROTECTED)
@Indexed
public class UserLinkKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@KeywordField
	@NonNull
	private String sourceUserId;
	
	@KeywordField
	@NonNull
	private String targetUserId;
	
	public static class Bridge implements IdentifierBridge<UserLinkKey> {

		@Override
		public String toDocumentIdentifier(UserLinkKey key, IdentifierBridgeToDocumentIdentifierContext context) {
			if (key == null) {
				return null;
			}
			return key.getSourceUserId() + "|" + key.getTargetUserId();
		}

		@Override
		public UserLinkKey fromDocumentIdentifier(String documentIdentifier, IdentifierBridgeFromDocumentIdentifierContext context) {
			int index = documentIdentifier.indexOf('|');
			if (index > 0) {
				return new UserLinkKey(documentIdentifier.substring(0, index), documentIdentifier.substring(index + 1));
			}
			return null;
		}
	}

}
