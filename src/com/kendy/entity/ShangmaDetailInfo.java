package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class ShangmaDetailInfo implements Entity{

	public SimpleStringProperty shangmaDetailName = new SimpleStringProperty();
	private SimpleStringProperty  shangmaSM= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaShishou= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaJu= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaPlayerId= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaPreSM= new SimpleStringProperty();//上一场上马，用于撤销时恢复数据
	private SimpleStringProperty  shangmaHasPayed= new SimpleStringProperty("否");//当局是否已支付
	
	public ShangmaDetailInfo() {
		super();
	}
	
	public ShangmaDetailInfo(String shangmaDetailName,
			String shangmaSM, String shangmaShishou,String shangmaPlayerId,String shangmaJu,
			String shangmaPreSM,String shangmaHasPayed) {
		super();
		this.shangmaDetailName = new SimpleStringProperty(shangmaDetailName);
		this.shangmaSM = new SimpleStringProperty(shangmaSM);
		this.shangmaShishou = new SimpleStringProperty(shangmaShishou);
		this.shangmaPlayerId = new SimpleStringProperty(shangmaPlayerId);
		this.shangmaJu = new SimpleStringProperty(shangmaJu);
		this.shangmaPreSM = new SimpleStringProperty(shangmaPreSM);
		this.shangmaHasPayed = new SimpleStringProperty(shangmaHasPayed);
	}



	//==========================================
	public SimpleStringProperty shangmaHasPayedProperty() {
		return this.shangmaHasPayed;
	}
	
	public String getShangmaHasPayed() {
		return this.shangmaHasPayedProperty().get();
	}
	
	public void setShangmaHasPayed(final String shangmaHasPayed) {
		this.shangmaHasPayedProperty().set(shangmaHasPayed);
	}
	//==========================================
	public SimpleStringProperty shangmaDetailNameProperty() {
		return this.shangmaDetailName;
	}

	public String getShangmaDetailName() {
		return this.shangmaDetailNameProperty().get();
	}

	public void setShangmaDetailName(final String shangmaDetailName) {
		this.shangmaDetailNameProperty().set(shangmaDetailName);
	}
	
	
	//==========================================
	public SimpleStringProperty shangmaSMProperty() {
		return this.shangmaSM;
	}
	public String getShangmaSM() {
		return this.shangmaSMProperty().get();
	}
	public void setShangmaSM(final String shangmaSM) {
		this.shangmaSMProperty().set(shangmaSM);
	}
	
	//==========================================
	
	public SimpleStringProperty shangmaShishouProperty() {
		return this.shangmaShishou;
	}
	public String getShangmaShishou() {
		return this.shangmaShishouProperty().get();
	}
	public void setShangmaShishou(final String shangmaShishou) {
		this.shangmaShishouProperty().set(shangmaShishou);
	}
	
	//==========================================
	public SimpleStringProperty shangmaJuProperty() {
		return this.shangmaJu;
	}
	public String getShangmaJu() {
		return this.shangmaJuProperty().get();
	}
	public void setShangmaJu(final String shangmaJu) {
		this.shangmaJuProperty().set(shangmaJu);
	}
	//==========================================
	public SimpleStringProperty shangmaPlayerIdProperty() {
		return this.shangmaPlayerId;
	}
	public String getShangmaPlayerId() {
		return this.shangmaPlayerIdProperty().get();
	}
	public void setShangmaPlayerId(final String shangmaPlayerId) {
		this.shangmaPlayerIdProperty().set(shangmaPlayerId);
	}
	//==========================================
	public SimpleStringProperty shangmaPreSMProperty() {
		return this.shangmaPreSM;
	}

	public String getShangmaPreSM() {
		return this.shangmaPreSMProperty().get();
	}

	public void setShangmaPreSM(final String shangmaPreSM) {
		this.shangmaPreSMProperty().set(shangmaPreSM);
	}

	
	
	
	

	

}
