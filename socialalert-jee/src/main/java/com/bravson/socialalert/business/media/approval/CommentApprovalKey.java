package com.bravson.socialalert.business.media.approval;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.search.engine.backend.types.Searchable;
import org.hibernate.search.mapper.pojo.bridge.IdentifierBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeFromDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.bridge.runtime.IdentifierBridgeToDocumentIdentifierContext;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

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
public class CommentApprovalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@GenericField(searchable = Searchable.NO)
	@NonNull
	private String commentId;
	
	@GenericField(searchable = Searchable.NO)
	@NonNull
	private String userId;
	
	public static class Bridge implements IdentifierBridge<CommentApprovalKey> {

		@Override
		public String toDocumentIdentifier(CommentApprovalKey propertyValue, IdentifierBridgeToDocumentIdentifierContext context) {
			return propertyValue.getCommentId() + "|" + propertyValue.getUserId();
		}
		
		@Override
		public CommentApprovalKey fromDocumentIdentifier(String documentIdentifier, IdentifierBridgeFromDocumentIdentifierContext context) {
			int index = documentIdentifier.indexOf('|');
			if (index > 0) {
				return new CommentApprovalKey(documentIdentifier.substring(0, index), documentIdentifier.substring(index + 1));
			}
			return null;
		}
	}
}
