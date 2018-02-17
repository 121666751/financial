package com.kendy.entity;

/**
 * 俱乐部从属
 * 
 * @author linzt
 * @time 2018年2月17日 下午7:20:27
 */
public class ClubHolder {

	private String clubId;
	private String  holder;
	
	private  Double rate1; // 
	private Double rate2; // 
	
	public ClubHolder() {
		super();
	}
	
	public ClubHolder(String clubId, String holder, Double rate1, Double rate2) {
		super();
		this.clubId = clubId;
		this.holder = holder;
		this.rate1 = rate1;
		this.rate2 = rate2;
	}



	public String getClubId() {
		return clubId;
	}

	public void setClubId(String clubId) {
		this.clubId = clubId;
	}

	public String getHolder() {
		return holder;
	}

	public void setHolder(String holder) {
		this.holder = holder;
	}

	public Double getRate1() {
		return rate1;
	}

	public void setRate1(Double rate1) {
		this.rate1 = rate1;
	}

	public Double getRate2() {
		return rate2;
	}

	public void setRate2(Double rate2) {
		this.rate2 = rate2;
	}
	
	
	

	
}
