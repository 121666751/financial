package com.kendy.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.controller.GDController;
import com.kendy.db.DBUtil;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.DangjuInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.JiaoshouInfo;
import com.kendy.entity.KaixiaoInfo;
import com.kendy.entity.PingzhangInfo;
import com.kendy.entity.Player;
import com.kendy.entity.ProfitInfo;
import com.kendy.entity.TeamHuishuiInfo;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.TotalInfo;
import com.kendy.entity.UserInfos;
import com.kendy.entity.WanjiaInfo;
import com.kendy.entity.ZijinInfo;
import com.kendy.excel.ExportMembersExcel;
import com.kendy.excel.ExportTeamhsExcel;
import com.kendy.interfaces.Entity;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

import application.Constants;
import application.DataConstans;
import application.Main;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextInputDialog;

/**
 * 场次信息服务类 
 * 
 * @author 林泽涛
 * @time 2017年10月28日 下午5:19:48
 */
public class MoneyService {
	
	private static Logger log = Logger.getLogger(MoneyService.class);

	//{玩家ID=CurrentMoneyInfo}
	public static Map<String,CurrentMoneyInfo>  Table_CMI_Map = new HashMap<>();
	public static TableView<CurrentMoneyInfo> tableCurrentMoneyInfo;
	
	public static void iniitMoneyInfo(TableView<CurrentMoneyInfo> tableCurrentMoney) {
		tableCurrentMoneyInfo = tableCurrentMoney;
	}
	
	public static DecimalFormat df = new DecimalFormat("#.00");

	/**
	 * 导入战绩成功后
	 * 自动填充玩家信息表、牌局表、团队表以及当局
	 * 备注：这些表数据在当局范围内是固定不变的
	 * 
	 * @param table 玩家信息表
	 * @param tablePaiju 牌局表
	 * @param userInfoList 导入的玩家战绩列表
	 */
	public static void fillTablerAfterImportZJ(TableView<TotalInfo> table,
			TableView<WanjiaInfo> tablePaiju,
			TableView<DangjuInfo> tableDangju,
			TableView<JiaoshouInfo> tableJiaoshou,
			TableView<TeamInfo> tableTeam,
			List<UserInfos> userInfoList,String tableId){
		//1清空 表数据
		table.setItems(null);
		//2获取InfoList
		ObservableList<TotalInfo> tableTotalInfoList = FXCollections.observableArrayList();
		ObservableList<WanjiaInfo> tableWanjiaInfoList = FXCollections.observableArrayList();
		TotalInfo info = null;
		Player player ;
		WanjiaInfo wanjia;
		List<TeamHuishuiInfo>  teamHuishuiList = null;//用于缓存和计算团累计团队回水
		Set<String> relatedTeamIdSet = new HashSet<>();//用于只展示本次战绩的团队信息
		DataConstans.Dangju_Team_Huishui_List = new LinkedList<>();//每次导入都去初始化当局团队战绩信息
		for(UserInfos zj : userInfoList){
			
			/*************************************************** 填充信息表 *****************/
			info = new TotalInfo();
			//团	ID	玩家	计分	实收	保险
			String playerId = zj.getPlayerId();
			player = DataConstans.membersMap.get(playerId);
			String teamId = "";
			if(player != null){
				teamId = player.getTeamName();
				teamId = StringUtil.isBlank(teamId) ? "" : teamId.toUpperCase();
			}
			//add 2017-10-26
			else {
				Player tempPlayer = DBUtil.getMemberById(playerId);
				if(tempPlayer != null && !StringUtil.isBlank(tempPlayer.getTeamName())) {
					DataConstans.membersMap.put(playerId, tempPlayer);
					teamId = tempPlayer.getTeamName();
					teamId = StringUtil.isBlank(teamId) ? "" : teamId.toUpperCase();
				}
			}
			String name = zj.getPlayerName();
			String baoxian = zj.getinsurance();
			String shishou = getShiShou(zj.getZj());
			String chuHuishui = digit1(getChuhuishui(zj.getZj(), teamId));
			String shuihouxian = digit1((-1)*Double.valueOf(zj.getinsurance())*0.975+"");
			String shouHuishui = digit1(Math.abs(Double.valueOf(zj.getZj()))*0.025+"");
			String baohui = digit1(getHuiBao(baoxian,teamId));
			String heLirun = digit2(getHeLirun(shouHuishui,chuHuishui,shuihouxian,baohui));

			//团(团ID)
			info.setTuan(teamId);
			//ID
			info.setWanjiaId(playerId);
			//玩家
			info.setWanjia(name);
			//计分
			info.setJifen(zj.getZj());//?计分 = 战绩？
			//实收
			info.setShishou(shishou);
			//保险
			info.setBaoxian(baoxian);
			//出回水
			info.setChuHuishui(chuHuishui);
			//保回
			info.setBaohui(baohui);
			//水后险
			info.setShuihouxian(shuihouxian);
			//收回水
			info.setShouHuishui(shouHuishui);
			//合利润
			info.setHeLirun(heLirun);
			
			tableTotalInfoList.add(info);
			
			/*************************************************** 填充牌局表 *****************/
			wanjia = new WanjiaInfo();
			String yicunJifen = getYicunJifen(zj.getPlayerId());
			String heji = digit0(getNum(yicunJifen) + getNum(shishou) + "");
			wanjia.setPaiju("第"+tableId+"局");
			wanjia.setWanjiaName(zj.getPlayerName());
			wanjia.setYicunJifen(yicunJifen);
			wanjia.setZhangji(shishou);
			wanjia.setHeji(heji);
			wanjia.setWanjiaId(playerId);
			
			/*************************************************** 缓存各团队回水记录 *****************/
			teamId = teamId.toUpperCase();
//			if(!StringUtil.isBlank(teamId) && !"公司".equals(teamId)) {
			if(!StringUtil.isBlank(teamId)) {
				//缓存到当局团队战绩信息中
				TeamHuishuiInfo teamHuishuiInfo = 
						new TeamHuishuiInfo(teamId,playerId,name,shishou,baoxian,chuHuishui,
								"第"+tableId+"局",zj.getZj(),baohui,shouHuishui,zj.getDay());
				DataConstans.Dangju_Team_Huishui_List.add(teamHuishuiInfo);
				
				if(!"公司".equals(teamId)) {
					relatedTeamIdSet.add(teamId);
				}
				//缓存到总团队回水中(结算按钮后从中减少)
				teamHuishuiList = DataConstans.Team_Huishui_Map.get(teamId);
				if( teamHuishuiList == null ) {
					teamHuishuiList = new ArrayList<>();
				}
				teamHuishuiList.add(teamHuishuiInfo);
				DataConstans.Team_Huishui_Map.put(teamId,teamHuishuiList);
//				//缓存到总团队回水中(不会从中减少)
//				teamHuishuiList = DataConstans.Total_Team_Huishui_Map.get(teamId);
//				if( teamHuishuiList == null ) {
//					teamHuishuiList = new ArrayList<>();
//				}
//				teamHuishuiList.add(
//						new TeamHuishuiInfo(teamId,playerId,name,shishou,baoxian,chuHuishui,"第"+tableId+"局")
//						);
//				DataConstans.Total_Team_Huishui_Map.put(teamId,teamHuishuiList);
			}else {
				log.warn("检测到团队ID是空或为公司，玩家是："+name);
			}

			//是否显示支付按钮
			wanjia.setHasPayed(relatedTeamIdSet.contains(teamId) ? teamId : "");
			tableWanjiaInfoList.add(wanjia);
		}
		
		//3填充
		table.setItems(tableTotalInfoList);
		table.refresh();
//		tablePaiju.setItems(tableWanjiaInfoList);
		fillTablePaiju(tablePaiju,tableWanjiaInfoList);//添加需要支付的按钮靠前排
		
		//缓存当局固定总和
		DataConstans.SumMap.putAll(getSums(tableTotalInfoList));
		//填充当局表和交收表
		initTableDangjuAndTableJiaoshou(tableDangju,tableJiaoshou);
		//填充团队表
		fillTableTeam(tableTeam,relatedTeamIdSet);
		//更新实时上码表的个人详情
		ShangmaService.updateShangDetailMap(tablePaiju);
	}
	
