package com.bravson.socialalert.media;

import com.bravson.socialalert.domain.approval.ApprovalModifier;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MediaDetail extends MediaInfo {

	private ApprovalModifier userApprovalModifier;
}
