package com.kendy.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.WaizhaiInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.zhaiwuInfo;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * 外债信息服务类
 * 
 * @author 林泽涛
 * @version 1.0
 */
public class WaizhaiService {

	
	private static Logger log = Logger.getLogger(WaizhaiService.class);
	
	public static DecimalFormat df = new DecimalFormat("#.00");
	
	/**
	 * 刷新汇总信息表
	 */
	public static void refreWaizhaiTable(TableView<WaizhaiInfo> tableWaizhai,TableView<zhaiwuInfo> tableZhaiwu) {
		Map<String,Map<String,String>> lockedMap = DataConstans.All_Locked_Data_Map;
		ZonghuiInfo zonghuiInfo = new ZonghuiInfo();
		ObservableList<ZonghuiInfo> obList = FXCollections.observableArrayList();
	}
	
	
	/**
	 * 自动生成外债信息表
	 * @param tableWaizhai
	 * @param hbox
	 * @param tableCurrentMoneyInfo
	 * @param tableTeam
	 */
	@SuppressWarnings("unchecked")
	public static void generateWaizhaiTables(TableView<WaizhaiInfo> tableWaizhai,HBox hbox, 
			TableView<CurrentMoneyInfo> tableCurrentMoneyInfo, TableView<TeamInfo> tableTeam) {
		//清空数据
		ObservableList<Node> allTables =  hbox.getChildren();
		if(allTables != null && allTables.size() > 0)
			hbox.getChildren().remove(0, allTables.size());
		
		if(DataConstans.All_Locked_Data_Map.size() == 0) {
			ShowUtil.show("你当前还未锁定任意一局，查询没有数据!",2);
			return;
		}
		ObservableList<CurrentMoneyInfo> CurrentMoneyInfo_OB_List= FXCollections.observableArrayList();
		Map<String,List<CurrentMoneyInfo>>  gudongMap = get_SSJE_Gudong_Map(tableCurrentMoneyInfo, tableTeam);
		Map<String,String> sumMap = getSum(gudongMap);
		
		
		int gudongMapSize = gudongMap.size();
		if(gudongMapSize == 0) {
			ShowUtil.show("股东列表为空或实时金额为空！");
			return;
		}
		
        TableView<CurrentMoneyInfo> table;
        
        for(Map.Entry<String, List<CurrentMoneyInfo>> entry : gudongMap.entrySet()) {
        	String gudongName = entry.getKey();
        	List<CurrentMoneyInfo> list = entry.getValue();
        	table = new TableView<CurrentMoneyInfo>();
	 
        	//设置列
	        TableColumn firstNameCol = new TableColumn("股东"+gudongName);
	        firstNameCol.setPrefWidth(110);
	        firstNameCol.setCellValueFactory(
	                new PropertyValueFactory<CurrentMoneyInfo, String>("mingzi"));
	 
	        TableColumn lastNameCol = new TableColumn(sumMap.get(gudongName));
	        lastNameCol.setStyle("-fx-alignment: CENTER;");
	        lastNameCol.setPrefWidth(95);
	        lastNameCol.setCellValueFactory(
	                new PropertyValueFactory<CurrentMoneyInfo, String>("shishiJine"));
	        lastNameCol.setCellFactory(MyController.getColorCellFactory(new CurrentMoneyInfo()));
	        table.setPrefWidth(210);
	        table.getColumns().addAll(firstNameCol, lastNameCol);
	 
	        //设置数据
	        CurrentMoneyInfo_OB_List= FXCollections.observableArrayList();
	        for(CurrentMoneyInfo info : list) {
	        	CurrentMoneyInfo_OB_List.add(info);
	        }
	        table.setItems(CurrentMoneyInfo_OB_List);
	        
	        hbox.setSpacing(5);
	        hbox.setPadding(new Insets(0, 0, 0, 0));
	        hbox.getChildren().addAll(table);
        }
        
        //设置债务表
        ObservableList<WaizhaiInfo> obList= FXCollections.observableArrayList();
        for(Map.Entry<String, String> entry : sumMap.entrySet()) {
        	obList.add(new WaizhaiInfo(entry.getKey(),entry.getValue()));
        }
        tableWaizhai.setItems(obList);
        setWaizhaiSum(tableWaizhai);
	}
	
	/**
	 * 设置外债信息总和
	 * 
	 * @time 2017年10月28日
	 * @param tableWaizhai
	 */
	public static void setWaizhaiSum(TableView<WaizhaiInfo> tableWaizhai) {
		Double sum = 0d;
		ObservableList<WaizhaiInfo> list = tableWaizhai.getItems();
		if(list != null && list.size() > 0) {
			for(WaizhaiInfo info : list) {
				sum += NumUtil.getNum(info.getWaizhaiMoney());
			}
		}else {
			sum = 0d;
		}
		tableWaizhai.getColumns().get(1).setText(NumUtil.digit0(sum));
	}
	
