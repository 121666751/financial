package com.kendy.entity;

/**
 * 上码中玩家的次日信息
 * @author 林泽涛
 * @time 2018年2月5日 下午9:24:19
 */
public class ShangmaNextday {

	private String playerId;
	private String playerName;
	private String changci;
	private String shangma;
	private String time;
	private String type = "0";
	
	
	public ShangmaNextday() {
		super();
	}
	
	
	/**
	 * @param playerId
	 * @param playerName
	 * @param changci
	 * @param shangma
	 * @param time
	 */
	public ShangmaNextday(String playerId, String playerName, String changci, String shangma, String time) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.changci = changci;
		this.shangma = shangma;
		this.time = time;
	}

	/**
	 * @param playerId
	 * @param playerName
	 * @param changci
	 * @param shangma
	 */
	public ShangmaNextday(String playerId, String playerName, String changci, String shangma) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.changci = changci;
		this.shangma = shangma;
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
	public String getChangci() {
		return changci;
	}
	public void setChangci(String changci) {
		this.changci = changci;
	}
	public String getShangma() {
		return shangma;
	}
	public void setShangma(String shangma) {
		this.shangma = shangma;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	
	

	
}
