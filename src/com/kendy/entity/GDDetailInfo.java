package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

/**
 * 股东贡献值明细表
 * 
 * @author 林泽涛
 *
 */
public class GDDetailInfo implements Entity{
	private SimpleStringProperty name = new SimpleStringProperty();
	private SimpleStringProperty ysgu = new SimpleStringProperty();
	private SimpleStringProperty jl3 = new SimpleStringProperty();
	private SimpleStringProperty jl7 = new SimpleStringProperty();
	private SimpleStringProperty total = new SimpleStringProperty();
	
	
	public GDDetailInfo() {
		super();
	}
	

	public GDDetailInfo(String name, String ysgu) {
		super();
		this.name = new SimpleStringProperty(name);
		this.ysgu = new SimpleStringProperty(ysgu);
	}
	
	public GDDetailInfo(String name, String ysgu, String total) {
		super();
		this.name = new SimpleStringProperty(name);
		this.ysgu = new SimpleStringProperty(ysgu);
		this.total = new SimpleStringProperty(total);
	}



	/*****************************************************************name***/
	public SimpleStringProperty nameProperty() {
		return this.name;
	}
	public String getName() {
		return this.nameProperty().get();
	}
	public void setName(final String name) {
		this.nameProperty().set(name);
	}
	/*****************************************************************ysgu***/
	public SimpleStringProperty ysguProperty() {
		return this.ysgu;
	}
	public String getYsgu() {
		return this.ysguProperty().get();
	}
	public void setYsgu(final String ysgu) {
		this.ysguProperty().set(ysgu);
	}
	/*****************************************************************jl3***/
	public SimpleStringProperty jl3Property() {
		return this.jl3;
	}
	public String getJl3() {
		return this.jl3Property().get();
	}
	public void setJl3(final String jl3) {
		this.jl3Property().set(jl3);
	}
	/*****************************************************************jl7***/
	public SimpleStringProperty jl7Property() {
		return this.jl7;
	}
	public String getJl7() {
		return this.jl7Property().get();
	}
	public void setJl7(final String jl7) {
		this.jl7Property().set(jl7);
	}
	/*****************************************************************total***/
	public SimpleStringProperty totalProperty() {
		return this.total;
	}
	public String getTotal() {
		return this.totalProperty().get();
	}
	public void setTotal(final String total) {
		this.totalProperty().set(total);
	}
	
	
	

}
