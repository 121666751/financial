package com.kendy.util;

import java.util.Collection;
import java.util.List;

/**
 * collection简单的工具类
 * 针对List和Set接口
 * CollectionUtils已经实现了更多的功能
 * 
 * @author 林泽涛
 * @time 2017年11月30日 下午10:24:44
 */
public class CollectUtil {

	public static boolean isNullOrEmpty(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}
	
	public static boolean isHaveValue(Collection<?> collection) {
		return !isNullOrEmpty(collection);
	}
	
	
}
