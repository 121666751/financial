package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * w代理查询中的团队当天查询
 * @author 林泽涛
 *
 */
public class ProxySumInfo implements Entity{

	private SimpleStringProperty proxySumType = new SimpleStringProperty();
	private SimpleStringProperty proxySum = new SimpleStringProperty();

	
	
	public ProxySumInfo() {
		super();
	}
	
	public ProxySumInfo(String proxySumType, String proxySum) {
		super();
		this.proxySumType = new SimpleStringProperty(proxySumType);
		this.proxySum = new SimpleStringProperty(proxySum);
	}

	/*****************************************************************proxySumType***/
	public SimpleStringProperty proxySumTypeProperty() {
		return this.proxySumType;
	}
	public String getProxySumType() {
		return this.proxySumTypeProperty().get();
	}
	public void setProxySumType(final String proxySumType) {
		this.proxySumTypeProperty().set(proxySumType);
	}
	/*****************************************************************proxySum***/
	public SimpleStringProperty proxySumProperty() {
		return this.proxySum;
	}
	public String getProxySum() {
		return this.proxySumProperty().get();
	}
	public void setProxySum(final String proxySum) {
		this.proxySumProperty().set(proxySum);
	}
	
	
	

}
