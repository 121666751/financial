package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class ZonghuiKaixiaoInfo implements Entity{
	private SimpleStringProperty zonghuiKaixiaoType = new SimpleStringProperty();//名字
	private SimpleStringProperty zonghuiKaixiaoMoney = new SimpleStringProperty();//金额
	
	public ZonghuiKaixiaoInfo() {
		super();
	}

	public ZonghuiKaixiaoInfo(String zonghuiKaixiaoType, String zonghuiKaixiaoMoney) {
		super();
		this.zonghuiKaixiaoType = new SimpleStringProperty(zonghuiKaixiaoType);
		this.zonghuiKaixiaoMoney = new SimpleStringProperty(zonghuiKaixiaoMoney);
	}

	
	public SimpleStringProperty zonghuiKaixiaoTypeProperty() {
		return this.zonghuiKaixiaoType;
	}
	


	public String getZonghuiKaixiaoType() {
		return this.zonghuiKaixiaoTypeProperty().get();
	}
	


	public void setZonghuiKaixiaoType(final String zonghuiKaixiaoType) {
		this.zonghuiKaixiaoTypeProperty().set(zonghuiKaixiaoType);
	}
	
	//-------------------------------
	public SimpleStringProperty zonghuiKaixiaoMoneyProperty() {
		return this.zonghuiKaixiaoMoney;
	}
	


	public String getZonghuiKaixiaoMoney() {
		return this.zonghuiKaixiaoMoneyProperty().get();
	}
	


	public void setZonghuiKaixiaoMoney(final String zonghuiKaixiaoMoney) {
		this.zonghuiKaixiaoMoneyProperty().set(zonghuiKaixiaoMoney);
	}
}
