package com.bravson.socialalert.business.media.approval;

import java.io.Serializable;

import javax.persistence.Embeddable;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.bridge.TwoWayStringBridge;

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
	
	@Field(analyze=Analyze.NO)
	@NonNull
	private String mediaUri;
	
	@Field(analyze=Analyze.NO)
	@NonNull
	private String userId;
	
	public static class Bridge implements TwoWayStringBridge {

		@Override
		public String objectToString(Object object) {
			if (object instanceof MediaApprovalKey) {
				MediaApprovalKey key = (MediaApprovalKey) object;
				return key.getMediaUri() + "|" + key.getUserId();
			}
			return null;
		}

		@Override
		public Object stringToObject(String stringValue) {
			int index = stringValue.indexOf('|');
			if (index > 0) {
				return new MediaApprovalKey(stringValue.substring(0, index), stringValue.substring(index + 1));
			}
			return null;
		}
	}
}
