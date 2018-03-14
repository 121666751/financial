package com.kendy.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.kendy.controller.TGController;
import com.kendy.db.DBUtil;
import com.kendy.entity.ProxyTeamInfo;
import com.kendy.entity.TGCompanyModel;
import com.kendy.entity.TGFwfinfo;
import com.kendy.entity.TGTeamInfo;
import com.kendy.entity.TypeValueInfo;
import com.kendy.util.CollectUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.MyController;
import javafx.collections.FXCollections;
import javafx.scene.control.TableView;

/**
 * 托管公司的服务费
 * 
 * @author 林泽涛
 * @time 2018年3月13日 下午6:58:18
 */
public class TGFwfService {
	
	public void setFwfDetail(String tgCompany, TableView<TGFwfinfo>  tableTGFwf) {
		if(StringUtil.isBlank(tgCompany)) {
			ShowUtil.show("请选择托管公司");
			//return;
		}
		
		TGController tgController = MyController.tgController;
		
		List<TGCompanyModel> tgCompanys = DBUtil.get_all_tg_company();
		
		Set<String> teamSet = new HashSet<>();
		if(CollectUtil.isHaveValue(tgCompanys)) {
			teamSet = tgCompanys.stream()
					.filter(info -> tgCompany.equals(info.getTgCompanyName()))
					.flatMap((TGCompanyModel info) ->  Stream.of(info.getTgTeamsStr().split("#")))
					.collect(Collectors.toSet());
		}
		if(CollectUtil.isNullOrEmpty(teamSet)) {
			ShowUtil.show("没有托管团队！", 2);
			return;
		}
		
		List<TGTeamInfo> companyProxyTeamInfo = new ArrayList<>();
		for(String teamId : teamSet) {
			//获取代理查询的团队数据
			final List<ProxyTeamInfo> proxyTeamInfoList = tgController.getProxyTeamInfoList(teamId);
			companyProxyTeamInfo.addAll(convert2TGTeamInfo(proxyTeamInfoList));
		}
		
		if(CollectUtil.isNullOrEmpty(companyProxyTeamInfo)) {
			ShowUtil.show("没有代理数据！", 2);
			return;
		}
		
		Map<String, Double> tgTeamRateMap = tgController.getTgTeamRateMap();
		 
		//转化为托管公司的团队数据
		Map<String, List<TGTeamInfo>> teamProxys = companyProxyTeamInfo.stream()
				.collect(Collectors.groupingBy(TGTeamInfo::getTgTeamId));
		
		List<TGFwfinfo> tgFwfInfoList = new ArrayList<>();
		
		teamProxys.forEach((teamID, tgTeamInfoList) -> {
			
			// 1 战绩2.5%：
			double zjRate25Sum = tgTeamInfoList.stream()
				.mapToDouble(info-> NumUtil.getNum(info.getTgZJ25()))
				.sum();
			
			// 2 战绩未知
			String columnName = "ss";// tgTeamRateMap.getOrDefault(teamID, 0d);
			double zjRateUnknowSum = tgTeamInfoList.stream()
					.mapToDouble(info-> NumUtil.getNum(info.getTgZJUnknow()))
					.sum();
			
			// 3 保险
			double zjBaoxianSum = tgTeamInfoList.stream()
					.mapToDouble(info-> NumUtil.getNum(info.getTgBaoxian()))
					.sum();
			
			// 4 回保
			double zjHuibaoSum = tgTeamInfoList.stream()
					.mapToDouble(info-> NumUtil.getNum(info.getTgHuiBao()))
					.sum();
			
			// 5 总和
			double zjProfitSum = zjRate25Sum - zjRateUnknowSum + zjBaoxianSum - zjHuibaoSum;
			
			TGFwfinfo fwfInfo = new TGFwfinfo(
					tgCompany,
					teamID,
					NumUtil.digit2(zjRate25Sum - zjRateUnknowSum +""), // 服务回水 = 战绩2.5% - 战绩未知
					NumUtil.digit2(zjBaoxianSum - zjHuibaoSum +""), // 服务回保 = 保险 - 回保
					NumUtil.digit2(zjProfitSum+""), // 单个总利润
					NumUtil.digit2(zjRateUnknowSum+""), //服务返水
					NumUtil.digit2(zjHuibaoSum+""), // 服务返保
					NumUtil.digit2(zjRate25Sum+""), // 服务全水
					NumUtil.digit2(zjBaoxianSum+""), //服务全保
					NumUtil.digit2((zjRate25Sum + zjBaoxianSum)/20.0 + "") // 服务合计
					);
			tgFwfInfoList.add(fwfInfo);
			
		});
		
		tableTGFwf.setItems(FXCollections.observableArrayList(tgFwfInfoList));
	}
	
	
	/**
	 * 代理查询中的数据转成托管中的团队信息数据
	 * @time 2018年3月7日
	 * @param teamId
	 * @param proxyTeamInfoList
	 * @return
	 */
	private List<TGTeamInfo> convert2TGTeamInfo(List<ProxyTeamInfo> proxyTeamInfoList){
		List<TGTeamInfo> list = new ArrayList<>();
		TGController tgController = MyController.tgController;
		Map<String, Double> tgTeamRateMap = tgController.getTgTeamRateMap();
		
		if(CollectUtil.isHaveValue(proxyTeamInfoList)) {
			list = proxyTeamInfoList.stream().map(info -> {
				TGTeamInfo tgTeam = new TGTeamInfo();
				tgTeam.setTgPlayerId(info.getProxyPlayerId());
				tgTeam.setTgPlayerName(info.getProxyPlayerName());
				tgTeam.setTgYSZJ(info.getProxyYSZJ());
				tgTeam.setTgBaoxian(info.getProxyBaoxian());
				tgTeam.setTgHuiBao(StringUtil.nvl(info.getProxyHuiBao(), "0.00"));
				tgTeam.setTgChangci(info.getProxyTableId());
				//设置战绩2.5% 
				String percent25Str = NumUtil.digit2(NumUtil.getNum(info.getProxyYSZJ()) * 0.025 + "");
				tgTeam.setTgZJ25(percent25Str);
				//设置战绩未知%
				String teamId = info.getProxyTeamId();
				Double teamUnknowValue = tgTeamRateMap.getOrDefault(teamId, 0d);
				String teamUnknowStr = NumUtil.digit2(NumUtil.getNum(info.getProxyYSZJ()) * teamUnknowValue + "");
				tgTeam.setTgZJUnknow(teamUnknowStr);
				//设置利润
				String profit = tgController.getRecordProfit(tgTeam);
				tgTeam.setTgProfit(profit);
				//设置团队
				tgTeam.setTgTeamId(teamId);
				
				return tgTeam;
			}).collect(Collectors.toList());
		}
		return list;
	}

}
