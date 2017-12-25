package com.kendy.entity;

import javafx.beans.property.SimpleStringProperty;

public class TeamHuishuiInfo {

	private String tuan;//团ID
	private String wanjiaId;//玩家ID
	private String wanjia;//玩家名称
	private String shishou;//实收
	private String baoxian;//保险
	private String chuHuishui;//出回水
	private String tableId;//
	private String zj;//原始战绩（计分）
	private String huibao;//回保（保回）
	private String shouHuishui;//收回水
	private String updateTime;
	
	public TeamHuishuiInfo() {
		super();
	}
	
	public TeamHuishuiInfo(String tuan, String wanjiaId, String wanjia, String shishou, String baoxian,
			String chuHuishui, String tableId,String zj,String huibao,String shouHuishui,String updateTime) {
		super();
		this.tuan = tuan;
		this.wanjiaId = wanjiaId;
		this.wanjia = wanjia;
		this.shishou = shishou;
		this.baoxian = baoxian;
		this.chuHuishui = chuHuishui;
		this.tableId = tableId;
		this.zj = zj;
		this.huibao = huibao;
		this.shouHuishui = shouHuishui;
		this.updateTime = updateTime;
	}





	public String getTuan() {
		return tuan;
	}
	public void setTuan(String tuan) {
		this.tuan = tuan;
	}
	public String getWanjiaId() {
		return wanjiaId;
	}
	public void setWanjiaId(String wanjiaId) {
		this.wanjiaId = wanjiaId;
	}
	public String getWanjia() {
		return wanjia;
	}
	public void setWanjia(String wanjia) {
		this.wanjia = wanjia;
	}
	public String getShishou() {
		return shishou;
	}
	public void setShishou(String shishou) {
		this.shishou = shishou;
	}
	public String getBaoxian() {
		return baoxian;
	}
	public void setBaoxian(String baoxian) {
		this.baoxian = baoxian;
	}
	public String getChuHuishui() {
		return chuHuishui;
	}
	public void setChuHuishui(String chuHuishui) {
		this.chuHuishui = chuHuishui;
	}
	public String getTableId() {
		return tableId;
	}
	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	
	
	public String getZj() {
		return zj;
	}

	public void setZj(String zj) {
		this.zj = zj;
	}

	public String getHuibao() {
		return huibao;
	}

	public void setHuibao(String huibao) {
		this.huibao = huibao;
	}

	public String getShouHuishui() {
		return shouHuishui;
	}

	public void setShouHuishui(String shouHuishui) {
		this.shouHuishui = shouHuishui;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}


	
}
