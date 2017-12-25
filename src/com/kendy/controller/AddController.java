package com.kendy.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.service.JifenService;
import com.kendy.service.MoneyService;
import com.kendy.service.ShangmaService;
import com.kendy.service.TeamProxyService;
import com.kendy.util.ConsUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.Constants;
import application.DataConstans;
import application.Main;
import application.MyController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 添加团队回水的控制器
 * @author Administrator
 *
 */
public class AddController implements Initializable{
	
	private static Logger log = Logger.getLogger(AddController.class);

    //=====================================================================新增团队回水对话框	
	@FXML
	private TextField gudong2Field; //股东
	@FXML
	private TextField beizhu2Field; //股东
    @FXML
    private TextField teamIdField; //新增团队ID
    @FXML
    private TextField teamNameField; //新增团队名称 
    @FXML
    private TextField huishui; //新增团队回水
    @FXML
    private TextField insuranceRate; //新增保险比例
    //=====================================================================新增人员名单对话框
    @FXML
    private TextField gameIdField;//团ID
    @FXML
    private TextField gudongField;//股东
    @FXML
    private TextField teamField;//团队名称
    @FXML
    private TextField playerNameField;//玩家名称
    @FXML
    private TextField beizhuField;//备注
    
    //=====================================================================新增实时开销对话框
    @FXML
    private TextField kaixiaoTypes;//名称
    @FXML
    private TextField kaixiaoMoneys;//开销金额
    //=====================================================================新增实时金额对话框
    @FXML
    private TextField cmName;//名称
    @FXML
    private TextField cmMoney;//实时金额
    @FXML
    private TextField cmPlayerId;
    @FXML
    private TextField cmEdu;//额度
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

	

    
    /**
     * 增加团队回水
     */
    public void addHuishuiOKAction(ActionEvent event){
    	
    	
    	Huishui hs = new Huishui();
    	hs.setTeamId(teamIdField.getText());
    	hs.setTeamName(teamNameField.getText());
    	hs.setGudong(gudong2Field.getText());
    	hs.setBeizhu(beizhu2Field.getText());
    	String _insuranceRate = insuranceRate.getText();
    	if(StringUtil.isBlank(_insuranceRate)) {
    		_insuranceRate = "0";
    	}else {
    		_insuranceRate = NumUtil.digit4(_insuranceRate);
    	}
    	hs.setInsuranceRate(_insuranceRate);
    	
    	String _huishui = huishui.getText();
    	if(StringUtil.isBlank(_huishui)) {
    		_huishui = "0";
    	}else {
    		_huishui = NumUtil.digit4(_huishui);
    	}
    	hs.setHuishuiRate(_huishui);
    	
    	hs.setZjManaged("是");
    	
    	if(!StringUtil.isBlank(hs.getTeamId()) && !StringUtil.isBlank(hs.getHuishuiRate())){
    		DataConstans.huishuiMap.put(hs.getTeamId(), hs);
    		//入库
    		DBUtil.addTeamHS(hs);
    		log.info("已经添加该团队回水:"+hs.toString());
    		ShowUtil.show("已经添加该团队回水", 2);
    		//add 2017-10-23
    		//添加回水后往实时上码表添加团ID按钮
    		ShangmaService.initShangmaButton();
    		//添加回水后往代理查询中添加新团 队ID
    		TeamProxyService.addNewTeamId(hs.getTeamId());
    		//积分查询添加新团队ID jfTeamIDCombox 小胖有反馈时再添加吧
    		JifenService.addNewTeamId(hs.getTeamId());
    		
    	}
    	//获取到新增回水窗口的实例
    	Stage huishuiStage = DataConstans.framesNameMap.get(Constants.ADD_TEAM_HUISHUI_FRAME);
    	DataConstans.framesNameMap.remove(Constants.ADD_TEAM_HUISHUI_FRAME);
    	huishuiStage.close();
    }
    
    /**
     * 增加人员名单
     */
    public void addNewPlayerOKAction(ActionEvent event){
        
    	Player player = new Player();
    	player.setGameId(gameIdField.getText());
    	player.setGudong(gudongField.getText());
    	player.setTeamName(teamField.getText());
    	player.setPlayerName(playerNameField.getText());
    	player.setEdu(beizhuField.getText());
    	if(!StringUtil.isBlank(player.getgameId()) && !StringUtil.isBlank(player.getTeamName())){
    		DataConstans.membersMap.put(player.getgameId(), player);
    		ConsUtil.refreshTeamIdAndPlayerId();
    		DBUtil.addMember(player);
    		log.info("已经添加该人员:"+player);
    		ShowUtil.show("已经添加该人员", 2);
    	}
    	//获取到新增人员窗口的实例
    	Stage addNewPlayerStage = DataConstans.framesNameMap.get(Constants.ADD_NEWPALYER_FRAME);
    	DataConstans.framesNameMap.remove(Constants.ADD_NEWPALYER_FRAME);
    	addNewPlayerStage.close();
    }
    
