package com.kendy.entity;

/**
 * 俱乐部桌费
 * 
 * @author 林泽涛
 * @time 2018年2月11日 下午9:57:14
 */
public class ClubZhuofei {

	private String time ;
	private String clubId;
	private String zhuofei;
	private String lmType;
	
	private String clubName; // 俱乐部名称
	private String gudong; // 所属股东
	
	public ClubZhuofei() {
		super();
	}
	
	
	
	
	/**
	 * @param time
	 * @param clubId
	 * @param zhuofei
	 */
	public ClubZhuofei(String time, String clubId, String zhuofei, String lmType) {
		super();
		this.time = time;
		this.clubId = clubId;
		this.zhuofei = zhuofei;
		this.lmType = lmType;
	}

	/**
	 * @param time
	 * @param clubId
	 * @param zhuofei
	 * @param clubName
	 * @param gudong
	 */
	public ClubZhuofei(String time, String clubId, String zhuofei, String lmType, String clubName, String gudong) {
		super();
		this.time = time;
		this.clubId = clubId;
		this.zhuofei = zhuofei;
		this.lmType = lmType;
		this.clubName = clubName;
		this.gudong = gudong;
	}


	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public String getGudong() {
		return gudong;
	}

	public void setGudong(String gudong) {
		this.gudong = gudong;
	}

	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getClubId() {
		return clubId;
	}
	public void setClubId(String clubId) {
		this.clubId = clubId;
	}
	public String getZhuofei() {
		return zhuofei;
	}
	public void setZhuofei(String zhuofei) {
		this.zhuofei = zhuofei;
	}
	public String getLmType() {
		return lmType;
	}
	public void setLmType(String lmType) {
		this.lmType = lmType;
	}

	@Override
	public String toString() {
		return "ClubZhuofei [time=" + time + ", clubId=" + clubId + ", zhuofei=" + zhuofei + ", lmType=" + lmType
				+ ", clubName=" + clubName + ", gudong=" + gudong + "]";
	}

	
}