	public static void fillTablePaiju(TableView<WanjiaInfo> tablePaiju,ObservableList<WanjiaInfo> tableWanjiaInfoList) {
		List<WanjiaInfo> linkedList = new LinkedList<>();
		List<WanjiaInfo> noNeedPayList = new ArrayList<>();
		
		if(tableWanjiaInfoList != null && tableWanjiaInfoList.size() > 0) {
			for(WanjiaInfo info : tableWanjiaInfoList) {
				linkedList.add(info);
			}
			
			ListIterator<WanjiaInfo> it = linkedList.listIterator();
			WanjiaInfo wj = null;
			while(it.hasNext()) {
				wj = it.next();
//				//解决不时本应支付确显示成已支付的bug
//				if(!"1".equals(wj.getHasPayed())){
//					btn.setText("支付");
//				}else {
//					btn.setText("已支付");
//				}
//				setGraphic(btn);//小林：这一行解决了支付按钮消失的问题
				//在此处增加是否要显示该按钮(如果玩家从属于某个非空或非公司的团队，则无需显示按钮)
				String tempTeamId = wj.getHasPayed();//这个tempTeamId是hasPayed的内容,这里没有公司的人
				if(!StringUtil.isBlank(tempTeamId) && !"0".equals(tempTeamId)) {
					//获取团队信息
					Huishui hs = DataConstans.huishuiMap.get(tempTeamId);
					//情况一：有从属团队的玩家，再分两种情况
					if(hs != null) {
						//A:若团队战绩要管理，需要显示支付按钮
						if("是".equals(hs.getZjManaged())) {
//							log.info("====teamId为不为空，要显示，要战绩管理：是");
//							setGraphic(btn);
						//B:若团队战绩不要管理，无须显示支付按钮
						}else {
							noNeedPayList.add(wj);
							it.remove();
//							log.info("hsPayed:====================hs为空："+hs.getZjManaged());
//							setGraphic(null);
						}
					}
				}else {
//					log.info("====teamId为空或为0，要显示+"+DataConstans.membersMap.get(wj.getWanjiaId()).getTeamName());
//					//情况二：对于没有从从属的团队的玩家或者团队是公司的玩家，一定需要需要显示支付按钮
//					setGraphic(btn);
				}
			}
			
			if(noNeedPayList.size() > 0) {
				for(WanjiaInfo info : noNeedPayList) {
					linkedList.add(info);
				}
			}
			
			if(noNeedPayList != null && noNeedPayList.size() > 0) {
				tableWanjiaInfoList.clear();
				for(WanjiaInfo info : linkedList) {
					tableWanjiaInfoList.add(info);
				}
			}
			//刷新牌局表
			tablePaiju.setItems(tableWanjiaInfoList);
			tablePaiju.refresh();
			
			
		}
	}
	
	
	//待返回的团队回水和团队保险总和
	private static double sum_teamHS_and_teamBS = 0.0;
	//填充团队表
	private static void fillTableTeam(TableView<TeamInfo> table,Set<String> relatedTeamIdSet) {
		sum_teamHS_and_teamBS = 0;
		//准备数据
		ObservableList<TeamInfo> list = FXCollections.observableArrayList();
		relatedTeamIdSet = DataConstans.Team_Huishui_Map.keySet();//这是后期增加：查看所有团队
		relatedTeamIdSet.forEach(relatedTeamId -> {
			if(!"公司".equals(relatedTeamId)) {
				double sumOfZJ = 0.0;
				double sumOfHS = 0.0;
				double sumOfBS = 0.0;
				List<TeamHuishuiInfo> teamLS = DataConstans.Team_Huishui_Map.get(relatedTeamId);
				for(TeamHuishuiInfo info : teamLS) {
					sumOfZJ += Double.valueOf(info.getShishou());
					sumOfHS += Math.abs(Double.valueOf(info.getChuHuishui()));
					sumOfBS += Double.valueOf(info.getBaoxian());//就是回保
				}
				double sum = 0.0d;
				if(sumOfBS != 0) {//需要乘以团队保险比例的
					//log.info(sumOfBS+"=="+getTeamInsuranceRate(relatedTeamId));
					sumOfBS *= -getTeamInsuranceRate(relatedTeamId);
				}
				if(isZjManaged(relatedTeamId)) {//战绩代管理,不加战绩(个人记录有支付按钮，个人处理战绩)
					sum = sumOfHS + sumOfBS;
				}else {
					sum = sumOfHS + sumOfBS+sumOfZJ;//战绩不代管理,加战绩(个人记录没有支付按钮，团队处理战绩)
				}
				sum_teamHS_and_teamBS += sum;//团队回水和团队保险的总和
				//计算当局需要显示的团队记录
				list.add(new TeamInfo(relatedTeamId,digit0(sumOfZJ),digit1(sumOfHS+""),digit1(sumOfBS+""),digit0(sum)));
			}
		});
		//缓存总和
		DataConstans.SumMap.put("团队回水及保险总和", sum_teamHS_and_teamBS);
		//填充团队表
		table.setItems(list);
		table.refresh();
	}
	//获取团队表的总和
	public static double  getTeamSum(ObservableList<TeamInfo> resultTeamList) {
		Double sum = 0.0;
		for(TeamInfo teamInfo : resultTeamList) {
			sum += getNum(teamInfo.getTeamSum());
		}
		return sum.intValue();
	}
	
	//获取团队实体
	private static TeamInfo getTeamInfoEntity(TeamInfo t1,TeamInfo t2) {
		return  new TeamInfo(
				t1.getTeamID(),
				getSumWithDigit0(t1.getTeamZJ(),t2.getTeamZJ()),
				getSumWithDigit0(t1.getTeamHS(),t2.getTeamHS()),
				getSumWithDigit0(t1.getTeamBS(),t2.getTeamBS()),
				getSumWithDigit0(t1.getTeamSum(),t2.getTeamSum())
				);
	}
	//求两个数据的和
	private static String getSumWithDigit0(String first , String second) {
		return digit0(getNum(first)+getNum(second));
	}
	//获取团队保险比例（回保）
	public static double getTeamInsuranceRate(String teamId) {
		Huishui hs = DataConstans.huishuiMap.get(teamId);
		if(hs != null) {
			String rate = hs.getInsuranceRate();
			if(!StringUtil.isBlank(rate)) {
				return Double.valueOf(rate);
			}
		}
		return 1.0d;
	}
	//战绩是否管理
	private static boolean isZjManaged(String teamId) {
		 boolean zjManage = false;
		 Huishui hs = DataConstans.huishuiMap.get(teamId);
         if(hs != null) {
             if("是".equals(hs.getZjManaged())) {
            	 zjManage = true;
             }
         }
         return zjManage;
	}
	
	private static void initTableDangjuAndTableJiaoshou(TableView<DangjuInfo> tableDangju,
			TableView<JiaoshouInfo> tableJiaoshou){
		
		if(DataConstans.SumMap != null && DataConstans.SumMap.size() != 0) {
			//初始化当局表
			ObservableList<DangjuInfo> list = FXCollections.observableArrayList();
			List<String> tableDangjuList = Arrays.asList("服务费","保险","团队回水","团队回保");
			tableDangjuList.forEach( type -> {
				list.add(new DangjuInfo(type,DataConstans.SumMap.get(type)+""));
			});
			tableDangju.setItems(list);
			//初始化交收表
			ObservableList<JiaoshouInfo> list2 = FXCollections.observableArrayList();
			List<String> tableJiaoshouList = Arrays.asList("客户输赢","收回水","水后险");
			tableJiaoshouList.forEach( type -> {
				list2.add(new JiaoshouInfo(type,DataConstans.SumMap.get(type)+""));
			});
			tableJiaoshou.setItems(list2);
		}
	}
	
	/**
	 * 缓存一些固定总和
	 * @param lists
	 */
	public static Map<String,Double> getSums(ObservableList<TotalInfo> lists) {
		List<String> jifenList = new ArrayList<>();
		List<String> shishouList = new ArrayList<>();
		List<String> baoxianList = new ArrayList<>();
		List<String> chuhuishuiList = new ArrayList<>();
		List<String> baohuiList = new ArrayList<>();
		List<String> shuihouxianList = new ArrayList<>();
		List<String> shouHuishuiList = new ArrayList<>();
		List<String> heLirunList = new ArrayList<>();
		
		lists.forEach(info -> {
			jifenList.add(info.getJifen());
			shishouList.add(info.getShishou());
			baoxianList.add(info.getBaoxian());
			chuhuishuiList.add(info.getChuHuishui());
			baohuiList.add(info.getBaohui());
			shuihouxianList.add(info.getShuihouxian());
			shouHuishuiList.add(info.getShouHuishui());
			heLirunList.add(info.getHeLirun());
		});
		
		Map<String,Double> cacheMap = new HashMap<>();
		//备份数据
		cacheMap.put("总计分", getSum(jifenList));
		cacheMap.put("总实收", getSum(shishouList));
		cacheMap.put("总保险", getSum(baoxianList));
		cacheMap.put("总出回水", getSum(chuhuishuiList));
		cacheMap.put("总保回", getSum(baohuiList));
		cacheMap.put("总水后险", getSum(shuihouxianList));
		cacheMap.put("总收回水", getSum(shouHuishuiList));
		cacheMap.put("总合利润", getSum(heLirunList));
		//当局表
		cacheMap.put("服务费", cacheMap.get("总收回水"));
		cacheMap.put("保险", cacheMap.get("总水后险"));
		cacheMap.put("团队回水", cacheMap.get("总出回水"));
		cacheMap.put("团队回保", cacheMap.get("总保回")*(-1));
		double dj = Double.sum(cacheMap.get("服务费"), cacheMap.get("保险"))+Double.sum(cacheMap.get("团队回水"), cacheMap.get("团队回保"));
		cacheMap.put("当局", dj);//总当局
		//交收表
		cacheMap.put("客户输赢", cacheMap.get("总实收"));
		cacheMap.put("收回水", cacheMap.get("总收回水"));
		cacheMap.put("水后险", cacheMap.get("总水后险"));
		cacheMap.put("交收", cacheMap.get("客户输赢")+cacheMap.get("收回水")+cacheMap.get("水后险"));//总
		
		return cacheMap;
	}
	
	public static Double getSum(List<String> list) {
		double sum = 0.0;
		for(String value : list) {
			sum += getNum(value);
		}
		return Double.valueOf(df.format(sum));
	}
	/**
	 * 计算回保
	 * 不要参考Excel上的公式！！
	 * 新公式：保险 * 团队保险比例 * (-1)
	 */
	public static String getHuiBao(String baoxian,String teamId) {
		if(StringUtil.isBlank(baoxian) || "0".equals(baoxian))
			return "0";
		Huishui hs = DataConstans.huishuiMap.get(teamId);
		if(hs == null) {
			return "0";
		}else {
			String insuranceRate = hs.getInsuranceRate();
			insuranceRate = !StringUtil.isBlank(insuranceRate) ? insuranceRate : "0";
			return Double.valueOf(baoxian) * Double.valueOf(insuranceRate)*(-1)+"";
		}
	}
	
	/**
	 * 计算实收
	 * 公式：战绩>0,则乘以0.95，若小于0，则直接返回原战绩
	 * 若是12.63则直接取整数
	 */
	public static String getShiShou(String zhanji){
		Double zj = Double.valueOf(zhanji);
		if(zj > 0){
			String shishou = zj * 0.95 +"";
			if(shishou.contains(".")) {
				shishou = shishou.substring(0, shishou.lastIndexOf("."));
			}
			return shishou;
		}else{
			return zhanji;
		}
	}
	
	/**
	 * 计算出回水
	 * 公式：相应团的回水比例 乘以|原始战绩|，若回水比例为无（如公司），则直接返回
	 * 
	 */
	public static String getChuhuishui(String zhanji,String teamId){
		Huishui hs = DataConstans.huishuiMap.get(teamId);
		if(hs == null){
			return "";
		}else{
			Double hsRate = Double.valueOf(hs.getHuishuiRate());
			Double zj = Double.valueOf(zhanji);
			return Math.abs(zj * hsRate )*-1 + "";
		}
	}
	
	/**
	 * 计算合利润
	 * 公式：IF(收回水>0,收回水+出回水+保回+水后检,0)
	 * 
	 */
	public static String getHeLirun(String shouHuishui,String chuHuishui,String baohui,String shuihouxian){
		double _shouHushui = getNum(shouHuishui);
		if(_shouHushui > 0){
			return _shouHushui + getNum(chuHuishui) + getNum(baohui) + getNum(shuihouxian) + "";
		}else{
			return "0";
		}
	}
	
