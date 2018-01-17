package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.kendy.entity.Record;
import com.kendy.entity.TypeValueInfo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * 股东贡献控制类
 * 
 * @author 林泽涛
 * @time 2018年1月14日 下午6:12:15
 */
public class GDController implements Initializable{

	//动态生成各个股东贡献值的区域
	@FXML HBox contributionHBox;
	
	//股东贡献值主表
	@FXML TableView<TypeValueInfo> tableGDSum;
	
	//1人次等于多少利润
	@FXML TextField personTime_ProfitRate_Text;
	
	
	//数据来源:当天某俱乐部的数据
	private static List<Record> dataList = new ArrayList();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	
	
	/**
	 * 股东贡献值即时刷新按钮
	 * 
	 * @time 2018年1月14日
	 * @param event
	 */
	public void GDContributeRefreshAction(ActionEvent event) {
		
	}
	
	/**
	 * 股东贡献值清空按钮
	 * 
	 * @time 2018年1月14日
	 * @param event
	 */
	public void clearDataAction(ActionEvent event) {
		
	}
}
