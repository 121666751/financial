package com.kendy.entity;

import java.io.Serializable;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class ZijinInfo implements Entity{

	public SimpleStringProperty zijinType = new SimpleStringProperty();//资金类型
	public SimpleStringProperty zijinAccount = new SimpleStringProperty();//金额
	
	
	public ZijinInfo() {
		super();
	}


	public ZijinInfo(String zijinType, String zijinAccount) {
		super();
		this.zijinType = new SimpleStringProperty(zijinType);
		this.zijinAccount = new SimpleStringProperty(zijinAccount);
	}


	public SimpleStringProperty zijinTypeProperty() {
		return this.zijinType;
	}
	


	public String getZijinType() {
		return this.zijinTypeProperty().get();
	}
	


	public void setZijinType(final String zijinType) {
		this.zijinTypeProperty().set(zijinType);
	}
	


	public SimpleStringProperty zijinAccountProperty() {
		return this.zijinAccount;
	}
	


	public String getZijinAccount() {
		return this.zijinAccountProperty().get();
	}
	


	public void setZijinAccount(final String zijinAccount) {
		this.zijinAccountProperty().set(zijinAccount);
	}
	
	
	
	
	
	
	
	
}
