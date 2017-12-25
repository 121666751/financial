package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class ShangmaInfo implements Entity{

	public SimpleStringProperty shangmaName = new SimpleStringProperty();
	private SimpleStringProperty  shangmaEdu= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaAvailableEdu= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaYiSM= new SimpleStringProperty();//已上码
	private SimpleStringProperty  shangmaSumOfZJ= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaPlayerId= new SimpleStringProperty();//
	private SimpleStringProperty  shangmaYCJF= new SimpleStringProperty();//已存积分
	private SimpleStringProperty  shangmaLianheEdu= new SimpleStringProperty();//已存积分
	
	public ShangmaInfo() {
		super();
	}

	/**
	 * @param shangmaName,shangmaEdu,shangmaAvailableEdu,shangmaYiSM,shangmaSumOfZJ,shangmaShishou
	 */
	public ShangmaInfo(String shangmaName, String shangmaEdu,
			String shangmaAvailableEdu, String shangmaYiSM,
			String shangmaSumOfZJ, String shangmaPlayerId,String shangmaYCJF,
			String shangmaLianheEdu) {
		super();
		this.shangmaName = new SimpleStringProperty(shangmaName);
		this.shangmaEdu = new SimpleStringProperty(shangmaEdu);
		this.shangmaAvailableEdu = new SimpleStringProperty(shangmaAvailableEdu);
		this.shangmaYiSM = new SimpleStringProperty(shangmaYiSM);
		this.shangmaSumOfZJ = new SimpleStringProperty(shangmaSumOfZJ);
		this.shangmaPlayerId = new SimpleStringProperty(shangmaPlayerId);
		this.shangmaYCJF = new SimpleStringProperty(shangmaYCJF);
		this.shangmaLianheEdu = new SimpleStringProperty(shangmaLianheEdu);
	}



	//==========================================
	public SimpleStringProperty shangmaNameProperty() {
		return this.shangmaName;
	}

	public String getShangmaName() {
		return this.shangmaNameProperty().get();
	}

	public void setShangmaName(final String shangmaName) {
		this.shangmaNameProperty().set(shangmaName);
	}
	//==========================================
	public SimpleStringProperty shangmaEduProperty() {
		return this.shangmaEdu;
	}
	public String getShangmaEdu() {
		return this.shangmaEduProperty().get();
	}
	public void setShangmaEdu(final String shangmaEdu) {
		this.shangmaEduProperty().set(shangmaEdu);
	}	
	
	//==========================================
	public SimpleStringProperty shangmaAvailableEduProperty() {
		return this.shangmaAvailableEdu;
	}
	public String getShangmaAvailableEdu() {
		return this.shangmaAvailableEduProperty().get();
	}
	public void setShangmaAvailableEdu(final String shangmaAvailableEdu) {
		this.shangmaAvailableEduProperty().set(shangmaAvailableEdu);
	}
	
	//==========================================
	public SimpleStringProperty shangmaYiSMProperty() {
		return this.shangmaYiSM;
	}
	public String getShangmaYiSM() {
		return this.shangmaYiSMProperty().get();
	}
	public void setShangmaYiSM(final String shangmaYiSM) {
		this.shangmaYiSMProperty().set(shangmaYiSM);
	}
	
	
	//==========================================
	public SimpleStringProperty shangmaSumOfZJProperty() {
		return this.shangmaSumOfZJ;
	}
	public String getShangmaSumOfZJ() {
		return this.shangmaSumOfZJProperty().get();
	}
	public void setShangmaSumOfZJ(final String shangmaSumOfZJ) {
		this.shangmaSumOfZJProperty().set(shangmaSumOfZJ);
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
	public SimpleStringProperty shangmaYCJFProperty() {
		return this.shangmaYCJF;
	}
	public String getShangmaYCJF() {
		return this.shangmaYCJFProperty().get();
	}
	public void setShangmaYCJF(final String shangmaYCJF) {
		this.shangmaYCJFProperty().set(shangmaYCJF);
	}
	//==========================================
	public SimpleStringProperty shangmaLianheEduProperty() {
		return this.shangmaLianheEdu;
	}

	public String getShangmaLianheEdu() {
		return this.shangmaLianheEduProperty().get();
	}

	public void setShangmaLianheEdu(final String shangmaLianheEdu) {
		this.shangmaLianheEduProperty().set(shangmaLianheEdu);
	}
	
	
	
	

	

}
