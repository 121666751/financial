package com.kendy.service;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.kendy.entity.Huishui;
import com.kendy.entity.ProxySumInfo;
import com.kendy.entity.ProxyTeamInfo;
import com.kendy.entity.TeamHuishuiInfo;
import com.kendy.excel.ExportExcel;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * 代理查询服务类
 * 
 * @author kendy
 *
 */
public class TeamProxyService {

	private static Logger log = Logger.getLogger(TeamProxyService.class);

	public static DecimalFormat df = new DecimalFormat("#.00");
	
	public static  TableView<ProxyTeamInfo> tableProxyTeam ;
	public static  HBox proxySumHBox;//每列上的总和
	public static  ComboBox<String> teamIDCombox;//团队ID下拉框
	public static  CheckBox isZjManage;//团对应的战绩是否被管理
	public static  Label proxyDateLabel;
	
	public static  TableView<ProxySumInfo> tableProxySum;
	public static  TextField proxyHSRate;//回水比例
	public static  TextField proxyHBRate;//回保比例
	public static  TextField proxyFWF;//服务费大于多少有效
	
	/**
	 * 初始化代理服务类
	 */
	public  static void initTeamProxy(
			TableView<ProxyTeamInfo> tableProxyTeam0,
			 HBox proxySumHBox0,//每列上的总和
			 ComboBox<String> teamIDCombox0,//团队ID下拉框
			 CheckBox isZjManage0,//团对应的战绩是否被管理
			 Label proxyDateLabel0,
			 TableView<ProxySumInfo> tableProxySum0,
			 TextField proxyHSRate0,//回水比例
			 TextField proxyHBRate0,//回保比例
			 TextField proxyFWF0//服务费大于多少有效
			) {
		tableProxyTeam = tableProxyTeam0;
		proxySumHBox = proxySumHBox0;
		teamIDCombox = teamIDCombox0;
		isZjManage = isZjManage0;
		proxyDateLabel = proxyDateLabel0;
		tableProxySum = tableProxySum0;
		proxyHSRate = proxyHSRate0;
		proxyHBRate = proxyHBRate0;
		proxyFWF = proxyFWF0; 
	}
	
	/**
	 * 初始化
	 * 
	 * @time 2017年10月28日
	 * @param teamIDCombox
	 */
	public static void initTeamSelectAndZjManage(ComboBox<String> teamIDCombox) {
		ObservableList<String> options = FXCollections.observableArrayList();
		if(DataConstans.huishuiMap != null ) {
		DataConstans.huishuiMap.forEach((teamId,huishuiInfo) -> {
			options.add(teamId);
		});
		teamIDCombox.setItems(options);
		//teamIDCombox.getSelectionModel().select(0); // [0, options.size())
		}
	}
	
	/**
	 * 添加新团队后自动更新到代理查询的团队下拉框中
	 * 
	 * @time 2017年11月11日
	 * @param teamId
	 */
	public static void addNewTeamId(String teamId) {
		if(teamIDCombox != null && teamIDCombox.getItems() != null) {
			teamIDCombox.getItems().add(teamId);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void initTeamSelectAction(ComboBox<String> teamIDCombox,CheckBox isZjManage,
			TableView<ProxyTeamInfo> tableProxyTeam,HBox proxySumHBox) {
		teamIDCombox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            	refresh_TableTeamProxy_TableProxySum(newValue);
            }
        });
		
