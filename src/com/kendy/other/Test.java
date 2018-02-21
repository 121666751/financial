package com.kendy.other;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.KaixiaoInfo;

public class Test {

	public static void main(String[] args) {
		String str = "[{\"kaixiaoGudong\":\"总公司\",\"kaixiaoID\":\"8771f3c9890e466fa8ef99aac366af25\",\"kaixiaoMoney\":\"200\",\"kaixiaoTime\":\"\",\"kaixiaoType\":\"1\"}]";
		List<KaixiaoInfo> KaixiaoInfoList = JSON.parseObject(str, new TypeReference<List<KaixiaoInfo>>() {});
		System.out.println("finishes..." + KaixiaoInfoList);
	}

}