	/**
	 * 计算每个股东的外债总和
	 * @param gudongMap
	 * @return
	 */
	public static Map<String,String> getSum(Map<String,List<CurrentMoneyInfo>>  gudongMap){
		final Map<String,String> map = new HashMap<>();
		if(gudongMap != null && gudongMap.size() > 0) {
			for(Map.Entry<String, List<CurrentMoneyInfo>> entry : gudongMap.entrySet()) {
				Double sum = 0d;
				for(CurrentMoneyInfo info : entry.getValue()) {
					sum += NumUtil.getNum(info.getShishiJine());
				}
				map.put(entry.getKey(), NumUtil.digit0(sum));
			}
		}
		return map;
	}
	
	
	public static Map<String,List<CurrentMoneyInfo>> get_SSJE_Gudong_Map(TableView<CurrentMoneyInfo> tableCurrentMoneyInfo, TableView<TeamInfo> tableTeam) {
		int pageIndex = DataConstans.All_Locked_Data_Map.size();
		if(pageIndex < 0) {return new HashMap<>();}
		//获取实时金额数据
		Map<String,String> map = DataConstans.All_Locked_Data_Map.get(pageIndex+"");
		List<CurrentMoneyInfo> CurrentMoneyInfoList = null;
		List<TeamInfo> teamInfoList = null;
		//情况1：从最新的表中获取数据
		if(tableTeam != null && tableTeam.getItems() != null) {
			CurrentMoneyInfoList = new ArrayList<>();
			for(CurrentMoneyInfo infos : tableCurrentMoneyInfo.getItems()) {
				CurrentMoneyInfoList.add(infos);
			}
			teamInfoList = new ArrayList<>();
			for(TeamInfo infos : tableTeam.getItems()) {
				teamInfoList.add(infos);
			}
		}
		//情况2：从最新的锁定表中获取数据
		else {
			CurrentMoneyInfoList = JSON.parseObject(MoneyService.getJsonString(map,"实时金额"), new TypeReference<List<CurrentMoneyInfo>>() {});
			teamInfoList = JSON.parseObject(MoneyService.getJsonString(map,"团队回水"), new TypeReference<List<TeamInfo>>() {});
		}
		List<CurrentMoneyInfo> SSJE_obList = new LinkedList<>();
		for(CurrentMoneyInfo infos : CurrentMoneyInfoList) {
			if(!StringUtil.isBlank(infos.getWanjiaId()) && !StringUtil.isBlank(infos.getMingzi())) {
				SSJE_obList.add(infos);
			}
		}
		
		//获取每个股东的实时金额数据
		List<String> gudongList = DataConstans.gudongList;
		int gudongSize = gudongList.size();
		List<CurrentMoneyInfo> eachGudongList = null;
		Map<String,List<CurrentMoneyInfo>> gudongMap = new HashMap<>();
		String playerId;
		Player player;
		
		//步骤1：添加玩家
		for(CurrentMoneyInfo infos : SSJE_obList) {
			if(StringUtil.isBlank(infos.getShishiJine())
				|| "0".equals(infos.getShishiJine())
				|| !infos.getShishiJine().contains("-")) {
				continue;
			}
			playerId = infos.getWanjiaId();
			if(!StringUtil.isBlank(playerId)) {
				player = DataConstans.membersMap.get(playerId);
				if(player == null) {
					ShowUtil.show("名单列表中匹配不到该玩家!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
					continue;
				}
				if(gudongList.contains(player.getGudong())) {
					for(String gudong : gudongList) {
						if(gudong.equals(player.getGudong())) {
							if(gudongMap.get(gudong) == null) {
								eachGudongList = new ArrayList<>();
								eachGudongList.add(infos);
							}else {
								eachGudongList = gudongMap.get(gudong);
								eachGudongList.add(infos);
							}
							gudongMap.put(gudong, eachGudongList);
							break;
						}
					}
				}else {
					ShowUtil.show("玩家的股东不存在于股东表中!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
					break;
				}
			}else {
				ShowUtil.show("玩家ID为空!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
			}
		}
		
		//步骤2：添加团队
		for(TeamInfo infos : teamInfoList) {
			String teamId = infos.getTeamID();
			Huishui hsInfo = DataConstans.huishuiMap.get(teamId);
			String isManaged = hsInfo.getZjManaged();
			String gudong = hsInfo.getGudong();
			String hasJiesuan = infos.getHasJiesuaned();
			String zhanji = infos.getTeamZJ();
			if(!"是".equals(isManaged) //战绩非管理的团队
					&& !"1".equals(hasJiesuan) //未结算的团队
					&& zhanji.contains("-")) {//战绩为负数的团队
				CurrentMoneyInfo cmi = new CurrentMoneyInfo();
				cmi.setMingzi("团队"+teamId);
				cmi.setShishiJine(infos.getTeamZJ());
				if(gudongMap.get(gudong) == null) {
					eachGudongList = new ArrayList<>();
					eachGudongList.add(cmi);
				}else {
					eachGudongList = gudongMap.get(gudong);
					eachGudongList.add(cmi);
				}
				gudongMap.put(gudong, eachGudongList);
			}
		}
		return gudongMap;
	}
	
}
