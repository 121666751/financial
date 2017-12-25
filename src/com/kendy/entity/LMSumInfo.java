package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 联盟总和表Model
 * 
 * @author 林泽涛
 * @time 2017年11月22日 下午9:22:57
 */
public class LMSumInfo implements Entity{

	private SimpleStringProperty lmSumName = new SimpleStringProperty("");//名字
	private SimpleStringProperty lmSumZJ = new SimpleStringProperty("");//单个俱乐部总战绩
	private SimpleStringProperty lmSumInsure = new SimpleStringProperty("");//单个俱乐部总战绩
	private SimpleStringProperty lmSumPersonCount = new SimpleStringProperty("");//单个俱乐部总人数
	
	public LMSumInfo() {
		super();
	}

	/**
	 * 构造方法
	 * @param lmSumName
	 * @param lmSumZJ
	 * @param lmSumInsure
	 * @param lmSumPersonCount
	 */
	public LMSumInfo(String lmSumName, String lmSumZJ,String lmSumInsure, String lmSumPersonCount) {
		super();
		this.lmSumName = new SimpleStringProperty(lmSumName);
		this.lmSumZJ = new SimpleStringProperty(lmSumZJ);
		this.lmSumInsure = new SimpleStringProperty(lmSumInsure);
		this.lmSumPersonCount = new SimpleStringProperty(lmSumPersonCount);
	}

	
	/*****************************************************************/
	public SimpleStringProperty lmSumNameProperty() {
		return this.lmSumName;
	}
	public String getLmSumName() {
		return this.lmSumNameProperty().get();
	}
	public void setLmSumName(final String lmSumName) {
		this.lmSumNameProperty().set(lmSumName);
	}
	

	/*****************************************************************/
	public SimpleStringProperty lmSumZJProperty() {
		return this.lmSumZJ;
	}
	public String getLmSumZJ() {
		return this.lmSumZJProperty().get();
	}
	public void setLmSumZJ(final String lmSumZJ) {
		this.lmSumZJProperty().set(lmSumZJ);
	}
	
	/*****************************************************************/
	public SimpleStringProperty lmSumInsureProperty() {
		return this.lmSumInsure;
	}
	public String getLmSumInsure() {
		return this.lmSumInsureProperty().get();
	}
	public void setLmSumInsure(final String lmSumInsure) {
		this.lmSumInsureProperty().set(lmSumInsure);
	}
	/*****************************************************************/
	public SimpleStringProperty lmSumPersonCountProperty() {
		return this.lmSumPersonCount;
	}
	public String getLmSumPersonCount() {
		return this.lmSumPersonCountProperty().get();
	}
	public void setLmSumPersonCount(final String lmSumPersonCount) {
		this.lmSumPersonCountProperty().set(lmSumPersonCount);
	}

	@Override
	public String toString() {
		return "LMSumInfo [lmSumName=" + lmSumName.get() + ", lmSumZJ=" + lmSumZJ.get() + ", lmSumInsure=" + lmSumInsure.get()
				+ ", lmSumPersonCount=" + lmSumPersonCount.get() + "]";
	}
	
	
}
