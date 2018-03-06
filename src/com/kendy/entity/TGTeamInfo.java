package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 托管团队当天查询实体
 * 
 * @author 林泽涛
 * @time 2018年3月6日 下午10:15:23
 */
public class TGTeamInfo implements Entity{
	
	private SimpleStringProperty tgTeamId = new SimpleStringProperty();
	private SimpleStringProperty tgPlayerId = new SimpleStringProperty();
	private SimpleStringProperty tgPlayerName = new SimpleStringProperty();
	private SimpleStringProperty tgYSZJ = new SimpleStringProperty();
	private SimpleStringProperty tgZJ25 = new SimpleStringProperty();
	private SimpleStringProperty tgZJUnknow = new SimpleStringProperty();
	private SimpleStringProperty tgProfit = new SimpleStringProperty();
	private SimpleStringProperty tgHuishui = new SimpleStringProperty();
	private SimpleStringProperty tgHuiBao = new SimpleStringProperty();
	private SimpleStringProperty tgBaoxian = new SimpleStringProperty();
	private SimpleStringProperty tgChangci = new SimpleStringProperty();
	
	
	public TGTeamInfo() {
		super();
	}
	
	/**
	 * @param tgTeamId 
	 * @param tgPlayerId
	 * @param tgPlayerName
	 * @param tgYSZJ
	 * @param tgZJ
	 * @param tgHuishui
	 * @param tgHuiBao
	 * @param tgTableId
	 */
	public TGTeamInfo(String tgTeamId, String tgPlayerId,
			String tgPlayerName, String tgYSZJ, String tgZJ,
			String tgHuishui, String tgHuiBao, 
			String tgBaoxian) {
		super();
		this.tgTeamId = new SimpleStringProperty(tgTeamId);
		this.tgPlayerId = new SimpleStringProperty(tgPlayerId);
		this.tgPlayerName = new SimpleStringProperty(tgPlayerName);
		this.tgYSZJ = new SimpleStringProperty(tgYSZJ);
		this.tgHuishui = new SimpleStringProperty(tgHuishui);
		this.tgHuiBao = new SimpleStringProperty(tgHuiBao);
		this.tgBaoxian = new SimpleStringProperty(tgBaoxian);
	}



	/*****************************************************************tgTeamId***/
	public SimpleStringProperty tgTeamIdProperty() {
		return this.tgTeamId;
	}
	public String getTgTeamId() {
		return this.tgTeamIdProperty().get();
	}
	public void setTgTeamId(final String tgTeamId) {
		this.tgTeamIdProperty().set(tgTeamId);
	}
	/*****************************************************************tgPlayerId***/
	public SimpleStringProperty tgPlayerIdProperty() {
		return this.tgPlayerId;
	}
	public String getTgPlayerId() {
		return this.tgPlayerIdProperty().get();
	}
	public void setTgPlayerId(final String tgPlayerId) {
		this.tgPlayerIdProperty().set(tgPlayerId);
	}
	/*****************************************************************tgPlayerName***/
	public SimpleStringProperty tgPlayerNameProperty() {
		return this.tgPlayerName;
	}
	public String getTgPlayerName() {
		return this.tgPlayerNameProperty().get();
	}
	public void setTgPlayerName(final String tgPlayerName) {
		this.tgPlayerNameProperty().set(tgPlayerName);
	}
	/*****************************************************************tgYSZJ***/
	public SimpleStringProperty tgYSZJProperty() {
		return this.tgYSZJ;
	}
	public String getTgYSZJ() {
		return this.tgYSZJProperty().get();
	}
	public void setTgYSZJ(final String tgYSZJ) {
		this.tgYSZJProperty().set(tgYSZJ);
	}
	/*****************************************************************tgHuishui***/
	public SimpleStringProperty tgHuishuiProperty() {
		return this.tgHuishui;
	}
	public String getTgHuishui() {
		return this.tgHuishuiProperty().get();
	}
	public void setTgHuishui(final String tgHuishui) {
		this.tgHuishuiProperty().set(tgHuishui);
	}
	/*****************************************************************tgHuiBao***/
	public SimpleStringProperty tgHuiBaoProperty() {
		return this.tgHuiBao;
	}
	public String getTgHuiBao() {
		return this.tgHuiBaoProperty().get();
	}
	public void setTgHuiBao(final String tgHuiBao) {
		this.tgHuiBaoProperty().set(tgHuiBao);
	}

	/*****************************************************************tgBaoxian***/
	public SimpleStringProperty tgBaoxianProperty() {
		return this.tgBaoxian;
	}
	public String getTgBaoxian() {
		return this.tgBaoxianProperty().get();
	}
	public void setTgBaoxian(final String tgBaoxian) {
		this.tgBaoxianProperty().set(tgBaoxian);
	}
	/*****************************************************************tgZJ25***/
	public SimpleStringProperty tgZJ25Property() {
		return this.tgZJ25;
	}
	public String getTgZJ25() {
		return this.tgZJ25Property().get();
	}
	public void setTgZJ25(final String tgZJ25) {
		this.tgZJ25Property().set(tgZJ25);
	}
	/*****************************************************************tgZJUnknow***/
	public SimpleStringProperty tgZJUnknowProperty() {
		return this.tgZJUnknow;
	}
	public String getTgZJUnknow() {
		return this.tgZJUnknowProperty().get();
	}
	public void setTgZJUnknow(final String tgZJUnknow) {
		this.tgZJUnknowProperty().set(tgZJUnknow);
	}
	/*****************************************************************tgProfit***/
	public SimpleStringProperty tgProfitProperty() {
		return this.tgProfit;
	}
	public String getTgProfit() {
		return this.tgProfitProperty().get();
	}
	public void setTgProfit(final String tgProfit) {
		this.tgProfitProperty().set(tgProfit);
	}
	/*****************************************************************tgChangci***/
	public SimpleStringProperty tgChangciProperty() {
		return this.tgChangci;
	}
	public String getTgChangci() {
		return this.tgChangciProperty().get();
	}
	public void setTgChangci(final String tgChangci) {
		this.tgChangciProperty().set(tgChangci);
	}


	
	
	
	
	

}
