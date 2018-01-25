package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 股东输入类型的TableView实体
 * 
 * @author 林泽涛
 * @time 2018年1月14日 下午6:36:24
 */
public class GDInputInfo implements Entity{

	private SimpleStringProperty type = new SimpleStringProperty();//股东
	private SimpleStringProperty rate = new SimpleStringProperty();//占比
	private SimpleStringProperty value = new SimpleStringProperty();//数值
	private SimpleStringProperty id = new SimpleStringProperty("");//ID(备选项)
	private SimpleStringProperty description = new SimpleStringProperty("");//描述（备选项）
	
	public GDInputInfo() {
		super();
	}
	public GDInputInfo(String type, String rate) {
		super();
		this.type = new SimpleStringProperty(type);
		this.rate = new SimpleStringProperty(rate);
	}
	 
	public SimpleStringProperty rateProperty() {
		return this.rate;
	}
	public String getRate() {
		return this.rateProperty().get();
	}
	public void setRate(final String rate) {
		this.rateProperty().set(rate);
	}
	//=======================
	public SimpleStringProperty typeProperty() {
		return this.type;
	}
	public String getType() {
		return this.typeProperty().get();
	}
	public void setType(final String type) {
		this.typeProperty().set(type);
	}
	//============================
	public SimpleStringProperty valueProperty() {
		return this.value;
	}
	public String getValue() {
		return this.valueProperty().get();
	}
	public void setValue(final String value) {
		this.valueProperty().set(value);
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
