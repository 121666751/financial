package com.kendy.entity;

import java.util.Date;

public class HistoryRecord {

	private String playerId;
	private String playerName;
	private String teamId;
	private String shishou;
	private String yszj;//原始战绩
	private String chuHuishui;
	private String shouHuishui;
	private String updateTime;

	public HistoryRecord() {
		super();
	}



	/**
	 * @param playerId
	 * @param playerName
	 * @param teamId
	 * @param shishou
	 * @param yszj
	 * @param chuHuishui
	 * @param shouHuishui
	 * @param updateTime
	 */
	public HistoryRecord(String playerId, String playerName, String teamId, String shishou, String yszj,
			String chuHuishui, String shouHuishui, String updateTime) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.teamId = teamId;
		this.shishou = shishou;
		this.yszj = yszj;
		this.chuHuishui = chuHuishui;
		this.shouHuishui = shouHuishui;
		this.updateTime = updateTime;
	}



	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getTeamId() {
		return teamId;
	}

	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public String getShishou() {
		return shishou;
	}

	public void setShishou(String shishou) {
		this.shishou = shishou;
	}



	public String getUpdateTime() {
		return updateTime;
	}



	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}



	public String getYszj() {
		return yszj;
	}

	public void setYszj(String yszj) {
		this.yszj = yszj;
	}



	public String getChuHuishui() {
		return chuHuishui;
	}



	public void setChuHuishui(String chuHuishui) {
		this.chuHuishui = chuHuishui;
	}



	public String getShouHuishui() {
		return shouHuishui;
	}



	public void setShouHuishui(String shouHuishui) {
		this.shouHuishui = shouHuishui;
	}
	
}
