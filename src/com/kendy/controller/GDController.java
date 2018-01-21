package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.kendy.db.DBUtil;
import com.kendy.entity.GudongRateInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.Record;
import com.kendy.service.MoneyService;
import com.kendy.service.TeamProxyService;
import com.kendy.util.CollectUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import application.MyController;
import application.PropertiesUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

/**
 * 股东贡献控制类
 * 
 * @author 林泽涛
 * @time 2018年1月14日 下午6:12:15
 */
public class GDController implements Initializable{

	
	
	//股东贡献值主表
	@FXML private TableView<GudongRateInfo> tableGDSum;
	@FXML private TableColumn<GudongRateInfo,String> gudongName;//股东名称
	@FXML private TableColumn<GudongRateInfo,String> gudongProfitRate;//股东利润占比
	
	//*************************************************************************//
	@FXML private Label dangtianProfits;//当天总利润
	@FXML private TextField personTime_ProfitRate_Text;//1人次等于多少利润
	
	@FXML private HBox contributionHBox;//动态生成各个股东贡献值的区域
	@FXML private Button clearBtn;
	@FXML private Button GDRefreshBtn;
	
	private static final String UN_KNOWN = "未知";
	
	
	//数据来源:当天某俱乐部的数据
	private static List<Record> dataList = new ArrayList();
	
	//将dataList转成特定数据结构
	//{股东ID:{团队ID:List<Record}}
	private static  Map<String,Map<String,List<Record>>> gudongTeamMap = new HashMap<>();
	
	//{股东ID:股东利润占比} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsRateMap = new HashMap<>();
	//{股东ID:股东利润值} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsValueMap = new HashMap<>();
	
	public static void initData() {
		//初始化dataList,根据当前的俱乐部去取是否有问题？
		initDataList();
		
		//将原始数据转换成特定的数据结构
		initGudongTeamMap();
		//
	}
	
	/**
	 * 初始化dataList
	 * 备注：获取当天保存到数据库的当前俱乐部记录List<Record>
	 * @time 2018年1月18日
	 */
	private static void initDataList() {
		String maxRecordTime = DBUtil.getMaxRecordTime(); 
		String currentClubId = PropertiesUtil.readProperty("clubId");
		if(!StringUtil.isAnyBlank(maxRecordTime,currentClubId)) {
			List<Record> list = DBUtil.getRecordsByMaxTimeAndClub(maxRecordTime, currentClubId);
			if(CollectUtil.isHaveValue(list)) {
				dataList = list;
			}
		}
	}
	
	/**
	 * 将原始数据转换成特定的数据结构
	 * {股东ID:{团队ID:List<Record}}
	 * 
	 * @time 2018年1月19日
	 */
	private static void initGudongTeamMap() {
		if(CollectUtil.isNullOrEmpty(dataList)) return;
		gudongTeamMap = 
		dataList.stream()
			    .collect(Collectors.groupingBy(//先按股东分
			    		record -> getGudongByPlayerId((Record)record),
			    		Collectors.groupingBy(info -> StringUtil.nvl(info.getTeamId(),UN_KNOWN))));//再按团队分
	}
	
	/**
	 * 根据玩家Id获取 
	 * @time 2018年1月20日
	 * @param playerId
	 * @return
	 */
	private static String getGudongByPlayerId(String playerId) {
		
		Player player = DataConstans.membersMap.get(playerId);
		if(player != null) {
			return StringUtil.nvl(player.getGudong(),UN_KNOWN);
		}else {
			return UN_KNOWN;
		}
	}
	/**
	 * 根据记录获取股东
	 * 
	 * @time 2018年1月20日
	 * @param record
	 * @return
	 */
	private static String getGudongByPlayerId(Record record) {
		String playerId = ((Record) record).getPlayerId();
		Player player = DataConstans.membersMap.get(playerId);
		if(player != null) {
			return StringUtil.nvl(player.getGudong(),UN_KNOWN);
		}else {
			return UN_KNOWN;
		}
	}
	
	
	
	/**'
	 * 准备所有数据
	 * 
	 * @time 2018年1月20日
	 */
	public  void prepareAllData() {
		initData();
		Map<String,List<Record>> gudongRecordList = dataList.stream()
				.collect(Collectors.groupingBy(record -> getGudongByPlayerId((Record)record)));
		//计算总利润
		Double totalProfits = getTotalProfits();
		if(Double.compare(totalProfits, 0) <= 0) {
			ShowUtil.show("当天总利润计算为0", 2);
			return;
		}else {
			dangtianProfits.setText(NumUtil.digit0(totalProfits));
		}
		//每个股东的利润占比
		gudongRecordList.forEach((gudong,eachRecordList) -> {
			Double eachGudong =  getHelirun(eachRecordList);//每个股东的利润
			String gudongRate = NumUtil.getPercentStr( eachGudong / totalProfits );//每个股东的利润占比
			//System.out.println(String.format("股东%s占比%s", gudong,gudongRate));
			gudongProfitsValueMap.put(gudong, NumUtil.digit0(eachGudong));//股东值占比
			gudongProfitsRateMap.put(gudong, gudongRate);//股东值
		});
		
	}
	
