package com.kendy.util;

import java.util.Map;

/**
 * MAP工具类
 * 
 * @author 林泽涛
 * @time 2017年11月30日 下午10:22:23
 */
public class MapUtil {

	
	public static boolean isNullOrEmpty(Map<?,?> map) {
		return map == null || map.isEmpty();
	}
	
	public static boolean isHavaValue(Map<?,?> map) {
		return !isNullOrEmpty(map);
	}
}
