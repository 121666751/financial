package application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.controller.CombineIDController;
import com.kendy.db.DBUtil;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.ProfitInfo;
import com.kendy.entity.ShangmaDetailInfo;
import com.kendy.entity.TeamHuishuiInfo;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.UserInfos;
import com.kendy.service.MoneyService;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import javafx.stage.Stage;

/**
 * 模拟数据表的常量类
 * 功能：缓存数据
 * @author 林泽涛
 */
public class DataConstans {
	
	private static Logger log = Logger.getLogger(DataConstans.class);
	
	//缓存人员名单登记Excel中的数据{玩家ID={}}
	public static Map<String, Player> membersMap = new HashMap<>();//撤销后不变
	//团队ID=玩家ID列表
//	public static Map<String, List<String>> teamWanjiaIdMap = new HashMap<>();//撤销后不变
	//玩家ID=上码详情列表（正在使用的值）
	public static Map<String,List<ShangmaDetailInfo>> SM_Detail_Map= new HashMap<>();
	//玩家ID=上码详情列表（上一场所定的数据，用于撤销时恢复原数据）
	public static Map<String,List<ShangmaDetailInfo>> SM_Detail_Map_Locked= new HashMap<>();
	
	//缓存战绩文件夹中多份excel中的数据 {场次=infoList...}
	public static Map<String,List<UserInfos>> zjMap = new LinkedHashMap<>();
	//缓存当局回水
	public static List<TeamHuishuiInfo> Dangju_Team_Huishui_List = new LinkedList<>();
	//缓存战绩文件夹中多份excel中的数据 {团队ID=List<TeamHuishuiInfo>...}这个可能会被修改，用在展示每场的tableTeam信息
	public static Map<String,List<TeamHuishuiInfo>> Team_Huishui_Map = new LinkedHashMap<>();
	//这个不会被修改，是总的团队回水记录。用在团队回水当天查询
	public static Map<String,List<TeamHuishuiInfo>> Total_Team_Huishui_Map = new LinkedHashMap<>();//撤销后不变
	
	//缓存120场次的所有锁定数据{页数第几局={...}}
	public static Map<String,Map<String,String>> All_Locked_Data_Map  = new HashMap<>();//撤销后不变
	//锁定后是第X局
	public static AtomicInteger Paiju_Index = new AtomicInteger(1);//撤销后不变
	//缓存场次与局映射
	public static Map<String,String> Index_Table_Id_Map = new HashMap<>();//撤销后不变
	
	//缓存父ID和子ID  {父ID=List<子ID>}
	public static Map<String,Set<String>> Combine_Super_Id_Map = new HashMap<>();
	//缓存子ID和父ID  {子ID=父ID}
	public static Map<String,String> Combine_Sub_Id_Map = new HashMap<>();
	
	public static String Root_Dir = System.getProperty("user.home");//撤销后不变
	
	//缓存团队回水
	public static Map<String, Huishui> huishuiMap = new HashMap<>();//撤销后不变
	
	//缓存实时开销
	public static Map<String, String> kaixiaoMap = new HashMap<>();
	
	//缓存昨日留底
//	public static Map<String,Map<String,String>> preDataMap = new HashMap<>();
	public static Map<String,String> preDataMap = new HashMap<>();
	
	//缓存所有股东名称
	public static List<String> gudongList = new ArrayList<>();//撤销后不变
	
	//缓存一些窗体的实例
	public static Map<String,Stage> framesNameMap = new HashMap<>();//撤销后不变
	
	//导入战绩后缓存一些总和信息
	public static Map<String,Double> SumMap = new HashMap<>();
	
	//缓存上一场次已经锁定的团队记录{teamID=TeamInfo...},主要用于合并团队记录(好像也没必要了这个)
	public static Map<String,TeamInfo> Team_Info_Pre_Map = new HashMap<>();//撤销后不变
	
	//缓存日期
	public static String Date_Str = "";
	
	//初始化股东列表
	public static void initGudong() {
		String gudongs = PropertiesUtil.readProperty("gudong");
		if(!StringUtil.isBlank(gudongs)){
			for(String gudong : gudongs.split(",")){
				gudongList.add(gudong);
			}
		}
	}
	
