package com.kendy.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.Huishui;
import com.kendy.entity.JifenInfo;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * 积分服务类
 * 
 * @author 林泽涛
 * @time 2017年11月12日 上午3:07:21
 */
public class JifenService {
	
	private static Logger log = Logger.getLogger(JifenService.class);

	
	public static  ComboBox<String> teamIDCombox;//团队ID下拉框
	
	/**
	 * 积分服务类初始化相关配置
	 */
	public static void initJifenService(ComboBox<String> teamIDCombox0) {
		teamIDCombox = teamIDCombox0;
	}
	
	/**
	 * 初始化团队ID下拉框
	 */
	public static void init_Jifen_TeamIdCombox() {
		ObservableList<String> options = FXCollections.observableArrayList();
		DataConstans.huishuiMap.forEach((teamId,huishuiInfo) -> {
			options.add(teamId);
		});
		teamIDCombox.setItems(options);
	}
	
	/**
	 * 新增团队ID
	 */
	public static void addNewTeamId(String teamId) {
		if(teamIDCombox != null && teamIDCombox.getItems() != null) {
			if(!StringUtil.isBlank(teamId))
				teamIDCombox.getItems().add(teamId);
		}
	}
	
	
	
	
//	@SuppressWarnings("unchecked")
//	public static void init_JFTeamSelect_Action(ComboBox<String> teamIDCombox,TextField jfTeamPercent) {
//		teamIDCombox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
//            @Override
//            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
//        		Huishui hs = DataConstans.huishuiMap.get(newValue);
//                if(hs != null) {
//                	jfTeamPercent.setText(hs.getJifenInput());
//                }else {
//                	jfTeamPercent.setText("0");
//                }
//            	
//            }
//        });
//	}
	
	/**
	 * 积分查询功能
	 * 
	 * @param tableJifen 积分表
	 * @param jfStartTime 开始时间 eg.2017-09-01
	 * @param jfEndTime 结束时间 eg.2017-09-20
	 * @param jifenInput 积分值 ，如1积分=10元
	 * @param jifenRankLimit 限定查询前多少名
	 * @param jfTeamIDCombox 选择团队
	 * @author 泽涛
	 */
	public static void jifenQuery(TableView<JifenInfo> tableJifen,DatePicker jfStartTime,
			DatePicker jfEndTime,TextField jifenInput,TextField jifenRankLimit,ComboBox<String> jfTeamIDCombox) {
		//获取各个值
		ObservableList<JifenInfo> obList = FXCollections.observableArrayList();
		String startTime = getFormatTime(jfStartTime.getValue());
		String endTime = getFormatTime(jfEndTime.getValue());
		String jfInput = jifenInput.getText();
		String limit = jifenRankLimit.getText();
		String teamId = jfTeamIDCombox.getSelectionModel().getSelectedItem();
		//查询数据
		List<JifenInfo> list = DBUtil.getJifenQuery(jfInput, teamId, startTime, endTime, limit);
		//更新积分表
		tableJifen.setItems(null);
		if(list != null && !list.isEmpty()) {
			for(JifenInfo info : list) {
				obList.add(info);
			}
			tableJifen.setItems(obList);
		}else {
			ShowUtil.show("查无数据！", 1);
		}
	}
	
	/**
	 * 格式化日历控件的用户选择的日期
	 * @param date
	 * @return
	 */
	public static String getFormatTime(LocalDate date) {
		String pattern = "yyyy-MM-dd";
		DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofPattern(pattern);
		if (date != null) {
            return dateFormatter.format(date);
        } else {
            return "";
        }
	}
}












