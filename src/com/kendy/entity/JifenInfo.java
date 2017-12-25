package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class JifenInfo implements Entity{
//jfRank,jfPlayerName,jfValue
	private SimpleStringProperty jfRank = new SimpleStringProperty();//排名
	private SimpleStringProperty jfPlayerName = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty jfValue = new SimpleStringProperty();//积分值
	
	public JifenInfo() {
		super();
	}
	public JifenInfo(String jfRank, String jfPlayerName,String jfValue) {
		super();
		this.jfRank = new SimpleStringProperty(jfRank);
		this.jfPlayerName = new SimpleStringProperty(jfPlayerName);
		this.jfValue = new SimpleStringProperty(jfValue);
	}
	 
	//=======================
	public SimpleStringProperty jfRankProperty() {
		return this.jfRank;
	}
	public String getJfRank() {
		return this.jfRankProperty().get();
	}
	public void setJfRank(final String jfRank) {
		this.jfRankProperty().set(jfRank);
	}
	//============================
	public SimpleStringProperty jfPlayerNameProperty() {
		return this.jfPlayerName;
	}
	public String getJfPlayerName() {
		return this.jfPlayerNameProperty().get();
	}
	public void setJfPlayerName(final String jfPlayerName) {
		this.jfPlayerNameProperty().set(jfPlayerName);
	}
	//============================
	public SimpleStringProperty jfValueProperty() {
		return this.jfValue;
	}
	public String getJfValue() {
		return this.jfValueProperty().get();
	}
	public void setJfValue(final String jfValue) {
		this.jfValueProperty().set(jfValue);
	}
	
}
