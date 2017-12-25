package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class WanjiaInfo implements Entity{

	public SimpleStringProperty paiju = new SimpleStringProperty();//牌局
	public SimpleStringProperty wanjiaName = new SimpleStringProperty();//玩家
	public SimpleStringProperty zhangji = new SimpleStringProperty();//战绩
	public SimpleStringProperty yicunJifen = new SimpleStringProperty();//已存积分
	public SimpleStringProperty heji = new SimpleStringProperty();//合计
	public SimpleStringProperty wanjiaId = new SimpleStringProperty();//ID隐藏
	public SimpleStringProperty hasPayed = new SimpleStringProperty();//是否已经支付过
	
	public WanjiaInfo() {
		super();
	}

	public WanjiaInfo(String paiju,String wanjiaName,String zhangji,String yicunJifen,String heji,String wanjiaId) {
		super();
		this.paiju = new SimpleStringProperty(paiju);
		this.wanjiaName = new SimpleStringProperty(wanjiaName);
		this.zhangji = new SimpleStringProperty(zhangji);
		this.yicunJifen = new SimpleStringProperty(yicunJifen);
		this.heji = new SimpleStringProperty(heji);
		this.wanjiaId = new SimpleStringProperty(wanjiaId);
	}

	public SimpleStringProperty paijuProperty() {
		return this.paiju;
	}
	

	public String getPaiju() {
		return this.paijuProperty().get();
	}
	

	public void setPaiju(final String paiju) {
		this.paijuProperty().set(paiju);
	}
	

	public SimpleStringProperty wanjiaNameProperty() {
		return this.wanjiaName;
	}
	

	public String getWanjiaName() {
		return this.wanjiaNameProperty().get();
	}
	

	public void setWanjiaName(final String wanjiaName) {
		this.wanjiaNameProperty().set(wanjiaName);
	}
	

	public SimpleStringProperty zhangjiProperty() {
		return this.zhangji;
	}
	

	public String getZhangji() {
		return this.zhangjiProperty().get();
	}
	

	public void setZhangji(final String zhangji) {
		this.zhangjiProperty().set(zhangji);
	}
	

	public SimpleStringProperty yicunJifenProperty() {
		return this.yicunJifen;
	}
	

	public String getYicunJifen() {
		return this.yicunJifenProperty().get();
	}
	

	public void setYicunJifen(final String yicunJifen) {
		this.yicunJifenProperty().set(yicunJifen);
	}
	

	public SimpleStringProperty hejiProperty() {
		return this.heji;
	}
	

	public String getHeji() {
		return this.hejiProperty().get();
	}
	

	public void setHeji(final String heji) {
		this.hejiProperty().set(heji);
	}
	

	public SimpleStringProperty wanjiaIdProperty() {
		return this.wanjiaId;
	}
	

	public String getWanjiaId() {
		return this.wanjiaIdProperty().get();
	}
	

	public void setWanjiaId(final String wanjiaId) {
		this.wanjiaIdProperty().set(wanjiaId);
	}
	
	//-------------
	public SimpleStringProperty hasPayedProperty() {
		return this.hasPayed;
	}
	

	public String getHasPayed() {
		return this.hasPayedProperty().get();
	}
	

	public void setHasPayed(final String hasPayed) {
		this.hasPayedProperty().set(hasPayed);
	}

	@Override
	public String toString() {
		return "WanjiaInfo [paiju=" + paiju.getValue() + ", wanjiaName=" + wanjiaName.getValue() + ", zhangji=" + zhangji.getValue() + ", yicunJifen="
				+ yicunJifen.getValue() + ", heji=" + heji.getValue() + ", wanjiaId=" + wanjiaId.getValue() + ", hasPayed=" + hasPayed.getValue() + "]";
	}
	
	



	
	
	
	
	
	
	
	
	
}