	/**
	 * 获取实时金额表中的已存积分（根据玩家ID)
	 * 
	 * @time 2017年11月12日
	 * @param playerName
	 * @param playerId
	 * @return
	 */
	public static String getYicunJifen(String playerId){
		//从当前实时金额表中获取最新的已存积分（波哥要求）
		ObservableList<CurrentMoneyInfo> list = tableCurrentMoneyInfo.getItems();
		if(list != null) {
			for(CurrentMoneyInfo info : list) {
				if(info.getWanjiaId() != null && !StringUtil.isBlank(playerId) && playerId.equals(info.getWanjiaId())) {
					return info.getShishiJine();
				}
			}
		}
		return "";
	}
	
	
	//*****************************************************************************
	public static Double getNum(String str){
		if(StringUtil.isBlank(str)){
			return 0.0;
		}else{
			try{
				return Double.valueOf(str).doubleValue();
			}catch(Exception e){
				return 0.0;
			}
		}
	}
	
	public static String digit2(String str){
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#######0.00");  
		if(!StringUtil.isBlank(str)){
			String res = df.format(Double.valueOf(str));
			return res;
		}
		return "0";
	}
	//截取一位小数，后面不进行舍入
	public static String digit1(String str){
		if(!(StringUtil.isBlank(str))) {
			try {
				if(str.contains(".")) {
					str = str.substring(0, str.lastIndexOf(".")+2);
					return str;
				}else {
					return str;
				}
			} catch (Exception e) {
				str = String.format("%.4f",Float.valueOf(str));
				str = str.substring(0, str.lastIndexOf(".")+2);
				return str;
			}
    	}
		return "0.0";
	}
	public static String digit0(String str){
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#######0");  
		if(!StringUtil.isBlank(str)){
			String res = "0";
			try {
				res = df.format(Double.valueOf(str));
			} catch (NumberFormatException e) {
				res = "0";
				log.error("检测到问题："+res+"=="+e.getMessage());
			}
			return res;
		}
		return "0";
	}
	public static String digit0(double num){
		java.text.DecimalFormat   df   =new   java.text.DecimalFormat("#######0");  
		String res = df.format(Double.valueOf(num));
		return res;
	}
	//*****************************************************************************	
	
	/**
	 * 将前一次的实时金额数据备份到第二张表 2017-01-01
	 * @param table
	 * @param userInfoList
	 */
	public static void fillTableCurrentMoneyInfo(TableView<CurrentMoneyInfo> table,
			TableView<ZijinInfo> tableZijin,
			TableView<ProfitInfo> tableProfit,
			TableView<KaixiaoInfo> tableKaixiao,
			Label LMLabel){

		Map<String,String> map = DataConstans.preDataMap;
		/************************************************ 实时金额表  **************/
		//1清空表数据
		table.setItems(null);
		//2匹配ID和额度
		ObservableList<CurrentMoneyInfo> observableList1 = FXCollections.observableArrayList();
		String name = "实时金额";
		Map<String,String> jineMap = JSON.parseObject(map.get(name), new TypeReference<Map<String,String>>() {});
		final String FLAG = "###";
		jineMap.forEach((mingziAndID,shishsijine) -> { //mingziAndID 之前是mingzi
			String mingzi = "";
			String playerId = "";
			if(mingziAndID.contains(FLAG)) {
				mingzi = mingziAndID.split(FLAG)[0];
				playerId = mingziAndID.split(FLAG)[1];
				playerId = playerId.replace("#", "");//playerId中含有#号，则删除#号（不严谨）
			}else {
				mingzi = mingziAndID;
			}
			Player player = DataConstans.membersMap.get(playerId);//add 现在根据玩家去匹配
			CurrentMoneyInfo cmi;
			if(player == null || StringUtil.isBlank(player.getgameId())) {
				//实时金额中的人名找不到对应的玩家ID
				cmi = new CurrentMoneyInfo(mingzi,shishsijine,"","");
			}else {
				cmi = new CurrentMoneyInfo(mingzi,shishsijine,player.getgameId(),player.getEdu());
			}
			observableList1.add(cmi);
		});
		table.setItems(observableList1);
		table.refresh();
		//刷新合并ID操作
		flush_SSJE_table();
		
		//加载其他几个表
		fillTables(tableZijin,tableProfit,tableKaixiao,LMLabel,map );
	}
	
	public static void fillTables(TableView<ZijinInfo> tableZijin,
			TableView<ProfitInfo> tableProfit,
			TableView<KaixiaoInfo> tableKaixiao,
			Label LMLabel,Map<String,String> map ) {
		/************************************************ 资金表  **************/
		//1清空 表数据
		tableZijin.setItems(null);
		//2获取InfoList
		ObservableList<ZijinInfo> observableList2 = FXCollections.observableArrayList();
		Map<String,String> zijinMap = JSON.parseObject(map.get("资金"), new TypeReference<Map<String,String>>() {});
		zijinMap.forEach((type,account) -> {
			observableList2.add(new ZijinInfo(type,account));
		});
		tableZijin.setItems(observableList2);
		/************************************************ 利润表  **************/
		//1清空 表数据
		tableProfit.setItems(null);
		//2获取InfoList
		ObservableList<ProfitInfo> observableList3 = FXCollections.observableArrayList();
		Map<String,String> profitMap = JSON.parseObject(map.get("昨日利润"), new TypeReference<Map<String,String>>() {});
		profitMap.forEach((type,account) -> {
			observableList3.add(new ProfitInfo(type,digit0(account)));
		});
		tableProfit.setItems(observableList3);
		/************************************************ 实时开销表  **************/
		tableKaixiao.setItems(null);
		//2获取InfoList
		ObservableList<KaixiaoInfo> observableList4 = FXCollections.observableArrayList();
		Map<String,String> kaixiaoMap = JSON.parseObject(map.get("实时开销"), new TypeReference<Map<String,String>>() {});
		kaixiaoMap.forEach((type,account) -> {
			observableList4.add(new KaixiaoInfo(type,digit0(account)));
		});
		tableKaixiao.setItems(observableList4);
		//缓存一一场的实时开销总和
		DataConstans.SumMap.put("上场开销", getSumOfTableKaixiao(tableKaixiao));
		/************************************************ 联盟对帐  **************/
		LMLabel.setText(LMLabel.getText());//注意这里是{联盟对帐={联盟对帐=3000}}
	}

	//模拟Oracle的nvl函数
	public static String nvl(String condition,String ifNullStr) {
		if(!StringUtil.isBlank(condition)) {
			return condition.trim();
		}else {
			return ifNullStr;
		}
	}
	
	private static void updatetTableProfitFirst(TableView<ProfitInfo> tableProfit) {
		/************************************************ 利润表  **************/
		//1清空 表数据
		tableProfit.setItems(null);
		//2获取InfoList
		ObservableList<ProfitInfo> observableList3 = FXCollections.observableArrayList();
		Map<String,String> profitMap = null;
		if(DataConstans.Index_Table_Id_Map.size() == 0) {
			log.info("=========================刷新（从昨天加载数据）");
			if(DBUtil.isPreData2017VeryFirst()) {
				profitMap = JSON.parseObject(DataConstans.preDataMap.get("昨日利润"), new TypeReference<Map<String,String>>() {});
				if(profitMap != null) {
					profitMap.forEach((type,account) -> {
						observableList3.add(new ProfitInfo(type,digit0(account)));
					});
				}
			}else {
				List<ProfitInfo> list = JSON.parseObject(DataConstans.preDataMap.get("利润"), new TypeReference<List<ProfitInfo>>() {});
				if(list != null) {
					list.forEach( info  -> {
						observableList3.add(new ProfitInfo(info.getProfitType(),digit0(info.getProfitAccount())));
					});
				}
			}
			
		}else {
			log.info("=========================刷新（从上一场加载数据）");
			Map<String,String> map = DataConstans.All_Locked_Data_Map.get(DataConstans.Index_Table_Id_Map.size()+"");
			List<ProfitInfo> ProfitInfoList = JSON.parseObject(MoneyService.getJsonString(map,"利润"), new TypeReference<List<ProfitInfo>>() {});
			for(ProfitInfo infos : ProfitInfoList) {
				observableList3.add(infos);
			}
			
			//sumMap相关设值
			MyController mc = Main.myController;
			int size = DataConstans.Index_Table_Id_Map.size();
			if(size >= 1) {
				//此情况下要从上一场加载==团队回水总和
//				DataConstans.SumMap = new HashMap<String,Double>();
				String shangchangKaixiao = MoneyService.getLockedInfo(size+"", "实时开销总和");
				DataConstans.SumMap.put("上场开销", Double.valueOf(shangchangKaixiao));//add 9-1
			}
		}
		tableProfit.setItems(observableList3);
		tableProfit.refresh();
		
	}
	
	
	
	
	
	/**
	 * 计算实时金额表的总和 
	 */
	private static Double getSumOfTableCurrentMoney(TableView<CurrentMoneyInfo> table) {
		//获取ObserableList
		ObservableList<CurrentMoneyInfo> list = table.getItems();
		Double sumOfTableCurrentMoney = 0.0 ;
		if(list == null)
			return sumOfTableCurrentMoney;
		String tempSingleVal = "";//实时金额表中每一行的具体金额
		for(CurrentMoneyInfo moneyInfo : list) {
			tempSingleVal = moneyInfo.getShishiJine();
			if(!StringUtil.isBlank(tempSingleVal) && !"".equals(moneyInfo.getMingzi())) {
				sumOfTableCurrentMoney += getNum(tempSingleVal);
			}
		};
		return sumOfTableCurrentMoney;
	}
	/**
	 * 计算资金表的总和 
	 */
	private static Double getSumOfTableZijin(TableView<ZijinInfo> table) {
		//获取ObserableList
		ObservableList<ZijinInfo> list = table.getItems();
		Double sumOfTable = 0.0 ;
		if(list == null)
			return sumOfTable;
		String tempSingleVal = "";//表中每一行的具体金额
		for(ZijinInfo moneyInfo : list) {
			tempSingleVal = moneyInfo.getZijinAccount();
			sumOfTable += getNum(tempSingleVal);
		};
		return sumOfTable;
	}
	/**
	 * 计算利润表的总和 
	 */
	private static Double getSumOfTableProfit(TableView<ProfitInfo> table) {
		//获取ObserableList
		ObservableList<ProfitInfo> list = table.getItems();
		Double sumOfTable = 0.0 ;
		String tempSingleVal = "";//表中每一行的具体金额
		for(ProfitInfo moneyInfo : list) {
			if(!"当日开销".endsWith(moneyInfo.getProfitType())) {
			tempSingleVal = moneyInfo.getProfitAccount();
			sumOfTable += getNum(tempSingleVal);
			}
		};
		return sumOfTable;
	}
	/**
	 * 计算开销表的总和 
	 */
	private static Double getSumOfTableKaixiao(TableView<KaixiaoInfo> table) {
		Double sumOfTable = 0.0 ;
		//获取ObserableList
		ObservableList<KaixiaoInfo> list = table.getItems();
		if(list == null) {
			return sumOfTable;
		}
	 	String tempSingleVal = "";//表中每一行的具体金额
		for(KaixiaoInfo moneyInfo : list) {
			tempSingleVal = moneyInfo.getKaixiaoMoney();
			sumOfTable += getNum(tempSingleVal);
		};
		return sumOfTable;
	}
	/**
	 * 计算平帐表的总和 
	 */
	private static Double getSumOfTablePingzhang(TableView<PingzhangInfo> table) {
		//获取ObserableList
		ObservableList<PingzhangInfo> list = table.getItems();
		Double sumOfTable = 0.0 ;
		if(list == null )
			return sumOfTable;
		if(list.size() == 2) {
			sumOfTable = getNum(list.get(1).getPingzhangMoney()) - getNum(list.get(0).getPingzhangMoney());
		}
		return sumOfTable;
	}
	
