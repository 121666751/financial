package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

/**
 * 托管开销模型
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午9:15:20
 */
public class TGKaixiaoInfo implements Entity{
	
	private SimpleStringProperty tgKaixiaoEntityId = new SimpleStringProperty();//ID
	private SimpleStringProperty tgKaixiaoDate = new SimpleStringProperty();//日期
	private SimpleStringProperty tgKaixiaoPlayerName = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty tgKaixiaoPayItem = new SimpleStringProperty();//支出项目
	private SimpleStringProperty tgKaixiaoMoney = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty tgKaixiaoCompany = new SimpleStringProperty();//从属托管公司
	
	public TGKaixiaoInfo() {
		super();
	}

	public TGKaixiaoInfo(String tgKaixiaoEntityId, String tgKaixiaoDate, String tgKaixiaoPlayerName
			,String tgKaixiaoPayItem, String tgKaixiaoMoney, String tgKaixiaoCompany) {
		this.tgKaixiaoEntityId = new SimpleStringProperty(tgKaixiaoEntityId);
		this.tgKaixiaoDate = new SimpleStringProperty(tgKaixiaoDate);
		this.tgKaixiaoPlayerName = new SimpleStringProperty(tgKaixiaoPlayerName);
		this.tgKaixiaoPayItem = new SimpleStringProperty(tgKaixiaoPayItem);
		this.tgKaixiaoMoney = new SimpleStringProperty(tgKaixiaoMoney);
		this.tgKaixiaoCompany = new SimpleStringProperty(tgKaixiaoCompany);
	}
	
	//=====================
	public SimpleStringProperty tgKaixiaoDateProperty() {
		return this.tgKaixiaoDate;
	}
	public String getTgKaixiaoDate() {
		return this.tgKaixiaoDateProperty().get();
	}
	public void setTgKaixiaoDate(final String tgKaixiaoDate) {
		this.tgKaixiaoDateProperty().set(tgKaixiaoDate);
	}
	

	//=====================
	public SimpleStringProperty tgKaixiaoPlayerNameProperty() {
		return this.tgKaixiaoPlayerName;
	}
	public String getTgKaixiaoPlayerName() {
		return this.tgKaixiaoPlayerNameProperty().get();
	}
	public void setTgKaixiaoPlayerName(final String tgKaixiaoPlayerName) {
		this.tgKaixiaoPlayerNameProperty().set(tgKaixiaoPlayerName);
	}
	
	//=====================
	public SimpleStringProperty tgKaixiaoPayItemProperty() {
		return this.tgKaixiaoPayItem;
	}
	public String getTgKaixiaoPayItem() {
		return this.tgKaixiaoPayItemProperty().get();
	}
	public void setTgKaixiaoPayItem(final String tgKaixiaoPayItem) {
		this.tgKaixiaoPayItemProperty().set(tgKaixiaoPayItem);
	}
	

	//=====================
	public SimpleStringProperty tgKaixiaoMoneyProperty() {
		return this.tgKaixiaoMoney;
	}

	public String getTgKaixiaoMoney() {
		return this.tgKaixiaoMoneyProperty().get();
	}

	public void setTgKaixiaoMoney(final String tgKaixiaoMoney) {
		this.tgKaixiaoMoneyProperty().set(tgKaixiaoMoney);
	}
	
	//=====================
	public SimpleStringProperty tgKaixiaoEntityIdProperty() {
		return this.tgKaixiaoEntityId;
	}

	public String getTgKaixiaoEntityId() {
		return this.tgKaixiaoEntityIdProperty().get();
	}

	public void setTgKaixiaoEntityId(final String tgKaixiaoEntityId) {
		this.tgKaixiaoEntityIdProperty().set(tgKaixiaoEntityId);
	}
	//=====================
	public SimpleStringProperty tgKaixiaoCompanyProperty() {
		return this.tgKaixiaoCompany;
	}
	public String getTgKaixiaoCompany() {
		return this.tgKaixiaoCompanyProperty().get();
	}
	public void setTgKaixiaoCompany(final String tgKaixiaoCompany) {
		this.tgKaixiaoCompanyProperty().set(tgKaixiaoCompany);
	}
	


}
