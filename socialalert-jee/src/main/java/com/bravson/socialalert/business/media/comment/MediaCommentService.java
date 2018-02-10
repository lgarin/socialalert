package com.bravson.socialalert.business.media.comment;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.media.MediaRepository;
import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.user.UserAccess;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.layer.Service;

import lombok.NonNull;

@Service
@Transactional
public class MediaCommentService {

	@Inject
	MediaCommentRepository commentRepository;
	
	@Inject
	MediaRepository mediaRepository;
	
	@Inject
	UserInfoService userService;
	
	@Inject
	CommentApprovalRepository approvalRepository;
	
	public MediaCommentInfo createComment(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = commentRepository.create(mediaUri, comment, userAccess);
		return userService.fillUserInfo(entity.toMediaCommentInfo());
	}

	public QueryResult<MediaCommentInfo> listComments(@NonNull String mediaUri, @NonNull PagingParameter paging) {
		mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		QueryResult<MediaCommentInfo> result = commentRepository.listByMediaUri(mediaUri, paging).map(MediaCommentEntity::toMediaCommentInfo);
		userService.fillUserInfo(result.getContent());
		return result;
	}
	
	public MediaCommentDetail setCommentModifier(@NonNull String commentId, ApprovalModifier modifier, @NonNull String userId) {
		MediaCommentEntity entity = commentRepository.find(commentId).orElseThrow(NotFoundException::new);
		
		ApprovalModifier oldModifier = approvalRepository.find(commentId, userId).map(CommentApprovalEntity::getModifier).orElse(null);
		ApprovalModifier newModifier = approvalRepository.changeApproval(entity, userId, modifier).map(CommentApprovalEntity::getModifier).orElse(null);

		entity.updateApprovalCount(oldModifier, newModifier);
		
		MediaCommentDetail info = entity.toMediaCommentDetail();
		info.setUserApprovalModifier(newModifier);
		return userService.fillUserInfo(info);
	}
}
