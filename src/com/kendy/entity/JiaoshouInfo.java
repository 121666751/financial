package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class JiaoshouInfo implements Entity{

	public SimpleStringProperty jiaoshouType = new SimpleStringProperty();//类型
	private SimpleStringProperty jiaoshouMoney = new SimpleStringProperty();//金额
	
	public JiaoshouInfo() {
		super();
	}

	public JiaoshouInfo(String jiaoshouType, String jiaoshouMoney) {
		super();
		this.jiaoshouType = new SimpleStringProperty(jiaoshouType);
		this.jiaoshouMoney =new SimpleStringProperty( jiaoshouMoney);
	}
	
	public SimpleStringProperty jiaoshouTypeProperty() {
		return this.jiaoshouType;
	}
	


	public String getJiaoshouType() {
		return this.jiaoshouTypeProperty().get();
	}
	


	public void setJiaoshouType(final String jiaoshouType) {
		this.jiaoshouTypeProperty().set(jiaoshouType);
	}
	


	public SimpleStringProperty jiaoshouMoneyProperty() {
		return this.jiaoshouMoney;
	}
	


	public String getJiaoshouMoney() {
		return this.jiaoshouMoneyProperty().get();
	}
	


	public void setJiaoshouMoney(final String jiaoshouMoney) {
		this.jiaoshouMoneyProperty().set(jiaoshouMoney);
	}
	
	
	
	

	

}
