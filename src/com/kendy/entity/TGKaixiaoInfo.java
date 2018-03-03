package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 托管开销模糊
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午9:15:20
 */
public class TGKaixiaoInfo implements Entity{

	private SimpleStringProperty entityId = new SimpleStringProperty();//ID
	private SimpleStringProperty date = new SimpleStringProperty();//日期
	private SimpleStringProperty playerName = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty payOutItem = new SimpleStringProperty();//支出项目
	private SimpleStringProperty payMoney = new SimpleStringProperty();//玩家名称
	
	public TGKaixiaoInfo() {
		super();
	}

	public TGKaixiaoInfo(String date, String playerName, String payOutItem, String payMoney) {
		this.date = new SimpleStringProperty(date);
		this.playerName = new SimpleStringProperty(playerName);
		this.payOutItem = new SimpleStringProperty(payOutItem);
		this.payMoney = new SimpleStringProperty(payMoney);
	}
	
	//=====================
	public SimpleStringProperty dateProperty() {
		return this.date;
	}
	public String getDate() {
		return this.dateProperty().get();
	}
	public void setDate(final String date) {
		this.dateProperty().set(date);
	}
	

	//=====================
	public SimpleStringProperty playerNameProperty() {
		return this.playerName;
	}
	public String getPlayerName() {
		return this.playerNameProperty().get();
	}
	public void setPlayerName(final String playerName) {
		this.playerNameProperty().set(playerName);
	}
	
	//=====================
	public SimpleStringProperty payOutItemProperty() {
		return this.payOutItem;
	}
	public String getPayOutItem() {
		return this.payOutItemProperty().get();
	}
	public void setPayOutItem(final String payOutItem) {
		this.payOutItemProperty().set(payOutItem);
	}
	

	//=====================
	public SimpleStringProperty payMoneyProperty() {
		return this.payMoney;
	}

	public String getPayMoney() {
		return this.payMoneyProperty().get();
	}

	public void setPayMoney(final String payMoney) {
		this.payMoneyProperty().set(payMoney);
	}
	
	//=====================
	public SimpleStringProperty entityIdProperty() {
		return this.entityId;
	}

	public String getEntityId() {
		return this.entityIdProperty().get();
	}

	public void setEntityId(final String entityId) {
		this.entityIdProperty().set(entityId);
	}
	


}