	/**
	 * 获取该俱乐部当天的总利润
	 * 
	 */
	public static Double getTotalProfits() {
		return getHelirun(dataList);
	}
	
	
	/**
	 * 计算多行战绩的利润(可以计算当天所有战绩的利润)
	 * @time 2018年1月20日
	 * @param recordList
	 * @return
	 */
	public static Double getHelirun(final List<Record> recordList) {
		return CollectUtil.isNullOrEmpty(recordList) ? 0d : 
			recordList.stream().mapToDouble(item->getHeLirun(item)).sum();
	}
	
	/**
	 * 计算每一行战绩的利润
	 * 备注：摘抄自场次信息的第一个表的计算步骤
	 * 
	 * @time 2018年1月20日
	 * @param record
	 * @return
	 */
	public static Double getHeLirun(final Record record) {
		String playerId = record.getPlayerId();
		String teamId = getTeamIdWithUperCase(playerId);
		String zhanji = record.getScore();
		String baoxian = record.getInsuranceEach();
		String shishou = MoneyService.getShiShou(record.getScore());
		String chuHuishui = NumUtil.digit1(MoneyService.getChuhuishui(zhanji, teamId));
		String shuihouxian = NumUtil.digit1((-1)*Double.valueOf(baoxian)*0.975+"");
		String shouHuishui = NumUtil.digit1(Math.abs(Double.valueOf(zhanji))*0.025+"");
		String baohui = NumUtil.digit1(MoneyService.getHuiBao(baoxian,teamId));
		String heLirun = NumUtil.digit2(MoneyService.getHeLirun(shouHuishui,chuHuishui,shuihouxian,baohui));
		return  NumUtil.getNum(heLirun);
	}
	
	/**
	 * 根据玩家ID获取玩家所属的团队ID
	 * 
	 * @time 2018年1月20日
	 * @param playerId
	 * @return
	 */
	public static String getTeamIdWithUperCase(String playerId) {
		Player player = DataConstans.membersMap.get(playerId);
		String teamId = "";
		if(player != null){
			teamId = player.getTeamName();
			teamId = StringUtil.isBlank(teamId) ? "" : teamId.toUpperCase();
		}
		//若缓存中的player不存在，则尝试去数据库取取加进缓存中
		else {
			Player tempPlayer = DBUtil.getMemberById(playerId);
			if(tempPlayer != null && !StringUtil.isBlank(tempPlayer.getTeamName())) {
				DataConstans.membersMap.put(playerId, tempPlayer);
				teamId = tempPlayer.getTeamName();
				teamId = StringUtil.isBlank(teamId) ? "" : teamId.toUpperCase();
			}
		}
		return teamId;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		MyController.bindCellValue(gudongName,gudongProfitRate);
		gudongProfitRate.setCellFactory(MyController.getColorCellFactory(new GudongRateInfo()));
	}
	
	
	/**
	 * 获取人次  1人次 = XX利润
	 */
	public String getRenci() {
		String renci = StringUtil.nvl(personTime_ProfitRate_Text.getText(),"0.0");
		return renci;
	}
	
	/**
	 * 设置主表数据
	 * 
	 * @time 2018年1月20日
	 */
	public  void setDataToSumTable() {
		ObservableList<GudongRateInfo> obList = FXCollections.observableArrayList();
		gudongProfitsValueMap.forEach((gudongName,gudongRate) -> {
			obList.add(new GudongRateInfo(gudongName,gudongRate));
		});
		tableGDSum.setItems(obList);
		tableGDSum.refresh();
	}
	
	/**
	 * 生成动态股东表
	 * 
	 * @time 2018年1月20日
	 */
	private void dynamicGenerateGDTable() {
		 TableView<GudongRateInfo> table;
	      
		ObservableList<GudongRateInfo> obList= FXCollections.observableArrayList();
        for(Map.Entry<String, String> entry : gudongProfitsRateMap.entrySet()) {
        	String gudongName = entry.getKey();
        	String gudongProfitRate = entry.getValue();
        	table = new TableView<GudongRateInfo>();
	 
        	//设置列
	        TableColumn firstNameCol = new TableColumn("股东"+gudongName);
	        firstNameCol.setStyle("-fx-alignment: CENTER;");
	        firstNameCol.setPrefWidth(110);
	        firstNameCol.setCellValueFactory(
	                new PropertyValueFactory<GudongRateInfo, String>("gudongName"));
	 
	        TableColumn lastNameCol = new TableColumn(gudongProfitRate);
	        lastNameCol.setStyle("-fx-alignment: CENTER;");
	        lastNameCol.setPrefWidth(95);
	        lastNameCol.setCellValueFactory(
	                new PropertyValueFactory<GudongRateInfo, String>("gudongProfitRate"));
	        lastNameCol.setCellFactory(MyController.getColorCellFactory(new GudongRateInfo()));
	        table.setPrefWidth(210);
	        table.getColumns().addAll(firstNameCol, lastNameCol);
	 
	        //设置数据
	        Map<String,List<Record>> teamMap = gudongTeamMap.get(gudongName);
	        setDynamicTableData(table,teamMap,gudongName);
	        
	        contributionHBox.setSpacing(5);
	        contributionHBox.setPadding(new Insets(0, 0, 0, 0));
	        contributionHBox.getChildren().addAll(table);
        }
	}
	
