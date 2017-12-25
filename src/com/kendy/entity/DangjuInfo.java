package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class DangjuInfo implements Entity{

	private SimpleStringProperty type = new SimpleStringProperty();//类型
	private SimpleStringProperty money = new SimpleStringProperty();//金额
	
	public DangjuInfo() {
		super();
	}

	public DangjuInfo(String type, String money) {
		this.type = new SimpleStringProperty(type);
		this.money = new SimpleStringProperty(money);
	}
	

	public SimpleStringProperty typeProperty() {
		return this.type;
	}
	public String getType() {
		return this.typeProperty().get();
	}
	public void setType(final String type) {
		this.typeProperty().set(type);
	}
	


	public SimpleStringProperty moneyProperty() {
		return this.money;
	}
	


	public String getMoney() {
		return this.moneyProperty().get();
	}
	


	public void setMoney(final String money) {
		this.moneyProperty().set(money);
	}


}
