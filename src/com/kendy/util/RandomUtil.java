package com.kendy.util;

import java.util.Random;

public class RandomUtil {

	public static void main(String[] args) {
		for(int i=0; i<50; i++) {
			System.out.println(getRandomNumber(10000,20000));
		}

	}
	
	public static int getRandomNumber(int from , int to) {
		Random random = new Random();
		if(to > from) {
			int randNumber = random.nextInt(to - from + 1) + from;
			return randNumber;
		}else {
			return random.nextInt();
		}
	}
	
	
	

}
