package com.kendy.entity;

import java.util.List;

/**
 * 托管公司模型
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午1:49:34
 */
public class TGCompanyModel {

	//托管公司名称
	private String tgCompanyName;
	//公司分红比例
	private String companyRate;
	//托管公司比例
	private String tgCompanyRate;
	//托管公司押金
	private String yajin;
	//托管公司额度
	private String edu;
	//托管公司的托管团队
	private String tgTeamsStr;
	//备注
	private String beizhu = "";
	
	//备选项：托管公司的托管团队列表
	private List<String> tgTeamList;
	
	
	public TGCompanyModel() {
		super();
	}
	

	/**
	 * 构造方法
	 * 
	 * @param tgCompanyName
	 * @param companyRate
	 * @param tgCompanyRate
	 * @param yajin
	 * @param edu
	 * @param tgTeamsStr
	 */
	public TGCompanyModel(String tgCompanyName, String companyRate, String tgCompanyRate, String yajin, String edu,
			String tgTeamsStr) {
		super();
		this.tgCompanyName = tgCompanyName;
		this.companyRate = companyRate;
		this.tgCompanyRate = tgCompanyRate;
		this.yajin = yajin;
		this.edu = edu;
		this.tgTeamsStr = tgTeamsStr;
	}


	public String getTgCompanyName() {
		return tgCompanyName;
	}

	public void setTgCompanyName(String tgCompanyName) {
		this.tgCompanyName = tgCompanyName;
	}

	public String getCompanyRate() {
		return companyRate;
	}

	public void setCompanyRate(String companyRate) {
		this.companyRate = companyRate;
	}

	public String getTgCompanyRate() {
		return tgCompanyRate;
	}

	public void setTgCompanyRate(String tgCompanyRate) {
		this.tgCompanyRate = tgCompanyRate;
	}

	public String getYajin() {
		return yajin;
	}

	public void setYajin(String yajin) {
		this.yajin = yajin;
	}

	public String getEdu() {
		return edu;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	public String getTgTeamsStr() {
		return tgTeamsStr;
	}

	public void setTgTeamsStr(String tgTeamsStr) {
		this.tgTeamsStr = tgTeamsStr;
	}

	public String getBeizhu() {
		return beizhu;
	}

	public void setBeizhu(String beizhu) {
		this.beizhu = beizhu;
	}

	public List<String> getTgTeamList() {
		return tgTeamList;
	}

	public void setTgTeamList(List<String> tgTeamList) {
		this.tgTeamList = tgTeamList;
	}

	@Override
	public String toString() {
		return "TGCompanyModel [tgCompanyName=" + tgCompanyName + ", companyRate=" + companyRate + ", tgCompanyRate="
				+ tgCompanyRate + ", yajin=" + yajin + ", edu=" + edu + ", tgTeamsStr=" + tgTeamsStr + ", beizhu="
				+ beizhu + ", tgTeamList=" + tgTeamList + "]";
	}
	
	
}