	static{
		//股东
		initGudong();
		
		//初始化人员表或团队回水表
		initMetaData();
		
		log.info("初始化完成。。。");
	}
	
	/**
	 * 锁定时获取缓存数据
	 * 注意：人员，回水和合并ID不缓存，单独存储到数据
	 * 
	 * @time 2017年11月4日
	 * @return
	 */
	public static Map<String,String> getLockedDataMap() {
		Map<String,String> lastLockedDataMap = new HashMap<>();
//		缓存人员名单登记Excel中的数据{玩家ID={}}
//		public static Map<String, Player> membersMap = new HashMap<>();//撤销后不变
//		lastLockedDataMap.put("membersMap", JSON.toJSONString(DataConstans.membersMap));
		
//		团队ID=玩家ID列表
//		public static Map<String, List<String>> teamWanjiaIdMap = new HashMap<>();//撤销后不变
//		lastLockedDataMap.put("teamWanjiaIdMap", JSON.toJSONString(DataConstans.teamWanjiaIdMap));
		
//		玩家ID=上码详情列表（正在使用的值）
//		public static Map<String,List<ShangmaDetailInfo>> SM_Detail_Map= new HashMap<>();
		lastLockedDataMap.put("SM_Detail_Map", JSON.toJSONString(DataConstans.SM_Detail_Map));
		
//		玩家ID=上码详情列表（上一场所定的数据，用于撤销时恢复原数据）
//		public static Map<String,List<ShangmaDetailInfo>> SM_Detail_Map_Locked= new HashMap<>();
		lastLockedDataMap.put("SM_Detail_Map_Locked", JSON.toJSONString(DataConstans.SM_Detail_Map_Locked));
		
//		缓存战绩文件夹中多份excel中的数据 {场次=infoList...}
//		public static Map<String,List<UserInfos>> zjMap = new LinkedHashMap<>();
		lastLockedDataMap.put("zjMap", JSON.toJSONString(DataConstans.zjMap));
		
//		缓存当局回水
//		public static List<TeamHuishuiInfo> Dangju_Team_Huishui_List = new LinkedList<>();
		lastLockedDataMap.put("Dangju_Team_Huishui_List", JSON.toJSONString(DataConstans.Dangju_Team_Huishui_List));
		
//		缓存战绩文件夹中多份excel中的数据 {团队ID=List<TeamHuishuiInfo>...}这个可能会被修改，用在展示每场的tableTeam信息
//		public static Map<String,List<TeamHuishuiInfo>> Team_Huishui_Map = new LinkedHashMap<>();
		lastLockedDataMap.put("Team_Huishui_Map", JSON.toJSONString(DataConstans.Team_Huishui_Map));
		
//		这个不会被修改，是总的团队回水记录。用在团队回水当天查询
//		public static Map<String,List<TeamHuishuiInfo>> Total_Team_Huishui_Map = new LinkedHashMap<>();//撤销后不变
		lastLockedDataMap.put("Total_Team_Huishui_Map", JSON.toJSONString(DataConstans.Total_Team_Huishui_Map));
		
//		缓存120场次的所有锁定数据{页数第几局={...}}
//		public static Map<String,Map<String,String>> All_Locked_Data_Map  = new HashMap<>();//撤销后不变
		lastLockedDataMap.put("All_Locked_Data_Map", JSON.toJSONString(DataConstans.All_Locked_Data_Map));
		
//		锁定后是第X局
//		public static AtomicInteger Paiju_Index = new AtomicInteger(1);//撤销后不变
		lastLockedDataMap.put("Paiju_Index", JSON.toJSONString(DataConstans.Paiju_Index));
		
//		缓存场次与局映射
//		public static Map<String,String> Index_Table_Id_Map = new HashMap<>();//撤销后不变
		lastLockedDataMap.put("Index_Table_Id_Map", JSON.toJSONString(DataConstans.Index_Table_Id_Map));
		
//		缓存父ID和子ID  {父ID=List<子ID>}
//		public static Map<String,Set<String>> Combine_Super_Id_Map = new HashMap<>();
//		lastLockedDataMap.put("Combine_Super_Id_Map", JSON.toJSONString(DataConstans.Combine_Super_Id_Map));
		
//		缓存子ID和父ID  {子ID=父ID}
//		public static Map<String,String> Combine_Sub_Id_Map = new HashMap<>();
//		lastLockedDataMap.put("Combine_Sub_Id_Map", JSON.toJSONString(DataConstans.Combine_Sub_Id_Map));
//		
//		public static String Root_Dir = System.getProperty("user.home");//撤销后不变
		lastLockedDataMap.put("Root_Dir", JSON.toJSONString(DataConstans.Root_Dir));

//		缓存团队回水
//		public static Map<String, Huishui> huishuiMap = new HashMap<>();//撤销后不变
//		lastLockedDataMap.put("huishuiMap", JSON.toJSONString(DataConstans.huishuiMap));
		
//		缓存实时开销
//		public static Map<String, String> kaixiaoMap = new HashMap<>();
		lastLockedDataMap.put("kaixiaoMap", JSON.toJSONString(DataConstans.kaixiaoMap));
		
//		缓存昨日留底
//		public static Map<String,Map<String,String>> preDataMap = new HashMap<>();;
		lastLockedDataMap.put("preDataMap", JSON.toJSONString(DataConstans.preDataMap));
		
//		缓存所有股东名称
//		public static List<String> gudongList = new ArrayList<>();//撤销后不变
		lastLockedDataMap.put("gudongList", JSON.toJSONString(DataConstans.gudongList));
		
//		缓存一些窗体的实例
//		public static Map<String,Stage> framesNameMap = new HashMap<>();//撤销后不变
		//lastLockedDataMap.put("framesNameMap", JSON.toJSONString(DataConstans.framesNameMap));
		
//		导入战绩后缓存一些总和信息
//		public static Map<String,Double> SumMap = new HashMap<>();
		lastLockedDataMap.put("SumMap", JSON.toJSONString(DataConstans.SumMap));

//		缓存上一场次已经锁定的团队记录{teamID=TeamInfo...},主要用于合并团队记录(好像也没必要了这个)
//		public static Map<String,TeamInfo> Team_Info_Pre_Map = new HashMap<>();//撤销后不变
		lastLockedDataMap.put("Team_Info_Pre_Map", JSON.toJSONString(DataConstans.Team_Info_Pre_Map));
		
//		缓存日期
//		public static String Date_Str = "";
		lastLockedDataMap.put("Date_Str", JSON.toJSONString(DataConstans.Date_Str));
		
		return lastLockedDataMap;
	}
	
	
	/**
	 * 初始化所有缓存
	 */
	public static void clearAllData() {
		//缓存人员名单登记Excel中的数据{玩家ID={}}
		membersMap = new HashMap<>();//撤销后不变
		//团队ID=玩家ID列表
//		teamWanjiaIdMap = new HashMap<>();//撤销后不变
		//玩家ID=上码详情列表（正在使用的值）
		SM_Detail_Map= new HashMap<>();
		//玩家ID=上码详情列表（上一场所定的数据，用于撤销时恢复原数据）
		SM_Detail_Map_Locked= new HashMap<>();
		//缓存战绩文件夹中多份excel中的数据 {场次=infoList...}
		zjMap = new LinkedHashMap<>();
		//缓存当局回水
		Dangju_Team_Huishui_List = new LinkedList<>();
		//缓存战绩文件夹中多份excel中的数据 {团队ID=List<TeamHuishuiInfo>...}这个可能会被修改，用在展示每场的tableTeam信息
		Team_Huishui_Map = new LinkedHashMap<>();
		//这个不会被修改，是总的团队回水记录。用在团队回水当天查询
		Total_Team_Huishui_Map = new LinkedHashMap<>();//撤销后不变
		//缓存120场次的所有锁定数据{页数第几局={...}}
		All_Locked_Data_Map  = new HashMap<>();//撤销后不变
		//锁定后是第X局
		Paiju_Index = new AtomicInteger(1);//撤销后不变
		//缓存场次与局映射
		Index_Table_Id_Map = new HashMap<>();//撤销后不变
		//缓存父ID和子ID  {父ID=List<子ID>}
		Combine_Super_Id_Map = new HashMap<>();
		//缓存子ID和父ID  {子ID=父ID}
		Combine_Sub_Id_Map = new HashMap<>();
		Root_Dir = System.getProperty("user.home");//撤销后不变
		//缓存团队回水
		huishuiMap = new HashMap<>();//撤销后不变
		//缓存实时开销
		kaixiaoMap = new HashMap<>();
		//缓存昨日留底
		preDataMap = new HashMap<>();
		//缓存所有股东名称
		gudongList = new ArrayList<>();//撤销后不变
		//缓存一些窗体的实例
		framesNameMap = new HashMap<>();//撤销后不变
		//导入战绩后缓存一些总和信息
		SumMap = new HashMap<>();
		//缓存上一场次已经锁定的团队记录{teamID=TeamInfo...},主要用于合并团队记录(好像也没必要了这个)
		Team_Info_Pre_Map = new HashMap<>();//撤销后不变
		Date_Str = "";
	}
	
