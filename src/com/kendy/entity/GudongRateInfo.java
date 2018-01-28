package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * Key-gudongProfit类型的TableView实体
 * 
 * @author 林泽涛
 * @time 2018年1月14日 下午6:36:24
 */
public class GudongRateInfo implements Entity{

	private SimpleStringProperty gudongName = new SimpleStringProperty();//类型
	private SimpleStringProperty gudongProfit = new SimpleStringProperty();//值
	private SimpleStringProperty id = new SimpleStringProperty("");//ID(备选项)
	private SimpleStringProperty description = new SimpleStringProperty("");//描述（备选项）
	
	public GudongRateInfo() {
		super();
	}
	public GudongRateInfo(String gudongName, String gudongProfit) {
		super();
		this.gudongName = new SimpleStringProperty(gudongName);
		this.gudongProfit = new SimpleStringProperty(gudongProfit);
	}
	public GudongRateInfo(String gudongName, String gudongProfit, String description) {
		super();
		this.gudongName = new SimpleStringProperty(gudongName);
		this.gudongProfit = new SimpleStringProperty(gudongProfit);
		this.description = new SimpleStringProperty(description);
	}
	 
	//=======================
	public SimpleStringProperty gudongNameProperty() {
		return this.gudongName;
	}
	public String getGudongName() {
		return this.gudongNameProperty().get();
	}
	public void setGudongName(final String gudongName) {
		this.gudongNameProperty().set(gudongName);
	}
	//============================
	public SimpleStringProperty gudongProfitProperty() {
		return this.gudongProfit;
	}
	public String getGudongProfit() {
		return this.gudongProfitProperty().get();
	}
	public void setGudongProfit(final String gudongProfit) {
		this.gudongProfitProperty().set(gudongProfit);
	}
	//============================
	public SimpleStringProperty idProperty() {
		return this.id;
	}
	public String getId() {
		return this.idProperty().get();
	}
	public void setId(final String id) {
		this.idProperty().set(id);
	}
	//=======================
	public SimpleStringProperty descriptionProperty() {
		return this.description;
	}
	public String getDescription() {
		return this.descriptionProperty().get();
	}
	public void setDescription(final String description) {
		this.descriptionProperty().set(description);
	}
	
}
