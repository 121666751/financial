package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

/**
 * 托管玩家备注
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午9:15:20
 */
public class TGCommentInfo implements Entity{

	private SimpleStringProperty tgCommentEntityId = new SimpleStringProperty();//ID
	private SimpleStringProperty tgCommentDate = new SimpleStringProperty();//日期
	private SimpleStringProperty tgCommentPlayerId = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty tgCommentPlayerName = new SimpleStringProperty();//支出项目
	private SimpleStringProperty tgCommentType = new SimpleStringProperty();//玩家名称
	
	private SimpleStringProperty tgCommentId = new SimpleStringProperty();//玩家名称
	private SimpleStringProperty tgCommentName = new SimpleStringProperty();//支出项目
	private SimpleStringProperty tgCommentBeizhu = new SimpleStringProperty();//玩家名称
	
	private SimpleStringProperty tgCommentCompany = new SimpleStringProperty();//玩家名称
	
	public TGCommentInfo() {
		super();
	}

	
	public TGCommentInfo(String tgCommentEntityId, String tgCommentDate, String tgCommentPlayerId, String tgCommentPlayerName, String tgCommentType
			,String tgCommentId, String tgCommentName, String tgCommentBeizhu, String tgCommentCompany) {
		this.tgCommentEntityId = new SimpleStringProperty(tgCommentEntityId);
		this.tgCommentDate = new SimpleStringProperty(tgCommentDate);
		this.tgCommentPlayerId = new SimpleStringProperty(tgCommentPlayerId);
		this.tgCommentPlayerName = new SimpleStringProperty(tgCommentPlayerName);
		this.tgCommentType = new SimpleStringProperty(tgCommentType);
		this.tgCommentId = new SimpleStringProperty(tgCommentId);
		this.tgCommentName = new SimpleStringProperty(tgCommentName);
		this.tgCommentBeizhu = new SimpleStringProperty(tgCommentBeizhu);
		this.tgCommentCompany = new SimpleStringProperty(tgCommentCompany);
	}
	
	//=====================
	public SimpleStringProperty tgCommentDateProperty() {
		return this.tgCommentDate;
	}
	public String getTgCommentDate() {
		return this.tgCommentDateProperty().get();
	}
	public void setTgCommentDate(final String tgCommentDate) {
		this.tgCommentDateProperty().set(tgCommentDate);
	}
	

	//=====================
	public SimpleStringProperty tgCommentPlayerIdProperty() {
		return this.tgCommentPlayerId;
	}
	public String getTgCommentPlayerId() {
		return this.tgCommentPlayerIdProperty().get();
	}
	public void setTgCommentPlayerId(final String tgCommentPlayerId) {
		this.tgCommentPlayerIdProperty().set(tgCommentPlayerId);
	}
	
	//=====================
	public SimpleStringProperty tgCommentPlayerNameProperty() {
		return this.tgCommentPlayerName;
	}
	public String getTgCommentPlayerName() {
		return this.tgCommentPlayerNameProperty().get();
	}
	public void setTgCommentPlayerName(final String tgCommentPlayerName) {
		this.tgCommentPlayerNameProperty().set(tgCommentPlayerName);
	}
	

	//=====================
	public SimpleStringProperty tgCommentTypeProperty() {
		return this.tgCommentType;
	}

	public String getTgCommentType() {
		return this.tgCommentTypeProperty().get();
	}

	public void setTgCommentType(final String tgCommentType) {
		this.tgCommentTypeProperty().set(tgCommentType);
	}
	
	//=====================
	public SimpleStringProperty tgCommentEntityIdProperty() {
		return this.tgCommentEntityId;
	}

	public String getTgCommentEntityId() {
		return this.tgCommentEntityIdProperty().get();
	}

	public void setTgCommentEntityId(final String id) {
		this.tgCommentEntityIdProperty().set(id);
	}
	
	//=====================
	public SimpleStringProperty tgCommentIdProperty() {
		return this.tgCommentId;
	}
	public String getTgCommentId() {
		return this.tgCommentIdProperty().get();
	}
	public void setTgCommentId(final String tgCommentId) {
		this.tgCommentIdProperty().set(tgCommentId);
	}
	
	//=====================
	public SimpleStringProperty tgCommentNameProperty() {
		return this.tgCommentName;
	}
	public String getTgCommentName() {
		return this.tgCommentNameProperty().get();
	}
	public void setTgCommentName(final String tgCommentName) {
		this.tgCommentNameProperty().set(tgCommentName);
	}
	

	//=====================
	public SimpleStringProperty tgCommentBeizhuProperty() {
		return this.tgCommentBeizhu;
	}

	public String getTgCommentBeizhu() {
		return this.tgCommentBeizhuProperty().get();
	}

	public void setTgCommentBeizhu(final String tgCommentBeizhu) {
		this.tgCommentBeizhuProperty().set(tgCommentBeizhu);
	}
	//=====================
	public SimpleStringProperty tgCommentCompanyProperty() {
		return this.tgCommentCompany;
	}

	public String getTgCommentCompany() {
		return this.tgCommentCompanyProperty().get();
	}

	public void setTgCommentCompany(final String tgCommentCompany) {
		this.tgCommentCompanyProperty().set(tgCommentCompany);
	}
}
