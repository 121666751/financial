package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 俱乐部银行卡信息
 * 
 * @author 林泽涛
 * @time 2017年12月18日 下午9:13:22
 */
public class ClubBankInfo implements Entity{

	public SimpleStringProperty clubId = new SimpleStringProperty();//俱乐部ID
	public SimpleStringProperty clubName = new SimpleStringProperty();//俱乐部名称
	public SimpleStringProperty mobilePayType = new SimpleStringProperty("支付宝");//移动手机付款类型，默认为“支付宝”
	public SimpleStringProperty personName = new SimpleStringProperty();//联系人姓名
	public SimpleStringProperty phoneNumber = new SimpleStringProperty();//联系人手机号
	public SimpleStringProperty bankType = new SimpleStringProperty("银行卡");//银行卡类型，默认为银行卡
	public SimpleStringProperty bankAccountInfo = new SimpleStringProperty();//银行卡信息
//	public SimpleStringProperty personName = new SimpleStringProperty();//颜色
//	public SimpleStringProperty phoneNumber = new SimpleStringProperty();//总和
	
	
	public ClubBankInfo() {
		super();
	}

//	public QuotaMoneyInfo(String clubId, String clubName,String quotaMoney,String mobilePayType) {
//		super();
//		this.clubId = new SimpleStringProperty(clubId);
//		this.quotaMoney = new SimpleStringProperty(quotaMoney);
//		this.clubName = new SimpleStringProperty(clubName);
//		this.mobilePayType = new SimpleStringProperty(mobilePayType);
//	}
//	
	
	/*****************************************************************/
	public SimpleStringProperty clubIdProperty() {
		return this.clubId;
	}
	public String getClubId() {
		return this.clubIdProperty().get();
	}
	public void setClubId(final String clubId) {
		this.clubIdProperty().set(clubId);
	}
	
	/*****************************************************************/
	public SimpleStringProperty clubNameProperty() {
		return this.clubName;
	}
	public String getClubName() {
		return this.clubNameProperty().get();
	}
	public void setClubName(final String clubName) {
		this.clubNameProperty().set(clubName);
	}
	
	/*****************************************************************/
	public SimpleStringProperty mobilePayTypeProperty() {
		return this.mobilePayType;
	}
	public String getMobilePayType() {
		return this.mobilePayTypeProperty().get();
	}
	public void setMobilePayType(final String mobilePayType) {
		this.mobilePayTypeProperty().set(mobilePayType);
	}
	
	/*****************************************************************/
	public SimpleStringProperty personNameProperty() {
		return this.personName;
	}
	public String getPersonName() {
		return this.personNameProperty().get();
	}
	public void setPersonName(final String personName) {
		this.personNameProperty().set(personName);
	}
	
	/*****************************************************************/
	public SimpleStringProperty phoneNumberProperty() {
		return this.phoneNumber;
	}
	public String getPhoneNumber() {
		return this.phoneNumberProperty().get();
	}
	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumberProperty().set(phoneNumber);
	}
	/*****************************************************************/
	public SimpleStringProperty bankTypeProperty() {
		return this.bankType;
	}
	public String getBankType() {
		return this.bankTypeProperty().get();
	}
	public void setBankType(final String bankType) {
		this.bankTypeProperty().set(bankType);
	}
	/*****************************************************************/
	public SimpleStringProperty bankAccountInfoProperty() {
		return this.bankAccountInfo;
	}
	public String getBankAccountInfo() {
		return this.bankAccountInfoProperty().get();
	}
	public void setBankAccountInfo(final String bankAccountInfo) {
		this.bankAccountInfoProperty().set(bankAccountInfo);
	}
	
	
	

}
