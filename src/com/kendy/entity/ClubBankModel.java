package com.kendy.entity;

/**
 * 关于俱乐部银行信息的模型域
 * 
 * @author 林泽涛
 * @time 2017年12月18日 下午10:11:24
 */
public class ClubBankModel {


	//俱乐部ID
	private String clubId ="";
	//俱乐部名称
	private String ClubName="";

	//移动支付类型
	private String mobilePayType = "支付宝";
	//联系人
	private String personName=""; 
	//手机号 
	private String phoneNumber="";
	//银行卡类型
	private String bankType = "银行卡";
	//银行卡信息
	private String bankAccountInfo="";
	
	
	public String getMobilePayType() {
		return mobilePayType;
	}
	public void setMobilePayType(String mobilePayType) {
		this.mobilePayType = mobilePayType;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getBankType() {
		return bankType;
	}
	public void setBankType(String bankType) {
		this.bankType = bankType;
	}
	public String getBankAccountInfo() {
		return bankAccountInfo;
	}
	public void setBankAccountInfo(String bankAccountInfo) {
		this.bankAccountInfo = bankAccountInfo;
	}
	public String getClubId() {
		return clubId;
	}
	public void setClubId(String clubId) {
		this.clubId = clubId;
	}
	
	public String getClubName() {
		return ClubName;
	}
	public void setClubName(String clubName) {
		ClubName = clubName;
	}
	
	@Override
	public String toString() {
		return "ClubBankModel [clubId=" + clubId + ", ClubName=" + ClubName + ", mobilePayType=" + mobilePayType
				+ ", personName=" + personName + ", phoneNumber=" + phoneNumber + ", bankType=" + bankType
				+ ", bankAccountInfo=" + bankAccountInfo + "]";
	}
	
	
	
	
	
	
	
}