	/**
	 * 设置总和，显示在界面上
	 * 适用于资金表、开销表、交收表、平帐表、当局表
	 * @param table
	 * @param sum
	 */
	public static void setTotalNumOnTable(TableView<? extends Entity> table,double sum) {
		table.getColumns().get(1).setText(digit0(sum+""));
	}
	public static void setTotalNumOnTable(TableView<? extends Entity> table,double sum,int sumCloumn) {
		table.getColumns().get(sumCloumn).setText(digit0(sum+""));
	}
	
	/**
	 * 在加载上一场或从昨天加载利润表时（点击平帐按钮）先缓存总团队服务费的值
	 * @time 2018年1月5日
	 * @param table
	 * @return
	 */
	private static String getAllTeamFWF(TableView<ProfitInfo> table) {
		String allTeamFWF = "0";
		try {
			ProfitInfo profitInfo = TableUtil.getItem(table).filtered(info-> "总团队服务费".equals(info.getProfitType())).get(0);
			allTeamFWF = profitInfo.getProfitAccount();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return allTeamFWF;
	}
	
	/**
	 * 利润表修改总团队服务费(累积该团队的服务费)
	 * @time 2018年1月5日
	 * @param teamFWF
	 */
	public static void add2AllTeamFWF_from_tableProfit(TableView<ProfitInfo> table,Double teamFWF) {
		try {
			ProfitInfo profitInfo = TableUtil.getItem(table)
					.filtered(info -> "总团队服务费".equals(info.getProfitType())).get(0);
			String allTeamFWF = NumUtil.digit0(NumUtil.getNum(profitInfo.getProfitAccount()) + teamFWF);
			profitInfo.setProfitAccount(allTeamFWF);
			table.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 点击刷新同步按钮时计算各面板总金额
	 */
	public static void refreshSumPane(
			TableView<TeamInfo> tableTeam,
			TableView<ZijinInfo> tableZijin,
			TableView<KaixiaoInfo> tableKaixiao,
			TableView<ProfitInfo> tableProfit,
			TableView<CurrentMoneyInfo> tableCurrentMoney,
			TableView<PingzhangInfo> tablePingzhang,
			Label LMLabel
			) {
		//log.info(JSON.toJSONString(DataConstans.SumMap));
		//0 重新初始化利润表(避免重复计算)
		//String allTeamFWF = getAllTeamFWF(tableProfit);//缓存总团队服务费
		
		updatetTableProfitFirst(tableProfit);
		add2AllTeamFWF_from_tableProfit(tableProfit,MyController.current_Jiesuaned_team_fwf_sum);//修改总团队服务费表
		
		//1 计算开销总和
		double sumOfKaixiao = getSumOfTableKaixiao(tableKaixiao);
		DataConstans.SumMap.put("开销", sumOfKaixiao);
		setTotalNumOnTable(tableKaixiao,sumOfKaixiao);
		
		//2 更新利润表
		updateTableProfit(tableProfit,sumOfKaixiao);
		
		//add 2018-02-25 
		clearTableProfit(tableProfit);
		//3 计算利润表总和
		double sumOfProfit = getSumOfTableProfit(tableProfit);
		DataConstans.SumMap.put("利润", sumOfProfit);
		setTotalNumOnTable(tableProfit,sumOfProfit);
		
		//4 计算资金表总和
		double sumOfZijin = getSumOfTableZijin(tableZijin);
		DataConstans.SumMap.put("资金", sumOfZijin);
		setTotalNumOnTable(tableZijin,sumOfZijin);
		
		//5 计算实时金额表总和 
		double sumOfCurrentMoney = getSumOfTableCurrentMoney(tableCurrentMoney);
		DataConstans.SumMap.put("实时金额", sumOfCurrentMoney);
		setTotalNumOnTable(tableCurrentMoney,sumOfCurrentMoney,2);
		
		//6 更新平帐表
		updateTablePingzhang(tablePingzhang,sumOfZijin,sumOfProfit,sumOfCurrentMoney,LMLabel);
		double sumOfPingzhang = getSumOfTablePingzhang(tablePingzhang);
		DataConstans.SumMap.put("平帐", sumOfPingzhang);
		setTotalNumOnTable(tablePingzhang,sumOfPingzhang);
		
		//7更新团队回水表（若果之前有点击结算按钮）
		double sumOfTeamHS = getTeamSum("团队回水及保险总和");//DataConstans.SumMap.get("团队回水及保险总和");
		setTotalNumOnTable(tableTeam,sumOfTeamHS,4);
		
	}
	
	/**
	 * 清空利润表
	 * 
	 * @time 2018年2月25日
	 * @param tableProfit
	 */
	public static void clearTableProfit(TableView<ProfitInfo> tableProfit) {
		//清空利润表
		if(GDController.has_quotar_oneKey) {
			tableProfit.getItems().forEach(info -> info.setProfitAccount("0"));
			ShowUtil.show("已经点击过贡献值中的一键分配，清空利润栏！", 2);
		}
	}
	
	/**
	 * 更新利润表
	 */
	public static void updateTableProfit(TableView<ProfitInfo> table,double todayKaixiao) {
		//获取ObserableList
		try {
			ObservableList<ProfitInfo> list = table.getItems();
			for(ProfitInfo info : list) {
				String type = info.getProfitType();
				if("总服务费".equals(type)){
					info.setProfitAccount(digit0(getNum(info.getProfitAccount())+getTeamSum("服务费")));
					
				}else if("总保险".endsWith(type)) {
					info.setProfitAccount(digit0(getNum(info.getProfitAccount())+getTeamSum("保险")));
					
				}else if("总回水".endsWith(type)) {
					info.setProfitAccount(digit0(getNum(info.getProfitAccount())+getTeamSum("团队回水")));
					
				}else if("总开销".endsWith(type)) {
					info.setProfitAccount(digit0((getNum(info.getProfitAccount())+todayKaixiao - getTeamSum("上场开销"))+""));
					
				}else if("总回保".endsWith(type)) {
					//无公式  总保回
					info.setProfitAccount(digit0(getNum(info.getProfitAccount())+(getTeamSum("总保回")*(-1))+""));
				}
			};
			table.setItems(list);
		} catch (Exception e) {
			log.error("检测到更新利润表出错，可能是没有导入数据就进行平帐和锁定");
		}
	}
	/**
	 * 更新平帐表
	 * 
	 */
	public static void updateTablePingzhang(TableView<PingzhangInfo> table,Double sumOfZijin,Double sumOfProfit,
			Double sumOfCurrentMoney,Label LMVal) {
		//获取ObserableList
		ObservableList<PingzhangInfo> list = FXCollections.observableArrayList();
		Double _LMVal = 0d;
		try {
			_LMVal = Double.valueOf(LMVal.getText());
		}catch(Exception e) {
			_LMVal = 0d;
		}
		list.add(new PingzhangInfo("外债+利润+团队回水+联盟对帐",digit0(sumOfProfit +sumOfCurrentMoney+
				getTeamSum("团队回水及保险总和") +_LMVal+"")));//Double.valueOf(LMVal.getText())
		list.add(new PingzhangInfo("资金",digit0(sumOfZijin+"")));
		table.setItems(list);
	}
	
	/**
	 * 获取团队累计的一些信息（可能有问题）
	 */
	public static double getTeamSum(String name) {
		if(DataConstans.SumMap.get(name) == null ) {
			return 0d;
		}else {
			return DataConstans.SumMap.get(name);
		}
	}
	
	/**
	 * 获取下一个要展示的位置
	 * 
	 * @time 2017年11月12日
	 * @param table
	 * @param searchText
	 * @return
	 */
	private static Integer getNextSelectedIndex(TableView<CurrentMoneyInfo> table,String searchText) {
		List<Integer> indexList = new LinkedList<>();
		if(!StringUtil.isBlank(searchText)) {
			if(table == null || table.getItems() == null) {
				return -1;
			}
			ObservableList<CurrentMoneyInfo> list = table.getItems();
			String tempName = "";
			int selectedIndex = table.getSelectionModel().getSelectedIndex();
			for(CurrentMoneyInfo moneyInfo : list) {
				tempName = moneyInfo.getMingzi();
				if(!StringUtil.isBlank(tempName)){
					if(tempName.contains(searchText.trim())
						|| tempName.toLowerCase().contains(searchText.trim().toLowerCase())
						|| tempName.toUpperCase().contains(searchText.trim().toUpperCase())
							) {
						int index = list.indexOf(moneyInfo);
						indexList.add(index);
					}
				}
			};
			int size = indexList.size();
			//返回排序序号
			if(size == 0) {
				return -1;
			}else if(size == 1) {
				//返回第一个
				return indexList.get(0);
			}else  {
				if(indexList.contains(selectedIndex)) {
					int i = indexList.indexOf(selectedIndex);
					if(i ==  (size-1)) {
						//返回第一个
						return indexList.get(0);
					}else {
						//返回下一个
						return indexList.get(i+1);
					}
				}else {
					//返回第一个
					return indexList.get(0);
				}
			}
		}
		return -1;
	}
	
	
	
	/**
	 * 搜索
	 * @param table
	 * @param searchText
	 */
	public static void searchRowAction(TableView<CurrentMoneyInfo> table,String searchText) {
		int nextIndex = getNextSelectedIndex(table,searchText);
		if( -1 == nextIndex ) {
			ShowUtil.show("找不到结果！", 1);
		}else {
			table.scrollTo(nextIndex);
			table.getSelectionModel().select(nextIndex);
		}
	}
	
	/**
	 * 编辑实时金额后修改对应的已存积分
	 * 备注：如果没有找到供修改的记录，也要让其通过
	 */
	public static boolean changeYicunJifen(TableView<WanjiaInfo> tablePaiju,String playerName,String newYicunJifen) {
		boolean isOK = false;
		if(!StringUtil.isBlank(newYicunJifen)) {
			try {
				Double.valueOf(newYicunJifen);
			}catch(Exception e) {
				ShowUtil.show(newYicunJifen+"不是数值！！");
			}
		}
		ObservableList<WanjiaInfo> wanjiaList = tablePaiju.getItems();
		if(wanjiaList != null && wanjiaList.size()>0) {
			boolean isInPaiju = false;
			for(WanjiaInfo info : wanjiaList) {
				if(info.getWanjiaName().equals(playerName)) {
					if("1".equals(info.getHasPayed())) {
						ShowUtil.show("抱歉，已支付过，请到下一场修改",2);
					}else {
						String zj = info.getZhangji();
						info.setYicunJifen(newYicunJifen);
						info.setHeji(digit0(Double.sum(Double.valueOf(zj), Double.valueOf(newYicunJifen))));
						ShowUtil.show("已经更新到已存积分",2);
						tablePaiju.setItems(wanjiaList);
						isOK = true;
						break;
					}
					isInPaiju = true;
				}
			};
			if(!isInPaiju ) {//如果没有找到供修改的记录，也要让其通过
				isOK = true;
			}
		}else {
			isOK = true;
		}
		return isOK;
	}
	
	
	/**
	 * 缓存十个表数据
	 * @param tableTeamHuishui
	 * @return
	 */
	public static Map<String,String> lock10Tables(
			TableView<TotalInfo> tableTotal,
			TableView<WanjiaInfo> tableWanjia,
			TableView<TeamInfo> tableTeam,
			TableView<CurrentMoneyInfo> tableCurrentMoney,
			TableView<ZijinInfo> tableZijin,
			TableView<KaixiaoInfo> tableKaixiao,
			TableView<ProfitInfo> tableProfit,
			TableView<DangjuInfo> tableDangju,
			TableView<JiaoshouInfo> tableJiaoshou,
			TableView<PingzhangInfo> tablePingzhang,
			Label LMLabel
			) throws Exception{
		/************************************************ 战绩表  **************/
		ObservableList<TotalInfo> TotalInfoObservableList = tableTotal.getItems();
		List<TotalInfo> TotalInfoList = new LinkedList<>();
		for(TotalInfo info : TotalInfoObservableList) {
			TotalInfoList.add(info);
		}
		
		/************************************************ 玩家表  **************/
		ObservableList<WanjiaInfo> WanjiaInfoObservableList = tableWanjia.getItems();
		if(WanjiaInfoObservableList == null) {
			WanjiaInfoObservableList = FXCollections.observableArrayList();
		}
		List<WanjiaInfo> WanjiaInfoList = new LinkedList<>();
		for(WanjiaInfo info : WanjiaInfoObservableList) {
			WanjiaInfoList.add(info);
		}
		
		/************************************************ 团队回水表  **************/
		ObservableList<TeamInfo> TeamInfoInfoObservableList = tableTeam.getItems();
		List<TeamInfo> TeamInfoList = new LinkedList<>();
		for(TeamInfo info : TeamInfoInfoObservableList) {
			String hasJiesuaned = info.getHasJiesuaned();//对于已结算过的不再记录
			if("".equals(hasJiesuaned) || "0".equals(hasJiesuaned)) {
				TeamInfoList.add(info);
			}
		}
		String sumOfTeam = tableTeam.getColumns().get(4).getText();
		
		/************************************************ 实时金额表  **************/
		ObservableList<CurrentMoneyInfo> ObservableList = tableCurrentMoney.getItems();
		List<CurrentMoneyInfo> list = new LinkedList<>();
		for(CurrentMoneyInfo info : ObservableList) {
			list.add(info);
		}
		String sumOfCurrentMoney = tableCurrentMoney.getColumns().get(2).getText();
		/************************************************ 资金表  **************/
		ObservableList<ZijinInfo> ZijinInfoObservableList = tableZijin.getItems();
		List<ZijinInfo> listZijinInfo = new LinkedList<>();
		for(ZijinInfo info : ZijinInfoObservableList) {
			listZijinInfo.add(info);
		}
		String sumOfZijin = tableZijin.getColumns().get(1).getText();
		/************************************************ 利润表  **************/
		ObservableList<ProfitInfo> ProfitInfoObservableList = tableProfit.getItems();
		List<ProfitInfo> listProfitInfo = new LinkedList<>();
		for(ProfitInfo info : ProfitInfoObservableList) {
			listProfitInfo.add(info);
		}
		String sumOfProfit = tableProfit.getColumns().get(1).getText();
		/************************************************ 实时开销表  **************/
		ObservableList<KaixiaoInfo> KaixiaoInfoObservableList = tableKaixiao.getItems();
		List<KaixiaoInfo> listKaixiaoInfo = new LinkedList<>();
		for(KaixiaoInfo info : KaixiaoInfoObservableList) {
			log.info(info.getKaixiaoType());
			listKaixiaoInfo.add(info);
		}
		String sumOfKaixiao = tableKaixiao.getColumns().get(1).getText();
		/************************************************ 当局表  **************/
		ObservableList<DangjuInfo> DangjuInfoObservableList = tableDangju.getItems();
		List<DangjuInfo> listDangjuInfo = new LinkedList<>();
		for(DangjuInfo info : DangjuInfoObservableList) {
			listDangjuInfo.add(info);
		}
		String sumOfDangjuInfo = tableDangju.getColumns().get(1).getText();
		/************************************************ 交收表  **************/
		ObservableList<JiaoshouInfo> JiaoshouInfoObservableList = tableJiaoshou.getItems();
		List<JiaoshouInfo> listJiaoshouInfo = new LinkedList<>();
		for(JiaoshouInfo info : JiaoshouInfoObservableList) {
			listJiaoshouInfo.add(info);
		}
		String sumOfJiaoshouInfo = tableJiaoshou.getColumns().get(1).getText();
		/************************************************ 平帐表  **************/
		ObservableList<PingzhangInfo> PingzhangInfoObservableList = tablePingzhang.getItems();
		List<PingzhangInfo> listPingzhangInfo = new LinkedList<>();
		for(PingzhangInfo info : PingzhangInfoObservableList) {
			listPingzhangInfo.add(info);
		}
		String sumOfPingzhangInfo = tablePingzhang.getColumns().get(1).getText();
		//汇总信息
		Map<String,String> map = new HashMap<>();
		map.put("战绩", JSON.toJSONString(TotalInfoList));
		map.put("玩家", JSON.toJSONString(WanjiaInfoList));
		map.put("团队回水", JSON.toJSONString(TeamInfoList));
		map.put("团队回水总和", sumOfTeam);
		map.put("实时金额", JSON.toJSONString(list));
		map.put("实时金额总和", sumOfCurrentMoney);
		map.put("资金", JSON.toJSONString(listZijinInfo));
		map.put("资金总和", sumOfZijin);
		map.put("利润", JSON.toJSONString(listProfitInfo));
		map.put("利润总和", sumOfProfit);
		map.put("实时开销", JSON.toJSONString(listKaixiaoInfo));
		map.put("实时开销总和", sumOfKaixiao);
		map.put("当局", JSON.toJSONString(listDangjuInfo));
		map.put("当局总和", sumOfDangjuInfo);
		map.put("交收", JSON.toJSONString(listJiaoshouInfo));
		map.put("交收总和", sumOfJiaoshouInfo);
		map.put("平帐", JSON.toJSONString(listPingzhangInfo));
		map.put("平帐总和", sumOfPingzhangInfo);
		map.put("联盟对帐", getLMLabelText(LMLabel));
		
		return map;
	}
	public static String getLMLabelText(Label LMLabel) {
		String LMVal = LMLabel.getText();
		if("0.00".equals(LMVal)) {
			LMLabel.setText(Constants.ZERO);
			return Constants.ZERO;
		}
		return LMVal;
		
	}
	
	/**
	 * 从上场锁定的数据中获取信息
	 * @param key
	 * @return
	 */
	public static String getLockedInfo(String keyOfJu,String subKey) {
		String value = "";//可能是JSONString,也可能是普通字符串
		if(DataConstans.Index_Table_Id_Map.size() != 0 ) {
			Map<String,String> map = DataConstans.All_Locked_Data_Map.get(keyOfJu);
			if(map != null) {
				value =  map.get(subKey);
				value = value == null ? "" : value;
			}
		}
		return value;
	}
	/**
	 * 根据分页自动恢复该页锁定的10个表数据
	 */
	public static void reCovery10TablesByPage(
			TableView<TotalInfo> tableTotal,
			TableView<WanjiaInfo> tableWanjia,
			TableView<TeamInfo> tableTeam,
			TableView<CurrentMoneyInfo> tableCurrentMoney,
			TableView<ZijinInfo> tableZijin,
			TableView<KaixiaoInfo> tableKaixiao,
			TableView<ProfitInfo> tableProfit,
			TableView<DangjuInfo> tableDangju,
			TableView<JiaoshouInfo> tableJiaoshou,
			TableView<PingzhangInfo> tablePingzhang,
			Label LMLabel,
			int pageIndex) throws Exception{

		//获取该页所有数据
		Map<String,String> map = DataConstans.All_Locked_Data_Map.get(pageIndex+"");
		/********************************************************/
//		List<TotalInfo> TotalInfoList = JSON.parseObject(map.get("战绩"), new TypeReference<List<TotalInfo>>() {});
		List<TotalInfo> TotalInfoList = JSON.parseObject(getJsonString(map,"战绩"), new TypeReference<List<TotalInfo>>() {});
		ObservableList<TotalInfo> obList= FXCollections.observableArrayList();
		for(TotalInfo infos : TotalInfoList) {
			obList.add(infos);
		}
		tableTotal.setItems(obList);
		tableTotal.refresh();
		/************************************************************/
		List<WanjiaInfo> WanjiaInfoList = JSON.parseObject(getJsonString(map,"玩家"), new TypeReference<List<WanjiaInfo>>() {});
		ObservableList<WanjiaInfo> WanjiaInfo_OB_List= FXCollections.observableArrayList();
		for(WanjiaInfo infos : WanjiaInfoList) {
			WanjiaInfo_OB_List.add(infos);
		}
		tableWanjia.setItems(WanjiaInfo_OB_List);
		tableWanjia.refresh();
		
		/****************************************************************************/
		List<TeamInfo> TeamInfoList = JSON.parseObject(getJsonString(map,"团队回水"), new TypeReference<List<TeamInfo>>() {});
		ObservableList<TeamInfo> TeamInfo_OB_List= FXCollections.observableArrayList();
		for(TeamInfo infos : TeamInfoList) {
			TeamInfo_OB_List.add(infos);
		}
		tableTeam.setItems(TeamInfo_OB_List);
		tableTeam.getColumns().get(4).setText(getJsonString(map,"团队回水总和"));
		tableTeam.refresh();
		
		/****************************************************************************/
		List<CurrentMoneyInfo> CurrentMoneyInfoList = JSON.parseObject(getJsonString(map,"实时金额"), new TypeReference<List<CurrentMoneyInfo>>() {});
		ObservableList<CurrentMoneyInfo> CurrentMoneyInfo_OB_List= FXCollections.observableArrayList();
		for(CurrentMoneyInfo infos : CurrentMoneyInfoList) {
			CurrentMoneyInfo_OB_List.add(infos);
		}
		tableCurrentMoney.setItems(CurrentMoneyInfo_OB_List);
		String sumOfCurrentMoney = getJsonString(map,"实时金额总和");
		tableCurrentMoney.getColumns().get(2).setText(sumOfCurrentMoney);
		tableCurrentMoney.refresh();
		
		/*************************************************************/
		List<ZijinInfo> ZijinInfoList = JSON.parseObject(getJsonString(map,"资金"), new TypeReference<List<ZijinInfo>>() {});
		ObservableList<ZijinInfo> ZijinInfo_OB_List= FXCollections.observableArrayList();
		for(ZijinInfo infos : ZijinInfoList) {
			ZijinInfo_OB_List.add(infos);
		}
		tableZijin.getColumns().get(1).setText(getJsonString(map,"资金总和"));
		tableZijin.setItems(ZijinInfo_OB_List);
		tableZijin.refresh();
		
		/******************************************************************/
		List<ProfitInfo> ProfitInfoList = JSON.parseObject(getJsonString(map,"利润"), new TypeReference<List<ProfitInfo>>() {});
		ObservableList<ProfitInfo> ProfitInfo_OB_List= FXCollections.observableArrayList();
		for(ProfitInfo infos : ProfitInfoList) {
			ProfitInfo_OB_List.add(infos);
		}
		tableProfit.getColumns().get(1).setText(getJsonString(map,"利润总和"));
		tableProfit.setItems(ProfitInfo_OB_List);
		tableProfit.refresh();
		
		/*************************************************************/
		List<KaixiaoInfo> KaixiaoInfoList = JSON.parseObject(getJsonString(map,"实时开销"), new TypeReference<List<KaixiaoInfo>>() {});
		ObservableList<KaixiaoInfo> KaixiaoInfo_OB_List= FXCollections.observableArrayList();
		for(KaixiaoInfo infos : KaixiaoInfoList) {
			KaixiaoInfo_OB_List.add(infos);
		}
		tableKaixiao.getColumns().get(1).setText(getJsonString(map,"实时开销总和"));
		tableKaixiao.setItems(KaixiaoInfo_OB_List);
		tableKaixiao.refresh();
		
		/***************************************************************/
		List<DangjuInfo> DangjuInfoList = JSON.parseObject(getJsonString(map,"当局"), new TypeReference<List<DangjuInfo>>() {});
		ObservableList<DangjuInfo> DangjuInfo_OB_List= FXCollections.observableArrayList();
		for(DangjuInfo infos : DangjuInfoList) {
			DangjuInfo_OB_List.add(infos);
		}
		tableDangju.getColumns().get(1).setText(getJsonString(map,"当局总和"));
		tableDangju.setItems(DangjuInfo_OB_List);
		tableDangju.refresh();
		
		/****************************************************************/
		List<JiaoshouInfo> JiaoshouInfoList = JSON.parseObject(getJsonString(map,"交收"), new TypeReference<List<JiaoshouInfo>>() {});
		ObservableList<JiaoshouInfo> JiaoshouInfo_OB_List= FXCollections.observableArrayList();
		for(JiaoshouInfo infos : JiaoshouInfoList) {
			JiaoshouInfo_OB_List.add(infos);
		}
		tableJiaoshou.getColumns().get(1).setText(getJsonString(map,"交收总和"));
		tableJiaoshou.setItems(JiaoshouInfo_OB_List);
		tableJiaoshou.refresh();
		
		/******************************************************************/
		List<PingzhangInfo> PingzhangInfoList = JSON.parseObject(getJsonString(map,"平帐"), new TypeReference<List<PingzhangInfo>>() {});
		ObservableList<PingzhangInfo> PingzhangInfo_OB_List= FXCollections.observableArrayList();
		for(PingzhangInfo infos : PingzhangInfoList) {
			PingzhangInfo_OB_List.add(infos);
		}
		tablePingzhang.getColumns().get(1).setText(getJsonString(map,"平帐总和"));
		tablePingzhang.setItems(PingzhangInfo_OB_List);
		tablePingzhang.refresh();
		/******************************************************************/
		LMLabel.setText(getJsonString(map,"联盟对帐"));
	}
	
	public static String getJsonString(Map<String,String> map,String key) {
		if(map != null && map.size() > 0) {
			return map.get(key);
		}else {
			if(key.contains("总和")) {
				return "0";
			}
			return JSON.toJSONString(Collections.EMPTY_LIST);
		}
	}
	


	/**
	 * 支付时修改玩家实时金额  或 添加 实时金额
	 * 
	 * @time 2017年11月12日
	 * @param wj 支付时的玩家信息
	 * @return
	 * @throws Exception
	 */
	public static void updateOrAdd_SSJE_after_Pay(WanjiaInfo wj) throws Exception {
		//1判断玩家是否在该金额表中
		String playerId = wj.getWanjiaId();
		if(StringUtil.isBlank(playerId)) {
			ShowUtil.show("支付按钮时找不到玩家"+wj.getWanjiaName()+"的ID!!请确认!");
		}
		//修改金额表中的玩家金额
		for(CurrentMoneyInfo moneyInfo : tableCurrentMoneyInfo.getItems()) {
			if(playerId.equals(moneyInfo.getWanjiaId())) {
				moneyInfo.setShishiJine(wj.getHeji());//设置新的实时金额的值 
				return;
			}
		}
		//2 到这里，说明玩家不存在于金额表中，需要新增记录
		Player p = DataConstans.membersMap.get(playerId);
		if(p == null) {
			throw new Exception("该玩家不存在于名单中，且匹配不到团ID!玩家名称："+wj.getWanjiaName()+",玩家ID:"+playerId);
		}
		
		CurrentMoneyInfo tempMoneyInfo = new CurrentMoneyInfo(wj.getWanjiaName(),wj.getHeji(),playerId,DataConstans.membersMap.get(playerId).getEdu());
		addInfo(tempMoneyInfo);
	}
	
	/**
	 * 更新实时金额表的映射
	 * {玩家ID=CurrentMoneyInfo}
	 */
	public static void update_Table_CMI_Map() {
		Table_CMI_Map.clear();
		if(tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() != null) {
			for(CurrentMoneyInfo moneyInfo : tableCurrentMoneyInfo.getItems()) {
				if(!StringUtil.isBlank(moneyInfo.getWanjiaId())){
					Table_CMI_Map.put(moneyInfo.getWanjiaId(), moneyInfo);
				}
			}
		}
	}
	
	/**
	 * 根据玩家ID获取实时金额（在少量的情况下可以用）;
	 * 
	 * @time 2017年11月12日
	 * @param playerId
	 * @return
	 */
	public static String get_SSJE_byId(String playerId) {
		update_Table_CMI_Map();
		CurrentMoneyInfo ssjeInfo = Table_CMI_Map.get(playerId);
		if(ssjeInfo == null ) {
			return "0";
		}
		return StringUtil.isBlank(ssjeInfo.getShishiJine()) ? "0" : ssjeInfo.getShishiJine();
	}
	
	/**
	 * 根据玩家ID删除实时金额记录
	 * 
	 * @time 2017年11月12日
	 * @param playerId
	 */
	public static void del_SSJE_byId(String playerId) {
		if(!StringUtil.isBlank(playerId) && tableCurrentMoneyInfo != null
				&& tableCurrentMoneyInfo.getItems().size() > 0) {
			update_Table_CMI_Map();
			CurrentMoneyInfo ssjeInfo = Table_CMI_Map.get(playerId);
			if(ssjeInfo == null ) {
				return;
			}
			CurrentMoneyInfo info = null;
			for(CurrentMoneyInfo moneyInfo : tableCurrentMoneyInfo.getItems()) {
				if(playerId.equals(moneyInfo.getWanjiaId())) {
					info = moneyInfo;
				}
			}
			if(info != null) {
				tableCurrentMoneyInfo.getItems().remove(info);
			}
			
		}
	}
	
	/**
	 * 将前一次的实时金额数据备份到第01表=== 非2017-01-01
	 * @param table
	 * @param userInfoList
	 */
	public static void fillTableCurrentMoneyInfo2(TableView<TeamInfo> tableTeam,TableView<CurrentMoneyInfo> table,
			TableView<ZijinInfo> tableZijin,
			TableView<ProfitInfo> tableProfit,
			TableView<KaixiaoInfo> tableKaixiao,
			Label LMLabel){

		Map<String,String> map = DataConstans.preDataMap;
		/************************************************ 团队回水表  **************/
		tableTeam.setItems(null);
		ObservableList<TeamInfo> obList = FXCollections.observableArrayList();
		List<TeamInfo> teamList = JSON.parseObject(DataConstans.preDataMap.get("团队回水"), new TypeReference<List<TeamInfo>>() {});
		for(TeamInfo info : teamList) {
			obList.add(info);
		}
		tableTeam.setItems(obList);
		/************************************************ 实时金额表  **************/
		//1清空 表数据
		table.setItems(null);
		//2获取InfoList
		ObservableList<CurrentMoneyInfo> obListCurrentMoney = FXCollections.observableArrayList();
		List<CurrentMoneyInfo> cmList = JSON.parseObject(DataConstans.preDataMap.get("实时金额"), new TypeReference<List<CurrentMoneyInfo>>() {});
		for(CurrentMoneyInfo info : cmList) {
			obListCurrentMoney.add(info);
		}
		table.setItems(obListCurrentMoney);
		table.refresh();
		
		//合并ID操作
		flush_SSJE_table();

		/************************************************ 资金表  **************/
		//1清空 表数据
		tableZijin.setItems(null);
		//2获取InfoList
		ObservableList<ZijinInfo> obListZijin = FXCollections.observableArrayList();
		List<ZijinInfo> zijinList = JSON.parseObject(DataConstans.preDataMap.get("资金"), new TypeReference<List<ZijinInfo>>() {});
		for(ZijinInfo info : zijinList) {
			obListZijin.add(info);
		}
		tableZijin.setItems(obListZijin);
		/************************************************ 利润表  **************/
		//1清空 表数据
		tableProfit.setItems(null);
		//2获取InfoList
		ObservableList<ProfitInfo> obListProfit = FXCollections.observableArrayList();
		List<ProfitInfo> profitList = JSON.parseObject(DataConstans.preDataMap.get("利润"), new TypeReference<List<ProfitInfo>>() {});
		for(ProfitInfo info : profitList) {
			obListProfit.add(info);
		}
		tableProfit.setItems(obListProfit);
		/************************************************ 实时开销表  **************/
		tableKaixiao.setItems(null);
		//2获取InfoList
		ObservableList<KaixiaoInfo> obListKaixiao = FXCollections.observableArrayList();
		List<KaixiaoInfo> kaixiaoList = JSON.parseObject(DataConstans.preDataMap.get("实时开销"), new TypeReference<List<KaixiaoInfo>>() {});
		for(KaixiaoInfo info : kaixiaoList) {
			obListKaixiao.add(info);
		}//add 注释掉：新一天的统计中实时开销为空
		tableKaixiao.setItems(obListKaixiao);
		//缓存一一场的实时开销总和
		DataConstans.SumMap.put("上场开销", getSumOfTableKaixiao(tableKaixiao));
		/************************************************ 联盟对帐  **************/
		String lm = DataConstans.preDataMap.get("联盟对帐");
//		LMLabel.setText(LMLabel.getText());//注意这里是{联盟对帐={联盟对帐=3000}}
		LMLabel.setText(lm);//注意这里是{联盟对帐={联盟对帐=3000}}
	}
	
	
	//右表：名称鼠标双击事件：打开对话框增加上码值
	public static void openAddZijinDiag(TableView<ZijinInfo> tableZijin,ZijinInfo info) {
		if(info != null && info.getZijinType() != null) {
			String oddZijin = StringUtil.isBlank(info.getZijinAccount()) ? "0" : info.getZijinAccount();
			String newSM = "";
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("添加");
			dialog.setHeaderText(null);
			dialog.setContentText("续增"+info.getZijinType()+"值(Enter):");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				try {
					Integer.valueOf(result.get().trim());
				}catch(Exception e) {
					ShowUtil.show("非法数据!");
					return;
				}
			    info.setZijinAccount(MoneyService.digit0(MoneyService.getNum(oddZijin)+MoneyService.getNum(result.get().trim())));
			}
			if(tableZijin != null && tableZijin.getItems() != null) {
				for(ZijinInfo zijin : tableZijin.getItems()) {
					if(zijin.getZijinType().equals(info.getZijinType())) {
						zijin.setZijinAccount(info.getZijinAccount());
						break;
					}
				}
			}
		}
	}
	
	
	/**
	 * 修改玩家名称
	 * 
	 * @time 2017年10月29日
	 * @param playerId
	 * @param oldeName
	 * @param newName
	 */
	public static void updatePlayerName(String playerId,String oldeName,String newName) {
		ObservableList<CurrentMoneyInfo> obList = tableCurrentMoneyInfo.getItems();
		boolean isExist = false;
		if(obList != null && obList.size() > 0) {
			for(CurrentMoneyInfo info : obList) {
				//先通过ID进行匹配
				if(info != null && info.getMingzi() != null && info.getWanjiaId() != null
						&& info.getWanjiaId().equals(playerId)
						) {
					info.setMingzi(newName);
					isExist = true;
					break;
				}
				//再通过名称进行匹配
				if(info != null && info.getMingzi() != null 
						&& info.getMingzi().trim().equals(oldeName)
						) {
					info.setMingzi(newName);
					isExist = true;
					break;
				}
			}
			if(isExist) {
				tableCurrentMoneyInfo.refresh();
			}
		}
		
	}
	
	/*************************   导出人员表Excel   ************************************/  
	
	public static void exportMemberExcel() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String title = "名单登记表-德扑圈" + sdf.format(new Date());
		log.info("导出人员表Excel:"+title);
		String[] rowsName = new String[] { "玩家ID", "股东", "团队", "游戏名字", "额度" };
		List<Object[]> dataList = new ArrayList<Object[]>();
		Object[] objs = null;
		Map<String, Player> memberMap = DataConstans.membersMap;
		String pId;
		Player player;
		for(Map.Entry<String, Player> entry : memberMap.entrySet()) {
			 pId = entry.getKey();
			 player = entry.getValue();
			 objs = new Object[rowsName.length];
			 objs[0] = pId;
			 objs[1] = player.getGudong();
			 objs[2] = player.getTeamName();
			 objs[3] = player.getPlayerName();
			 objs[4] = player.getEdu();
			 dataList.add(objs);
		}
		String out = "D:/" + title;
		ExportMembersExcel ex = new ExportMembersExcel(title, rowsName, dataList, out);
		try {
			ex.export();
			log.info("导出人员表Excel成功");
		} catch (Exception e) {
			ErrorUtil.err("导出人员表Excel失败",e);
		}
	}
	
	
	/*************************   导出团队回水表Excel   ************************************/  
	
	public static void exportTeamhsExcel() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String title = "回水表" + sdf.format(new Date());
		log.info("导出团队回水表Excel:"+title);
	    String[] rowsName = new String[]{"团队ID","团队名字","比例","保险比例","股东","战绩是否代管理","备注","回水比例","回保比例","服务费判定"};
		List<Object[]> dataList = new ArrayList<Object[]>();
		Object[] objs = null;
		Map<String, Huishui> huishuiMap = DataConstans.huishuiMap;
		String teamId;
		Huishui hs;
		for(Map.Entry<String, Huishui> entry : huishuiMap.entrySet()) {
			teamId = entry.getKey();
			hs = entry.getValue();
			objs = new Object[rowsName.length];
			objs[0] = teamId;
			objs[1] = hs.getTeamName();
			objs[2] = NumUtil.getPercentStr(NumUtil.getNum(NumUtil.digit4(hs.getHuishuiRate())));
			objs[3] = NumUtil.getPercentStr(NumUtil.getNum(NumUtil.digit4(hs.getInsuranceRate())));
			objs[4] = hs.getGudong();
			objs[5] = hs.getZjManaged();
			objs[6] = hs.getBeizhu();//"回水比例","回保比例","服务费判定","服务费判定"
			objs[7] = hs.getProxyHSRate();
			objs[8] = hs.getProxyHBRate();
			objs[9] = hs.getProxyFWF();
			dataList.add(objs);
		}
		String out = "D:/" + title;
		ExportTeamhsExcel ex = new ExportTeamhsExcel(title, rowsName, dataList, out);
		try {
			ex.export();
			log.debug("导出回水表Excel成功");
		} catch (Exception e) {
			ErrorUtil.err("导出回水表Excel失败",e);
		}
	}
	
