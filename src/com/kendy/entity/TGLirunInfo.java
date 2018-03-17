package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 托管团队当天查询实体
 * 
 * @author 林泽涛
 * @time 2018年3月6日 下午10:15:23
 */
public class TGLirunInfo implements Entity{
	
	private SimpleStringProperty tgLirunDate = new SimpleStringProperty();
	private SimpleStringProperty tgLirunTotalProfit = new SimpleStringProperty();
	private SimpleStringProperty tgLirunTotalKaixiao = new SimpleStringProperty();
	private SimpleStringProperty tgLirunATMCompany = new SimpleStringProperty();
	private SimpleStringProperty tgLirunTGCompany = new SimpleStringProperty();
	private SimpleStringProperty tgLirunTeamProfit = new SimpleStringProperty();
	private SimpleStringProperty tgLirunHeji = new SimpleStringProperty();
	
	private SimpleStringProperty tgLirunRestHeji = new SimpleStringProperty();
	private SimpleStringProperty tgLirunCompanyName = new SimpleStringProperty();
//	private SimpleStringProperty tgBaoxian = new SimpleStringProperty();
	
	
	public TGLirunInfo() {
		super();
	}
	

	/**
	 * @param tgLirunDate
	 * @param tgLirunTotalProfit
	 * @param tgLirunTotalKaixiao
	 * @param tgLirunATMCompany
	 * @param tgLirunTGCompany
	 * @param tgLirunTeamProfit
	 * @param tgLirunHeji
	 */
	public TGLirunInfo(String tgLirunDate, String tgLirunTotalProfit,
			String tgLirunTotalKaixiao, String tgLirunATMCompany,
			String tgLirunTGCompany, String tgLirunTeamProfit,
			String tgLirunHeji, String tgLirunRestHeji, String tgLirunCompanyName) {
		super();
		this.tgLirunDate = new SimpleStringProperty(tgLirunDate);
		this.tgLirunTotalProfit = new SimpleStringProperty(tgLirunTotalProfit);
		this.tgLirunTotalKaixiao = new SimpleStringProperty(tgLirunTotalKaixiao);
		this.tgLirunATMCompany = new SimpleStringProperty(tgLirunATMCompany);
		this.tgLirunTGCompany = new SimpleStringProperty(tgLirunTGCompany);
		this.tgLirunTeamProfit = new SimpleStringProperty(tgLirunTeamProfit);
		this.tgLirunHeji = new SimpleStringProperty(tgLirunHeji);
		this.tgLirunRestHeji = new SimpleStringProperty(tgLirunRestHeji);
		this.tgLirunCompanyName = new SimpleStringProperty(tgLirunCompanyName);
	}

	/*****************************************************************tgLirunDate***/
	public SimpleStringProperty tgLirunDateProperty() {
		return this.tgLirunDate;
	}
	public String getTgLirunDate() {
		return this.tgLirunDateProperty().get();
	}
	public void setTgLirunDate(final String tgLirunDate) {
		this.tgLirunDateProperty().set(tgLirunDate);
	}
	/*****************************************************************tgLirunTotalProfit***/
	public SimpleStringProperty tgLirunTotalProfitProperty() {
		return this.tgLirunTotalProfit;
	}
	public String getTgLirunTotalProfit() {
		return this.tgLirunTotalProfitProperty().get();
	}
	public void setTgLirunTotalProfit(final String tgLirunTotalProfit) {
		this.tgLirunTotalProfitProperty().set(tgLirunTotalProfit);
	}
	/*****************************************************************tgLirunTotalKaixiao***/
	public SimpleStringProperty tgLirunTotalKaixiaoProperty() {
		return this.tgLirunTotalKaixiao;
	}
	public String getTgLirunTotalKaixiao() {
		return this.tgLirunTotalKaixiaoProperty().get();
	}
	public void setTgLirunTotalKaixiao(final String tgLirunTotalKaixiao) {
		this.tgLirunTotalKaixiaoProperty().set(tgLirunTotalKaixiao);
	}
	/*****************************************************************tgLirunATMCompany***/
	public SimpleStringProperty tgLirunATMCompanyProperty() {
		return this.tgLirunATMCompany;
	}
	public String getTgLirunATMCompany() {
		return this.tgLirunATMCompanyProperty().get();
	}
	public void setTgLirunATMCompany(final String tgLirunATMCompany) {
		this.tgLirunATMCompanyProperty().set(tgLirunATMCompany);
	}
	/*****************************************************************tgLirunTGCompany***/
	public SimpleStringProperty tgLirunTGCompanyProperty() {
		return this.tgLirunTGCompany;
	}
	public String getTgLirunTGCompany() {
		return this.tgLirunTGCompanyProperty().get();
	}
	public void setTgLirunTGCompany(final String tgLirunTGCompany) {
		this.tgLirunTGCompanyProperty().set(tgLirunTGCompany);
	}
	/*****************************************************************tgLirunTeamProfit***/
	public SimpleStringProperty tgLirunTeamProfitProperty() {
		return this.tgLirunTeamProfit;
	}
	public String getTgLirunTeamProfit() {
		return this.tgLirunTeamProfitProperty().get();
	}
	public void setTgLirunTeamProfit(final String tgLirunTeamProfit) {
		this.tgLirunTeamProfitProperty().set(tgLirunTeamProfit);
	}
	/*****************************************************************tgLirunHeji***/
	public SimpleStringProperty tgLirunHejiProperty() {
		return this.tgLirunHeji;
	}
	public String getTgLirunHeji() {
		return this.tgLirunHejiProperty().get();
	}
	public void setTgLirunHeji(final String tgLirunHeji) {
		this.tgLirunHejiProperty().set(tgLirunHeji);
	}


	@Override
	public String toString() {
		return "TGLirunInfo [tgLirunDate=" + tgLirunDate + ", tgLirunTotalProfit=" + tgLirunTotalProfit
				+ ", tgLirunTotalKaixiao=" + tgLirunTotalKaixiao + ", tgLirunATMCompany=" + tgLirunATMCompany
				+ ", tgLirunTGCompany=" + tgLirunTGCompany + ", tgLirunTeamProfit=" + tgLirunTeamProfit
				+ ", tgLirunHeji=" + tgLirunHeji + "]";
	}

	/*****************************************************************tgLirunRestHeji***/
	public SimpleStringProperty tgLirunRestHejiProperty() {
		return this.tgLirunRestHeji;
	}
	public String getTgLirunRestHeji() {
		return this.tgLirunRestHejiProperty().get();
	}
	public void setTgLirunRestHeji(final String tgLirunRestHeji) {
		this.tgLirunRestHejiProperty().set(tgLirunRestHeji);
	}
	/*****************************************************************tgLirunCompanyName***/
	public SimpleStringProperty tgLirunCompanyNameProperty() {
		return this.tgLirunCompanyName;
	}
	public String getTgLirunCompanyName() {
		return this.tgLirunCompanyNameProperty().get();
	}
	public void setTgLirunCompanyName(final String tgLirunCompanyName) {
		this.tgLirunCompanyNameProperty().set(tgLirunCompanyName);
	}
//
//	/*****************************************************************tgBaoxian***/
//	public SimpleStringProperty tgBaoxianProperty() {
//		return this.tgBaoxian;
//	}
//	public String getTgBaoxian() {
//		return this.tgBaoxianProperty().get();
//	}
//	public void setTgBaoxian(final String tgBaoxian) {
//		this.tgBaoxianProperty().set(tgBaoxian);
//	}


}
