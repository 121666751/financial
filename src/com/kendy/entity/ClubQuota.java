package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 联盟配额中的俱乐部配额记录
 * 
 * @author 林泽涛
 * @time 2017年12月16日 下午2:36:40
 */
public class ClubQuota implements Entity{

	private SimpleStringProperty quotaClubId = new SimpleStringProperty();//俱乐部ID
	private SimpleStringProperty quotaClubName = new SimpleStringProperty();//俱乐部名称
	private SimpleStringProperty quotaJieyu = new SimpleStringProperty("0");//俱乐部结余
	private SimpleStringProperty quotaRest = new SimpleStringProperty("0");//俱乐部剩余
	
	private SimpleStringProperty  quotaHedgeFirst = new SimpleStringProperty("");//对冲1
	private SimpleStringProperty  quotaHedgeSecond = new SimpleStringProperty("");//对冲2
	private SimpleStringProperty  quotaHedgeThree = new SimpleStringProperty("");//对冲3
	private SimpleStringProperty  quotaHedgeFour= new SimpleStringProperty("");//对冲4
	private SimpleStringProperty  quotaHedgeFive = new SimpleStringProperty();//对冲5
//	private SimpleStringProperty  quotaHedge6 = new SimpleStringProperty();//对冲6
//	private SimpleStringProperty  quotaHedge7 = new SimpleStringProperty();//对冲7
//	private SimpleStringProperty  quotaHedge8 = new SimpleStringProperty();//对冲8
//	private SimpleStringProperty  quotaHedge9 = new SimpleStringProperty();//对冲9
//	private SimpleStringProperty  quotaHedgeFirst0 = new SimpleStringProperty();//对冲10
	
	public ClubQuota() {
		super();
	}

	/**
	 * @param quotaClubId
	 * @param quotaClubName
	 * @param quotaJieyu
	 * @param quotaRest
	 */
	public ClubQuota(String quotaClubId, String quotaClubName,
			String quotaJieyu, String quotaRest) {
		super();
		this.quotaClubId = new SimpleStringProperty(quotaClubId);
		this.quotaClubName = new SimpleStringProperty(quotaClubName);
		this.quotaJieyu = new SimpleStringProperty(quotaJieyu);
		this.quotaRest = new SimpleStringProperty(quotaRest);
	}

	/***********************************************************/
	public SimpleStringProperty quotaClubIdProperty() {
		return this.quotaClubId;
	}
	public String getQuotaClubId() {
		return this.quotaClubIdProperty().get();
	}
	public void setEuotaClubId(final String quotaClubId) {
		this.quotaClubIdProperty().set(quotaClubId);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaClubNameProperty() {
		return this.quotaClubName;
	}
	public String getQuotaClubName() {
		return this.quotaClubNameProperty().get();
	}
	public void setQuotaClubName(final String quotaClubName) {
		this.quotaClubNameProperty().set(quotaClubName);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaJieyuProperty() {
		return this.quotaJieyu;
	}
	public String getQuotaJieyu() {
		return this.quotaJieyuProperty().get();
	}
	public void setQuotaJieyu(final String quotaJieyu) {
		this.quotaJieyuProperty().set(quotaJieyu);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaRestProperty() {
		return this.quotaRest;
	}
	public String getQuotaRest() {
		return this.quotaRestProperty().get();
	}
	public void setQuotaRest(final String quotaRest) {
		this.quotaRestProperty().set(quotaRest);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaHedgeFirstProperty() {
		return this.quotaHedgeFirst;
	}
	public String getQuotaHedgeFirst() {
		return this.quotaHedgeFirstProperty().get();
	}
	public void setQuotaHedgeFirst(final String quotaHedgeFirst) {
		this.quotaHedgeFirstProperty().set(quotaHedgeFirst);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaHedgeSecondProperty() {
		return this.quotaHedgeSecond;
	}
	public String getQuotaHedgeSecond() {
		return this.quotaHedgeSecondProperty().get();
	}
	public void setQuotaHedgeSecond(final String quotaHedgeSecond) {
		this.quotaHedgeSecondProperty().set(quotaHedgeSecond);
	}
	/***********************************************************/
	public SimpleStringProperty quotaHedgeThreeProperty() {
		return this.quotaHedgeThree;
	}
	public String getQuotaHedgeThree() {
		return this.quotaHedgeThreeProperty().get();
	}
	public void setQuotaHedgeThree(final String quotaHedgeThree) {
		this.quotaHedgeThreeProperty().set(quotaHedgeThree);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaHedgeFourProperty() {
		return this.quotaHedgeFour;
	}
	public String getQuotaHedgeFour() {
		return this.quotaHedgeFourProperty().get();
	}
	public void setQuotaHedgeFour(final String quotaHedgeFour) {
		this.quotaHedgeFourProperty().set(quotaHedgeFour);
	}
	
	/***********************************************************/
	public SimpleStringProperty quotaHedgeFiveProperty() {
		return this.quotaHedgeFive;
	}
	

	public String getQuotaHedgeFive() {
		return this.quotaHedgeFiveProperty().get();
	}
	

	public void setQuotaHedgeFive(final String quotaHedgeFive) {
		this.quotaHedgeFiveProperty().set(quotaHedgeFive);
	}
	
	/***********************************************************/
	
	
	/***********************************************************/
	
	
	/***********************************************************/
}
