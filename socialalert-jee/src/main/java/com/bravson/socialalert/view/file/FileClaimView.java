package com.bravson.socialalert.view.file;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.inject.Produces;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import com.bravson.socialalert.business.file.FileSearchService;
import com.bravson.socialalert.business.media.MediaConstants;
import com.bravson.socialalert.domain.file.FileInfo;
import com.bravson.socialalert.view.PageName;

import lombok.Getter;
import lombok.Setter;

@Named
@ConversationScoped
public class FileClaimView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter @Setter
	@NotEmpty @Size(max=MediaConstants.MAX_TITLE_LENGTH)
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
	FileSearchService fileSearchService;
	
	@Inject
	Conversation conversation;
	
	@Getter @Setter
	FileInfo selectedFile;
	
	@Produces
	@Named("fileUriConverter")
	public Converter createFileUriConverter() {
		return new Converter() {
			@Override
			public String getAsString(FacesContext context, UIComponent component, Object value) {
				if (value == null) {
					return null;
				}
				return ((FileInfo) value).getFileUri();
			}
			
			@Override
			public Object getAsObject(FacesContext context, UIComponent component, String value) {
				if (value == null) {
					return null;
				}
				return fileSearchService.findFileByUri(value).orElse(null);
			}
		};
	}
	
	public String updateLocation() {
		if (!conversation.isTransient()) {
			conversation.begin();
		}
		return PageName.INDEX;
	}
}
