package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 单个联盟详情表Model
 * 
 * @author 林泽涛
 * @time 2017年11月22日 下午9:22:57
 */
public class LMDetailInfo implements Entity{

	private SimpleStringProperty lmDetailTableId = new SimpleStringProperty();//名字
	private SimpleStringProperty lmDetailZJ = new SimpleStringProperty();//单个俱乐部具体战绩
	private SimpleStringProperty lmDetailInsure = new SimpleStringProperty();//单个俱乐部具体战绩
	private SimpleStringProperty lmDetailPersonCount = new SimpleStringProperty();//单个俱乐部具体人数
	
	public LMDetailInfo() {
		super();
	}

	/**
	 * 构造方法
	 * @param lmDetailTableId
	 * @param lmDetailZJ
	 * @param lmDetailInsure
	 * @param lmDetailPersonCount
	 */
	public LMDetailInfo(String lmDetailTableId, String lmDetailZJ,String lmDetailInsure, String lmDetailPersonCount) {
		super();
		this.lmDetailTableId = new SimpleStringProperty(lmDetailTableId);
		this.lmDetailZJ = new SimpleStringProperty(lmDetailZJ);
		this.lmDetailInsure = new SimpleStringProperty(lmDetailInsure);
		this.lmDetailPersonCount = new SimpleStringProperty(lmDetailPersonCount);
	}

	
	/*****************************************************************/
	public SimpleStringProperty lmDetailTableIdProperty() {
		return this.lmDetailTableId;
	}
	public String getLmDetailTableId() {
		return this.lmDetailTableIdProperty().get();
	}
	public void setLmDetailTableId(final String lmDetailTableId) {
		this.lmDetailTableIdProperty().set(lmDetailTableId);
	}
	

	/*****************************************************************/
	public SimpleStringProperty lmDetailZJProperty() {
		return this.lmDetailZJ;
	}
	public String getLmDetailZJ() {
		return this.lmDetailZJProperty().get();
	}
	public void setLmDetailZJ(final String lmDetailZJ) {
		this.lmDetailZJProperty().set(lmDetailZJ);
	}
	
	/*****************************************************************/
	public SimpleStringProperty lmDetailInsureProperty() {
		return this.lmDetailInsure;
	}
	public String getLmDetailInsure() {
		return this.lmDetailInsureProperty().get();
	}
	public void setLmDetailInsure(final String lmDetailInsure) {
		this.lmDetailInsureProperty().set(lmDetailInsure);
	}
	/*****************************************************************/
	public SimpleStringProperty lmDetailPersonCountProperty() {
		return this.lmDetailPersonCount;
	}
	public String getLmDetailPersonCount() {
		return this.lmDetailPersonCountProperty().get();
	}
	public void setLmDetailPersonCount(final String lmDetailPersonCount) {
		this.lmDetailPersonCountProperty().set(lmDetailPersonCount);
	}
	

	@Override
	public String toString() {
		return "LMDetailInfo [lmDetailTableId=" + lmDetailTableId.get() + ", lmDetailZJ=" + lmDetailZJ.get() + ", lmDetailInsure=" + lmDetailInsure.get()
				+ ", lmDetailPersonCount=" + lmDetailPersonCount.get() + "]";
	}
	
	
}
