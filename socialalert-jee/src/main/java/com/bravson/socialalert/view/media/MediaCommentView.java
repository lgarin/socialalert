package com.bravson.socialalert.view.media;

import java.io.Serializable;
import java.time.Instant;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.bravson.socialalert.business.media.comment.MediaCommentService;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;
import lombok.Setter;

@Named
@ConversationScoped
public class MediaCommentView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	MediaCommentService commentService;

	@Getter
	@Setter
	String comment;
	
	@Inject
	Conversation conversation;
	
	@Getter
	@Setter
	String mediaUri;
	
	@Inject
	UserAccess userAccess;
	
	@Getter
	Instant timestamp;
	
	@Getter
	LazyCommentList commentList;
	
	
	public void startConversation() {
		if (conversation.isTransient()) {
			timestamp = Instant.now();
			conversation.begin();
			commentList =  new LazyCommentList(mediaUri, timestamp, commentService);
		}
	}
	
	public String postComment() {
		if (comment != null) {
			commentService.createComment(mediaUri, comment, userAccess);
			comment = null;
			timestamp = Instant.now();
			commentList =  new LazyCommentList(mediaUri, timestamp, commentService);
		}
		
		return PageName.COMMENT_MEDIA + "?faces-redirect=true&uri=" + mediaUri;
	}
}