		isZjManage.selectedProperty().addListener(new ChangeListener<Boolean>() {
	        public void changed(ObservableValue<? extends Boolean> ov,
	            Boolean old_val, Boolean new_val) {
	        	String teamId = teamIDCombox.getSelectionModel().getSelectedItem();
	        	if(!StringUtil.isBlank(teamId)) {
	        		Huishui hs = DataConstans.huishuiMap.get(teamId);
	        		if(hs != null) {
	        			hs.setZjManaged(new_val?"是":"否");
	        			DataConstans.huishuiMap.put(teamId,hs);
	        		}
	        	}
	        }
	    });

	}
	
	/**
	 * 刷新两个表，共用代码(选择团下拉框和点击刷新按钮时共用的代码)
	 * 
	 * @param newValue teamID
	 */
	public static void refresh_TableTeamProxy_TableProxySum(Object newValue) {
		Huishui hs = DataConstans.huishuiMap.get(newValue);
        if(hs != null) {
            if("否".equals(hs.getZjManaged())) {
            	isZjManage.setSelected(false);
            }else {
            	isZjManage.setSelected(true);
            }
        }
        //加载数据{teamId={}}
        double sumYSZJ = 0d;
        double sumZJ = 0d;
        double sumHS = 0d;
        double sumHB = 0d;
        double sumBX = 0d;
        int sumRC = 0;
        //Map<String,List<TeamHuishuiInfo>> teamMap = DataConstans.Total_Team_Huishui_Map;//锁定就保留信息，不减
        Map<String,List<TeamHuishuiInfo>> teamMap = getTotalTeamHuishuiMap();
        if(teamMap != null && teamMap.size() == 0) {
        	System.out.println("----------------");//这个有问题，后期再看
        }
        List<TeamHuishuiInfo> teamList = teamMap.get(newValue.toString().toUpperCase());
        ObservableList<ProxyTeamInfo> obList = FXCollections.observableArrayList();
        if(teamList != null) {
            for(TeamHuishuiInfo info : teamList) {
            	obList.add(new ProxyTeamInfo(
            			info.getTuan(),
            			info.getWanjiaId(),
            			info.getWanjia(),
            			info.getZj(),//yszj
            			info.getShishou(),
            			MoneyService.getNum(info.getChuHuishui())*(-1)+"",//出回水是否等于回水
            			info.getHuibao(),//保险是否等于回保
            			info.getTableId(),
            			info.getBaoxian()//保险
            			));
            	sumYSZJ += MoneyService.getNum(info.getZj());
            	sumZJ += MoneyService.getNum(info.getShishou());
            	sumBX += MoneyService.getNum(info.getBaoxian());
            	sumHS += (MoneyService.getNum(info.getChuHuishui()))*(-1);
            	sumHB += MoneyService.getNum(info.getHuibao());
            	sumRC += 1;
            }
        }
        tableProxyTeam.setItems(obList);
        tableProxyTeam.refresh();
        
        ObservableList<Node> sumHBox = proxySumHBox.getChildren();
        for( Node node : sumHBox) {
        	Label label = (Label)node;
        	String labelId = label.getId();
        	switch(labelId) {
        	case "sumYSZJ": label.setText(MoneyService.digit0(sumYSZJ));break;
        	case "sumZJ":	label.setText(MoneyService.digit0(sumZJ));break;
        	case "sumBX":	label.setText(MoneyService.digit0(sumBX));break;
        	case "sumHS":	label.setText(MoneyService.digit1(sumHS+""));break;
        	case "sumHB":	label.setText(sumHB+"");break;
        	case "sumRC":	label.setText(sumRC+"");break;
        	}
        }
        //add by kendy 添加总回保比例，总回水比例，服务费和合计
        proxyHSRate.setText(hs.getProxyHSRate());
        proxyHBRate.setText(hs.getProxyHBRate());
        proxyFWF.setText(hs.getProxyFWF());
        
        double HSRate = getNumByPercent(hs.getProxyHSRate());
        double HBRate = getNumByPercent(hs.getProxyHBRate());
        double FWFValid = NumUtil.getNum(hs.getProxyFWF());//服务费有效值
        //计算服务费
        double proxyFWFVal = calculateProxSumFWF(sumHS,HSRate,sumHB,HBRate,FWFValid);
        //计算合计
        double proxyHeji = sumZJ + sumHS + sumHB - proxyFWFVal;
        //初始化合计表
        tableProxySum.setItems(null);
        ObservableList<ProxySumInfo> ob_Heji_List = FXCollections.observableArrayList();
        ob_Heji_List.addAll(
        		new ProxySumInfo("总战绩",NumUtil.digit0(sumZJ)),
        		new ProxySumInfo("总回水",NumUtil.digit1(sumHS+"")),
        		new ProxySumInfo("总回保",NumUtil.digit1(sumHB+"")),
        		new ProxySumInfo("服务费",NumUtil.digit1(proxyFWFVal+"")),
        		new ProxySumInfo("总人次",sumRC+"")
        		);
        tableProxySum.setItems(ob_Heji_List);
        tableProxySum.getColumns().get(1).setText(NumUtil.digit1(proxyHeji+""));
        tableProxySum.refresh();
	}
	
	
	/**
	 * 获取最新锁定数据（指导入战绩）+可能已经导入的战绩
	 */
	public static Map<String,List<TeamHuishuiInfo>> getTotalTeamHuishuiMap(){
		Map<String,List<TeamHuishuiInfo>> teamMap = new HashMap<>();
		//复制锁定数据(putAll方法不影响已锁定的数据）
		//teamMap.putAll(DataConstans.Total_Team_Huishui_Map);//锁定就保留信息，不减(此方法不是深层复制，会影响DataConstans.Total_Team_Huishui_Map)
		//深层复制（代替以上代码）
		teamMap = copy_Total_Team_Huishui_Map();
		
		//加上最新导入的当局信息（可能没有）
		if(DataConstans.Dangju_Team_Huishui_List.size() > 0) {
			for(TeamHuishuiInfo info : DataConstans.Dangju_Team_Huishui_List) {
				String teamId = info.getTuan();
				System.out.println(teamId);
				List<TeamHuishuiInfo> teamHuishuiList = teamMap.get(teamId);
				if( teamHuishuiList == null ) {
					teamHuishuiList = new ArrayList<>();
				}
				//add 去重
				boolean isExist = false;
				for(TeamHuishuiInfo teamInfo : teamHuishuiList) {
					if(teamInfo.getTableId().equals(info.getTableId())
							&& teamInfo.getWanjia().equals(info.getWanjia())) {
						isExist = true;
					}
				}
				if(!isExist) {
					teamHuishuiList.add(info);
				}
				teamMap.put(teamId,teamHuishuiList);
			}
		}
		return teamMap;
	}
	
	/**
	 * add 深层克隆Total_Team_Huishui_Map对象
	 * 
	 * @time 2017年10月29日
	 * @return
	 */
	private static Map<String,List<TeamHuishuiInfo>> copy_Total_Team_Huishui_Map() {
		Map<String,List<TeamHuishuiInfo>> teamMap = new HashMap<>();
		//复制锁定数据(不影响已锁定的数据）
		//深层复制（代替以上代码）
		for(Map.Entry<String, List<TeamHuishuiInfo>> entry : DataConstans.Total_Team_Huishui_Map.entrySet()) {
			String teamId = entry.getKey();
			List<TeamHuishuiInfo> list = entry.getValue();
			List<TeamHuishuiInfo> tempList = new ArrayList<>();
			for(TeamHuishuiInfo tInfo : list) {
				TeamHuishuiInfo tempHs = new TeamHuishuiInfo();
				tempHs.setTuan(tInfo.getTuan());
				tempHs.setWanjiaId(tInfo.getWanjiaId());
				tempHs.setWanjia(tInfo.getWanjia());
				tempHs.setShishou(tInfo.getShishou());
				tempHs.setBaoxian(tInfo.getBaoxian());
				tempHs.setChuHuishui(tInfo.getChuHuishui());
				tempHs.setTableId(tInfo.getTableId());
				tempHs.setZj(tInfo.getZj());
				tempHs.setHuibao(tInfo.getHuibao());
				tempHs.setShouHuishui(tInfo.getShouHuishui());
				tempHs.setUpdateTime(tInfo.getUpdateTime());
				tempList.add(tempHs);
			}
			teamMap.put(teamId, tempList);
		}
		return teamMap;
	}
	/**
	 * 刷新按钮
	 */
	public static void proxyRefresh() {
		//先同步缓存
		String HSRateStr = proxyHSRate.getText();
		String HBRateStr = proxyHBRate.getText();
		String FWFStr = proxyFWF.getText();
		if(StringUtil.isBlank(HSRateStr) || StringUtil.isBlank(HBRateStr) || StringUtil.isBlank(FWFStr)) {
			ShowUtil.show("比例或服务费不能为空!");
			return;
		}else if(!HSRateStr.trim().contains("%") || !HBRateStr.trim().contains("%")){
			ShowUtil.show("比例的单位是%,请确认!");
			return;
		}else {
			//1先同步缓存
			String teamId = teamIDCombox.getSelectionModel().getSelectedItem();
			if(teamId != null) {
				teamId = teamId.trim().toUpperCase();
			}else {
				ShowUtil.show("请选择团队ID!");
				return;
			}
			Huishui hs = DataConstans.huishuiMap.get(teamId);
	        if(hs != null) {
	            hs.setProxyHSRate(HSRateStr);
	            hs.setProxyHBRate(HBRateStr);
	            hs.setProxyFWF(FWFStr);
	            DataConstans.huishuiMap.put(teamId,hs);//更新到缓存中
	        }else {
	        	ShowUtil.show("刷新失败，没有"+teamId+"的相关信息!");
	        	return;
	        }
			
			//2刷新数据
	        refresh_TableTeamProxy_TableProxySum(teamId);
	        ShowUtil.show("刷新成功",1);
		}
	}
	

	/**
	 * 计算服务费
	 * 公式 ：服务费 = 总回保 * 回保比例 + 总回水 * 回水比例
	 */
	public static Double calculateProxSumFWF(double sumOfHS,double HSRate,double sumOfHB,double HBRate,double FWFValid) {
		if(HSRate == 0d) {
			if(sumOfHB > FWFValid) {
				return sumOfHB * HBRate;
			}else {
				return 0d;
			}
		}
		if(HBRate == 0d) {
			if(sumOfHS > FWFValid) {
				return sumOfHS * HSRate;
			}else {
				return 0d;
			}
		}
		if( HSRate > 0d && HBRate > 0d) {
			if(  (sumOfHS +  sumOfHB) > FWFValid) {
				return sumOfHS * HSRate + sumOfHB * HBRate;
			}else {
				return 0d;
			}
		}
		
		return 0d;
	}
	/**
	 * 计算服务费
	 * 公式 ：服务费 = 总回保 * 回保比例 + 总回水 * 回水比例
	 */
	public static Double calculateProxSumFWF(String sumOfHS,String HSRate,String sumOfHB,String HBRate) {
		Double _sumOfHS = NumUtil.getNum(sumOfHS);
		Double _sumOfHB = NumUtil.getNum(sumOfHB);
		Double _HSRate  = getNumByPercent(HSRate);
		Double _HBRate  = getNumByPercent(HBRate);
		Double res = _sumOfHS * _HSRate + _HSRate * _HBRate;
		return res;
	}
	/**
	 * 把百分比转成小数
	 * @param percentStr
	 * @return
	 */
	public static Double getNumByPercent(String percentStr) {
		if(!StringUtil.isBlank(percentStr) && percentStr.contains("%")) {
			 return new Double(percentStr.substring(0, percentStr.indexOf("%"))) / 100;
		}
		return 0d;
	}
	
	/**
	 * 根据小数转成百分比
	 * @param number
	 * @return
	 */
	public static String getPercentStr(Double number) {
    	NumberFormat num = NumberFormat.getPercentInstance(); 
    	num.setMaximumIntegerDigits(3); 
    	num.setMaximumFractionDigits(2); 
    	//double csdn = 0.31; 
    	String percentString = num.format(number);
    	return percentString;
	}
	
	
	/*************************   导出Excel   ************************************/  
	public static void exportExcel() {
		String teamId = teamIDCombox.getSelectionModel().getSelectedItem();
		boolean isManage = isZjManage.isSelected();
		String time = DataConstans.Date_Str;
		if(StringUtil.isBlank(teamId)) {
			ShowUtil.show("导出失败! 请先选择团队ID!!");
			return;
		}
		if(StringUtil.isBlank(time)) {
			ShowUtil.show("导出失败! 您今天还没导入01场次的战绩，无法确认时间!!");
			return;
		}
		List<ProxyTeamInfo> list = new ArrayList<>();
		ObservableList<ProxyTeamInfo> obList = tableProxyTeam.getItems();
		if(obList != null && obList.size() > 0) {
			for(ProxyTeamInfo info : obList) {
				list.add(info);
			}
		}else {
			ShowUtil.show("没有需要导出的数据!!");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  	    String title = teamId + "-"+sdf.format(new Date());
  	    log.info(title);
  	  
	      String[] rowsName = new String[]{"玩家ID","玩家名称","原始战绩","战绩","保险","回水","回保","场次"};
	      List<Object[]>  dataList = new ArrayList<Object[]>();
	      Object[] objs = null;
	      for(ProxyTeamInfo info : list) {
	          objs = new Object[rowsName.length];
	          objs[0] = info.getProxyPlayerId();
	          objs[1] = info.getProxyPlayerName();
	          objs[2] = info.getProxyYSZJ();
	          objs[3] = info.getProxyZJ();
	          objs[4] = info.getProxyBaoxian();
	          objs[5] = info.getProxyHuishui();
	          objs[6] = info.getProxyHuiBao();
	          objs[7] = info.getProxyTableId();
	          dataList.add(objs);
	      }
	      
	      String[] rowsName2 = new String[]{"合计","0"};
	      List<Object[]> sumList = new ArrayList<>();
	      Object[] sumObjs = null;
		  ObservableList<ProxySumInfo> ob_List = tableProxySum.getItems();
			if(ob_List != null && ob_List.size() > 0) {
				for(ProxySumInfo info : ob_List) {
					sumObjs= new Object[rowsName2.length];
					sumObjs[0] = info.getProxySumType();
					sumObjs[1] = info.getProxySum();
					sumList.add(sumObjs);
				}
				String sum = tableProxySum.getColumns().get(1).getText();
				rowsName2[1] = sum;
			}
	      
	      String out = "D:/"+title+System.currentTimeMillis();
	      ExportExcel ex = new ExportExcel(teamId,time,isManage,title,rowsName, dataList,out,rowsName2,sumList);
	      try {
			ex.export();
			log.info("代理查询导出成功");
		} catch (Exception e) {
			ErrorUtil.err("代理查询导出失败",e);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
