package com.kendy.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.kendy.entity.ProxySumInfo;
import com.kendy.entity.ProxyTeamInfo;
import com.kendy.interfaces.Entity;
import com.kendy.service.ShangmaService;
import com.kendy.service.TeamProxyService;

import application.MyController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * 团队代理查询控制类
 * 单独出来，原先是在MyController中
 * 
 * @author linzt
 * @time 2018年2月14日 上午9:32:23
 */
public class TeamProxyController implements Initializable{

	Logger log = Logger.getLogger(TeamProxyController.class);
	
//	//===============================================================代理查询Tab
//	@FXML   public TableView<ProxyTeamInfo> tableProxyTeam;
//	@FXML   public HBox proxySumHBox;//每列上的总和
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyTeamId ;//团ID
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyPlayerId;//玩家ID
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyPlayerName;//玩家名称
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyYSZJ;//原始战绩
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyZJ;//战绩
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyBaoxian;//保险
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyHuishui;//回水
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyHuiBao;//回保
//	@FXML   public TableColumn<ProxyTeamInfo,String> proxyTableId;//场次
//
//	@FXML   public ComboBox<String> teamIDCombox;//团队ID下拉框
//	@FXML   public CheckBox isZjManage;//团对应的战绩是否被管理
//	@FXML   public CheckBox hasTeamBaoxian;//导出是否团队无保险
//	@FXML   public Label proxyDateLabel;
//	
//	@FXML   public TableView<ProxySumInfo> tableProxySum;
//	@FXML   public TableColumn<ProxySumInfo,String> proxySumType;
//	@FXML   public TableColumn<ProxySumInfo,String> proxySum;
//	@FXML   public TextField proxyHSRate;//回水比例
//	@FXML   public TextField proxyHBRate;//回保比例
//	@FXML   public TextField proxyFWF;//服务费大于多少有效
//	
//	
//	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		//绑定代理查询表（团队当天查询）
//		MyController.bindCellValue(proxyPlayerId,proxyPlayerName,proxyYSZJ,proxyZJ,proxyBaoxian,proxyHuishui,proxyHuiBao,proxyTableId);
//		proxyYSZJ.setCellFactory(MyController.getColorCellFactory(new ProxyTeamInfo()));
//		proxyZJ.setCellFactory(MyController.getColorCellFactory(new ProxyTeamInfo()));
//		proxyBaoxian.setCellFactory(MyController.getColorCellFactory(new ProxyTeamInfo()));
//		
//		//绑定代理查询中的合计表
//		MyController.bindCellValue(proxySumType,proxySum);
//		proxySum.setCellFactory(MyController.getColorCellFactory(new ProxySumInfo()));
//		
//		////////////////////代理查询中的团队回水选择
//		TeamProxyService.initTeamProxy(
//				tableProxyTeam,proxySumHBox,teamIDCombox,isZjManage,proxyDateLabel,tableProxySum,proxyHSRate,proxyHBRate,proxyFWF,hasTeamBaoxian);
//		////////////////////代理查询中的团队回水选择
//		TeamProxyService.initTeamSelectAction(teamIDCombox,isZjManage,tableProxyTeam,proxySumHBox);
	}
//	
//	/**
//	 * 代理查询导出为Excel
//	 */
//	public void exportExcelAction(ActionEvent event) {
//		TeamProxyService.exportExcel();
//	}
//	
//	/**
//	 * 代理查询一键导出为Excel
//	 */
//	public void exportExcelBatchAction(ActionEvent event) {
//		//TeamProxyService.exportExcel();
//	}
//	
//	/**
//	 * 实时上码导出为Excel
//	 */
//	public void exportSMExcelAction(ActionEvent event) {
//		//ShowUtil.show("未开发",2);
//		ShangmaService.exportShangmaExcel();
//	}
//	
//	
//	/**
//	 * 代理查询刷新按钮 
//	 */
//	public void proxyRefreshAction(ActionEvent event) {
//		TeamProxyService.proxyRefresh();
//	}
//	
//	public void teamIDSelectedAction(ActionEvent event) {
//		
//	}
//	
//	
//	/**
//	 * 隐藏今日无数据的团队
//	 * @time 2018年1月1日
//	 * @param event
//	 */
//	public void proxyHideNoDataTeamAction(ActionEvent event) {
//		TeamProxyService.proxyHideNoDataTeam();
//	}
	
	
	
}
