package com.kendy.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * IP工具类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:57:48
 */
public class IPUtil {

	public static void main(String[] args) throws SocketException, UnknownHostException {
		String macAddr = getLocalMac();
		System.out.println("本机MAC地址:"+macAddr.toString().toUpperCase());
	}
	
	public static String getLocalMac() throws SocketException, UnknownHostException {

		InetAddress ia = InetAddress.getLocalHost();
		//获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		StringBuffer sb = new StringBuffer("");
		for(int i=0; i<mac.length; i++) {
			if(i!=0) {
				sb.append("-");
			}
			//字节转换为整数
			int temp = mac[i]&0xff;
			String str = Integer.toHexString(temp);
			if(str.length()==1) {
				sb.append("0"+str);
			}else {
				sb.append(str);
			}
		}
		return sb.toString().toUpperCase();
	}
		
		
		
		
//		//获得本机IP
//		String addr = "";
//		try {
//			addr = InetAddress.getLocalHost().getHostAddress();
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		System.out.println("本机IP:"+addr);
//		
//		int aa = Integer.parseInt("192", 16);
//		System.out.println(aa);
//		
//	}
}
