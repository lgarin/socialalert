package com.bravson.socialalert.business.feed.item;

import java.util.stream.Collectors;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.IdentifierBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.DocumentId;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.KeywordField;

import com.bravson.socialalert.business.media.comment.MediaCommentEntity;
import com.bravson.socialalert.business.media.entity.MediaEntity;
import com.bravson.socialalert.business.user.token.UserAccess;
import com.bravson.socialalert.domain.feed.FeedActivity;
import com.bravson.socialalert.domain.feed.FeedItemInfo;
import com.bravson.socialalert.infrastructure.entity.DefaultStringIdentifierBridge;
import com.bravson.socialalert.infrastructure.entity.FieldLength;
import com.bravson.socialalert.infrastructure.entity.VersionInfo;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Entity(name="FeedItem")
@Indexed(index = "FeedItem")
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@ToString(of="id")
@EqualsAndHashCode(of="id")
public class FeedItemEntity {

	@Getter
	@Id
	@Column(name = "id", length = FieldLength.ID)
	@DocumentId(identifierBridge = @IdentifierBridgeRef(type=DefaultStringIdentifierBridge.class))
	@GenericField
	@GenericGenerator(name="system-uuid", strategy = "uuid2")
	@GeneratedValue(generator="system-uuid")
	private String id;
	
	@Getter
	@ManyToOne(fetch=FetchType.EAGER, optional = false)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_FeedItem_Media"))
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private MediaEntity media;
	
	@Getter
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(foreignKey = @ForeignKey(name = "FK_FeedItem_Comment"))
	@IndexedEmbedded(includePaths= {"id"})
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
	private MediaCommentEntity comment;

	@Column(name = "activity", nullable = false)
	@Getter
	@NonNull
	@KeywordField
	private FeedActivity activity;
	
	@NonNull
	@Embedded
	@IndexedEmbedded
	private VersionInfo versionInfo;
	
	@Getter
	@Column(name = "text", length = FieldLength.TEXT)
	@FullTextField(analyzer="languageAnalyzer")
	private String text;
	
	@Getter
	@Column(name = "category", length = FieldLength.NAME)
	@GenericField
	private String category;
	
	@Getter
	@Column(name = "tags", length = FieldLength.TEXT)
	@FullTextField(analyzer="languageAnalyzer")
	private String tags;
	
	public FeedItemEntity(@NonNull MediaEntity media, MediaCommentEntity comment, @NonNull FeedActivity activity, @NonNull UserAccess userAccess) {
		this.media = media;
		this.comment = comment;
		this.activity = activity;
		category = media.getCategory();
		tags = media.getTags().stream().collect(Collectors.joining("\n"));
		text = comment != null ? comment.getComment() : media.getTitle();
		versionInfo = VersionInfo.of(userAccess.getUserId(), userAccess.getIpAddress());
	}
	
	public String getUserId() {
		return versionInfo.getUserId();
	}
	
	public FeedItemInfo toItemInfo() {
		FeedItemInfo info = new FeedItemInfo();
		info.setId(getId());
		info.setActivity(getActivity());
		info.setMedia(getMedia().toMediaInfo());
		if (getComment() != null) {
			info.setComment(getComment().toMediaCommentInfo());
		}
		info.setCreatorId(versionInfo.getUserId());
		info.setCreation(versionInfo.getCreation());
		return info;
	}
}
