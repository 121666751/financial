package com.kendy.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.MemberZJInfo;
import com.kendy.entity.Player;
import com.kendy.entity.TeamHuishuiInfo;
import com.kendy.util.StringUtil;

import application.DataConstans;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * 会员查询服务类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:51:39
 */
public class MemberService {
	
	private static Logger log = Logger.getLogger(MemberService.class);

	public static void initMemberQuery(ListView<String> memberListView,TableView<MemberZJInfo> tableMemberZJ,
			Label memberDateStr,Label memberPlayerId,Label memberPlayerName,Label memberSumOfZJ,Label memberTotalZJ) {
		//ListView变化时自动更新右边的信息
		memberListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener() {
		            @Override
		            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		            	/****会员当天战绩区域赋值 ***/
		            	//清空原有数据
		            	memberDateStr.setText("2017-00-00");
		            	memberPlayerId.setText("");
		            	memberPlayerName.setText("");
		            	memberSumOfZJ.setText("");
		            	tableMemberZJ.setItems(null);
		            	if(!StringUtil.isBlank(DataConstans.Date_Str)) {
		            		memberDateStr.setText(DataConstans.Date_Str);
		            	}
		            	String newVal = (String)newValue;
		            	if(!StringUtil.isBlank(newVal)) {
		            		//玩家ID和玩家名称赋值
		            		String wanjiaName = newVal.trim().split(" ")[0];
		            		memberPlayerId.setText(newVal.trim().split(" ")[1]);
		            		memberPlayerName.setText(wanjiaName);
		            		//根据玩家名称去查询数据
		            		updateMemberTable(tableMemberZJ,memberPlayerId.getText(),memberSumOfZJ);
		            	}
		            	
		            	/****会员历史战绩区域赋值 ***/
		            	String totalZJ = DBUtil.getTotalZJByPId(memberPlayerId.getText());
		            	memberTotalZJ.setText(totalZJ);
		            }
				});
	}
	
	/**
	 * 更新人员表
	 * 
	 * @time 2017年10月28日
	 * @param tableMemberZJ
	 * @param wanjiaId
	 * @param memberSumOfZJ
	 */
	public static void updateMemberTable(TableView<MemberZJInfo> tableMemberZJ,String wanjiaId,Label memberSumOfZJ) {
		//根据名称获取团队ID
		Player wanjia = DataConstans.membersMap.get(wanjiaId);
		String teamId = "";
		if(wanjia != null && !StringUtil.isBlank(wanjia.getTeamName())) {
			teamId = wanjia.getTeamName();
		}
		ObservableList<MemberZJInfo> obList = FXCollections.observableArrayList();
		String zj = "";//战绩
		Double sumOfZJ = 0d;//战绩总和
		if(DataConstans.Total_Team_Huishui_Map.size() != 0) {
			List<TeamHuishuiInfo> list = DataConstans.Total_Team_Huishui_Map.get(teamId);
			if(list != null) {
				for(TeamHuishuiInfo info : list) {
					if(info.getWanjiaId().equals(wanjiaId)) {
						zj = info.getShishou();
						obList.add(new MemberZJInfo(info.getTableId(),zj));
						sumOfZJ += MoneyService.getNum(zj);
					}
				}
			}
		}
		//赋值总和
		memberSumOfZJ.setText(MoneyService.digit0(sumOfZJ));
		//刷新表
		tableMemberZJ.setItems(obList);
		tableMemberZJ.refresh();
		
		
	}
	
	public static void setResult2ListView(TextField memberNameField,ListView<String> memberListView) {
    	String name = memberNameField.getText();
    	Set<Player> set = new HashSet<>();
    	if(!StringUtil.isBlank(name)) {
    		DataConstans.membersMap.forEach((mId,mPlayer) ->{
    			if(mPlayer.getPlayerName().contains(name)) {
    				set.add(mPlayer);
    			}
    		});
    	}
    	ObservableList<String> list = FXCollections.observableArrayList();
    	memberListView.setItems(list);
    	if(set.size() >0) {
    		set.forEach(play -> {
    			memberListView.getItems().add(play.getPlayerName()+" "+play.getgameId());
    		});
    	}
    	memberListView.refresh();
	}
}
