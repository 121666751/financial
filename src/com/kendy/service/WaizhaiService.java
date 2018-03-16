package com.kendy.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.controller.LMController;
import com.kendy.entity.Club;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.WaizhaiInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.zhaiwuInfo;
import com.kendy.util.CollectUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.MapUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import application.MyController;
import javafx.beans.property.SimpleStringProperty;
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
		
		if(DataConstans.Index_Table_Id_Map.size() == 0) {
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
	
	/**
	 * 获取每个股东的实时金额
	 * 包括个人和存在于左边的团队
	 * @time 2017年12月27日
	 * @param tableCurrentMoneyInfo
	 * @param tableTeam
	 * @return
	 */
	public static Map<String,List<CurrentMoneyInfo>> get_SSJE_Gudong_Map(TableView<CurrentMoneyInfo> tableCurrentMoneyInfo, TableView<TeamInfo> tableTeam) {
		int pageIndex = DataConstans.Index_Table_Id_Map.size();
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
			boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(infos.getWanjiaId());
			if(!isSuperId) {//为解决联合ID的问题，在这里把父节点信息加了进来，后面会把父节点的联合额度为0或空的清除掉，问题：能否在此处就过滤过？？
				if(StringUtil.isBlank(infos.getShishiJine())
					|| "0".equals(infos.getShishiJine())
					|| !infos.getShishiJine().contains("-")) {
					continue;
				}
			}
//			if(StringUtil.isBlank(infos.getShishiJine())
//					|| "0".equals(infos.getShishiJine())
//					|| !infos.getShishiJine().contains("-")) {
//				continue;
//			}
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
								//eachGudongList.add(infos);
								
							}else {
								eachGudongList = gudongMap.get(gudong);
								//eachGudongList.add(infos);
							}
							/*****2018-01-01 add****/
							CurrentMoneyInfo tempInfo = copyCurrentMoneyInfo(infos);
							eachGudongList.add(tempInfo);
							/*********/
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
		
		//步骤2：处理个人外债和有联合额度的外债
		Map<String,CurrentMoneyInfo> ssje_map = get_SSJE_Map(SSJE_obList);
		handlePersonWaizhai(gudongMap,ssje_map);
		System.out.println("===============================================以上外债信息为：处理个人外债和有联合额度的外债finishes");
		
		//步骤3：添加团队
		for(TeamInfo infos : teamInfoList) {
			String teamId = infos.getTeamID();
			Huishui hsInfo = DataConstans.huishuiMap.get(teamId);
			if(hsInfo==null) {
				ErrorUtil.err("外债信息：添加负数团队时检测到缓存中没有该团队信息，团队ID:"+teamId);
				continue;
			}
			String isManaged = hsInfo.getZjManaged();
			String gudong = hsInfo.getGudong();
			String hasJiesuan = infos.getHasJiesuaned();
			String zhanji = infos.getTeamZJ();
			if(!"是".equals(isManaged) && //战绩非管理的团队
					!"1".equals(hasJiesuan) //未结算的团队
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
		
		//步骤4：将属于负数的负数个人加载到团队中
		/*
		 * A、负数个人（若其父节点的联合额度为正则不显示 ）
		 * B、公司（不显示团队；若其父节点的联合额度为正则不显示，否则不显示子ID,只显示联合额度；其他节点不变
		 * C、非公司团队（全部累加到相应团队；若其父节点的联合额度为正则只累加联合额度；其他节点累加实时金额)
		 */
		if(MapUtil.isHavaValue(gudongMap)) {
			//LMController.allClubMap.keySet().forEach(clubID -> log.info(LMController.allClubMap.keySet()));
			Iterator<Map.Entry<String, List<CurrentMoneyInfo>>> it = gudongMap.entrySet().iterator();  
		    while(it.hasNext()){  
	            Map.Entry<String, List<CurrentMoneyInfo>> entry = it.next();  
	            String gudongName = entry.getKey();
	            //if(!"B".equals(gudongName)) continue;//只测试股东B
	            List<CurrentMoneyInfo> eachList = entry.getValue();
				List<CurrentMoneyInfo> tempEachList = copyListCurrentMoneyInfo(eachList);//深层复制
				List<CurrentMoneyInfo> tempSuperList = new ArrayList<>();
	            //过滤掉没有负数团队的股东,过滤掉没有联合ID的股东
	            if(CollectUtil.isNullOrEmpty(eachList)) continue;
	            //if(eachList.stream().filter(cmi->cmi.getMingzi().startsWith("团队")).count() == 0) continue;
	            if(eachList.stream().filter(cmi->DataConstans.Combine_Super_Id_Map.containsKey(cmi.getWanjiaId())).count() == 0) continue;
	            //处理包含有负数团队的股东（既有联合ID,又有负数团队）
	            ListIterator<CurrentMoneyInfo> ite = eachList.listIterator();
	            while(ite.hasNext()) {
	            	CurrentMoneyInfo cmi = ite.next();
	            	String pId= cmi.getWanjiaId();
	            	// TODO 对下面的特殊情况进行分析
	            	if(pId == null && cmi.getMingzi() != null && cmi.getMingzi().startsWith("团队")) {
	            		continue;
	            	}
	            	boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(pId);
	            	Player _player = DataConstans.membersMap.get(pId);//1528833636
	            	String teamID = _player.getTeamName();//如ST,公司
	            	//将联合ID的金额设置到对应的团队里
	            	if(isSuperId) {
	            		String playerName = DataConstans.membersMap.get(pId).getPlayerName();
	            		if("公司".equals(teamID)) {
	            			final String _cmSuperIdSum = cmi.getCmSuperIdSum();
	            			cmi.setShishiJine(_cmSuperIdSum);
	            			log.info(String.format("外债：股东%s--属于公司的父节点%s(%s）设置联合额度%s为实时金额",gudongName,playerName,pId,_cmSuperIdSum));
	            			continue;
	            		}
	            		final String _teamId = "团队"+ DataConstans.membersMap.get(pId).getTeamName();
	            		Optional<CurrentMoneyInfo> teamInfoOpt = eachList.stream().filter(info->info.getMingzi().equals(_teamId)).findFirst();
	            		if(teamInfoOpt.isPresent()) {
	            			CurrentMoneyInfo teamInfo = teamInfoOpt.get();
	            			teamInfo.setShishiJine(NumUtil.getSum(teamInfo.getShishiJine(),cmi.getShishiJine()));
	            			ite.remove();
	            			log.info(String.format("外债：股东%s--有联合ID的父节点%s(%s)将%s转移到%s，并删除父节点", gudongName,playerName,pId,cmi.getShishiJine(),teamInfo.getMingzi()));
	            		}else {
	            			//新增一个所属团队信息
	            			ite.remove();
	            			CurrentMoneyInfo cmiInfo = new CurrentMoneyInfo(_teamId,cmi.getCmSuperIdSum(),"","");
	            			ite.add(cmiInfo);
	            			log.info(String.format("外债：股东%s--根据父点(%s)新建团队外债信息（%s），联合额度（团队的实时金额）为%s，并删除父节点", gudongName, playerName,_teamId,cmi.getCmSuperIdSum()));
	            		}
	            	}
	            	//非公司团队，非父节点，累加进其所属的团队中
	            	else if(!"公司".equals(teamID)) {
	            		String playerName = DataConstans.membersMap.get(pId).getPlayerName();
	            		final String _teamId = "团队"+ DataConstans.membersMap.get(pId).getTeamName();
	            		Optional<CurrentMoneyInfo> teamInfoOpt = eachList.stream().filter(info->info.getMingzi().equals(_teamId)).findFirst();
	            		if(teamInfoOpt.isPresent()) {
	            			CurrentMoneyInfo teamInfo = teamInfoOpt.get();
	            			teamInfo.setShishiJine(NumUtil.getSum(teamInfo.getShishiJine(),cmi.getShishiJine()));
	            			ite.remove();
	            			log.info(String.format("外债：股东%s--非父节点%s(%s)将%s转移到%s，并删除非父节点", gudongName,playerName,pId,cmi.getShishiJine(),teamInfo.getMingzi()));
	            		}else {
	            			//新增一个所属团队信息
	            			ite.remove();
	            			CurrentMoneyInfo cmiInfo = new CurrentMoneyInfo(_teamId,cmi.getShishiJine(),"","");
	            			ite.add(cmiInfo);
	            			log.info(String.format("外债：股东%s--根据非父点(%s)新建团队外债信息（%s），联合额度（团队的实时金额）为%s，并删除非父节点", gudongName, playerName,_teamId,cmi.getShishiJine()));
	            		}
	            	}
	            }
		    }
		}
		return gudongMap;
	}
	
	
	
	/**
	 * 复制一个列表
	 * @time 2018年1月13日
	 * @param sourceList
	 * @return
	 */
	private static List<CurrentMoneyInfo> copyListCurrentMoneyInfo(List<CurrentMoneyInfo> sourceList){
		List<CurrentMoneyInfo> list = new ArrayList<>();
		if(CollectUtil.isHaveValue(sourceList)) {
			sourceList.forEach(cmi -> {
				list.add(copyCurrentMoneyInfo(cmi));
			});
		}
		return list;
		
	}
	/**
	 * 获取实时金额表中的映射{玩家ID ： 金额信息}
	 * @time 2017年12月28日
	 * @param SSJE_obList
	 */
	private static Map<String,CurrentMoneyInfo> get_SSJE_Map(List<CurrentMoneyInfo> SSJE_obList) {
		Map<String,CurrentMoneyInfo> ssje_map = new HashMap<>();
		SSJE_obList.stream()
			.filter(m -> StringUtil.isNotBlank(m.getWanjiaId()))
			.forEach(info -> {
				ssje_map.put(info.getWanjiaId(), copyCurrentMoneyInfo(info));//copy一个实时金额记录
			});
		return ssje_map;
	}
	
	/**
	 * 判断父类ID是否存在于实时金额表中
	 * @time 2017年12月28日
	 * @param playerId
	 * @param ssje_map
	 * @return 
	 */
	private static boolean isExistIn_SSJE(String playerId,Map<String,CurrentMoneyInfo> ssje_map) {
		return ssje_map.containsKey(playerId);
	}
	
	/**
	 * 处理个人外债和有联合额度的外债
	 * @time 2017年12月28日
	 * @param gudongMap
	 * @param ssje_map
	 */
	private static void  handlePersonWaizhai(Map<String,List<CurrentMoneyInfo>> gudongMap,Map<String,CurrentMoneyInfo> ssje_map) {
		
		
		if(MapUtil.isNullOrEmpty(gudongMap)) return;
		
		Map<String,List<CurrentMoneyInfo>> _gudongMap = new HashMap<>();
		Map<String,List<CurrentMoneyInfo>> tempSuperMap = new HashMap<>();//临时用的变量
		Map<String,CurrentMoneyInfo> tempSuperInfoMap = new HashMap<>();//{父ID : 复制的CurrentMoneyInfo}
		
		//对负数的实时金额表记录进行处理，主要是对合并ID的记录进行
		Iterator<Map.Entry<String, List<CurrentMoneyInfo>>> it = gudongMap.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<String, List<CurrentMoneyInfo>> entry = it.next();  
            String gudongID = entry.getKey();
            List<CurrentMoneyInfo> subList = entry.getValue();
            ListIterator<CurrentMoneyInfo> ite = subList.listIterator();
    		//删除有联合额度的子ID(只保留父节点信息，联合额度为负数），注意此父节点为复制的
    		while(ite.hasNext()) {
    			CurrentMoneyInfo cmiInfo = ite.next();
    			String playerId = cmiInfo.getWanjiaId();
    			boolean isSubId = DataConstans.Combine_Sub_Id_Map.containsKey(playerId);
    			boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(playerId);
    			//是子节点
    			if(isSubId) {
    				String superId = DataConstans.Combine_Sub_Id_Map.get(playerId);
    				boolean isSuperInfo_exist_in_ssje = isExistIn_SSJE(superId,ssje_map);
    				if(isSuperInfo_exist_in_ssje) {//父节点存在于实时金额表，直接删掉子ID
    					log.info("外债：删除子节点("+getPlayerName(playerId)+"),其父节点是"+getSuperPlayerName(playerId));
    					ite.remove();
    					//特殊情况：此时父节点>=0
    				}
    				//Question:模拟的父节点是否要添加进来？？？？
					 else {////父节点不存在于实时金额表，直接删掉子ID
						 //CurrentMoneyInfo tempNewSuperInfo = getTempNewSuperInfo(superId,tempSuperMap);
						 //ite.remove();//也要删掉
						 //ite.add(tempNewSuperInfo);//添加一条模拟的父节点于外债表中
						 log.error("外债：进入未开发的代码("+getPlayerName(playerId)+")，子节点在实时金额表，但父子点并未在金额表中！");
					 }
    				
    			}
    			//删除联合额度为正数的父节点（合并id总和为正，不提取进外债菜单显示）
    			if(isSuperId) {
    				if(isSubId) {
    					ErrorUtil.err(String.format("玩家ID(%s)既是子ID,又是父ID", playerId));
    				}
    				String superId = playerId;
    				if(superId != null && tempSuperInfoMap.get(superId) == null) {
	    				//CurrentMoneyInfo superInfo = ssje_map.get(playerId);
	    				CurrentMoneyInfo superInfo = cmiInfo;
	    				if(superInfo !=null && NumUtil.getNum(superInfo.getCmSuperIdSum()) >= 0) {
	    					log.info("外债：删除父节点("+getPlayerName(playerId)+")，其联合ID为正");
	    					ite.remove();
	    				}
	    				if(superInfo !=null && NumUtil.getNum(superInfo.getCmSuperIdSum()) < 0 ) {
							log.info(String.format("外债：修改父节点%s的实时金额从%s到%s",getPlayerName(superId),
									superInfo.getShishiJine(),superInfo.getCmSuperIdSum()));
	    					superInfo.setShishiJine(superInfo.getCmSuperIdSum());//核心
	    					tempSuperInfoMap.put(superId, superInfo);
	    				}
    				}
    			}
    			//Question:对于实时金额表中有，但其团队已经存在于左边的团队信息框中，是要加在团队信息中，而自己不显示？？？
    			
    		}
        } 
		
	}
	
	
	
	/**
	 * 复制一个实时金额表的记录
	 * @time 2017年12月29日
	 * @param info
	 * @param tempSuperInfoMap
	 * @return
	 */
	private static CurrentMoneyInfo copyCurrentMoneyInfo(CurrentMoneyInfo info) {
		CurrentMoneyInfo copyInfo = new CurrentMoneyInfo(info.getMingzi(),info.getShishiJine(),
				info.getWanjiaId(),info.getCmiEdu());
		copyInfo.setColor(info.getColor());
		copyInfo.setCmSuperIdSum(info.getCmSuperIdSum());
		return copyInfo;
	}
	
	private static String getPlayerName(String playerId) {
		String name = "";
		try {
			name = DataConstans.membersMap.get(playerId).getPlayerName()+"["+playerId+"]";
		}catch(Exception e) {
			log.error("找不到ID为"+playerId+"的人员名字！");
		}
		return name;
	}
	
	private static String getSuperPlayerName(String subPlayerId) {
		String name = "";
		try {
			String supertId = DataConstans.Combine_Sub_Id_Map.get(subPlayerId);
			name = DataConstans.membersMap.get(supertId).getPlayerName()+"["+supertId+"]";
		}catch(Exception e) {
			log.error("找不到ID为"+subPlayerId+"的人员名字！");
		}
		return name;
	}
	
	/**
	 * 构造实时金额表的父节点信息
	 * 场景：子节点存在于实时金额表中，但父节点没有存在于实时金额表
	 * 效果：模拟出一条父节点信息，代替其下所有子ID信息（主要是联合额度）
	 * @time 2017年12月28日
	 * @param superId 父节点信息
	 * @param tempSuperMap 临时存储的父节点及子列表信息，用于计算联合额度
	 * @return
	 */
	private static CurrentMoneyInfo getTempNewSuperInfo(String superId,Map<String,List<CurrentMoneyInfo>> tempSuperMap) {
		//获取父节点信息
		Player superInfo = DataConstans.membersMap.get(superId);
		//构造实时金额表的父节点信息
		CurrentMoneyInfo tempNewSuperInfo = new CurrentMoneyInfo(superInfo.getPlayerName(),"0",superId,superInfo.getEdu());
		//计算联合额度(待完成)...
		
		return tempNewSuperInfo;
	}
	
//	public static Map<String,List<CurrentMoneyInfo>> get_SSJE_Gudong_Map(TableView<CurrentMoneyInfo> tableCurrentMoneyInfo, TableView<TeamInfo> tableTeam) {
//		int pageIndex = DataConstans.Index_Table_Id_Map.size();
//		if(pageIndex < 0) {return new HashMap<>();}
//		//获取实时金额数据
//		Map<String,String> map = DataConstans.All_Locked_Data_Map.get(pageIndex+"");
//		List<CurrentMoneyInfo> CurrentMoneyInfoList = null;
//		List<TeamInfo> teamInfoList = null;
//		//情况1：从最新的表中获取数据
//		if(tableTeam != null && tableTeam.getItems() != null) {
//			CurrentMoneyInfoList = new ArrayList<>();
//			for(CurrentMoneyInfo infos : tableCurrentMoneyInfo.getItems()) {
//				CurrentMoneyInfoList.add(infos);
//			}
//			teamInfoList = new ArrayList<>();
//			for(TeamInfo infos : tableTeam.getItems()) {
//				teamInfoList.add(infos);
//			}
//		}
//		//情况2：从最新的锁定表中获取数据
//		else {
//			CurrentMoneyInfoList = JSON.parseObject(MoneyService.getJsonString(map,"实时金额"), new TypeReference<List<CurrentMoneyInfo>>() {});
//			teamInfoList = JSON.parseObject(MoneyService.getJsonString(map,"团队回水"), new TypeReference<List<TeamInfo>>() {});
//		}
//		List<CurrentMoneyInfo> SSJE_obList = new LinkedList<>();
//		for(CurrentMoneyInfo infos : CurrentMoneyInfoList) {
//			if(!StringUtil.isBlank(infos.getWanjiaId()) && !StringUtil.isBlank(infos.getMingzi())) {
//				SSJE_obList.add(infos);
//			}
//		}
//		
//		//获取每个股东的实时金额数据
//		List<String> gudongList = DataConstans.gudongList;
//		int gudongSize = gudongList.size();
//		List<CurrentMoneyInfo> eachGudongList = null;
//		Map<String,List<CurrentMoneyInfo>> gudongMap = new HashMap<>();
//		String playerId;
//		Player player;
//		
//		//步骤1：添加玩家
//		for(CurrentMoneyInfo infos : SSJE_obList) {
//			if(StringUtil.isBlank(infos.getShishiJine())
//				|| "0".equals(infos.getShishiJine())
//				|| !infos.getShishiJine().contains("-")) {
//				continue;
//			}
//			playerId = infos.getWanjiaId();
//			if(!StringUtil.isBlank(playerId)) {
//				player = DataConstans.membersMap.get(playerId);
//				if(player == null) {
//					ShowUtil.show("名单列表中匹配不到该玩家!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
//					continue;
//				}
//				if(gudongList.contains(player.getGudong())) {
//					for(String gudong : gudongList) {
//						if(gudong.equals(player.getGudong())) {
//							if(gudongMap.get(gudong) == null) {
//								eachGudongList = new ArrayList<>();
//								eachGudongList.add(infos);
//							}else {
//								eachGudongList = gudongMap.get(gudong);
//								eachGudongList.add(infos);
//							}
//							gudongMap.put(gudong, eachGudongList);
//							break;
//						}
//					}
//				}else {
//					ShowUtil.show("玩家的股东不存在于股东表中!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
//					break;
//				}
//			}else {
//				ShowUtil.show("玩家ID为空!玩家名称："+infos.getMingzi()+" ID是"+infos.getWanjiaId());
//			}
//		}
//		
//		//步骤2：添加团队
//		for(TeamInfo infos : teamInfoList) {
//			String teamId = infos.getTeamID();
//			Huishui hsInfo = DataConstans.huishuiMap.get(teamId);
//			String isManaged = hsInfo.getZjManaged();
//			String gudong = hsInfo.getGudong();
//			String hasJiesuan = infos.getHasJiesuaned();
//			String zhanji = infos.getTeamZJ();
//			if(!"是".equals(isManaged) //战绩非管理的团队
//					&& !"1".equals(hasJiesuan) //未结算的团队
//					&& zhanji.contains("-")) {//战绩为负数的团队
//				CurrentMoneyInfo cmi = new CurrentMoneyInfo();
//				cmi.setMingzi("团队"+teamId);
//				cmi.setShishiJine(infos.getTeamZJ());
//				if(gudongMap.get(gudong) == null) {
//					eachGudongList = new ArrayList<>();
//					eachGudongList.add(cmi);
//				}else {
//					eachGudongList = gudongMap.get(gudong);
//					eachGudongList.add(cmi);
//				}
//				gudongMap.put(gudong, eachGudongList);
//			}
//		}
//		return gudongMap;
//	}
	
}
