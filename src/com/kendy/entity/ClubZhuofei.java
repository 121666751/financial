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
	public ClubZhuofei(String time, String clubId, String zhuofei) {
		super();
		this.time = time;
		this.clubId = clubId;
		this.zhuofei = zhuofei;
	}

	/**
	 * @param time
	 * @param clubId
	 * @param zhuofei
	 * @param clubName
	 * @param gudong
	 */
	public ClubZhuofei(String time, String clubId, String zhuofei, String clubName, String gudong) {
		super();
		this.time = time;
		this.clubId = clubId;
		this.zhuofei = zhuofei;
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




	@Override
	public String toString() {
		return "ClubZhuofei [time=" + time + ", clubId=" + clubId + ", zhuofei=" + zhuofei + ", clubName=" + clubName
				+ ", gudong=" + gudong + "]";
	}
	
	
	
}
