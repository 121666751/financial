package com.kendy.entity;

/**
 * 玩家信息类
 *
 */
public class UserInfos {

	//俱乐部id
	public String clubId;
	
	//战绩
	public String zj;
	
	//保险=保险合计
	public String insurance;
	
	//玩家名字
	public String playerName;
	
	//玩家id
	public String playerId;
	
	//桌号
	public String tableId;
	
	//俱乐部名称
	public String clubName;
	
	//时间
	public String day;
	
	//=================== constructors ================================================
	public UserInfos() {
		super();
	}
	
	public UserInfos(String clubId, String zj, String insurance, String playerName, String playerId,
			String tableId, String clubName, String day) {
		super();
		this.clubId = clubId;
		this.zj = zj;
		this.insurance = insurance;
		this.playerName = playerName;
		this.playerId = playerId;
		this.tableId = tableId;
		this.clubName = clubName;
		this.day = day;
	}

	//=================== getter and setter ================================================

	public String getClubId() {
		return clubId;
	}


	public void setClubId(String clubId) {
		this.clubId = clubId;
	}

	public String getZj() {
		return zj;
	}

	public void setZj(String zj) {
		this.zj = zj;
	}

	public String getinsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}


	public String getTableId() {
		return tableId;
	}


	public void setTableId(String tableId) {
		this.tableId = tableId;
	}


	public String getClubName() {
		return clubName;
	}


	public void setClubName(String clubName) {
		this.clubName = clubName;
	}


	public String getDay() {
		return day;
	}


	public void setDay(String day) {
		this.day = day;
	}

	
	
	
}
