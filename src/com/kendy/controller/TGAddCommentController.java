package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.db.DBUtil;
import com.kendy.entity.Player;
import com.kendy.entity.TGCommentInfo;
import com.kendy.entity.TGKaixiaoInfo;
import com.kendy.util.CollectUtil;
import com.kendy.util.MapUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TimeUtil;

import application.DataConstans;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

/**
 * 新增玩家备注控制类
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午2:25:46
 */
public class TGAddCommentController implements Initializable{
	
	private static Logger log = Logger.getLogger(TGAddCommentController.class);
	
	@FXML private TextField searchField; //玩家名称(模糊搜索)
	
	@FXML private TextField FinalPlaerNameField; //玩家名称
	
	@FXML private TextField FinalPlaerIdField; //玩家ID
	
	@FXML private ListView<String> playersView; // 待补充的玩家视图

	@FXML private ChoiceBox<String> typeChoice; // 类别
	
	@FXML private TextField IDField; // ID
	
	@FXML private TextField nameField; // 名称
	
	@FXML private TextField beizhuField; // 备注
	
	private static final String TG_WANGJIA_COMMENT_DB_KEY = "tg_wangjia_comment"; //保存到数据库的key
	
	public static List<String> typeItems = new ArrayList<>();
	
	private static List<Player> players = new ArrayList<>();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//获取人员数据
		initPlayers();
		
		//添加文本框监听
		addListener();
		
		//初始化类别数据
		initTypeChoice();
		
		//自动选值第一个
		if(CollectUtil.isHaveValue(typeItems)) {
			typeChoice.getSelectionModel().select(0);
		}

	}
	
	/**
	 * 获取人员数据
	 * @time 2018年3月3日
	 */
	private void initPlayers() {
		Map<String, Player> membersMap = DataConstans.membersMap;
		if(MapUtil.isHavaValue(membersMap)) {
			players = new ArrayList<>(membersMap.values());
		}
	}
	
	/**
	 * 添加文本框监听(监听输入框和ListView点击框)
	 * 
	 * @time 2018年3月3日
	 */
	private void addListener() {
		//监听输入框
		searchField.textProperty().addListener(event -> {
			String text = searchField.getText();
			List<String> playerNames =  players.parallelStream()
					   .filter(info -> ((Player)info).getPlayerName().contains(text))
					   .map(info -> info.getPlayerName() + "##" + info.getgameId())
					   .collect(Collectors.toList());
			playersView.setItems(FXCollections.observableArrayList(playerNames));
		});
		
		//监听ListView点击框
		playersView.getSelectionModel().selectedItemProperty().addListener(event -> {
			String text = playersView.getSelectionModel().getSelectedItem();
			if(StringUtil.isBlank(text)) {
				FinalPlaerNameField.setText("");
				FinalPlaerIdField.setText("");
			}else {
				FinalPlaerNameField.setText(text.split("##")[0]);
				FinalPlaerIdField.setText(text.split("##")[1]);
			}
		});
	}
	
	/**
	 * 初始化类别数据
	 * 
	 * @time 2018年3月3日
	 */
	private void initTypeChoice() {
		String typeItemsJson = DBUtil.getValueByKey(TG_WANGJIA_COMMENT_DB_KEY);
		if(StringUtil.isNotBlank(typeItemsJson) && !"{}".equals(typeItemsJson)) {
			typeItems = JSON.parseObject(typeItemsJson, new TypeReference<List<String>>() {});
		}else {
			if(typeItems == null || typeItems.isEmpty()) {
				typeItems = new ArrayList<>(Arrays.asList("推荐玩家","改号","小号"));
				saveTypeItem();
			}
		}
		typeChoice.setItems(FXCollections.observableArrayList(typeItems));
	}
	
	
	/**
	 * 添加类别选项
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addTypeItemAction(ActionEvent event) {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("添加");
		dialog.setHeaderText(null);
		dialog.setContentText("新增类别选项:");
		
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()){
			String newPayItem = result.get();
			if(typeItems.contains(newPayItem)) {
				ShowUtil.show("已经存在类别选项：" + newPayItem);
			}else {
				//修改界面和缓存 
				typeItems.add(newPayItem);
				typeChoice.setItems(FXCollections.observableArrayList(typeItems));
				//更新到数据库
				saveTypeItem();
				//刷新
				initTypeChoice();
				//自动选值
				typeChoice.getSelectionModel().select(newPayItem);
			}
		}
	}
	
	/**
	 * 保存类别选项到数据库
	 * 
	 * @time 2018年3月3日
	 */
	private void saveTypeItem() {
		String typeItemsJson = JSON.toJSONString(typeItems);
		DBUtil.saveOrUpdateOthers(TG_WANGJIA_COMMENT_DB_KEY, typeItemsJson);
	}
	
	/**
	 * 检验参数
	 * @time 2018年3月3日
	 * @return
	 */
	private boolean hasAnyParamBlank() {
		return StringUtil.isAnyBlank(
				FinalPlaerNameField.getText(),
				FinalPlaerIdField.getText(),
				typeChoice.getSelectionModel().getSelectedItem(),
				IDField.getText(),
				nameField.getText(),
				beizhuField.getText()
				);
	}
	
	/**
	 * 获取待提交的数据
	 * @time 2018年3月3日
	 * @return
	 */
	private TGCommentInfo getSubmitData() {
		TGController tgController = MyController.tgController;
		//获取当前托管公司
		String tgCompany = tgController.getCurrentTGCompany();
		
		TGCommentInfo entity = new TGCommentInfo(
				UUID.randomUUID().toString(),
				TimeUtil.getDateString(),
				FinalPlaerIdField.getText(),
				FinalPlaerNameField.getText(),
				typeChoice.getSelectionModel().getSelectedItem(),
				IDField.getText(),
				nameField.getText(),
				beizhuField.getText(),
				tgCompany
				);
		return entity;
	}
	
	
	
	/**
	 * 按钮：确定提交托管开销数据
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addTGCommentBtnAction(ActionEvent event) {
		//检验参数
		if(hasAnyParamBlank()) {
			ShowUtil.show("Sorry, 提交信息不完整，请查看！");
			return;
		}
		//传递给主控制类处理逻辑 TODO
		TGCommentInfo tgCommentInfo = getSubmitData();
		TGController tgController = MyController.tgController;
		//保存到数据库
		DBUtil.saveOrUpdate_tg_comment(tgCommentInfo);
		//刷新界面
		tgController.refreshTableTGComment();
		ShowUtil.show("添加完成",1);
	}
	

}
