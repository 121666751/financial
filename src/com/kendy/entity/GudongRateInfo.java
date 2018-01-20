package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * Key-gudongProfitRate类型的TableView实体
 * 
 * @author 林泽涛
 * @time 2018年1月14日 下午6:36:24
 */
public class GudongRateInfo implements Entity{

	private SimpleStringProperty gudongName = new SimpleStringProperty();//类型
	private SimpleStringProperty gudongProfitRate = new SimpleStringProperty();//值
	private SimpleStringProperty id = new SimpleStringProperty("");//ID(备选项)
	private SimpleStringProperty description = new SimpleStringProperty("");//描述（备选项）
	
	public GudongRateInfo() {
		super();
	}
	public GudongRateInfo(String gudongName, String gudongProfitRate) {
		super();
		this.gudongName = new SimpleStringProperty(gudongName);
		this.gudongProfitRate = new SimpleStringProperty(gudongProfitRate);
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
	public SimpleStringProperty gudongProfitRateProperty() {
		return this.gudongProfitRate;
	}
	public String getGudongProfitRate() {
		return this.gudongProfitRateProperty().get();
	}
	public void setGudongProfitRate(final String gudongProfitRate) {
		this.gudongProfitRateProperty().set(gudongProfitRate);
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
