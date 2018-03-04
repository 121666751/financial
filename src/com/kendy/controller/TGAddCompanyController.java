package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.Huishui;
import com.kendy.entity.TGCompanyModel;
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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * 新增托管公司控制类
 * 
 * @author 林泽涛
 * @time 2018年3月3日 下午2:25:46
 */
public class TGAddCompanyController implements Initializable{
	
	private static Logger log = Logger.getLogger(TGAddCompanyController.class);
	
	@FXML private TextField tg_company_field; //托管公司名称
	
	@FXML private ChoiceBox<String> gudongChoice; //选择股东
	
	@FXML private ListView<String> tg_team_view; //托管公司的托管团队
	
	@FXML private ListView<String> gudong_team_view; //当前股东的团队
	
	@FXML private TextField companyRateField; // 我公司的分红比例
	
	@FXML private TextField tgCompanyRateField; //托管公司的分红比例
	
	@FXML private TextField yajin; //托管公司押金
	
	@FXML private TextField edu; //托管公司额度
	
	//股东所拥有的团队 {股东：List<团队>}
	private static Map<String,List<Huishui>> gudongTeamMap =  new HashMap<>();
	
	

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
		//清空表数据
		initListViews();
		//初始化股东所拥有的团队
		initGudongTeamMap();
		//初始化股东列表
		initGudongChoice();
		
	}
	
	/**
	 * 清空表数据
	 * 
	 * @time 2018年3月3日
	 */
	private void initListViews() {
		tg_team_view.setItems(FXCollections.observableArrayList());
		gudong_team_view.setItems(FXCollections.observableArrayList());
	}
	
	/**
	 * 初始化股东所拥有的团队
	 * 
	 * @time 2018年3月3日
	 */
	private void initGudongTeamMap() {
		List<Huishui> allTeamHS = DBUtil.getAllTeamHS();
		if(CollectUtil.isHaveValue(allTeamHS)) {
			gudongTeamMap = allTeamHS.stream().collect(Collectors.groupingBy(Huishui::getGudong));
		}
	}
	
	/**
	 * 初始化股东列表（赋值和监听）
	 * 
	 * @time 2018年3月3日
	 */
	private void initGudongChoice() {
		try {
			ObservableList<String> gudongList = MyController.getGudongList();
			if(CollectUtil.isHaveValue(gudongList)) {
				gudongChoice.setItems(gudongList);
				//监听
				gudongChoice.getSelectionModel().selectedItemProperty().addListener(event -> {
					initListViews();
					String gudong = gudongChoice.getSelectionModel().getSelectedItem();
					final List<Huishui> teamList = gudongTeamMap.getOrDefault(gudong, new ArrayList<>());
					List<String> teamIDList = teamList.stream().map(Huishui::getTeamId).collect(Collectors.toList());
					gudong_team_view.setItems(FXCollections.observableArrayList(teamIDList));
				});
			}
		} catch (Exception e) {
			ErrorUtil.err("初始化股东列表（赋值和监听）失败", e);
		}
	}
	
	/**
	 * 移除托管团队（左表转到右表）
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void removeTGTeamAction(ActionEvent event) {
		String selectedTeam = tg_team_view.getSelectionModel().getSelectedItem();
		if(StringUtil.isBlank(selectedTeam)) {
			ShowUtil.show("未选择左表的团队!");
			return;
		}else{
			boolean existSelectedTeam = gudong_team_view.getItems().stream().anyMatch(info -> info.equals(selectedTeam));
			if(!existSelectedTeam) {
				// 增加到右表
				gudong_team_view.getItems().add(selectedTeam);
			}
			tg_team_view.getItems().remove(selectedTeam);
		}
	}
	
	/**
	 * 增加托管团队（右表转到左表）
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addTGTeamAction(ActionEvent event) {
		String selectedTeam = gudong_team_view.getSelectionModel().getSelectedItem();
		if(StringUtil.isBlank(selectedTeam)) {
			ShowUtil.show("未选择右表的团队!");
			return;
		}else{
			boolean existSelectedTeam = tg_team_view.getItems().stream().anyMatch(info -> info.equals(selectedTeam));
			if(!existSelectedTeam) {
				// 增加到左表
				tg_team_view.getItems().add(selectedTeam);
			}
			gudong_team_view.getItems().remove(selectedTeam);
		}
	}
	
	/**
	 * 检验参数
	 * @time 2018年3月3日
	 * @return
	 */
	private boolean hasAnyParamBlank() {
		return StringUtil.isAnyBlank(
				tg_company_field.getText(),
				companyRateField.getText(),
				tgCompanyRateField.getText(),
				yajin.getText(),
				edu.getText(),
				gudongChoice.getSelectionModel().getSelectedItem()
				);
	}
	
	/**
	 * 获取待提交的数据
	 * @time 2018年3月3日
	 * @return
	 */
	private TGCompanyModel getSubmitData() {
		TGCompanyModel tgCompnayModel = new TGCompanyModel(
				tg_company_field.getText().trim(),
				companyRateField.getText(),
				tgCompanyRateField.getText(),
				yajin.getText(),
				edu.getText(),
				""
				);
		tgCompnayModel.setBeizhu(gudongChoice.getSelectionModel().getSelectedItem());
		ObservableList<String> items = tg_team_view.getItems();
		if(CollectUtil.isHaveValue(items)) {
			tgCompnayModel.setTgTeamsStr(items.stream().collect(Collectors.joining("#")));
			tgCompnayModel.setTgTeamList(items.stream().collect(Collectors.toList()));
		}
		
		return tgCompnayModel;
	}
	
	/**
	 * 按钮：确认添加新托管公司
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addNewTGCompanyOKBtnAction(ActionEvent event) {
		//检验参数
		if(hasAnyParamBlank()) {
			ShowUtil.show("Sorry, 提交信息不完整，请查看！");
			return;
		}
		//传递给主控制类处理逻辑 TODO
		TGCompanyModel tgCompanyModel = getSubmitData();
		System.out.println(tgCompanyModel);
		TGController tgController = MyController.tgController;
		ObservableList<Node> companyList = tgController.TG_Company_VBox.getChildren();
		if(CollectUtil.isHaveValue(companyList)) {
			companyList.add(new Button(tgCompanyModel.getTgCompanyName()));
		}
	}

}
