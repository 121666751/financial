package com.kendy.other;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.KaixiaoInfo;

public class Test {

	public static void main(String[] args) {
		// -0.000000 / -0.000000 = NaN
		Double a = -895.300000d ;
		Double b = -0.000000d ;
		//System.out.println( a / b);
		if(a.compareTo(0d) == 0 || b.compareTo(0d) == 0 || b.compareTo(-0.0) == 0) {
			System.out.println(a.compareTo(0d) == 0);
			System.out.println(b.compareTo(0d) == 0);
			System.out.println(b.compareTo(-0.0) == 0);
		}else {
			System.out.println(a.compareTo(0d) == 0);
			System.out.println(b.compareTo(0d) == 0);
			System.out.println(b.compareTo(-0.0) == 0);
			System.out.printf(" %f / %f = " + a / b +"\n", a, b);
		}
	}

}
