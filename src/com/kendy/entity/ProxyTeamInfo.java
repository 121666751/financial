package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * w代理查询中的团队当天查询中的总和
 * @author 林泽涛
 *
 */
public class ProxyTeamInfo implements Entity{
	private SimpleStringProperty proxyTeamId = new SimpleStringProperty();
	private SimpleStringProperty proxyPlayerId = new SimpleStringProperty();
	private SimpleStringProperty proxyPlayerName = new SimpleStringProperty();
	private SimpleStringProperty proxyYSZJ = new SimpleStringProperty();
	private SimpleStringProperty proxyZJ = new SimpleStringProperty();
	private SimpleStringProperty proxyHuishui = new SimpleStringProperty();
	private SimpleStringProperty proxyHuiBao = new SimpleStringProperty();
	private SimpleStringProperty proxyTableId = new SimpleStringProperty();
	private SimpleStringProperty proxyBaoxian = new SimpleStringProperty();
	
	
	public ProxyTeamInfo() {
		super();
	}
	
	/**
	 * @param proxyTeamId 
	 * @param proxyPlayerId
	 * @param proxyPlayerName
	 * @param proxyYSZJ
	 * @param proxyZJ
	 * @param proxyHuishui
	 * @param proxyHuiBao
	 * @param proxyTableId
	 */
	public ProxyTeamInfo(String proxyTeamId, String proxyPlayerId,
			String proxyPlayerName, String proxyYSZJ, String proxyZJ,
			String proxyHuishui, String proxyHuiBao, String proxyTableId,
			String proxyBaoxian) {
		super();
		this.proxyTeamId = new SimpleStringProperty(proxyTeamId);
		this.proxyPlayerId = new SimpleStringProperty(proxyPlayerId);
		this.proxyPlayerName = new SimpleStringProperty(proxyPlayerName);
		this.proxyYSZJ = new SimpleStringProperty(proxyYSZJ);
		this.proxyZJ = new SimpleStringProperty(proxyZJ);
		this.proxyHuishui = new SimpleStringProperty(proxyHuishui);
		this.proxyHuiBao = new SimpleStringProperty(proxyHuiBao);
		this.proxyTableId = new SimpleStringProperty(proxyTableId);
		this.proxyBaoxian = new SimpleStringProperty(proxyBaoxian);
	}



	/*****************************************************************proxyTeamId***/
	public SimpleStringProperty proxyTeamIdProperty() {
		return this.proxyTeamId;
	}
	public String getProxyTeamId() {
		return this.proxyTeamIdProperty().get();
	}
	public void setProxyTeamId(final String proxyTeamId) {
		this.proxyTeamIdProperty().set(proxyTeamId);
	}
	/*****************************************************************proxyPlayerId***/
	public SimpleStringProperty proxyPlayerIdProperty() {
		return this.proxyPlayerId;
	}
	public String getProxyPlayerId() {
		return this.proxyPlayerIdProperty().get();
	}
	public void setProxyPlayerId(final String proxyPlayerId) {
		this.proxyPlayerIdProperty().set(proxyPlayerId);
	}
	/*****************************************************************proxyPlayerName***/
	public SimpleStringProperty proxyPlayerNameProperty() {
		return this.proxyPlayerName;
	}
	public String getProxyPlayerName() {
		return this.proxyPlayerNameProperty().get();
	}
	public void setProxyPlayerName(final String proxyPlayerName) {
		this.proxyPlayerNameProperty().set(proxyPlayerName);
	}
	/*****************************************************************proxyYSZJ***/
	public SimpleStringProperty proxyYSZJProperty() {
		return this.proxyYSZJ;
	}
	public String getProxyYSZJ() {
		return this.proxyYSZJProperty().get();
	}
	public void setProxyYSZJ(final String proxyYSZJ) {
		this.proxyYSZJProperty().set(proxyYSZJ);
	}
	/*****************************************************************proxyZJ***/
	public SimpleStringProperty proxyZJProperty() {
		return this.proxyZJ;
	}
	public String getProxyZJ() {
		return this.proxyZJProperty().get();
	}
	public void setProxyZJ(final String proxyZJ) {
		this.proxyZJProperty().set(proxyZJ);
	}
	/*****************************************************************proxyHuishui***/
	public SimpleStringProperty proxyHuishuiProperty() {
		return this.proxyHuishui;
	}
	public String getProxyHuishui() {
		return this.proxyHuishuiProperty().get();
	}
	public void setProxyHuishui(final String proxyHuishui) {
		this.proxyHuishuiProperty().set(proxyHuishui);
	}
	/*****************************************************************proxyHuiBao***/
	public SimpleStringProperty proxyHuiBaoProperty() {
		return this.proxyHuiBao;
	}
	public String getProxyHuiBao() {
		return this.proxyHuiBaoProperty().get();
	}
	public void setProxyHuiBao(final String proxyHuiBao) {
		this.proxyHuiBaoProperty().set(proxyHuiBao);
	}
	/*****************************************************************proxyTableId***/
	public SimpleStringProperty proxyTableIdProperty() {
		return this.proxyTableId;
	}
	public String getProxyTableId() {
		return this.proxyTableIdProperty().get();
	}
	public void setProxyTableId(final String proxyTableId) {
		this.proxyTableIdProperty().set(proxyTableId);
	}
	/*****************************************************************proxyBaoxian***/
	public SimpleStringProperty proxyBaoxianProperty() {
		return this.proxyBaoxian;
	}
	public String getProxyBaoxian() {
		return this.proxyBaoxianProperty().get();
	}
	public void setProxyBaoxian(final String proxyBaoxian) {
		this.proxyBaoxianProperty().set(proxyBaoxian);
	}


	@Override
	public String toString() {
		return "ProxyTeamInfo [proxyTeamId=" + proxyTeamId.get() + ", proxyPlayerId=" + proxyPlayerId.get() + ", proxyPlayerName="
				+ proxyPlayerName.get() + ", proxyYSZJ=" + proxyYSZJ.get() + ", proxyZJ=" + proxyZJ.get() + ", proxyHuishui="
				+ proxyHuishui.get() + ", proxyHuiBao=" + proxyHuiBao.get() + ", proxyTableId=" + proxyTableId.get() + 
				", proxyBaoxian=" + proxyBaoxian.get() +"]";
	}
	
	
	
	

}
