package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 *联盟配账记录实体
 * 
 * @author 林泽涛
 * @time 2017年10月28日 下午8:54:19
 */
public class QuotaMoneyInfo implements Entity{

	public SimpleStringProperty quotaMoneyClubId = new SimpleStringProperty();//名字
	public SimpleStringProperty quotaMoney = new SimpleStringProperty();//实时金额
	public SimpleStringProperty quotaMoneyPaytor = new SimpleStringProperty();//变动
	public SimpleStringProperty quotaMoneyGather = new SimpleStringProperty();//类型
//	public SimpleStringProperty color = new SimpleStringProperty();//颜色
//	public SimpleStringProperty cmSuperIdSum = new SimpleStringProperty();//总和
	
	
	public QuotaMoneyInfo() {
		super();
	}

	public QuotaMoneyInfo(String quotaMoneyClubId, String quotaMoneyPaytor,String quotaMoney,String quotaMoneyGather) {
		super();
		this.quotaMoneyClubId = new SimpleStringProperty(quotaMoneyClubId);
		this.quotaMoney = new SimpleStringProperty(quotaMoney);
		this.quotaMoneyPaytor = new SimpleStringProperty(quotaMoneyPaytor);
		this.quotaMoneyGather = new SimpleStringProperty(quotaMoneyGather);
	}
	
	
	
	
	public SimpleStringProperty quotaMoneyProperty() {
		return this.quotaMoney;
	}
	public String getQuotaMoney() {
		return this.quotaMoneyProperty().get();
	}
	public void setQuotaMoney(final String quotaMoney) {
		this.quotaMoneyProperty().set(quotaMoney);
	}
	
	/*****************************************************************/
	public SimpleStringProperty quotaMoneyClubIdProperty() {
		return this.quotaMoneyClubId;
	}
	public String getQuotaMoneyClubId() {
		return this.quotaMoneyClubIdProperty().get();
	}
	public void setQuotaMoneyClubId(final String quotaMoneyClubId) {
		this.quotaMoneyClubIdProperty().set(quotaMoneyClubId);
	}
	
	/*****************************************************************/
	public SimpleStringProperty quotaMoneyPaytorProperty() {
		return this.quotaMoneyPaytor;
	}
	public String getQuotaMoneyPaytor() {
		return this.quotaMoneyPaytorProperty().get();
	}
	public void setQuotaMoneyPaytor(final String quotaMoneyPaytor) {
		this.quotaMoneyPaytorProperty().set(quotaMoneyPaytor);
	}
	
	/*****************************************************************/
	public SimpleStringProperty quotaMoneyGatherProperty() {
		return this.quotaMoneyGather;
	}
	public String getQuotaMoneyGather() {
		return this.quotaMoneyGatherProperty().get();
	}
	public void setQuotaMoneyGather(final String quotaMoneyGather) {
		this.quotaMoneyGatherProperty().set(quotaMoneyGather);
	}
	
//	/*****************************************************************/
//	public SimpleStringProperty colorProperty() {
//		return this.color;
//	}
//	public String getColor() {
//		return this.colorProperty().get();
//	}
//	public void setColor(final String color) {
//		this.colorProperty().set(color);
//	}
//	
//	/*****************************************************************/
//	public SimpleStringProperty cmSuperIdSumProperty() {
//		return this.cmSuperIdSum;
//	}
//	public String getCmSuperIdSum() {
//		return this.cmSuperIdSumProperty().get();
//	}
//	public void setCmSuperIdSum(final String cmSuperIdSum) {
//		this.cmSuperIdSumProperty().set(cmSuperIdSum);
//	}
	
	
	
	
	
}