	/*************************   导出实时金额Excel   ************************************/  
	
	public static void exportSSJEAction(TableView<CurrentMoneyInfo> tableCurrentMoneyInfo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String title = "实时金额表" + sdf.format(new Date());
		log.info("导出实时金额表Excel:"+title);
		String[] rowsName = new String[] { "总和","玩家ID", "名字", "实时金额", "额度" };
		List<Object[]> dataList = new ArrayList<Object[]>();
		Object[] objs = null;
		ObservableList<CurrentMoneyInfo> currentInfoList = tableCurrentMoneyInfo.getItems();
		for(CurrentMoneyInfo info : currentInfoList) {
			 objs = new Object[rowsName.length];
			 objs[0] = info.getCmSuperIdSum();
			 objs[1] = info.getWanjiaId();
			 objs[2] = info.getMingzi();
			 objs[3] = info.getShishiJine();
			 objs[4] = info.getCmiEdu();
			 //objs[4] = info.getWanjiaId();
			 dataList.add(objs);
		}
		String out = "D:/" + title;
		ExportMembersExcel ex = new ExportMembersExcel(title, rowsName, dataList, out);
		try {
			ex.export();
			log.debug("导出实时金额表Excel成功");
		} catch (Exception e) {
			ErrorUtil.err("导出实时金额表Excel失败", e);
		}
	}
	
