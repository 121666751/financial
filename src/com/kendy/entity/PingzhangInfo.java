package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class PingzhangInfo implements Entity{

	private SimpleStringProperty pingZhangType = new SimpleStringProperty();//类型
	private SimpleStringProperty pingZhangMoney = new SimpleStringProperty();//金额
	
	public PingzhangInfo() {
		super();
	}

	public PingzhangInfo(String pingZhangType, String pingZhangMoney) {
		this.pingZhangType = new SimpleStringProperty(pingZhangType);
		this.pingZhangMoney = new SimpleStringProperty(pingZhangMoney);
	}
	

	public SimpleStringProperty pingZhangTypeProperty() {
		return this.pingZhangType;
	}
	


	public String getPingzhangType() {
		return this.pingZhangTypeProperty().get();
	}
	


	public void setPingzhangType(final String pingZhangType) {
		this.pingZhangTypeProperty().set(pingZhangType);
	}
	


	public SimpleStringProperty pingZhangMoneyProperty() {
		return this.pingZhangMoney;
	}
	


	public String getPingzhangMoney() {
		return this.pingZhangMoneyProperty().get();
	}
	


	public void setPingzhangMoney(final String pingZhangMoney) {
		this.pingZhangMoneyProperty().set(pingZhangMoney);
	}


}
