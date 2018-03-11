package com.kendy.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.controller.TGController;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.TypeValueInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.zhaiwuInfo;
import com.kendy.util.CollectUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.MapUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

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
 * 托管外债
 * 
 * @author 林泽涛
 * @time 2018年3月8日 下午8:49:07
 */
public class TgWaizhaiService {

	
	private static Logger log = Logger.getLogger(TgWaizhaiService.class);
	
	private static final String UNKNOW_TG_TEAM = "未知托管团队";
	public static DecimalFormat df = new DecimalFormat("#.00");
	
	/**
	 * 刷新汇总信息表
	 */
	public static void refreWaizhaiTable(TableView<TypeValueInfo> tableWaizhai,TableView<zhaiwuInfo> tableZhaiwu) {
		Map<String,Map<String,String>> lockedMap = DataConstans.All_Locked_Data_Map;
		ZonghuiInfo zonghuiInfo = new ZonghuiInfo();
		ObservableList<ZonghuiInfo> obList = FXCollections.observableArrayList();
	}
	
	/**
	 * 获取托管团队ID
	 * 
	 * @time 2018年3月8日
	 * @return
	 */
	public static Set<String> getTGTeamId(){
		Set<String> tgTeamSet = new HashSet<>();
		try {
			TGController tgController = MyController.tgController;
			TableView<TypeValueInfo> tableTGTeamRate = tgController.tableTGTeamRate;
			if(TableUtil.isHasValue(tableTGTeamRate)) {
				ObservableList<TypeValueInfo> items = tableTGTeamRate.getItems();
				tgTeamSet = items.stream().map(TypeValueInfo::getType).collect(Collectors.toSet());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.info("托管团队：" + tgTeamSet);
		return tgTeamSet;
	}
	
	
	/**
	 * 自动生成外债信息表
	 * @param tableWaizhai
	 * @param hbox
	 * @param tableCurrentMoneyInfo
	 * @param tableTeam
	 */
	@SuppressWarnings("unchecked")
	public static void generateWaizhaiTables(TableView<TypeValueInfo> tableWaizhai,HBox hbox, 
			TableView<CurrentMoneyInfo> tableCurrentMoneyInfo, TableView<TeamInfo> tableTeam) {
//		//获取托管团队ID
//		Set<String> tgTeamIdSet = getTGTeamId();
		
		//清空数据
		ObservableList<Node> allTables =  hbox.getChildren();
		if(allTables != null && allTables.size() > 0)
			hbox.getChildren().remove(0, allTables.size());
		
		if(DataConstans.All_Locked_Data_Map.size() == 0) {
			ShowUtil.show("你当前还未锁定任意一局，查询没有数据!",2);
			return;
		}
		ObservableList<CurrentMoneyInfo> CurrentMoneyInfo_OB_List= FXCollections.observableArrayList();
		Map<String,List<CurrentMoneyInfo>>  tgTeamIdMap = get_SSJE_Gudong_Map(tableCurrentMoneyInfo);
		Map<String,String> sumMap = getSum(tgTeamIdMap);
		
		
		int tgTeamIdMapSize = tgTeamIdMap.size();
		if(tgTeamIdMapSize == 0) {
			ShowUtil.show("股东列表为空或实时金额为空！");
			return;
		}
		
        TableView<CurrentMoneyInfo> table;
        
        for(Map.Entry<String, List<CurrentMoneyInfo>> entry : tgTeamIdMap.entrySet()) {
        	String tgTeamName = entry.getKey();
        	List<CurrentMoneyInfo> list = entry.getValue();
        	table = new TableView<CurrentMoneyInfo>();
	 
        	//设置列
	        TableColumn firstNameCol = new TableColumn("团队"+tgTeamName);
	        firstNameCol.setSortable(false);//禁止排序
	        firstNameCol.setPrefWidth(80);
	        firstNameCol.setCellValueFactory(
	                new PropertyValueFactory<CurrentMoneyInfo, String>("mingzi"));
	 
	        TableColumn lastNameCol = new TableColumn(sumMap.get(tgTeamName));
	        lastNameCol.setSortable(false);//禁止排序
	        lastNameCol.setStyle("-fx-alignment: CENTER;");
	        lastNameCol.setPrefWidth(65);
	        lastNameCol.setCellValueFactory(
	                new PropertyValueFactory<CurrentMoneyInfo, String>("shishiJine"));
	        lastNameCol.setCellFactory(MyController.getColorCellFactory(new CurrentMoneyInfo()));
	        table.setPrefWidth(150);
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
        ObservableList<TypeValueInfo> obList= FXCollections.observableArrayList();
        for(Map.Entry<String, String> entry : sumMap.entrySet()) {
        	obList.add(new TypeValueInfo(entry.getKey(),entry.getValue()));
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
	public static void setWaizhaiSum(TableView<TypeValueInfo> tableWaizhai) {
		Double sum = 0d;
		ObservableList<TypeValueInfo> list = tableWaizhai.getItems();
		if(list != null && list.size() > 0) {
			for(TypeValueInfo info : list) {
				sum += NumUtil.getNum(info.getValue());
			}
		}else {
			sum = 0d;
		}
		tableWaizhai.getColumns().get(1).setText(NumUtil.digit0(sum));
	}
	
	/**
	 * 计算每个股东的外债总和
	 * @param tgTeamIdMap
	 * @return
	 */
	public static Map<String,String> getSum(Map<String,List<CurrentMoneyInfo>>  tgTeamIdMap){
		final Map<String,String> map = new HashMap<>();
		if(tgTeamIdMap != null && tgTeamIdMap.size() > 0) {
			for(Map.Entry<String, List<CurrentMoneyInfo>> entry : tgTeamIdMap.entrySet()) {
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
	 * 获取每个托管团队的实时金额
	 * 备注：不包括个人和存在于左边的团队
	 * 
	 */
	public static Map<String,List<CurrentMoneyInfo>> get_SSJE_Gudong_Map(TableView<CurrentMoneyInfo> tableCurrentMoneyInfo) {
		int pageIndex = DataConstans.All_Locked_Data_Map.size();
		if(pageIndex < 0) {return new HashMap<>();}
		
		//获取托管团队ID
		Set<String> tgTeamIdSet = getTGTeamId();
		
		//获取实时金额数据
		List<CurrentMoneyInfo> CurrentMoneyInfoList  = new ArrayList<>();
		for(CurrentMoneyInfo infos : tableCurrentMoneyInfo.getItems()) {
			CurrentMoneyInfoList.add(infos);
		}
		//情况2：从最新的锁定表中获取数据
//		CurrentMoneyInfoList = JSON.parseObject(MoneyService.getJsonString(map,"实时金额"), new TypeReference<List<CurrentMoneyInfo>>() {});
		
		//添加只属于托管的团队和个人信息
		List<CurrentMoneyInfo> SSJE_obList = new LinkedList<>();
		for(CurrentMoneyInfo infos : CurrentMoneyInfoList) {
			if(!StringUtil.isAnyBlank(infos.getWanjiaId(), infos.getMingzi())) {
				String playerId = infos.getWanjiaId();
				Player player = DataConstans.membersMap.get(playerId);
				if(player == null || StringUtil.isBlank(player.getTeamName()) || !tgTeamIdSet.contains(player.getTeamName().toUpperCase())) {
					continue;
				}else if(tgTeamIdSet.contains(player.getTeamName().toUpperCase() )){
					SSJE_obList.add(copyCurrentMoneyInfo(infos)); //深层克隆
				}
			}
		}
		
		//获取每个股东的实时金额数据
		List<String> gudongList = DataConstans.gudongList;
		int gudongSize = gudongList.size();
		List<CurrentMoneyInfo> eachGudongList = null;
		
		//托管团队的实时金额信息{ 托管团队：托管团队金额列表List}
		String playerId;
		Player player;
		
		//步骤1：添加玩家
		Map<String, List<CurrentMoneyInfo>> tgTeamCMIMap = SSJE_obList.stream()
			.filter(infos -> { 
					boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(infos.getWanjiaId());
					if(!isSuperId) {//为解决联合ID的问题，在这里把父节点信息加了进来，后面会把父节点的联合额度为0或空的清除掉，问题：能否在此处就过滤过？？
						if(StringUtil.isBlank(infos.getShishiJine())
							|| "0".equals(infos.getShishiJine())
							|| !infos.getShishiJine().contains("-")) {
							return false;
						}
					}
					return true;
				}
			)
			//.map(info -> copyCurrentMoneyInfo(info)) //复制一份
			.collect(Collectors.groupingBy(info-> {
				CurrentMoneyInfo cmi = (CurrentMoneyInfo)info; 
				Player p = DataConstans.membersMap.get(cmi.getWanjiaId());
				if(p == null) {
					return UNKNOW_TG_TEAM;
				}else {
					return p.getTeamName();
				}
			}));
			
		
		//步骤2：处理个人外债和有联合额度的外债
		//Map<String,CurrentMoneyInfo> ssje_map = get_SSJE_Map(SSJE_obList);
		//handlePersonWaizhai(tgTeamIdMap,ssje_map); TODO
		System.out.println("===============================================以上外债信息为：处理个人外债和有联合额度的外债finishes");
		tgTeamCMIMap = getFinalTGTeamMap(SSJE_obList);
		return tgTeamCMIMap;
	}
	
	/**
	 * 处理托管团队数据
	 * 
	 * @time 2018年3月8日
	 * @param tgTeamMap
	 * @return
	 */
	private static Map<String, List<CurrentMoneyInfo>> getFinalTGTeamMap(List<CurrentMoneyInfo> SSJE_obList){
		
		if(CollectUtil.isNullOrEmpty(SSJE_obList)) {
			return Collections.EMPTY_MAP;
		}
		// 1 获取非父非子节点 A
		List<CurrentMoneyInfo> not_supter_not_sub_list = SSJE_obList.stream()
			.filter(info-> not_supter_not_sub(info))
			.collect(Collectors.toList());
		
		// 2 获取子节点
//		List<CurrentMoneyInfo> sub_list = SSJE_obList.stream()
//				.filter(info-> subList(info))
//				.collect(Collectors.toList());
		
		// 3 获取父节点 
		List<CurrentMoneyInfo> super_list = SSJE_obList.stream()
				.filter(info-> superList(info))
				.map(superCMI -> {superCMI.setShishiJine(superCMI.getCmSuperIdSum()); return superCMI;})//直接把联合总和赋值到父节点的实时金额
				.collect(Collectors.toList());
		
		// 4 把子节点赋值给父节点 {父ID : 子ID列表}
//		Map<String, Double> subSumMap = getSubSumMap(sub_list);
//		List<CurrentMoneyInfo> superComputedList = super_list.stream().map(superInfo -> {
//			Double subSum = subSumMap.getOrDefault(superInfo.getWanjiaId(), 0d);
//			superInfo.setShishiJine(NumUtil.getSum(superInfo.getShishiJine(), subSum + ""));
//			return superInfo;
//		}).collect(Collectors.toList());
		
		// 5 整合A+B
		List<CurrentMoneyInfo> totalList = new ArrayList<>();
		totalList.addAll(not_supter_not_sub_list);
//		totalList.addAll(superComputedList);
		totalList.addAll(super_list);
		
		Map<String, List<CurrentMoneyInfo>> finalList = totalList.stream()
			.filter(cmi -> cmi.getShishiJine().contains("-"))
			.collect(Collectors.groupingBy(
				info->{
					 CurrentMoneyInfo cmi = (CurrentMoneyInfo)info;
					 Player p = DataConstans.membersMap.get(cmi.getWanjiaId());
					 if(p == null) {
						 log.error("玩家"+cmi.getWanjiaId()+",找不到！");
						 return UNKNOW_TG_TEAM;
					 }
					 return p.getTeamName();
				}
			));
		
		
		return finalList;
	}
	
	private static boolean not_supter_not_sub(CurrentMoneyInfo cmi) {
		boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(cmi.getWanjiaId());
		boolean isSubId = DataConstans.Combine_Sub_Id_Map.containsKey(cmi.getWanjiaId());
		return !isSuperId && !isSubId;
	}
	
	private static boolean subList(CurrentMoneyInfo cmi) {
		boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(cmi.getWanjiaId());
		boolean isSubId = DataConstans.Combine_Sub_Id_Map.containsKey(cmi.getWanjiaId());
		return !isSuperId && isSubId;
	}
	
	private static boolean superList(CurrentMoneyInfo cmi) {
		boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(cmi.getWanjiaId());
		boolean isSubId = DataConstans.Combine_Sub_Id_Map.containsKey(cmi.getWanjiaId());
		return isSuperId && !isSubId;
	}
	
	/*
	 * 获取子节点的实时金额总和
	 */
	private static Map<String, Double> getSubSumMap(List<CurrentMoneyInfo> sub_list){
		Map<String, List<CurrentMoneyInfo>> _temSubMap = sub_list.stream().collect(
				Collectors.groupingBy(info->{
					CurrentMoneyInfo cmi = (CurrentMoneyInfo)info;
					 String superId = DataConstans.Combine_Sub_Id_Map.get(cmi.getWanjiaId());
					 if(StringUtil.isBlank(superId)) {
						 log.error("玩家"+cmi.getWanjiaId()+"的父节点为空！处理托管团队数据有误！");
						 superId = "";
					 }
					 return superId;
				})
				//,Collectors.countint())  // 要是可以使用这个方法的话，就不用下面的代码 了
		);
		
		/*
		 * 把父列表中的值与子列表集合中的金额总和进行合并
		 * 备注：有一种，就是子节点中有更多的父ID信息，此情况是子ID在金额表中，但父ID不在金额表中
		 */
		Map<String, Double> subMap = new HashMap<>();
		_temSubMap.forEach((superId, subList) -> 
			subMap.put(superId, subList.stream().mapToDouble(cmi -> NumUtil.getNum(cmi.getShishiJine())).sum()));
		log.info(subMap.keySet());
		return subMap;
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
	 * @param tgTeamIdMap
	 * @param ssje_map
	 */
	private static void  handlePersonWaizhai(Map<String,List<CurrentMoneyInfo>> tgTeamIdMap,Map<String,CurrentMoneyInfo> ssje_map) {
		
		
		if(MapUtil.isNullOrEmpty(tgTeamIdMap)) return;
		
		Map<String,List<CurrentMoneyInfo>> _tgTeamIdMap = new HashMap<>();
		Map<String,List<CurrentMoneyInfo>> tempSuperMap = new HashMap<>();//临时用的变量
		Map<String,CurrentMoneyInfo> tempSuperInfoMap = new HashMap<>();//{父ID : 复制的CurrentMoneyInfo}
		
		//对负数的实时金额表记录进行处理，主要是对合并ID的记录进行
		Iterator<Map.Entry<String, List<CurrentMoneyInfo>>> it = tgTeamIdMap.entrySet().iterator();  
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
}
