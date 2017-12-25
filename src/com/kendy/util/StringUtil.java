package com.kendy.util;

/**
 * 自制字符串处理类
 * @author 林泽涛
 * @time 2017年10月7日 下午4:02:08
 */
public class StringUtil {
	
	public static boolean isBlank(String str){
		return str == null || "".equals(str);
	}
	
	//模拟Oracle的nvl函数
	public static String nvl(String condition,String ifNullStr) {
		if(!StringUtil.isBlank(condition)) {
			return condition.trim();
		}else {
			return ifNullStr;
		}
	}
	
	//是否其中一个为空
	public static boolean isAnyBlank(String... strings) {
		if(strings == null || strings.length == 0) return true;
	
		for(String str : strings) {
			if(isBlank(str)) {
				return true;
			}
		}
		return false;
	}
	
}
