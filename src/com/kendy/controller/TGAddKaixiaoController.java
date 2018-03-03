package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.db.DBUtil;
import com.kendy.entity.Huishui;
import com.kendy.entity.TGCompanyModel;
import com.kendy.service.MoneyService;
import com.kendy.util.CollectUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.Main;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;

/**
 * 新增托管开销控制类
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午2:25:46
 */
public class TGAddKaixiaoController implements Initializable{
	
	private static Logger log = Logger.getLogger(TGAddKaixiaoController.class);
	
	@FXML private TextField playerNameField; //玩家名称
	
	@FXML private ChoiceBox<String> payItemsChoice; // 支出项目
	
	@FXML private ListView<String> playersView; // 待补充的玩家视图
	
	@FXML private TextField kaixiaoMoneyField; // 开销金额
	
	private static final String PAY_ITEMS_DB_KEY = "payItems"; //保存到数据库的key
	
	private static List<String> payItems = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//初始化支出项目数据
		initPayItemChoice();
		//自动选值第一个
		if(CollectUtil.isHaveValue(payItems)) {
			payItemsChoice.getSelectionModel().select(0);
		}

	}
	
	/**
	 * 初始化支出项目数据
	 * 
	 * @time 2018年3月3日
	 */
	private void initPayItemChoice() {
		String payItemsJson = DBUtil.getValueByKey(PAY_ITEMS_DB_KEY);
		if(StringUtil.isNotBlank(payItemsJson) && !"{}".equals(payItemsJson)) {
			payItems = JSON.parseObject(payItemsJson, new TypeReference<List<String>>() {});
		}else {
			if(payItems == null)
				payItems = new ArrayList<>();
		}
		payItemsChoice.setItems(FXCollections.observableArrayList(payItems));
	}
	
	
	/**
	 * 添加支出项目
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addPayItemAction(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("添加");
		dialog.setHeaderText(null);
		dialog.setContentText("新增支出项目:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String newPayItem = result.get();
			if(payItems.contains(newPayItem)) {
				ShowUtil.show("已经存在支出项目：" + newPayItem);
			}else {
				//修改界面和缓存 
				payItems.add(newPayItem);
				payItemsChoice.setItems(FXCollections.observableArrayList(payItems));
				//更新到数据库
				savePayItem();
				//刷新
				initPayItemChoice();
				//自动选值
				payItemsChoice.getSelectionModel().select(newPayItem);
			}
		}
	}
	
	/**
	 * 保存支出项目到数据库
	 * 
	 * @time 2018年3月3日
	 */
	private void savePayItem() {
		String payItemsJson = JSON.toJSONString(payItems);
		DBUtil.saveOrUpdateOthers(PAY_ITEMS_DB_KEY, payItemsJson);
	}
	

}