	/*************************   导出合并ID列表   ************************************/  
	public static void exportCombineIDAction() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String title = "数据库中的合并ID表" + sdf.format(new Date());
		String[] rowsName = new String[] { "父ID","子ID列表(请用#隔开)"};
		List<Object[]> dataList = new ArrayList<Object[]>();
		Object[] objs = null;
		Map<String,Set<String>> combineIdMap = DBUtil.getCombineData();
		if(combineIdMap == null || combineIdMap.size() == 0) { 
			ShowUtil.show("数据库中无合并ID数据");
			return;
		}
		StringBuffer sb=new StringBuffer();
		for(Map.Entry<String, Set<String>> entry : combineIdMap.entrySet()) {
			String parentId = entry.getKey();
			Set<String> subIdSet = entry.getValue();
			
			sb.delete(0, sb.length());
	        for(String subId : subIdSet){
	        	sb.append(subId).append("#");
	        }
	        String subIdString = sb.substring(0, sb.lastIndexOf("#"));
			objs = new Object[rowsName.length];
			if(!StringUtil.isBlank(parentId) && !StringUtil.isBlank(subIdString)) {
			objs[0] = parentId;
			objs[1] = subIdString;
			dataList.add(objs);
			}
		}
		String out = "D:/" + title;
		ExportMembersExcel ex = new ExportMembersExcel(title, rowsName, dataList, out);
		try {
			ex.export();
			log.info("导出合并ID表Excel成功");
		} catch (Exception e) {
			ErrorUtil.err("导出合并ID表Excel失败", e);
		}
	}
	
	//***************************************** 刷新实时金额表新方案 ************************************
	//			1  更新实时金额表的具体记录
	//					A：操作类型（增，删，改）
	//			2  更新实时金额表的所有记录
	//					A：合并ID总和及上下空行
	//					B：滚动到那一条记录（无则不选）
	//***************************************** 刷新实时金额表新方案 ************************************
	
	/**
	 * 增加 and 修改实时金额
	 * 
	 * @time 2017年10月31日
	 * @param cmiInfo
	 */
	public static void saveOrUpdate(CurrentMoneyInfo cmiInfo) {
		if(cmiInfo == null) 
			return;
		String playerId = cmiInfo.getWanjiaId();
		CurrentMoneyInfo info = getInfoById(playerId);
		if(info == null) {
			addInfo(cmiInfo);//添加一条实时金额信息
		}else {
			info = cmiInfo;//修改
		}
		refreshRecord();
	}
	
	/**
	 * 根据玩家ID获取实时金额表中的人员信息
	 * 
	 * @time 2017年10月31日
	 * @param playerId
	 * @return
	 */
	public static CurrentMoneyInfo getInfoById(String playerId) {
		if(StringUtil.isBlank(playerId))
			return null;
		if(tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() != null) {
			ObservableList<CurrentMoneyInfo> obList = tableCurrentMoneyInfo.getItems();
			for(CurrentMoneyInfo entity : obList) {
				if(entity != null && !StringUtil.isBlank(entity.getWanjiaId()) 
						&& playerId.equals(entity.getWanjiaId())) {
					return entity;
				}
			}
		}
		return null;
	}
	/**
	 * 根据名称获取金额表中的信息
	 * 
	 * @time 2017年11月9日
	 * @param playerId
	 * @return
	 */
	public static CurrentMoneyInfo getInfoByName(String name) {
		if(StringUtil.isBlank(name))
			return null;
		if(tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() != null) {
			ObservableList<CurrentMoneyInfo> obList = tableCurrentMoneyInfo.getItems();
			for(CurrentMoneyInfo entity : obList) {
				if(entity != null && !StringUtil.isBlank(entity.getMingzi()) 
						&& name.toUpperCase().equals(entity.getMingzi().toUpperCase())) {
					return entity;
				}
			}
		}
		return null;
	}
	
	/**
	 * 是否存于实时金额表（根据ID)
	 */
	public static boolean isExistIn_SSJE_Table_byId(String playerId) {
		return getInfoById(playerId) == null ? false : true;
	}
	
	/**
	 * 是否存于实时金额表（根据名称)
	 */
	public static boolean isExistIn_SSJE_Table_byName(String name) {
		return getInfoByName(name) == null ? false : true;
	}
	
	/**
	 * 添加一条实时金额信息
	 * 
	 * @time 2017年10月31日
	 * @param info
	 */
	public static void addInfo(CurrentMoneyInfo info) {
		if(tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() != null) {
			ObservableList<CurrentMoneyInfo> obList = tableCurrentMoneyInfo.getItems();
			obList.add(info);
		}
	}
	
	/**
	 * 刷新金额表
	 */
	public static void refreshRecord() {
		if(tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() != null) {
			tableCurrentMoneyInfo.refresh();
		}
	}
	
	/**
	 * 判断金额表是否为空
	 */
	private static boolean isTableNotNull() {
		return tableCurrentMoneyInfo != null && tableCurrentMoneyInfo.getItems() !=null;
	}

	/**
	 * 刷新最新的实时金额（最重要！！！！）
	 * 即添加进合并ID
	 * 
	 * @time 2017年11月1日
	 * @throws Exception
	 */
	public static void flush_SSJE_table(){
		LinkedList<CurrentMoneyInfo> srcList = new LinkedList<>();
		if(!isTableNotNull()) {
			return;
		}else {
			for(CurrentMoneyInfo info : tableCurrentMoneyInfo.getItems())
				srcList.add(info);
		}
		
		//组装数据为空
		//对组装的数据列表进行处理，空行直接删除，父ID删除，子ID删除后缓存
		ListIterator<CurrentMoneyInfo> it = srcList.listIterator();
		Map<String,CurrentMoneyInfo> superIdInfoMap = new HashMap<>();//存放父ID那条记录的信息
    	Map<String,List<CurrentMoneyInfo>> superIdSubListMap = new HashMap<>();//存放父ID下的所有子记录信息
    	String playerId = "";
    	while(it.hasNext()) {  
    		CurrentMoneyInfo item = it.next();  
    	    //删除空行
    	    if(StringUtil.isBlank(item.getWanjiaId()) && StringUtil.isBlank(item.getMingzi()) && 
    	    		StringUtil.isBlank(item.getShishiJine())) {
    	    	it.remove();//没有玩家ID的就是空行
    	    	continue;
    	    }
    	    //删除父ID
    	    playerId  = item.getWanjiaId();
    	    if(DataConstans.Combine_Super_Id_Map.get(playerId) != null) {
    	    	superIdInfoMap.put(playerId, item);
    	    	it.remove();
    	    	continue;
    	    }
    	    //删除子ID
    	    if(DataConstans.Combine_Sub_Id_Map.get(playerId) != null) {
    	    	//是子ID节点
    	    	String parentID = DataConstans.Combine_Sub_Id_Map.get(playerId);
    	    	List<CurrentMoneyInfo> childList = superIdSubListMap.get(parentID);
    	    	if(childList == null) {
    	    		childList = new ArrayList<>();
    	    	}
    	    	childList.add(item);
    	    	superIdSubListMap.put(parentID, childList);
    	    	it.remove();
    	    	continue;
    	    }
    	} 
    	
    	//添加子ID不在实时金额表中的父ID记录（单条父记录）
    	superIdInfoMap.forEach((superId,info) -> {
    		if(!superIdSubListMap.containsKey(superId)) {
    			info.setCmSuperIdSum("");
    			srcList.add(info);//添加父记录
    		}
    	});
    	
    	//添加父ID不在实时金额表中的子ID记录
        Iterator<Map.Entry<String, List<CurrentMoneyInfo>>> ite = superIdSubListMap.entrySet().iterator();  
        while(ite.hasNext()){  
            Map.Entry<String, List<CurrentMoneyInfo>> entry = ite.next();  
            String superId = entry.getKey();
    		CurrentMoneyInfo superInfo = superIdInfoMap.get(superId);
    		if(superInfo == null) {
    			for(CurrentMoneyInfo info : entry.getValue()) {
    				srcList.add(info);//添加子记录
    			}
    			ite.remove();//删除
    		}
        } 
    	
    	//计算总和并填充父子节点和空行
    	String superId = "";
    	srcList.add(new CurrentMoneyInfo());//空开一行
    	for(Map.Entry<String, List<CurrentMoneyInfo>> entry : superIdSubListMap.entrySet()) {
    		superId = entry.getKey();
    		CurrentMoneyInfo superInfo = superIdInfoMap.get(superId);

    		//计算父节点总和
    		List<CurrentMoneyInfo> subInfoList = entry.getValue();
    		Double superIdSum = 0d;//合并ID节点总和
    		for(CurrentMoneyInfo info : subInfoList) {
    			superIdSum += NumUtil.getNum(info.getShishiJine());
    		}
    		superInfo.setCmSuperIdSum(NumUtil.digit0(superIdSum+NumUtil.getNum(superInfo.getShishiJine())));
    		
    		//添加空行和子节点
    		srcList.add(superInfo);//先添加父节点
    		for(CurrentMoneyInfo info : subInfoList) {//再添加子节点
    			srcList.add(info);
    		}
    		srcList.add(new CurrentMoneyInfo());//空开一行
    	}
    	//更新
    	ObservableList<CurrentMoneyInfo> obList = FXCollections.observableArrayList();
    	for(CurrentMoneyInfo cmi : srcList) {
    		obList.add(cmi);
    	}
    	tableCurrentMoneyInfo.setItems(obList);
    	tableCurrentMoneyInfo.refresh();
	}
	
	/**
	 * 滚动到指定行（根据玩家ID)
	 */
	public static boolean scrolById(String playerId) {
		boolean isExist = false;//检查该玩家是否存在
		if(isTableNotNull() && playerId != null) {
			playerId = playerId.trim();
			ObservableList<CurrentMoneyInfo> list = tableCurrentMoneyInfo.getItems();
			String tempId = "";
			for(CurrentMoneyInfo moneyInfo : list) {
				tempId = moneyInfo.getWanjiaId();
				if(StringUtil.isBlank(tempId)) {
					continue;
				}else {
					tempId = tempId.trim();
				}
				if(playerId.equals(moneyInfo.getShishiJine())) {
					int index = list.indexOf(moneyInfo);
					tableCurrentMoneyInfo.scrollTo(index);
					//table.getSelectionModel().focus(index);
					tableCurrentMoneyInfo.getSelectionModel().select(index);
					isExist = true;
					break;
				}
			}
		}
		return isExist;
	}
	
	/**
	 * 滚动到指定行（根据玩家名称)
	 */
	public static boolean scrolByName(String playerName) {
		boolean isExist = false;//检查该玩家是否存在
		if(isTableNotNull() && playerName != null) {
			playerName = playerName.trim().toUpperCase();	
			ObservableList<CurrentMoneyInfo> list = tableCurrentMoneyInfo.getItems();
			String tempName = "";
			for(CurrentMoneyInfo moneyInfo : list) {
				tempName = moneyInfo.getMingzi();
				if(StringUtil.isBlank(tempName)) {
					continue;
				}else {
					tempName = tempName.trim().toUpperCase();
				}
				if(playerName.equals(tempName)) {
					int index = list.indexOf(moneyInfo);
					tableCurrentMoneyInfo.scrollTo(index);
					//table.getSelectionModel().focus(index);
					tableCurrentMoneyInfo.getSelectionModel().select(index);
					isExist = true;
					break;
				}
			}
		}
		return isExist;
	}
	
	

	
	
}
