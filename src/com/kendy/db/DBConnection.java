package com.kendy.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.kendy.controller.CombineIDController;

/**
 * 连接数据库
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:54:27
 */
public class DBConnection {
	
	private static Logger log = Logger.getLogger(DBConnection.class);
	public static void main(String[] args){
		log.info("success..");
		getConnection();
	}
	
	
	
	private static Statement st;
	private static ResultSet rs;
	private static ResultSetMetaData metadata;
	private static Connection connection = null;
	
	public DBConnection(){
		connection = null;
	}
	
	
    private static final String URL = "jdbc:mysql://localhost:3306/financial";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";
	
	public static Connection getConnection(){ 

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			log.error("Where is your mysql JDBC Driver?");
			e.printStackTrace();
			return null;

		}

		try {
//
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
//			String URL2 = MainForm.textLocalhost.getText();
//			String USER2 = MainForm.textRoot.getText();
//			String PASSWORD2 = MainForm.textKikro.getText();
//			String PORT = MainForm.textPort.getText();
////			URL2 = "jdbc:mysql://"+URL2+":"+PORT+"/mybatis?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=utf8&useSSL=false&autoReconnect=true";
////			connection = DriverManager.getConnection(URL2, USER2, PASSWORD2);
//
		} catch (SQLException e) {

			log.info("Connection Failed! ");
			e.printStackTrace();
			return null;

		}

		if (connection != null) {
			//log.info("connect successfully!");
		} else {
			log.error("Failed to establish connection!");
		}
		
		
		return connection;
		
	}
	

}

