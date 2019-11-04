package com.bravson.socialalert.domain.paging;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Schema(description="Group the items in a page.")
@Data
@NoArgsConstructor(access=AccessLevel.PROTECTED)
@AllArgsConstructor(access=AccessLevel.PROTECTED)
public class QueryResult<T> {
	
	@NonNull
	private List<T> content;
	private int pageNumber;
	private int pageCount;
	
	private PagingParameter nextPage;
	
	public QueryResult(@NonNull List<T> content, long matchCount, @NonNull PagingParameter currentPaging) {
		this.content = content;
		this.pageNumber = currentPaging.getPageNumber();
		this.pageCount = (int) ((matchCount + currentPaging.getPageSize() - 1) / currentPaging.getPageSize());
		if (pageNumber + 1 < pageCount) {
			nextPage = new PagingParameter(currentPaging.getTimestamp(), pageNumber + 1, currentPaging.getPageSize());
		} else {
			nextPage = null;
		}
	}
	
	public <S> QueryResult<S> map(Function<T, S> mapper) {
		return new QueryResult<>(content.stream().map(mapper).collect(Collectors.toList()), pageNumber, pageCount, nextPage);
	}
}
