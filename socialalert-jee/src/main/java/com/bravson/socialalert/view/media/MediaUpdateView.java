package com.bravson.socialalert.view.media;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.bravson.socialalert.business.media.MediaConstants;
import com.bravson.socialalert.business.media.MediaUpsertService;
import com.bravson.socialalert.business.media.UpsertMediaParameter;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.domain.media.MediaDetail;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;
import lombok.Setter;

@Named
@ConversationScoped
public class MediaUpdateView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	@NotBlank @Size(max=MediaConstants.MAX_TITLE_LENGTH)
	private String title;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_DESCRIPTION_LENGTH)
	private String description;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_CATEGORY_COUNT)
	private List<String> categories;
	
	@Getter @Setter
	@Size(max=MediaConstants.MAX_TAG_COUNT)
	private List<String> tags;
	
	@Inject
	Conversation conversation;
	
	@Getter
	@Setter
	MediaDetail selectedMedia;
	
	@Inject
	MediaUpsertService mediaUpsertService;
	
	@Inject
	UserAccess userAccess;
	
	public void startConversation() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
		if (selectedMedia != null) {
			title = selectedMedia.getTitle();
			description = selectedMedia.getDescription();
			categories = selectedMedia.getCategories();
			tags = selectedMedia.getTags();
		}
	}
	
	public String cancel() {
		return PageName.SHOW_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
	
	public String update() {
		if (tags == null) {
			tags = Collections.emptyList();
		}
		if (categories == null) {
			categories = Collections.emptyList();
		}
		UpsertMediaParameter param = UpsertMediaParameter.builder()
				.title(title).description(description)
				.categories(categories).tags(tags)
				.build();
		mediaUpsertService.updateMedia(selectedMedia.getMediaUri(), param, userAccess);
		conversation.end();
		return PageName.SHOW_MEDIA + "?faces-redirect=true&uri=" + selectedMedia.getMediaUri();
	}
}
