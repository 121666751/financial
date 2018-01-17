package com.kendy.entity;

/**
 * 每一场次的战绩记录
 * 
 * @author 林泽涛
 * @time 2017年11月22日 下午8:32:29
 */
public class Record {

	private String id;
	private String tableId;
	private String clubId;
	private String playerId;
	private String score;
	private String insurance;//俱乐部保险
	private String blind;
	private String day;
	private String clubName;
	private String lmType;
	private String teamId;
	private String insuranceEach;//保险合计
	private String isJiesuaned = "0";//是否已经结算过，默认为“0”未结算
	
	//临时数据
	private String personCount;

	public Record() {
		super();
	}


	public String getId() {
		return id;
	}



	/**
	 * @param id
	 * @param tableId
	 * @param clubId
	 * @param playerId
	 * @param score
	 * @param insurance
	 * @param blind
	 * @param day
	 * @param clubName
	 */
	public Record(String id, String tableId, String clubId, String playerId, String score, String insurance,
			String blind, String day, String clubName,String lmType,String teamId,String insuranceEach, String isJiesuaned) {
		super();
		this.id = id;
		this.tableId = tableId;
		this.clubId = clubId;
		this.playerId = playerId;
		this.score = score;
		this.insurance = insurance;
		this.blind = blind;
		this.day = day;
		this.clubName = clubName;
		this.lmType = lmType;
		this.teamId = teamId;
		this.insuranceEach = insuranceEach;
		this.isJiesuaned = isJiesuaned;
	}


	public void setId(String id) {
		this.id = id;
	}

	public String getClubId() {
		return clubId;
	}

	public void setClubId(String clubId) {
		this.clubId = clubId;
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getInsurance() {
		return insurance;
	}

	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}

	public String getBlind() {
		return blind;
	}

	public void setBlind(String blind) {
		this.blind = blind;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
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


	
	public String getPersonCount() {
		return personCount;
	}


	public void setPersonCount(String personCount) {
		this.personCount = personCount;
	}


	public String getLmType() {
		return lmType;
	}


	public void setLmType(String lmType) {
		this.lmType = lmType;
	}


	public String getTeamId() {
		return teamId;
	}


	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}


	public String getInsuranceEach() {
		return insuranceEach;
	}


	public void setInsuranceEach(String insuranceEach) {
		this.insuranceEach = insuranceEach;
	}
	
	


	public String getIsJiesuaned() {
		return isJiesuaned;
	}


	public void setIsJiesuaned(String isJiesuaned) {
		this.isJiesuaned = isJiesuaned;
	}


	@Override
	public String toString() {
		return "Record [id=" + id + ", tableId=" + tableId + ", clubId=" + clubId + ", playerId=" + playerId
				+ ", score=" + score + ", insurance=" + insurance + ", blind=" + blind + ", day=" + day + ", clubName="
				+ clubName + ", lmType=" + lmType + ", teamId=" + teamId + ", insuranceEach=" + insuranceEach
				+ ", isJiesuaned=" + isJiesuaned + ", personCount=" + personCount + "]";
	}

}
