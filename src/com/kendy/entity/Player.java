package com.kendy.entity;

public class Player {

	//玩家ID
	public String gameId;
	
	//团ID
	public String teamName;
	
	//股东
	public String gudong;
	
	//玩家名称
	public String playerName;
	
	//额度
	public String edu;

	//=================== constructors ================================================
	public Player() {
		super();
	}

	public Player(String gameId, String teamName, String gudong, String playerName, String edu) {
		super();
		this.gameId = gameId;
		this.teamName = teamName;
		this.gudong = gudong;
		this.playerName = playerName;
		this.edu = edu;
	}
	
	
	
	
	//=================== getter and setter ================================================

	public String getgameId() {
		return gameId;
	}

	public void setGameId(String gameId) {
		this.gameId = gameId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getGudong() {
		return gudong;
	}

	public void setGudong(String gudong) {
		this.gudong = gudong;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getEdu() {
		return edu;
	}

	public void setEdu(String edu) {
		this.edu = edu;
	}

	//===========================================================
	@Override
	public String toString() {
		return "Player [gameId=" + gameId + ", teamName=" + teamName + ", gudong=" + gudong + ", playerName="
				+ playerName + ", edu=" + edu + "]";
	}
	
	
	
	
	
}