    /**
     * 增加开销
     */
    public void addKaixiaoOKAction(ActionEvent event){
    	
    	if(!StringUtil.isBlank(kaixiaoTypes.getText())){
    		DataConstans.kaixiaoMap.put(kaixiaoTypes.getText(), kaixiaoMoneys.getText());
    		ShowUtil.show("添加开销成功", 2);
    		//更新到表
    		MyController mc = Main.myController;
//    		MyController mc = (MyController) Main._fxmlLoader.getController();
    		if(mc != null) {
    			mc.updateKaixiaoTable(kaixiaoTypes.getText(), kaixiaoMoneys.getText());
    		}else {
    			log.info("===========================mc = null ");
    		}
    		
    	}
    	//获取到新增人员窗口的实例
    	Stage addNewPlayerStage = DataConstans.framesNameMap.get(Constants.ADD_KAIXIAO_FRAME);
    	DataConstans.framesNameMap.remove(Constants.ADD_KAIXIAO_FRAME);
    	addNewPlayerStage.close();
    }
    /**
     * 增加人员的实时金额
     */
    public void add_SSJE_person_Action(ActionEvent event){
    	//先对玩家ID进行判断
    	String playerId = cmPlayerId.getText();
    	Player player;
    	if(StringUtil.isBlank(playerId)) {
    		ShowUtil.show("操作失败！请详看系统说明！");
    		return;
    	}else {
    		playerId = playerId.trim();
    		player = DataConstans.membersMap.get(playerId);//从人员表查找相关记录
    		if(player == null) {
    			ShowUtil.show("操作失败！人员表中无此ID,请先添加该人员！");
    			return;
    		}
    		//重复判断
    		if(MoneyService.isExistIn_SSJE_Table_byId(playerId)) {
    			ShowUtil.show("操作失败！实时金额表中已经存在此ID,请查检！");
    			return;
    		}
    	}
    	//添加实时金额
    	CurrentMoneyInfo tempMoneyInfo = new CurrentMoneyInfo(player.getPlayerName(),cmMoney.getText(),player.getgameId(),player.getEdu());
		MoneyService.addInfo(tempMoneyInfo);
		ShowUtil.show("添加成功", 2);
		MoneyService.flush_SSJE_table();
		MoneyService.scrolById(playerId);
    	
    	//获取到新增人员窗口的实例
    	Stage addNewPlayerStage = DataConstans.framesNameMap.get(Constants.ADD_CURRENT_MONEY_FRAME);
    	DataConstans.framesNameMap.remove(Constants.ADD_CURRENT_MONEY_FRAME);
    	addNewPlayerStage.close();
    }
    
    /**
     * 增加临时实时金额（非人员金额）
     * 
     * @time 2017年11月9日
     * @param event
     */
    public void add_SSJE_other_Action(ActionEvent event){
    	//先对名称进行判断
    	String name = cmName.getText();
    	if(StringUtil.isBlank(name)) {
    		ShowUtil.show("操作失败！请详看系统说明！");
    		return;
    	}
    	//重复判断
		if(MoneyService.isExistIn_SSJE_Table_byName(name)) {
			ShowUtil.show("操作失败！实时金额表中已经存在此名称,请查检！");
			return;
		}
    	//添加实时金额
    	CurrentMoneyInfo tempMoneyInfo = new CurrentMoneyInfo(name,cmMoney.getText(),"","");
		MoneyService.addInfo(tempMoneyInfo);
		ShowUtil.show("添加成功", 2);
		MoneyService.flush_SSJE_table();
		MoneyService.scrolByName(name);
		
    	
    	//获取到新增人员窗口的实例
    	Stage addNewPlayerStage = DataConstans.framesNameMap.get(Constants.ADD_CURRENT_MONEY_FRAME);
    	DataConstans.framesNameMap.remove(Constants.ADD_CURRENT_MONEY_FRAME);
    	addNewPlayerStage.close();
    }
	
	

}
