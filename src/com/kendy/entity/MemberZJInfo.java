package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 会员当天战绩表实体
 * @author 林泽涛
 *
 */
public class MemberZJInfo implements Entity{

	private SimpleStringProperty memberJu = new SimpleStringProperty();//第几局
	private SimpleStringProperty memberZJ = new SimpleStringProperty();//战绩（实收）
	
	public MemberZJInfo() {
		super();
	}
	public MemberZJInfo(String memberJu, String memberZJ) {
		this.memberJu = new SimpleStringProperty(memberJu);
		this.memberZJ = new SimpleStringProperty(memberZJ);
	}
	
	public SimpleStringProperty memberJuProperty() {
		return this.memberJu;
	}
	public String getPingzhangType() {
		return this.memberJuProperty().get();
	}
	public void setPingzhangType(final String memberJu) {
		this.memberJuProperty().set(memberJu);
	}
	
	
	public SimpleStringProperty memberZJProperty() {
		return this.memberZJ;
	}
	public String getMemberZJ() {
		return this.memberZJProperty().get();
	}
	public void setMemberZJ(final String memberZJ) {
		this.memberZJProperty().set(memberZJ);
	}

}
