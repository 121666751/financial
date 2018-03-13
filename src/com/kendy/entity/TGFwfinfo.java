package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 托管工具中的服务费明细表
 * 
 * @author 林泽涛
 * @time 2018年3月13日 下午7:20:42
 */
public class TGFwfinfo implements Entity{
	
	private SimpleStringProperty tgFwfCompany = new SimpleStringProperty();
	private SimpleStringProperty tgFwfTeamId = new SimpleStringProperty();
	private SimpleStringProperty tgFwfHuishui = new SimpleStringProperty();
	private SimpleStringProperty tgFwfHuiBao = new SimpleStringProperty();
	private SimpleStringProperty tgFwfProfit = new SimpleStringProperty();
	
	private SimpleStringProperty tgFwfFanshui = new SimpleStringProperty();
	private SimpleStringProperty tgFwfFanbao = new SimpleStringProperty();
	
	private SimpleStringProperty tgFwfQuanshui = new SimpleStringProperty();
	private SimpleStringProperty tgFwfQuanbao = new SimpleStringProperty();
	private SimpleStringProperty tgFwfHeji = new SimpleStringProperty();
	
	
	public TGFwfinfo() {
		super();
	}


	/**
	 * @param tgFwfCompany
	 * @param tgFwfTeamId
	 * @param tgFwfHuishui
	 * @param tgFwfHuiBao
	 * @param tgFwfProfit
	 * @param tgFwfFanshui
	 * @param tgFwfFanbao
	 * @param tgFwfQuanshui
	 * @param tgFwfQuanbao
	 * @param tgFwfHeji
	 */
	public TGFwfinfo(String tgFwfCompany, String tgFwfTeamId,
			String tgFwfHuishui, String tgFwfHuiBao, String tgFwfProfit,
			String tgFwfFanshui, String tgFwfFanbao, String tgFwfQuanshui,
			String tgFwfQuanbao, String tgFwfHeji) {
		super();
		this.tgFwfCompany = new SimpleStringProperty(tgFwfCompany);
		this.tgFwfTeamId = new SimpleStringProperty(tgFwfTeamId);
		this.tgFwfHuishui = new SimpleStringProperty(tgFwfHuishui);
		this.tgFwfHuiBao = new SimpleStringProperty(tgFwfHuiBao);
		this.tgFwfProfit = new SimpleStringProperty(tgFwfProfit);
		this.tgFwfFanshui = new SimpleStringProperty(tgFwfFanshui);
		this.tgFwfFanbao = new SimpleStringProperty(tgFwfFanbao);
		this.tgFwfQuanshui = new SimpleStringProperty(tgFwfQuanshui);
		this.tgFwfQuanbao = new SimpleStringProperty(tgFwfQuanbao);
		this.tgFwfHeji = new SimpleStringProperty(tgFwfHeji);
	}


	/*****************************************************************tgFwfCompany***/
	public SimpleStringProperty tgFwfCompanyProperty() {
		return this.tgFwfCompany;
	}
	public String getTgFwfCompany() {
		return this.tgFwfCompanyProperty().get();
	}
	public void setTgFwfCompany(final String tgFwfCompany) {
		this.tgFwfCompanyProperty().set(tgFwfCompany);
	}
	/*****************************************************************tgFwfTeamId***/
	public SimpleStringProperty tgFwfTeamIdProperty() {
		return this.tgFwfTeamId;
	}
	public String getTgFwfTeamId() {
		return this.tgFwfTeamIdProperty().get();
	}
	public void setTgFwfTeamId(final String tgFwfTeamId) {
		this.tgFwfTeamIdProperty().set(tgFwfTeamId);
	}
	/*****************************************************************tgFwfFanshui***/
	public SimpleStringProperty tgFwfFanshuiProperty() {
		return this.tgFwfFanshui;
	}
	public String getTgFwfFanshui() {
		return this.tgFwfFanshuiProperty().get();
	}
	public void setTgFwfFanshui(final String tgFwfFanshui) {
		this.tgFwfFanshuiProperty().set(tgFwfFanshui);
	}
	/*****************************************************************tgFwfFanbao***/
	public SimpleStringProperty tgFwfFanbaoProperty() {
		return this.tgFwfFanbao;
	}
	public String getTgFwfFanbao() {
		return this.tgFwfFanbaoProperty().get();
	}
	public void setTgFwfFanbao(final String tgFwfFanbao) {
		this.tgFwfFanbaoProperty().set(tgFwfFanbao);
	}
	/*****************************************************************tgFwfHuishui***/
	public SimpleStringProperty tgFwfHuishuiProperty() {
		return this.tgFwfHuishui;
	}
	public String getTgFwfHuishui() {
		return this.tgFwfHuishuiProperty().get();
	}
	public void setTgFwfHuishui(final String tgFwfHuishui) {
		this.tgFwfHuishuiProperty().set(tgFwfHuishui);
	}
	/*****************************************************************tgFwfHuiBao***/
	public SimpleStringProperty tgFwfHuiBaoProperty() {
		return this.tgFwfHuiBao;
	}
	public String getTgFwfHuiBao() {
		return this.tgFwfHuiBaoProperty().get();
	}
	public void setTgFwfHuiBao(final String tgFwfHuiBao) {
		this.tgFwfHuiBaoProperty().set(tgFwfHuiBao);
	}

	/*****************************************************************tgFwfHeji***/
	public SimpleStringProperty tgFwfHejiProperty() {
		return this.tgFwfHeji;
	}
	public String getTgFwfHeji() {
		return this.tgFwfHejiProperty().get();
	}
	public void setTgFwfHeji(final String tgFwfHeji) {
		this.tgFwfHejiProperty().set(tgFwfHeji);
	}
	/*****************************************************************tgFwfQuanshui***/
	public SimpleStringProperty tgFwfQuanshuiProperty() {
		return this.tgFwfQuanshui;
	}
	public String getTgFwfQuanshui() {
		return this.tgFwfQuanshuiProperty().get();
	}
	public void setTgFwfQuanshui(final String tgFwfQuanshui) {
		this.tgFwfQuanshuiProperty().set(tgFwfQuanshui);
	}
	/*****************************************************************tgFwfQuanbao***/
	public SimpleStringProperty tgFwfQuanbaoProperty() {
		return this.tgFwfQuanbao;
	}
	public String getTgFwfQuanbao() {
		return this.tgFwfQuanbaoProperty().get();
	}
	public void setTgFwfQuanbao(final String tgFwfQuanbao) {
		this.tgFwfQuanbaoProperty().set(tgFwfQuanbao);
	}
	/*****************************************************************tgFwfProfit***/
	public SimpleStringProperty tgFwfProfitProperty() {
		return this.tgFwfProfit;
	}
	public String getTgFwfProfit() {
		return this.tgFwfProfitProperty().get();
	}
	public void setTgFwfProfit(final String tgFwfProfit) {
		this.tgFwfProfitProperty().set(tgFwfProfit);
	}

	
	
}

