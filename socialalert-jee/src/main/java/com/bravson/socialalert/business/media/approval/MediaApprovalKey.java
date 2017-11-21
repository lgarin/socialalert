package com.bravson.socialalert.business.media.approval;

import java.io.Serializable;

import javax.persistence.Embeddable;

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
public class MediaApprovalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String mediaUri;
	
	@NonNull
	private String userId;
}
