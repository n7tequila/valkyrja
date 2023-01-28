package org.valkyrja2.mvc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
import org.valkyrja2.util.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Page<?>对象的组装器
 * 将查询返回结果的Page对线各种的PO对象转换成VO对象
 *
 * @author Tequila
 * @create 2022/08/02 01:27
 **/
public class PageAssembler {

	private PageAssembler() {
		throw new IllegalStateException("Assembly class");
	}

	/**
	 * 将Page类型的po对象转换成vo对象
	 *
	 * @param page SpringPage对象
	 * @param c    转换操作接口对象
	 * @return {@link Page }<{@link R }>
	 * @author Tequila
	 * @date 2022/08/02 01:28
	 */
	public static <T, R> Page<R> pos2vos(Page<T> page, VOConvertOperator<T, R> c) {
		List<R> list = new ArrayList<>();
		for (T obj : page.getContent()) {
			list.add(c.convert(obj));
		}

		return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
	}

	/**
	 * 将Iterable类型的po对象转换成vo对象
	 *
	 * @param collection collection对象
	 * @param c          转换操作接口对象
	 * @return {@link Iterable }<{@link R }>
	 * @author Tequila
	 * @date 2022/08/02 01:32
	 */
	public static <T, R> Iterable<R> pos2vos(Iterable<T> collection, VOConvertOperator<T, R> c) {
		List<R> list = new ArrayList<>();
		collection.forEach(obj -> list.add(c.convert(obj)));
		
		return list;
	}

	/**
	 * 转换操作接口
	 *
	 * @param <S> 源数据
	 * @param <D> 转换后的数据
	 *
	 * @author Tequila
	 * @create 2022/08/02 01:28
	 **/
	@FunctionalInterface
	public interface VOConvertOperator<S, D> {

		/**
		 * 转换
		 *
		 * @param src src
		 * @return {@link D }
		 * @author Tequila
		 * @date 2022/08/02 01:29
		 */
		D convert(S src);
	}

	/**
	 * 将Page&lt;?&gt;对象进行合并
	 *
	 * @param pages 页面
	 * @return {@link Page }<{@link T }>
	 * @author Tequila
	 * @date 2022/08/02 01:56
	 */
	@SuppressWarnings({ "unchecked", "safevargs" })
	public static <T> Page<T> merge(Page<? extends T> ... pages) {
		List<T> mergeContent = new ArrayList<>();
		Pageable pageable = pages[0].getPageable();

		long maxTotal = 0L;
		long amountTotal = 0L;
		for (Page<? extends T> page: pages) {
			mergeContent.addAll(page.getContent());
			
			amountTotal += page.getTotalElements();
			if (page.getTotalElements() > maxTotal) {
				maxTotal = page.getTotalElements();
			}
		}

		/* 重新排序 */
		List<T> sortContent = mergeContent.stream().sorted((t1, t2) -> {
			while (pageable.getSort().iterator().hasNext()) {
				Order order = pageable.getSort().iterator().next();
				Comparable v1 = BeanUtils.getBeanPropertyValue(t1, order.getProperty());
				Comparable v2 = BeanUtils.getBeanPropertyValue(t2, order.getProperty());

				if (v1 != null && v2 != null) {
					int result = v1.compareTo(v2);
					if (result != 0) {  // 如果两个数据一致，则使用下一个配置进行排序
						if (order.isDescending()) {
							return result * -1;
						} else {
							return result;
						}
					}
				}
			}
			
			return 0;
		}).collect(Collectors.toList());
		
		return new MergePageImpl<>(sortContent, pageable, maxTotal, amountTotal);
	}

}
