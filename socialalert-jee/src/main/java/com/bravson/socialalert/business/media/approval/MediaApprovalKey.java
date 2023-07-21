package com.bravson.socialalert.business.media.approval;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeFromDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeToDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import com.bravson.socialalert.infrastructure.entity.FieldLength;

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
public class MediaApprovalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "media_id", length = FieldLength.ID)
	@GenericField(searchable = Searchable.NO)
	@NonNull
	private String mediaUri;
	
	@Column(name = "user_id", length = FieldLength.ID)
	@GenericField(searchable = Searchable.NO)
	@NonNull
	private String userId;
	
	public static class Bridge implements IdentifierBridge<MediaApprovalKey> {

		@Override
		public String toDocumentIdentifier(MediaApprovalKey propertyValue, IdentifierBridgeToDocumentIdentifierContext context) {
			return propertyValue.getMediaUri() + "|" + propertyValue.getUserId();
		}
		
		@Override
		public MediaApprovalKey fromDocumentIdentifier(String documentIdentifier, IdentifierBridgeFromDocumentIdentifierContext context) {
			int index = documentIdentifier.indexOf('|');
			if (index > 0) {
				return new MediaApprovalKey(documentIdentifier.substring(0, index), documentIdentifier.substring(index + 1));
			}
			return null;
		}
	}
}
