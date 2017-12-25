package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 利润表
 *
 */
public class ProfitInfo implements Entity{

	public SimpleStringProperty profitType = new SimpleStringProperty();//资金类型
	public SimpleStringProperty profitAccount = new SimpleStringProperty();//金额
	
	
	public ProfitInfo() {
		super();
	}


	public ProfitInfo(String profitType, String profitAccount) {
		super();
		this.profitType = new SimpleStringProperty(profitType);
		this.profitAccount = new SimpleStringProperty(profitAccount);
	}


	public SimpleStringProperty profitTypeProperty() {
		return this.profitType;
	}
	public String getProfitType() {
		return this.profitTypeProperty().get();
	}
	public void setProfitType(final String profitType) {
		this.profitTypeProperty().set(profitType);
	}
	
	public SimpleStringProperty profitAccountProperty() {
		return this.profitAccount;
	}
	


	public String getProfitAccount() {
		return this.profitAccountProperty().get();
	}
	


	public void setProfitAccount(final String profitAccount) {
		this.profitAccountProperty().set(profitAccount);
	}
	


	
	
	
	
	
}
