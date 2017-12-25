package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class WaizhaiInfo implements Entity{

	private SimpleStringProperty waizhaiType = new SimpleStringProperty();//名字
	private SimpleStringProperty waizhaiMoney = new SimpleStringProperty();//金额
	
	public WaizhaiInfo() {
		super();
	}
	public WaizhaiInfo(String waizhaiType, String waizhaiMoney) {
		super();
		this.waizhaiType = new SimpleStringProperty(waizhaiType);
		this.waizhaiMoney = new SimpleStringProperty(waizhaiMoney);
	}
	
	//=======================
	public SimpleStringProperty waizhaiTypeProperty() {
		return this.waizhaiType;
	}
	public String getWaizhaiType() {
		return this.waizhaiTypeProperty().get();
	}
	public void setWaizhaiType(final String waizhaiType) {
		this.waizhaiTypeProperty().set(waizhaiType);
	}
	//============================
	public SimpleStringProperty waizhaiMoneyProperty() {
		return this.waizhaiMoney;
	}
	public String getWaizhaiMoney() {
		return this.waizhaiMoneyProperty().get();
	}
	public void setWaizhaiMoney(final String waizhaiMoney) {
		this.waizhaiMoneyProperty().set(waizhaiMoney);
	}
	
}
