package com.bravson.socialalert.domain.user.approval;

public enum ApprovalModifier {

	LIKE ,
	DISLIKE;
	
	public static int computeLikeDelta(ApprovalModifier oldModifier, ApprovalModifier newModifier) {
		if (newModifier == LIKE && oldModifier != LIKE) {
			return 1;
		} else if (oldModifier == LIKE && newModifier != LIKE) {
			return -1;
		} else {
			return 0;
		}
	}
	
	public static int computeDislikeDelta(ApprovalModifier oldModifier, ApprovalModifier newModifier) {
		if (newModifier == DISLIKE && oldModifier != DISLIKE) {
			return 1;
		} else if (oldModifier == DISLIKE && newModifier != DISLIKE) {
			return -1;
		} else {
			return 0;
		}
	}
}
