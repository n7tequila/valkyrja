package org.valkyrja2.mvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

/**
 * 合并页面impl
 *
 * @author Tequila
 * @create 2022/08/02 01:33
 **/
public class MergePageImpl<T> extends PageImpl<T> {

	/** 实际的最大个数，是多个数据合并后的数据 */
	private long amountTotal;
	
	/**
	 * Constructor of {@code PageImpl}.
	 *
	 * @param content the content of this page, must not be {@literal null}.
	 * @param pageable the paging information, must not be {@literal null}.
	 * @param total the total amount of items available. The total might be adapted considering the length of the content
	 *          given, if it is going to be the content of the last page. This is in place to mitigate inconsistencies.
	 * @param amountTotal 实际数据大小，是多个数据合并后的总数
	 */
	public MergePageImpl(List<T> content, Pageable pageable, long total, long amountTotal) {
		super(content, pageable, total);
		
		this.amountTotal = amountTotal;
	}

	/**
	 * Creates a new {@link PageImpl} with the given content. This will result in the created {@link Page} being identical
	 * to the entire {@link List}.
	 *
	 * @param content must not be {@literal null}.
	 */
	public MergePageImpl(List<T> content) {
		super(content);
	}

	public long getAmountTotal() {
		return amountTotal;
	}

	public void setAmountTotal(long amountTotal) {
		this.amountTotal = amountTotal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		MergePageImpl<?> mergePage = (MergePageImpl<?>) o;
		return amountTotal == mergePage.amountTotal;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), amountTotal);
	}
}
