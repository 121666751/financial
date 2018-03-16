package com.kendy.service;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.DangjuInfo;
import com.kendy.entity.DangtianHuizongInfo;
import com.kendy.entity.KaixiaoInfo;
import com.kendy.entity.ProfitInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.ZonghuiKaixiaoInfo;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

import application.DataConstans;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 * 总汇信息服务类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:50:55
 */
public class ZonghuiService {
	
	private static Logger log = Logger.getLogger(ZonghuiService.class);

	public static DecimalFormat df = new DecimalFormat("#.00");
	
	
	/**
	 * 刷新汇总信息表
	 */
	public static void refreHuizongTable(TableView<ZonghuiInfo> tableZonghui,TableView<DangtianHuizongInfo> tableDangtianHuizong,
			TableView<ZonghuiKaixiaoInfo> tableZonghuiKaixiao,TableView<ProfitInfo> tableProfit) {
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
			
			//添加总团队服务费
			Double teamSumFWF = getTeamSumFWF(tableProfit);
			
			//计算总和
			sumOfTotal = sumOfFuwufei+sumOfBaoxian+sumOfTeamHuishui+sumOfTeamHuibao+Double.valueOf(sumOfKaixiao) + teamSumFWF;
			//创建实体2(当天汇总)
			obSumList.addAll(
					new DangtianHuizongInfo("总服务费",NumUtil.digit1(sumOfFuwufei+"")),
					new DangtianHuizongInfo("总保险",NumUtil.digit1(sumOfBaoxian+"")),
					new DangtianHuizongInfo("总团队回水",NumUtil.digit1(sumOfTeamHuishui+"")),
					new DangtianHuizongInfo("总团队回保",NumUtil.digit1(sumOfTeamHuibao+"")),
					new DangtianHuizongInfo("总开销",sumOfKaixiao),
					new DangtianHuizongInfo("总团队服务费",NumUtil.digit0(teamSumFWF))
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
	 * 获取场次信息中利润表的总团队服务费
	 * @time 2018年1月9日
	 * @param tableProfit 利润表
	 * @return
	 */
	public static Double getTeamSumFWF(TableView<ProfitInfo> tableProfit) {
		Double teamSumFWF = 0d;
//		if(TableUtil.isHasValue(tableProfit)) {
//			try {
//				String fwf = tableProfit.getItems()
//						.stream().filter(info -> "总团队服务费".equals(info.getProfitType()))
//						.collect(Collectors.toList()).get(0)
//						.getProfitAccount();
//				teamSumFWF = NumUtil.getNum(fwf);
//			} catch (Exception e) {
//				ErrorUtil.err("总团队服务费不存在于昨日留底中!");
//			}
//		}
		teamSumFWF = getTodayTotalTeamFWF();
		return teamSumFWF;
	}
	
	/**
	 * 总汇Tab中的开销表赋值
	 */
	public static String initTableKaixiaoAndGetSum(TableView<ZonghuiKaixiaoInfo> tableZonghuiKaixiao) {
		ObservableList<ZonghuiKaixiaoInfo> obList = FXCollections.observableArrayList();
		Map<String,Map<String,String>> lockedMap = DataConstans.All_Locked_Data_Map;
		String sumOfKaixiao = "0";
		if(lockedMap.size() > 0) {
			Map<String,String> map = lockedMap.get(DataConstans.Index_Table_Id_Map.size()+"");
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
	
    /**
     * 设置今天的总团队服务费
     * 昨日留底总团队服务费与所有总团队服务费的差 = 当天汇总中的总团队服务费
     * @time 2018年2月7日
     */
    private static Double getTodayTotalTeamFWF() {
    	Double yesterday_diff_today_total_team_fwf_sum = 0d;
    	List<ProfitInfo> profitList;
    	String profit = DataConstans.preDataMap.get("利润");
    	if(StringUtil.isBlank(profit)) {
    		profitList = Collections.EMPTY_LIST;
    	}else {
    		profitList = JSON.parseObject(profit, new TypeReference<List<ProfitInfo>>() {});
    	}
    	String yestoday = profitList.stream()
    			.filter(info-> "总团队服务费".equals(info.getProfitType()))
    			.map(info->info.getProfitAccount()).findFirst().orElse("0");
    	
    	String now = MyController.table_Profit.getItems().stream()
		    	.filter(info-> "总团队服务费".equals(info.getProfitType()))
				.map(info->info.getProfitAccount()).findFirst().orElse("0");
    	
    	Double yester = NumUtil.getNum(yestoday);
    	Double today = NumUtil.getNum(now);
    	if(yester > today) {
    		log.error("检测到昨天的总团队服务费大于今天！请检查");
    		yesterday_diff_today_total_team_fwf_sum = 0d;
    	}else {
    		yesterday_diff_today_total_team_fwf_sum = today - yester;
    	}
    	return yesterday_diff_today_total_team_fwf_sum;
    }

}
