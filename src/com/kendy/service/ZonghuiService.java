package com.kendy.service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.DangjuInfo;
import com.kendy.entity.DangtianHuizongInfo;
import com.kendy.entity.KaixiaoInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.ZonghuiKaixiaoInfo;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;

import application.DataConstans;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 * 总汇信息服务类
 * @author 林泽涛
 *
 */
public class ZonghuiService {
	
	private static Logger log = Logger.getLogger(ZonghuiService.class);

	public static DecimalFormat df = new DecimalFormat("#.00");
	
	/**
	 * 刷新汇总信息表
	 */
	public static void refreHuizongTable(TableView<ZonghuiInfo> tableZonghui,TableView<DangtianHuizongInfo> tableDangtianHuizong,
			TableView<ZonghuiKaixiaoInfo> tableZonghuiKaixiao) {
		Map<String,Map<String,String>> lockedMap = DataConstans.All_Locked_Data_Map;
		ZonghuiInfo zonghuiInfo = new ZonghuiInfo();
		ObservableList<ZonghuiInfo> obList = FXCollections.observableArrayList();
		ObservableList<DangtianHuizongInfo> obSumList = FXCollections.observableArrayList();
		String fuwufei,baoxiao,teamHuishui,teamHuibao;
		double sumOfFuwufei= 0d,sumOfBaoxian= 0d,sumOfTeamHuishui= 0d,sumOfTeamHuibao = 0d,sumOfTotal=0d;
		if(lockedMap.size() > 0) {
			//主表复值
			for(Map.Entry<String, Map<String,String>> entry : lockedMap.entrySet() ) {
				String keyOfJu = entry.getKey();//页数第几局
				String tableId = DataConstans.Index_Table_Id_Map.get(keyOfJu)+"";
				Map<String,String> valueMap = entry.getValue();
				String jsonString = valueMap.get("当局");
				//[{"money":"37.1","type":"服务费"},{"money":"295.4","type":"保险"},{"money":"-18.0","type":"团队回水"},{"money":"-0.0","type":"团队回保"}]
				List<DangjuInfo> dangjuList = JSON.parseObject(jsonString, new TypeReference<List<DangjuInfo>>() {});
				fuwufei = dangjuList.get(0).getMoney();//服务费
				baoxiao = dangjuList.get(1).getMoney();//保险
				teamHuishui = dangjuList.get(2).getMoney();//团队回水
				teamHuibao = dangjuList.get(3).getMoney();//团队回保
				//累计求和
				sumOfFuwufei += MoneyService.getNum(fuwufei);
				sumOfBaoxian += MoneyService.getNum(baoxiao);
				sumOfTeamHuishui += MoneyService.getNum(teamHuishui);
				sumOfTeamHuibao += MoneyService.getNum(teamHuibao);
				//创建实体1
				zonghuiInfo = new ZonghuiInfo(tableId,fuwufei,baoxiao,teamHuishui,teamHuibao);
				obList.add(zonghuiInfo);
			}
			//开销表赋值并返回总值 
			String sumOfKaixiao = initTableKaixiaoAndGetSum(tableZonghuiKaixiao);
			
			//计算总和
			sumOfTotal = sumOfFuwufei+sumOfBaoxian+sumOfTeamHuishui+sumOfTeamHuibao+Double.valueOf(sumOfKaixiao);
			//创建实体2(当天汇总)
			obSumList.addAll(
					new DangtianHuizongInfo("总服务费",NumUtil.digit1(sumOfFuwufei+"")),
					new DangtianHuizongInfo("总保险",NumUtil.digit1(sumOfBaoxian+"")),
					new DangtianHuizongInfo("总团队回水",NumUtil.digit1(sumOfTeamHuishui+"")),
					new DangtianHuizongInfo("总团队回保",NumUtil.digit1(sumOfTeamHuibao+"")),
					new DangtianHuizongInfo("总开销",sumOfKaixiao)
					);
			
			
			ShowUtil.show("刷新成功",1);
		}else {
			ShowUtil.show("查无数据",1);
		}
		tableZonghui.setItems(obList);
		tableZonghui.refresh();
		
		
		//当天汇总表复值
		tableDangtianHuizong.setItems(obSumList);
		tableDangtianHuizong.getColumns().get(1).setText(MoneyService.digit2(sumOfTotal+""));
		tableDangtianHuizong.refresh();
	}
	
	/**
	 * 总汇Tab中的开销表赋值
	 */
	public static String initTableKaixiaoAndGetSum(TableView<ZonghuiKaixiaoInfo> tableZonghuiKaixiao) {
		ObservableList<ZonghuiKaixiaoInfo> obList = FXCollections.observableArrayList();
		Map<String,Map<String,String>> lockedMap = DataConstans.All_Locked_Data_Map;
		String sumOfKaixiao = "0";
		if(lockedMap.size() > 0) {
			Map<String,String> map = lockedMap.get(DataConstans.All_Locked_Data_Map.size()+"");
			List<KaixiaoInfo> KaixiaoInfoList = JSON.parseObject(MoneyService.getJsonString(map,"实时开销"), new TypeReference<List<KaixiaoInfo>>() {});
			for(KaixiaoInfo infos : KaixiaoInfoList) {
				obList.add(new ZonghuiKaixiaoInfo(infos.getKaixiaoType(),infos.getKaixiaoMoney()));
			}
			sumOfKaixiao = MoneyService.getJsonString(map,"实时开销总和");
			tableZonghuiKaixiao.getColumns().get(1).setText(sumOfKaixiao);
		}
		tableZonghuiKaixiao.setItems(obList);
		tableZonghuiKaixiao.refresh();
		return sumOfKaixiao;
	}
	
}
