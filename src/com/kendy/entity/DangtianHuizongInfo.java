package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class DangtianHuizongInfo implements Entity{

	private SimpleStringProperty huizongType = new SimpleStringProperty();//类型
	private SimpleStringProperty huizongMoney = new SimpleStringProperty();//金额
	
	public DangtianHuizongInfo() {
		super();
	}

	public DangtianHuizongInfo(String huizongType, String huizongMoney) {
		this.huizongType = new SimpleStringProperty(huizongType);
		this.huizongMoney = new SimpleStringProperty(huizongMoney);
	}
	

	public SimpleStringProperty huizongTypeProperty() {
		return this.huizongType;
	}
	


	public String getHuizongTypeType() {
		return this.huizongTypeProperty().get();
	}
	


	public void setHuizongType(final String huizongType) {
		this.huizongTypeProperty().set(huizongType);
	}
	

//================================================================
	public SimpleStringProperty huizongMoneyProperty() {
		return this.huizongMoney;
	}

	public String getHuizongMoney() {
		return this.huizongMoneyProperty().get();
	}


	public void setHuizongMoney(final String huizongMoney) {
		this.huizongMoneyProperty().set(huizongMoney);
	}


}
