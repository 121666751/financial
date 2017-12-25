package com.kendy.entity;

import com.kendy.interfaces.Entity;

import javafx.beans.property.SimpleStringProperty;

public class TeamInfo implements Entity{

	private SimpleStringProperty teamID = new SimpleStringProperty();//团ID
	private SimpleStringProperty teamZJ = new SimpleStringProperty();//团战绩
	private SimpleStringProperty teamHS = new SimpleStringProperty();//团回水
	private SimpleStringProperty teamBS = new SimpleStringProperty();//团保险
	//
	private SimpleStringProperty teamSum = new SimpleStringProperty("0");//团和
	private SimpleStringProperty hasJiesuaned = new SimpleStringProperty("0");//团保险
	
	
	public TeamInfo() {
		super();
	}

	
	
	

	/**
	 * @param teamID 团ID
	 * @param teamZJ 团战绩
	 * @param teamHS 团回水
	 * @param teamBS 团保险
	 */
	public TeamInfo(String teamID, String teamZJ, String teamHS,String teamBS,String teamSum) {
		super();
		this.teamID = new SimpleStringProperty(teamID);
		this.teamZJ = new SimpleStringProperty(teamZJ);
		this.teamHS = new SimpleStringProperty(teamHS);
		this.teamBS = new SimpleStringProperty(teamBS);
		this.teamSum = new SimpleStringProperty(teamSum);
	}





	public SimpleStringProperty teamIDProperty() {
		return this.teamID;
	}
	


	public String getTeamID() {
		return this.teamIDProperty().get();
	}
	


	public void setTeamID(final String teamID) {
		this.teamIDProperty().set(teamID);
	}
	


	public SimpleStringProperty teamZJProperty() {
		return this.teamZJ;
	}
	


	public String getTeamZJ() {
		return this.teamZJProperty().get();
	}
	

	public void setTeamZJ(final String teamZJ) {
		this.teamZJProperty().set(teamZJ);
	}
	
	//=====================
	public SimpleStringProperty teamHSProperty() {
		return this.teamHS;
	}
	


	public String getTeamHS() {
		return this.teamHSProperty().get();
	}
	


	public void setTeamHS(final String teamHS) {
		this.teamHSProperty().set(teamHS);
	}
	


	public SimpleStringProperty teamBSProperty() {
		return this.teamBS;
	}
	


	public String getTeamBS() {
		return this.teamBSProperty().get();
	}
	


	public void setTeamBS(final String teamBS) {
		this.teamBSProperty().set(teamBS);
	}
	//=====================
	public SimpleStringProperty teamSumProperty() {
		return this.teamSum;
	}
	
	
	
	public String getTeamSum() {
		return this.teamSumProperty().get();
	}
	
	
	
	public void setTeamSum(final String teamSum) {
		this.teamSumProperty().set(teamSum);
	}
	
	
	
	public SimpleStringProperty hasJiesuanedProperty() {
		return this.hasJiesuaned;
	}
	
	
	
	public String getHasJiesuaned() {
		return this.hasJiesuanedProperty().get();
	}
	
	
	
	public void setHasJiesuaned(final String hasJiesuaned) {
		this.hasJiesuanedProperty().set(hasJiesuaned);
	}





	@Override
	public String toString() {
		return "TeamInfo [teamID=" + teamID.get() + ", teamZJ=" + teamZJ.get() + ", teamHS=" + teamHS.get() + ", teamBS=" + teamBS.get()
				+ ", teamSum=" + teamSum.get() + ", hasJiesuaned=" + hasJiesuaned.get() + "]";
	}


}
