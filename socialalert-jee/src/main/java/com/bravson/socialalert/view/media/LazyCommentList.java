package com.bravson.socialalert.view.media;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.bravson.socialalert.business.media.comment.MediaCommentService;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LazyCommentList extends LazyDataModel<MediaCommentInfo> {

	private static final long serialVersionUID = 1L;
	
	private String mediaUri;
	private Instant timestamp;
	private MediaCommentService commentService;
	
	@Override
	public List<MediaCommentInfo> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		System.out.println("Load f:" + first + " s:" + pageSize + " t:" + getRowCount());
		return commentService.listComments(mediaUri, new PagingParameter(timestamp, first / pageSize, pageSize)).getContent();
	}
	
	@Override
	public List<MediaCommentInfo> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		System.out.println("Load2 f:" + first + " s:" + pageSize + " t:" + getRowCount());
		return commentService.listComments(mediaUri, new PagingParameter(timestamp, first / pageSize, pageSize)).getContent();
	}
	
}
