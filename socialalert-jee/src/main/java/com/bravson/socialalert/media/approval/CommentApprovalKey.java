package com.bravson.socialalert.media.approval;

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
public class CommentApprovalKey implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@NonNull
	private String commentId;
	
	@NonNull
	private String userId;
}