	/**
	 * 初始化数据
	 */
	public static void initMetaData() {
		//清空所有数据
		//clearAllData();
		
		try {
			//初始化人员数据
			List<Player> memberList = DBUtil.getAllMembers();
			membersMap = new HashMap<>();
			memberList.forEach(player -> {
				membersMap.put(player.getgameId(), player);
			});
			
			//初始化团队回水
			List<Huishui> teamHSList = DBUtil.getAllTeamHS();
			huishuiMap = new HashMap<>();
			teamHSList.forEach(hs -> {
				huishuiMap.put(hs.getTeamId().toUpperCase(), hs);
			});
			
			//初始化合并ID关系
			initCombineId();
			
		} catch (Exception e) {
			ShowUtil.show("警告：初始化人员表或团队回水表或合并ID表失败！原因："+e.getMessage());
		}
	}
	
	
	/**
	 * 初始化合并ID关系
	 * 
	 * @time 2017年11月4日
	 */
	public static void initCombineId() {
		DataConstans.Combine_Super_Id_Map = DBUtil.getCombineData();
		if(DataConstans.Combine_Super_Id_Map ==null ) {
			DataConstans.Combine_Super_Id_Map = new HashMap<>();
		}else {
			DataConstans.Combine_Sub_Id_Map = new HashMap<>();
			DataConstans.Combine_Super_Id_Map.forEach((parentId,subIdSet) -> {
				for(String subId : subIdSet)
					DataConstans.Combine_Sub_Id_Map.put(subId, parentId);
			});
		}
	}
	
	
	/**
	 * 加载昨日数据
	 */
	public static void loadPreData() {
		//初始化昨日留底数据
		preDataMap = new HashMap<>();
		try {
			preDataMap = DBUtil.getLastPreData();
			//从数据库中获取上一次保存的锁定数据
			if(!DBUtil.isPreData2017VeryFirst()) {
				Map<String, String> map = DBUtil.getLastLockedData();
				SumMap = JSON.parseObject(map.get("SumMap"), new TypeReference<Map<String, Double>>() {});
				//add 2017-10-22
				//增加父子合并ID数据
				//缓存父ID和子ID  {父ID=List<子ID>}
//				Combine_Super_Id_Map = JSON.parseObject(map.get("Combine_Super_Id_Map"), new TypeReference<Map<String, Set<String>>>() {});
				//缓存子ID和父ID  {子ID=父ID}
//				Combine_Sub_Id_Map = JSON.parseObject(map.get("Combine_Sub_Id_Map"), new TypeReference<Map<String, String> >() {});
			}
		} catch (Exception e) {
			ShowUtil.show("警告：初始化昨日留底数据失败！原因："+e.getMessage());
		}
	}
	
	
	public static Map<String,Map<String,String>> getPreData(){
		
		
		return null;
	}
	
