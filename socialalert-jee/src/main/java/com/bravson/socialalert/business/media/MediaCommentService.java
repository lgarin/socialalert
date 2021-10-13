package com.bravson.socialalert.business.media;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import com.bravson.socialalert.business.media.approval.CommentApprovalEntity;
import com.bravson.socialalert.business.media.approval.CommentApprovalRepository;
import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.comment.MediaCommentRepository;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.media.entity.MediaRepository;
import com.bravson.socialalert.business.user.UserInfoService;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.media.comment.MediaCommentDetail;
import com.bravson.socialalert.domain.media.comment.MediaCommentInfo;
import com.bravson.socialalert.domain.media.comment.UserCommentDetail;
import com.bravson.socialalert.domain.paging.PagingParameter;
import com.bravson.socialalert.domain.paging.QueryResult;
import com.bravson.socialalert.domain.user.UserInfo;
import com.bravson.socialalert.domain.user.approval.ApprovalModifier;
import com.bravson.socialalert.infrastructure.entity.DislikedEntity;
import com.bravson.socialalert.infrastructure.entity.LikedEntity;
import com.bravson.socialalert.infrastructure.entity.NewEntity;
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
	
	@Inject
	@LikedEntity
	Event<MediaCommentEntity> commentLikedEvent;
	
	@Inject
	@DislikedEntity
	Event<MediaCommentEntity> commentDislikedEvent;
	
	@Inject
	@NewEntity
	Event<MediaCommentEntity> newCommentEvent;
	
	public MediaCommentInfo createComment(@NonNull String mediaUri, @NonNull String comment, @NonNull UserAccess userAccess) {
		MediaEntity media = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		MediaCommentEntity entity = commentRepository.create(media.getId(), comment, userAccess);
		newCommentEvent.fire(entity);
		return userService.fillUserInfo(entity.toMediaCommentInfo());
	}

	public QueryResult<MediaCommentDetail> listMediaComments(@NonNull String mediaUri, @NonNull String userId, @NonNull PagingParameter paging) {
		MediaEntity media = mediaRepository.findMedia(mediaUri).orElseThrow(NotFoundException::new);
		QueryResult<MediaCommentDetail> result = commentRepository.searchByMediaUri(media.getId(), paging).map(MediaCommentEntity::toMediaCommentDetail);
		Map<String, ApprovalModifier> approvalMap = buildUserCommentApprovalMap(mediaUri, userId);
		userService.fillUserInfo(result.getContent());
		for (MediaCommentDetail comment : result.getContent()) {
			comment.setUserApprovalModifier(approvalMap.get(comment.getId()));
		}
		return result;
	}
	
	public QueryResult<UserCommentDetail> listUserComments(@NonNull String userId, @NonNull PagingParameter paging) {
		UserInfo userInfo = userService.findUserInfo(userId).orElseThrow(NotFoundException::new);
		return commentRepository.searchByUserId(userInfo.getId(), paging).map(MediaCommentEntity::toUserCommentDetail);
	}

	private Map<String, ApprovalModifier> buildUserCommentApprovalMap(String mediaUri, String userId) {
		List<CommentApprovalEntity> approvals = approvalRepository.findAllByMediaUri(mediaUri, userId);
		Map<String, ApprovalModifier> approvalMap = new HashMap<>(approvals.size());
		approvals.forEach(e -> approvalMap.put(e.getCommentId(), e.getModifier()));
		return approvalMap;
	}
	
	public MediaCommentDetail setApprovalModifier(@NonNull String commentId, ApprovalModifier modifier, @NonNull String userId) {
		MediaCommentEntity entity = commentRepository.find(commentId).orElseThrow(NotFoundException::new);
		
		ApprovalModifier oldModifier = approvalRepository.find(commentId, userId).map(CommentApprovalEntity::getModifier).orElse(null);
		ApprovalModifier newModifier = approvalRepository.changeApproval(entity, userId, modifier).map(CommentApprovalEntity::getModifier).orElse(null);

		if (oldModifier != newModifier) {
			entity.updateApprovalCount(oldModifier, newModifier);
			if (newModifier == ApprovalModifier.LIKE) {
				commentLikedEvent.fire(entity);
			} else if (newModifier == ApprovalModifier.DISLIKE) {
				commentDislikedEvent.fire(entity);
			}
		}
		
		MediaCommentDetail info = entity.toMediaCommentDetail();
		info.setUserApprovalModifier(newModifier);
		return userService.fillUserInfo(info);
	}
}
