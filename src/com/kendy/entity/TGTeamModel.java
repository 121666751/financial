package com.kendy.entity;

public class TGTeamModel {

	private String tgTeamId ;
	private String tgHuishui ;
	private String tgHuiBao ;
	private String tgFWF ;
	
	
	/**
	 * 
	 */
	public TGTeamModel() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * @param tgTeamId
	 * @param tgHuishui
	 * @param tgHuiBao
	 * @param tgFWF
	 */
	public TGTeamModel(java.lang.String tgTeamId, java.lang.String tgHuishui, java.lang.String tgHuiBao,
			java.lang.String tgFWF) {
		super();
		this.tgTeamId = tgTeamId;
		this.tgHuishui = tgHuishui;
		this.tgHuiBao = tgHuiBao;
		this.tgFWF = tgFWF;
	}
	
	
	public String getTgTeamId() {
		return tgTeamId;
	}
	public void setTgTeamId(String tgTeamId) {
		this.tgTeamId = tgTeamId;
	}
	public String getTgHuishui() {
		return tgHuishui;
	}
	public void setTgHuishui(String tgHuishui) {
		this.tgHuishui = tgHuishui;
	}
	public String getTgHuiBao() {
		return tgHuiBao;
	}
	public void setTgHuiBao(String tgHuiBao) {
		this.tgHuiBao = tgHuiBao;
	}
	public String getTgFWF() {
		return tgFWF;
	}
	public void setTgFWF(String tgFWF) {
		this.tgFWF = tgFWF;
	}


	@Override
	public String toString() {
		return "TGTeamModel [tgTeamId=" + tgTeamId + ", tgHuishui=" + tgHuishui + ", tgHuiBao=" + tgHuiBao + ", tgFWF="
				+ tgFWF + "]";
	}
	
	
	
	
}
