package com.bravson.socialalert.media;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class MediaDetail extends MediaInfo {

	private ApprovalModifier userApprovalModifier;
}
