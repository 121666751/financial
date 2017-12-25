package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class ZonghuiInfo implements Entity{

	private SimpleStringProperty zonghuiTabelId = new SimpleStringProperty();
	private SimpleStringProperty zonghuiFuwufei = new SimpleStringProperty();
	private SimpleStringProperty zonghuiBaoxian = new SimpleStringProperty();
	private SimpleStringProperty zonghuiHuishui = new SimpleStringProperty();
	private SimpleStringProperty zonghuiHuiBao = new SimpleStringProperty();
	
	public ZonghuiInfo() {
		super();
	}
	
	/**
	 * @param zonghuiTabelId
	 * @param zonghuiFuwufei
	 * @param zonghuiBaoxian
	 * @param zonghuiHuishui
	 * @param zonghuiHuiBao
	 */
	public ZonghuiInfo(String zonghuiTabelId, String zonghuiFuwufei,
			String zonghuiBaoxian, String zonghuiHuishui,
			String zonghuiHuiBao) {
		super();
		this.zonghuiTabelId = new SimpleStringProperty(zonghuiTabelId);
		this.zonghuiFuwufei = new SimpleStringProperty(zonghuiFuwufei);
		this.zonghuiBaoxian = new SimpleStringProperty(zonghuiBaoxian);
		this.zonghuiHuishui = new SimpleStringProperty(zonghuiHuishui);
		this.zonghuiHuiBao  = new SimpleStringProperty(zonghuiHuiBao);
	}

	/*****************************************************************zonghuiTabelId***/
	public SimpleStringProperty zonghuiTabelIdProperty() {
		return this.zonghuiTabelId;
	}
	public String getZonghuiTabelId() {
		return this.zonghuiTabelIdProperty().get();
	}
	public void setZonghuiTabelId(final String zonghuiTabelId) {
		this.zonghuiTabelIdProperty().set(zonghuiTabelId);
	}
	/*****************************************************************zonghuiFuwufei***/
	public SimpleStringProperty zonghuiFuwufeiProperty() {
		return this.zonghuiFuwufei;
	}
	public String getZonghuiFuwufei() {
		return this.zonghuiFuwufeiProperty().get();
	}
	public void setZonghuiFuwufei(final String zonghuiFuwufei) {
		this.zonghuiFuwufeiProperty().set(zonghuiFuwufei);
	}
	/*****************************************************************zonghuiBaoxian***/
	public SimpleStringProperty zonghuiBaoxianProperty() {
		return this.zonghuiBaoxian;
	}
	public String getZonghuiBaoxian() {
		return this.zonghuiBaoxianProperty().get();
	}
	public void setZonghuiBaoxian(final String zonghuiBaoxian) {
		this.zonghuiBaoxianProperty().set(zonghuiBaoxian);
	}
	/*****************************************************************zonghuiHuishui***/
	public SimpleStringProperty zonghuiHuishuiProperty() {
		return this.zonghuiHuishui;
	}
	public String getZonghuiHuishui() {
		return this.zonghuiHuishuiProperty().get();
	}
	public void setZonghuiHuishui(final String zonghuiHuishui) {
		this.zonghuiHuishuiProperty().set(zonghuiHuishui);
	}
	/*****************************************************************zonghuiHuiBao***/
	public SimpleStringProperty zonghuiHuiBaoProperty() {
		return this.zonghuiHuiBao;
	}
	public String getZonghuiHuiBao() {
		return this.zonghuiHuiBaoProperty().get();
	}
	public void setZonghuiHuiBao(final String zonghuiHuiBao) {
		this.zonghuiHuiBaoProperty().set(zonghuiHuiBao);
	}



	@Override
	public String toString() {
		return "ZonghuiInfo [zonghuiTabelId=" + zonghuiTabelId.get() + ", zonghuiFuwufei=" + zonghuiFuwufei.get()
				+ ", zonghuiBaoxian=" + zonghuiBaoxian.get() + ", zonghuiHuishui=" + zonghuiHuishui.get() + ", zonghuiHuiBao="
				+ zonghuiHuiBao.get() + "]";
	}
	
	
	
}
