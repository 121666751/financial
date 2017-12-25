package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 实时金额实体类
 * 
 * @author 林泽涛
 * @time 2017年10月28日 下午8:54:19
 */
public class CurrentMoneyInfo implements Entity{

	public SimpleStringProperty mingzi = new SimpleStringProperty();//名字
	public SimpleStringProperty shishiJine = new SimpleStringProperty();//实时金额
	public SimpleStringProperty wanjiaId = new SimpleStringProperty();//变动
	public SimpleStringProperty cmiEdu = new SimpleStringProperty();//类型
	public SimpleStringProperty color = new SimpleStringProperty();//颜色
	public SimpleStringProperty cmSuperIdSum = new SimpleStringProperty();//总和
	
	
	public CurrentMoneyInfo() {
		super();
	}

	public CurrentMoneyInfo(String mingzi, String shishiJine,String wanjiaId,String cmiEdu) {
		super();
		this.mingzi = new SimpleStringProperty(mingzi);
		this.shishiJine = new SimpleStringProperty(shishiJine);
		this.wanjiaId = new SimpleStringProperty(wanjiaId);
		this.cmiEdu = new SimpleStringProperty(cmiEdu);
	}
//	public CurrentMoneyInfo(String mingzi, String shishiJine) {
//		super();
//		this.mingzi = new SimpleStringProperty(mingzi);
//		this.shishiJine = new SimpleStringProperty(shishiJine);
//	}

	public SimpleStringProperty mingziProperty() {
		return this.mingzi;
	}
	

	public String getMingzi() {
		return this.mingziProperty().get();
	}
	

	public void setMingzi(final String mingzi) {
		this.mingziProperty().set(mingzi);
	}
	

	public SimpleStringProperty shishiJineProperty() {
		return this.shishiJine;
	}
	

	public String getShishiJine() {
		return this.shishiJineProperty().get();
	}
	

	public void setShishiJine(final String shishiJine) {
		this.shishiJineProperty().set(shishiJine);
	}
	

	public SimpleStringProperty wanjiaIdProperty() {
		return this.wanjiaId;
	}
	

	public String getWanjiaId() {
		return this.wanjiaIdProperty().get();
	}
	

	public void setWanjiaId(final String wanjiaId) {
		this.wanjiaIdProperty().set(wanjiaId);
	}
	

	public SimpleStringProperty cmiEduProperty() {
		return this.cmiEdu;
	}
	

	public String getCmiEdu() {
		return this.cmiEduProperty().get();
	}
	

	public void setCmiEdu(final String cmiEdu) {
		this.cmiEduProperty().set(cmiEdu);
	}
	
	//
	public SimpleStringProperty colorProperty() {
		return this.color;
	}
	public String getColor() {
		return this.colorProperty().get();
	}
	public void setColor(final String color) {
		this.colorProperty().set(color);
	}
	
	//
	public SimpleStringProperty cmSuperIdSumProperty() {
		return this.cmSuperIdSum;
	}
	public String getCmSuperIdSum() {
		return this.cmSuperIdSumProperty().get();
	}
	public void setCmSuperIdSum(final String cmSuperIdSum) {
		this.cmSuperIdSumProperty().set(cmSuperIdSum);
	}
	
	
	
	
	
}
