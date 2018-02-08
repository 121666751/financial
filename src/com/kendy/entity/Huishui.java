package com.kendy.entity;

public class Huishui {

	//团id
	public String teamId;
	
	//团名
	public String teamName;
	
	//团队回水rate
	public String huishuiRate;
	
	//保险比例
	public String insuranceRate;
	
	//股东
	public String gudong;
	
	//战绩是否代管理
	public String zjManaged;
	
	//备注
	public String beizhu;
	
	//回水比例--回水的回水
	public String proxyHSRate = "0%";
	
	//回保比例
	public String proxyHBRate = "0%";
	
	//服务费有效值，比如大如1000，合计才会去计算服务费
	public String proxyFWF = "0";
	
//	public String jifenInput = "0";
	
	//=================== constructors ================================================
	public Huishui() {
		super();
	}


//	public Huishui(String teamId, String teamName, String gudong, String huishuiRate, String beizhu,
//			String insuranceRate, String zjManaged) {
//		super();
//		this.teamId = teamId;
//		this.teamName = teamName;
//		this.gudong = gudong;
//		this.huishuiRate = huishuiRate;
//		this.beizhu = beizhu;
//		this.insuranceRate = insuranceRate;
//		this.zjManaged = zjManaged;
//	}
//	
//
//	
//	
	
	//=================== getter and setter ================================================



	public String getTeamId() {
		return teamId;
	}
	public Huishui(String teamId, String teamName, String huishuiRate, String insuranceRate, String gudong,
			String zjManaged, String beizhu, String proxyHSRate, String proxyHBRate, String proxyFWF) {
		super();
		this.teamId = teamId;
		this.teamName = teamName;
		this.huishuiRate = huishuiRate;
		this.insuranceRate = insuranceRate;
		this.gudong = gudong;
		this.zjManaged = zjManaged;
		this.beizhu = beizhu;
		this.proxyHSRate = proxyHSRate;
		this.proxyHBRate = proxyHBRate;
		this.proxyFWF = proxyFWF;
	}


	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getGudong() {
		return gudong;
	}

	public void setGudong(String gudong) {
		this.gudong = gudong;
	}

	public String getHuishuiRate() {
		return huishuiRate;
	}

	public void setHuishuiRate(String huishuiRate) {
		this.huishuiRate = huishuiRate;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public String getInsuranceRate() {
		return insuranceRate;
	}

	public void setInsuranceRate(String insuranceRate) {
		this.insuranceRate = insuranceRate;
	}

	public String getZjManaged() {
		return zjManaged;
	}

	public void setZjManaged(String zjManaged) {
		this.zjManaged = zjManaged;
	}


	public String getProxyHBRate() {
		return proxyHBRate;
	}


	public void setProxyHBRate(String proxyHBRate) {
		this.proxyHBRate = proxyHBRate;
	}


	public String getProxyHSRate() {
		return proxyHSRate;
	}


	public void setProxyHSRate(String proxyHSRate) {
		this.proxyHSRate = proxyHSRate;
	}


	public String getProxyFWF() {
		return proxyFWF;
	}


	public void setProxyFWF(String proxyFWF) {
		this.proxyFWF = proxyFWF;
	}





	//=================== toString ================================================

	
	

	
}
