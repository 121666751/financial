package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class KaixiaoInfo implements Entity{

	private SimpleStringProperty kaixiaoType = new SimpleStringProperty();//开销名字
	private SimpleStringProperty kaixiaoMoney = new SimpleStringProperty();//开销金额
	
	public KaixiaoInfo() {
		super();
	}

	public KaixiaoInfo(String kaixiaoType, String kaixiaoMoney) {
		super();
		this.kaixiaoType = new SimpleStringProperty(kaixiaoType);
		this.kaixiaoMoney = new SimpleStringProperty(kaixiaoMoney);
	}

	public SimpleStringProperty kaixiaoTypeProperty() {
		return this.kaixiaoType;
	}
	


	public String getKaixiaoType() {
		return this.kaixiaoTypeProperty().get();
	}
	


	public void setKaixiaoType(final String kaixiaoType) {
		this.kaixiaoTypeProperty().set(kaixiaoType);
	}
	


	public SimpleStringProperty kaixiaoMoneyProperty() {
		return this.kaixiaoMoney;
	}
	


	public String getKaixiaoMoney() {
		return this.kaixiaoMoneyProperty().get();
	}
	


	public void setKaixiaoMoney(final String kaixiaoMoney) {
		this.kaixiaoMoneyProperty().set(kaixiaoMoney);
	}
	
	
	
	
	

	@Override
	public String toString() {
		return "KaixiaoInfo [kaixiaoType=" + kaixiaoType.get() + ", kaixiaoMoney=" + kaixiaoMoney.get() + "]";
	}
	
	
	
	
}
