package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class zhaiwuInfo implements Entity{

	private SimpleStringProperty zhaiwuType = new SimpleStringProperty();//名字
	private SimpleStringProperty zhaiwuMoney = new SimpleStringProperty();//金额
	
	public zhaiwuInfo() {
		super();
	}
	public zhaiwuInfo(String zhaiwuType, String zhaiwuMoney) {
		super();
		this.zhaiwuType = new SimpleStringProperty(zhaiwuType);
		this.zhaiwuMoney = new SimpleStringProperty(zhaiwuMoney);
	}
	
	
	//=======================
	public SimpleStringProperty zhaiwuTypeProperty() {
		return this.zhaiwuType;
	}
	public String getZhaiwuType() {
		return this.zhaiwuTypeProperty().get();
	}
	public void setZhaiwuType(final String zhaiwuType) {
		this.zhaiwuTypeProperty().set(zhaiwuType);
	}
	//===========================
	public SimpleStringProperty zhaiwuMoneyProperty() {
		return this.zhaiwuMoney;
	}
	public String getZhaiwuMoney() {
		return this.zhaiwuMoneyProperty().get();
	}
	public void setZhaiwuMoney(final String zhaiwuMoney) {
		this.zhaiwuMoneyProperty().set(zhaiwuMoney);
	}
}