	/**
	 * 中途恢复缓存数据
	 * 
	 * （人员和回水以及合并ID不在此处，在调用此方法前已经设置了）
	 */
	public static void recoveryAllCache() {
		//清空所有数据
		clearAllData();
		
		//从数据库中获取上一次保存的锁定数据
		Map<String, String> map = DBUtil.getLastLockedData();
		
		//开始恢复数据
		
		//缓存人员名单登记Excel中的数据{玩家ID={}}
//		membersMap = JSON.parseObject(map.get("membersMap"), new TypeReference<Map<String, Player>>() {});
		//团队ID=玩家ID列表
//		teamWanjiaIdMap = JSON.parseObject(map.get("teamWanjiaIdMap"), new TypeReference<Map<String, List<String>>>() {});
		//玩家ID=上码详情列表（正在使用的值）
		SM_Detail_Map= JSON.parseObject(map.get("SM_Detail_Map"), new TypeReference<Map<String, List<ShangmaDetailInfo>>>() {});
		//玩家ID=上码详情列表（上一场所定的数据，用于撤销时恢复原数据）
		SM_Detail_Map_Locked= JSON.parseObject(map.get("SM_Detail_Map_Locked"), new TypeReference<Map<String, List<ShangmaDetailInfo>>>() {});
		//缓存战绩文件夹中多份excel中的数据 {场次=infoList...}
		zjMap = JSON.parseObject(map.get("zjMap"), new TypeReference<Map<String, List<UserInfos>>>() {});
		//缓存当局回水
		Dangju_Team_Huishui_List = JSON.parseObject(map.get("Dangju_Team_Huishui_List"), new TypeReference<List<TeamHuishuiInfo>>() {});
		//缓存战绩文件夹中多份excel中的数据 {团队ID=List<TeamHuishuiInfo>...}这个可能会被修改，用在展示每场的tableTeam信息
		Team_Huishui_Map = JSON.parseObject(map.get("Team_Huishui_Map"), new TypeReference<Map<String, List<TeamHuishuiInfo>>>() {});
		//这个不会被修改，是总的团队回水记录。用在团队回水当天查询
		Total_Team_Huishui_Map = JSON.parseObject(map.get("Total_Team_Huishui_Map"), new TypeReference<Map<String, List<TeamHuishuiInfo>>>() {});
		//缓存120场次的所有锁定数据{页数第几局={...}}
		All_Locked_Data_Map  =JSON.parseObject(map.get("All_Locked_Data_Map"), new TypeReference<Map<String, Map<String, String>>>() {});
		//锁定后是第X局
		Paiju_Index = JSON.parseObject(map.get("Paiju_Index"), new TypeReference<AtomicInteger>() {});
		//缓存场次与局映射
		Index_Table_Id_Map = JSON.parseObject(map.get("Index_Table_Id_Map"), new TypeReference<Map<String, String>>() {});
		
		//缓存父ID和子ID  {父ID=List<子ID>}
//		Combine_Super_Id_Map = JSON.parseObject(map.get("Combine_Super_Id_Map"), new TypeReference<Map<String, Set<String>>>() {});
		//缓存子ID和父ID  {子ID=父ID}
//		Combine_Sub_Id_Map = JSON.parseObject(map.get("Combine_Sub_Id_Map"), new TypeReference<Map<String, String> >() {});
		
		Root_Dir = JSON.parseObject(map.get("Root_Dir"), new TypeReference<String>() {});
		//缓存团队回水
//		huishuiMap = JSON.parseObject(map.get("huishuiMap"), new TypeReference<Map<String, Huishui>>() {});
		//缓存实时开销
		kaixiaoMap = JSON.parseObject(map.get("kaixiaoMap"), new TypeReference<Map<String, String>>() {});
		//缓存昨日留底
		preDataMap =JSON.parseObject(map.get("preDataMap"), new TypeReference<Map<String, String>>() {});
		//缓存所有股东名称
		gudongList = JSON.parseObject(map.get("gudongList"), new TypeReference<List<String>>() {});
		//缓存一些窗体的实例
		//framesNameMap = JSON.parseObject(map.get("framesNameMap"), new TypeReference<Map<String, Stage> >() {});
		//导入战绩后缓存一些总和信息
		SumMap = JSON.parseObject(map.get("SumMap"), new TypeReference<Map<String, Double>>() {});
		//缓存上一场次已经锁定的团队记录{teamID=TeamInfo...},主要用于合并团队记录(好像也没必要了这个)
		Team_Info_Pre_Map = JSON.parseObject(map.get("Team_Info_Pre_Map"), new TypeReference<Map<String, TeamInfo>>() {});
		Date_Str = JSON.parseObject(map.get("Date_Str"), new TypeReference<String>() {});

		System.out.println("==============中途恢复缓存数据。。。finishes");
	}
	
	
	
}