	/**
	 * 设置单个动态表的数据
	 * 注意：
	 * 		1、公司的计入C客；
	 * 		2、团队服务费问题：目前是直接引用代理查询表的数据，但最好重新计算！！！TODO
	 * 		3、后期加入联盟桌费！！！！TODO
	 * 
	 * @time 2018年1月20日
	 * @param table  单个动态表
	 * @param teamMap 单个动态表的团队数据，不包括联盟
	 */
	private  void setDynamicTableData(TableView<GudongRateInfo> table,Map<String,List<Record>> teamMap,String gudong) {
		//Loop设置单个动态表的数据(团队部分)
		for(Map.Entry<String, List<Record>> teamEntry : teamMap.entrySet()) {
        	String teamId = teamEntry.getKey();
        	List<Record> teamList = teamEntry.getValue();
        	setDynamicTableData_team_part(table,teamId,teamList,gudong);
        }
		//设置单个动态表的数据(联盟部分)
		setDynamicTableData_team_part(table,gudong);
	}

	
	
	/**
	 * 设置单个动态表的数据(联盟部分)
	 * 
	 * @time 2018年1月21日
	 * @param table
	 * @param gudong
	 */
	private  void setDynamicTableData_team_part(TableView<GudongRateInfo> table,String gudong) {
		Double LM1Zhuofei = LMController.getLM1TotalZhuofei(gudong);
		table.getItems().add(new GudongRateInfo("联盟桌费",LM1Zhuofei.intValue()+""));
		table.refresh();
	}
	
	/**
	 * 设置单个动态表的数据(团队部分)
	 * 		1、公司的计入X客，如股东A,就计入A客
	 * 		2、团队服务费问题：目前是直接引用代理查询表的数据，但最好重新计算！！！（已经重算了）
	 * 
	 * @time 2018年1月20日
	 * @param table
	 * @param teamId
	 */
	private  void setDynamicTableData_team_part(TableView<GudongRateInfo> table,String teamId, List<Record> teamList,String gudong) {
		//判断teamId，公司的计入C客
		
		//获取团队服务费
		//String teamFWF = TeamProxyService.get_TeamFWF_byTeamId(teamId);//获取服务费（这个方法不太准确，除非总利润一致）
		String teamFWF = TeamProxyService.getTeamFWF_GD(teamId,teamList);//获取服务费（根据锁定的存到数据库中的数据）
		
		//计算团队占比      公式 = (人次 + 团队服务费) / 当天总利润 
		Double teamRenci = teamList.size() * NumUtil.getNum(getRenci());
		Double teamRate_Double = ( teamRenci + NumUtil.getNum(teamFWF)) / NumUtil.getNum(dangtianProfits.getText()); 
		String teamRateStr = NumUtil.getPercentStr(teamRate_Double);
		table.getItems().add(new GudongRateInfo(getFinalTeamId(teamId,gudong),teamRateStr));
		
		table.refresh();
	}
	
	/**
	 * 获取最终的团队名称
	 * 
	 * @time 2018年1月21日
	 * @param teamId
	 * @param gudong
	 * @return
	 */
	private String getFinalTeamId(String teamId,String gudong) {
		return "团队"+("公司".equals(teamId) ? gudong+"客" : teamId);
	}
	
	
	/**
	 * 股东贡献值即时刷新按钮
	 * 
	 * @time 2018年1月14日
	 * @param event
	 */
	public void GDContributeRefreshAction(ActionEvent event) {
		//清空数据
		clearBtn.fire();
		//准备数据
		prepareAllData();
		//设置表数据
		setDataToSumTable();
		//生成动态股东表
		dynamicGenerateGDTable();
		
	}
	
	/**
	 * 股东贡献值清空按钮
	 * 
	 * @time 2018年1月14日
	 * @param event
	 */
	public void clearDataAction(ActionEvent event) {
		//清空数据来源
		dataList.clear();
		
		//清空总和表
		if(tableGDSum.getItems() != null)
			tableGDSum.getItems().clear();
		
		//清空动态表
		contributionHBox.getChildren().clear();
		
		//清空其他数据
		dangtianProfits.setText("0.0");
		gudongProfitsRateMap.clear();
		gudongProfitsValueMap.clear();
	}
	
	/**
	 * 输入人次利润比后回车直接查询数据
	 * 
	 * @time 2018年1月20日
	 * @param even
	 */
	public void renciEnterAction(ActionEvent even) {
		GDRefreshBtn.fire();
	}
}
