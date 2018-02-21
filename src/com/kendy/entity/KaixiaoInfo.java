package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class KaixiaoInfo implements Entity{

	private SimpleStringProperty kaixiaoID = new SimpleStringProperty();//开销标识
	private SimpleStringProperty kaixiaoType = new SimpleStringProperty();//开销名字
	private SimpleStringProperty kaixiaoMoney = new SimpleStringProperty();//开销金额
	private SimpleStringProperty kaixiaoGudong = new SimpleStringProperty();//开销从属股东
	private SimpleStringProperty kaixiaoTime = new SimpleStringProperty();//开销时间
	
	
	public KaixiaoInfo() {
		super();
	}

	public KaixiaoInfo(String kaixiaoType, String kaixiaoMoney) {
		super();
		this.kaixiaoType = new SimpleStringProperty(kaixiaoType);
		this.kaixiaoMoney = new SimpleStringProperty(kaixiaoMoney);
	}
	
	

	/**
	 * @param kaixiaoID
	 * 
	 * @param kaixiaoType
	 * @param kaixiaoMoney
	 * @param kaixiaoGudong
	 * @param kaixiaoTime
	 */
	public KaixiaoInfo(String kaixiaoID, String kaixiaoType,
			String kaixiaoMoney, String kaixiaoGudong, String kaixiaoTime) {
		super();
		this.kaixiaoID = new SimpleStringProperty(kaixiaoID);
		this.kaixiaoType = new SimpleStringProperty(kaixiaoType);
		this.kaixiaoMoney = new SimpleStringProperty(kaixiaoMoney);
		this.kaixiaoGudong = new SimpleStringProperty(kaixiaoGudong);
		this.kaixiaoTime = new SimpleStringProperty(kaixiaoTime);
	}


	//=======================
	public SimpleStringProperty kaixiaoIDProperty() {
		return this.kaixiaoID;
	}
	public String getKaixiaoID() {
		return this.kaixiaoIDProperty().get();
	}
	public void setKaixiaoID(final String kaixiaoID) {
		this.kaixiaoIDProperty().set(kaixiaoID);
	}
	
	//=======================
	public SimpleStringProperty kaixiaoTimeProperty() {
		return this.kaixiaoTime;
	}
	public String getKaixiaoTime() {
		return this.kaixiaoTimeProperty().get();
	}
	public void setKaixiaoTime(final String kaixiaoTime) {
		this.kaixiaoTimeProperty().set(kaixiaoTime);
	}
	
	//=======================
	public SimpleStringProperty kaixiaoGudongProperty() {
		return this.kaixiaoGudong;
	}
	public String getKaixiaoGudong() {
		return this.kaixiaoGudongProperty().get();
	}
	public void setKaixiaoGudong(final String kaixiaoGudong) {
		this.kaixiaoGudongProperty().set(kaixiaoGudong);
	}
	
	
	//=======================
	public SimpleStringProperty kaixiaoTypeProperty() {
		return this.kaixiaoType;
	}

	public String getKaixiaoType() {
		return this.kaixiaoTypeProperty().get();
	}

	public void setKaixiaoType(final String kaixiaoType) {
		this.kaixiaoTypeProperty().set(kaixiaoType);
	}
	

	//=======================
	public SimpleStringProperty kaixiaoMoneyProperty() {
		return this.kaixiaoMoney;
	}

	public String getKaixiaoMoney() {
		return this.kaixiaoMoneyProperty().get();
	}

	public void setKaixiaoMoney(final String kaixiaoMoney) {
		this.kaixiaoMoneyProperty().set(kaixiaoMoney);
	}
	
	
	
	
	

	@Override
	public String toString() {
		return "KaixiaoInfo [kaixiaoType=" + kaixiaoType.get() + ", kaixiaoMoney=" + kaixiaoMoney.get() + "]";
	}
	
	
	
	
}
