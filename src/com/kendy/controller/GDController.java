package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.kendy.db.DBUtil;
import com.kendy.entity.GDInputInfo;
import com.kendy.entity.GudongRateInfo;
import com.kendy.entity.Player;
import com.kendy.entity.Record;
import com.kendy.service.MoneyService;
import com.kendy.service.TeamProxyService;
import com.kendy.util.CollectUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

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
import javafx.scene.control.cell.TextFieldTableCell;
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
	@FXML private TableColumn<GudongRateInfo,String> gudongProfit;//股东利润占比
	
	//*************************************************************************//
	@FXML private Label computeTotalProfit;//计算总利润
	@FXML private Label changciTotalProfit;//场次总利润
	@FXML private Label difTotalProfit;//总利润差值 （为实时开销与桌费的差值）
	
	@FXML private TextField personTime_ProfitRate_Text;//1人次等于多少利润
	@FXML private TextField gd_currage_money;//股东奖励值
	
	@FXML private HBox contributionHBox;//动态生成各个股东贡献值的区域
	@FXML private Button clearBtn;
	@FXML private Button GDRefreshBtn;
	//*************************************************************************//股东原始股
	@FXML private TableView<GDInputInfo> tableYSGu;
	@FXML private TableColumn<GDInputInfo,String> YS_gudongName;//股东名称
	@FXML private TableColumn<GDInputInfo,String> YS_rate;//占比
	@FXML private TableColumn<GDInputInfo,String> YS_value;//数值
	//*************************************************************************//股东奖励股
	@FXML private TableView<GDInputInfo> tableEncourageGu;
	@FXML private TableColumn<GDInputInfo,String> Encourage_gudongName;//股东名称
	@FXML private TableColumn<GDInputInfo,String> Encourage_rate;//占比
	@FXML private TableColumn<GDInputInfo,String> Encourage_value;//数值
	
	//*************************************************************************//客服占股
	@FXML private TableView<GDInputInfo> tablekfGu;
	@FXML private TableColumn<GDInputInfo,String> KF_gudongName;//股东名称
	@FXML private TableColumn<GDInputInfo,String> KF_rate;//占比
	@FXML private TableColumn<GDInputInfo,String> KF_value;//数值
	
	
	private static final String UN_KNOWN = "未知";
	
	
	//数据来源:当天某俱乐部的数据
	private static List<Record> dataList = new ArrayList();
	
	//将dataList转成特定数据结构
	//{股东ID:{团队ID:List<Record}}
	private static  Map<String,Map<String,List<Record>>> gudongTeamMap = new HashMap<>();
	
	//用于计算每个团队与股东客的利润（因为总利润 = 人次 + 团队+股东客 + 联盟桌费）
	//{团队ID:List<Record>}
	private static Map<String,List<Record>> teamMap = new HashMap<>();
	
	//{股东ID:股东利润占比} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsRateMap = new HashMap<>();
	//{股东ID:股东利润值} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsValueMap = new HashMap<>();
	
	public static void initData() {
		//初始化dataList,根据当前的俱乐部去取是否有问题？
		initDataList();
		
		//将原始数据转换成特定的数据结构
		initGudongTeamMap();
		initTeamMap();
	}
	
	
	/**
	 * 初始化dataList
	 * 备注：获取当天保存到数据库的当前俱乐部记录List<Record>
	 * @time 2018年1月18日
	 */
	private static void initDataList() {
		String currentClubId = PropertiesUtil.readProperty("clubId");
		if(!StringUtil.isAnyBlank(currentClubId)) {
			List<Record> list = DBUtil.getRecordsByClubId(currentClubId);
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
	 * 将原始数据转换成特定的数据结构
	 * {团队ID:List<Record}
	 * 
	 * @time 2018年1月19日
	 */
	private static void initTeamMap() {
		if(CollectUtil.isNullOrEmpty(dataList)) return;
		teamMap = 
		dataList.stream()
			    .collect(
			    		Collectors.groupingBy(info -> StringUtil.nvl(info.getTeamId(),UN_KNOWN)));//按团队分

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
		String playerId = record.getPlayerId();
		return getGudongByPlayerId(playerId);
	}
	
	/**
	 * 设置计算总利润与场次总利润的差值
	 * @time 2018年1月25日
	 */
	public void refreshDifTatalValue() {
		String computeTotalProfitVal = computeTotalProfit.getText();
		//获取场次信息中的总利润(从锁定的数据中获取)
		String changciTotalProfitVal = getLastProfit();
		changciTotalProfit.setText(NumUtil.digit0(changciTotalProfitVal));
		//计算差值 
		Double difProfitVal = NumUtil.getNum(changciTotalProfitVal) - NumUtil.getNum(computeTotalProfitVal);
		difTotalProfit.setText(NumUtil.digit0(difProfitVal));
	}
	
	/**
	 * 从锁定的数据中获取最后的总利润
	 * 
	 * @time 2018年1月25日
	 * @return
	 */
	private String getLastProfit() {
		String lockedProfit = MyController.getChangciTotalProfit();
		return StringUtil.nvl(lockedProfit, "0.0");
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
			computeTotalProfit.setText(NumUtil.digit0(totalProfits));
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
	 * 获取总利润
	 * 股东的总利润 = 人次 + 团队+股东客 + 联盟桌费
	 */
	public  Double getTotalProfits() {
		Double totalProfits = 0d;
		
		//获取人次利润
		int size = teamMap.values().stream().map(List::size).reduce(Integer::sum).get();
		Double renciProfit = NumUtil.getNumTimes(size+"", getRenci());
		totalProfits += renciProfit;
		
		//获取团队(服务费) and 获取股东客
		for(Map.Entry<String, List<Record>> entry : teamMap.entrySet()) {
			String teamId = entry.getKey();
			List<Record> teamList = entry.getValue();
			if("公司".equals(teamId)) {
				Double companyProfit = getHelirun(teamList);
				totalProfits += companyProfit;
			}else {
				String teamFWF = TeamProxyService.getTeamFWF_GD(teamId,teamList);//获取服务费（根据锁定的存到数据库中的数据）
				totalProfits += NumUtil.getNum(teamFWF);
			}
		} 
		
		//获取联盟桌费
		Double LM1Zhuofei = LMController.getLM1TotalZhuofei() *(-1);
		totalProfits += LM1Zhuofei;
		
		return totalProfits;
	}
	
	
	/**
	 * 计算多行战绩的利润(可以计算所有战绩的利润)
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
		Optional<String> findFirst = dataList.stream().filter(record->record.getPlayerId()==playerId).findFirst().map(Record::getTeamId);
		findFirst.orElse(UN_KNOWN);
		return findFirst.get();
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//主表
		KF_gudongName.setEditable(true);
		MyController.bindCellValue(gudongName, gudongProfit);
		gudongProfit.setCellFactory(MyController.getColorCellFactory(new GudongRateInfo()));
		
		//绑定数据（股东原始股表\股东奖励股表\客服占股表）
		bind3TableColumns();
		
		//三个股东表设置模拟的空行数据
		setTableMockData(tableYSGu, 9);//12表示前多少行是模拟数据，可以编辑
		setTableMockData(tableEncourageGu,9);
		setTableMockData(tablekfGu,10);
		
	}
	
	
	/**
	 * 绑定三个表的数据
	 * 
	 * @time 2018年1月27日
	 */
	private void bind3TableColumns() {
		//股东原始股表
		tableYSGu.setEditable(true);
		YS_gudongName.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("type") );
		YS_gudongName.setCellFactory(TextFieldTableCell.forTableColumn());
		YS_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		YS_rate.setCellFactory(TextFieldTableCell.forTableColumn());
		YS_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
		
		//股东奖励股表
		tableEncourageGu.setEditable(true);
		Encourage_gudongName.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("type") );
		Encourage_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		Encourage_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
		
		//客服占股表
		tablekfGu.setEditable(true);
		KF_gudongName.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("type") );
		KF_gudongName.setCellFactory(TextFieldTableCell.forTableColumn());
		KF_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		KF_rate.setCellFactory(TextFieldTableCell.forTableColumn());
		KF_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
	}
	
	/**
	 * 三个表模拟数据
	 * 
	 * @time 2018年1月26日
	 * @param tables
	 */
	private void setTableMockData(TableView<GDInputInfo> table,int mockRows) {
		ObservableList<GDInputInfo> obList = FXCollections.observableArrayList();
		if(table.getItems() !=null && !table.getItems().isEmpty()) 
			return;
		if(mockRows != 9) {
			for(int i=1; i<=mockRows; i++) {
				obList.add(new GDInputInfo("股东"+i,getRandomRate()));
			}
		}
		table.setItems(obList);
		table.refresh();
	}
	
	private String getRandomRate() {
		Random random = new Random();
		String randomString = random.nextInt(10)+"%";
		return randomString;
	}
	
	
	
	/**
	 * 获取人次  1人次 = XX利润
	 */
	public String getRenci() {
		String renci = StringUtil.nvl(personTime_ProfitRate_Text.getText(),"0.0");
		return renci;
	}
	

	
	/**
	 * 生成动态股东表
	 * 
	 * @time 2018年1月20日
	 */
	private void dynamicGenerateGDTable() {
		
		//股东列表
		Set<String> gudongSet = gudongTeamMap.keySet();
		
		 TableView<GudongRateInfo> table;
	      
		ObservableList<GudongRateInfo> obList= FXCollections.observableArrayList();
        for(String gudongName : gudongSet) {
        	table = new TableView<GudongRateInfo>();
	 
        	//设置列
	        TableColumn firstNameCol = new TableColumn("股东"+gudongName);
	        firstNameCol.setStyle("-fx-alignment: CENTER;");
	        firstNameCol.setPrefWidth(110);
	        firstNameCol.setCellValueFactory(
	                new PropertyValueFactory<GudongRateInfo, String>("gudongName"));
	 
	        TableColumn lastNameCol = new TableColumn("0%");
	        lastNameCol.setStyle("-fx-alignment: CENTER;");
	        lastNameCol.setPrefWidth(95);
	        lastNameCol.setCellValueFactory(
	        		new PropertyValueFactory<GudongRateInfo, String>("gudongProfit"));
	        lastNameCol.setCellFactory(MyController.getColorCellFactory(new GudongRateInfo()));
	        table.setPrefWidth(210);
	        
	        TableColumn tempValCol = new TableColumn("0");
	        tempValCol.setStyle("-fx-alignment: CENTER;");
	        tempValCol.setPrefWidth(60);
	        tempValCol.setCellValueFactory(
	                new PropertyValueFactory<GudongRateInfo, String>("description"));
	        tempValCol.setCellFactory(MyController.getColorCellFactory(new GudongRateInfo()));
	        table.setPrefWidth(210+60);
	        
	        table.getColumns().addAll(firstNameCol, lastNameCol,tempValCol);
	 
	        //设置数据
	        //{团队ID:List<Record}
	        Map<String,List<Record>> teamMap = gudongTeamMap.get(gudongName);
	        setDynamicTableData(table,teamMap,gudongName);
	        //往左边的股东表中添加记录
	        setDataToSumTable(table);
	        
	        contributionHBox.setSpacing(5);
	        contributionHBox.setPadding(new Insets(0, 0, 0, 0));
	        contributionHBox.getChildren().addAll(table);
        }
	}
	
	/**
	 * 设置主表数据
	 * 每生成一个动态表就往左边的股东表中添加记录
	 * 
	 * @time 2018年1月20日
	 */
	public  void setDataToSumTable(TableView<GudongRateInfo> dynamicTable) {
		String gudongName = dynamicTable.getColumns().get(0).getText();
		String gudongValue = dynamicTable.getColumns().get(2).getText();
		String gudongRate = dynamicTable.getColumns().get(1).getText();
		
		tableGDSum.getItems().add(new GudongRateInfo(gudongName,gudongValue,gudongRate));
		tableGDSum.refresh();
	}
	
	/**
	 * 设置单个动态表的数据
	 * 注意：
	 * 		1、公司的计入对应的股东客；
	 * 		2、团队服务费问题：目前是直接引用代理查询表的数据，但最好重新计算！！！TODO
	 * 		3、后期加入联盟桌费！！！！TODO
	 * 
	 * @time 2018年1月20日
	 * @param table  单个动态表
	 * @param teamMap 单个动态表的团队数据，不包括联盟
	 */
	private  void setDynamicTableData(TableView<GudongRateInfo> table,Map<String,List<Record>> teamMap,String gudong) {
		//设置股东的人次
		setGudongRenci(table,teamMap);
		
		//Loop设置单个动态表的数据(团队部分)
		for(Map.Entry<String, List<Record>> teamEntry : teamMap.entrySet()) {
        	String teamId = teamEntry.getKey();
        	List<Record> teamList = teamEntry.getValue();
        	if("公司".equals(teamId)) {
        		setDynamicTableData_team_company_part(table,teamId,teamList,gudong);
        	}else {
        		setDynamicTableData_team_not_comanpy_part(table,teamId,teamList,gudong);
        	}
        }
		//设置单个动态表的数据(联盟部分)
		setDynamicTableData_team_part(table,gudong);
		
		//修改该表的利润占比百分比
		setColumnSum(table);
	}
	
	/**
	 * 修改该表的利润占比百分比
	 * 
	 * @time 2018年1月27日
	 * @param table
	 */
	private void setColumnSum(TableView<GudongRateInfo> table) {
		if(TableUtil.isNullOrEmpty(table)) {
			table.getColumns().get(1).setText("0%");
			table.getColumns().get(2).setText("0");
			return;
		}
		//占比总和
		Double rateSum = table.getItems().stream()
			.map(GudongRateInfo::getGudongProfit)
			.map(rate -> { System.out.println(rate+"==="+NumUtil.getNumByPercent(rate)); return NumUtil.getNumByPercent(rate);})
			.reduce(Double::sum)
			.get();
		table.getColumns().get(1).setText(NumUtil.getPercentStr(rateSum));
		//具体数值总和
		Integer valSum = table.getItems().stream()
				.map(GudongRateInfo::getDescription)
				.map(val -> { return NumUtil.getNum(val);})
				.reduce(Double::sum)
				.get()
				.intValue();
		table.getColumns().get(2).setText(valSum+"");
		table.refresh();
		
	}
	
	/**
	 * 计算每个股东的人次
	 * 计算公式：人次总值 = 1人次的利润值  * 人次
	 * 
	 * @time 2018年1月25日
	 * @param table 需要改变的表格
	 * @param teamMap 用于计算生活中的人次
	 */
	private void setGudongRenci(TableView<GudongRateInfo> table,Map<String,List<Record>> teamMap) {
		//整个股东的所有人次（生活）
		Long gudongRenciCount = teamMap.values().stream().collect(Collectors.summarizingInt(l->l.size())).getSum();
		Double teamRenci = gudongRenciCount * NumUtil.getNum(getRenci());
		Double teamRenci_Double =  teamRenci / getComputeTotalProfit(); 
		String teamRenciStr = NumUtil.getPercentStr(teamRenci_Double);
		table.getItems().add(new GudongRateInfo("人次",teamRenciStr,teamRenci.intValue()+""));
		table.refresh();
	}

	
	
	/**
	 * 设置单个动态表的数据(联盟桌费部分)
	 * 
	 * @time 2018年1月21日
	 * @param table
	 * @param gudong
	 */
	private  void setDynamicTableData_team_part(TableView<GudongRateInfo> table,String gudong) {
		Double LM1Zhuofei = LMController.getLM1TotalZhuofei(gudong) *(-1);
		Double LM1Zhuofei_Double =  LM1Zhuofei / getComputeTotalProfit(); 
		String LM1ZhuofeiStr = NumUtil.getPercentStr(LM1Zhuofei_Double);
		table.getItems().add(new GudongRateInfo("联盟桌费",LM1ZhuofeiStr, LM1Zhuofei.intValue()+""));
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
	private  void setDynamicTableData_team_not_comanpy_part(TableView<GudongRateInfo> table,String teamId, List<Record> teamList,String gudong) {
		//获取团队服务费
		String teamFWF = TeamProxyService.getTeamFWF_GD(teamId,teamList);//获取服务费（根据锁定的存到数据库中的数据）
		
		//计算团队占比      公式 = (人次 + 团队服务费) / 当天总利润 
		Double teamRate_Double =  NumUtil.getNum(teamFWF) / getComputeTotalProfit(); 
		String teamRateStr = NumUtil.getPercentStr(teamRate_Double);
		table.getItems().add(new GudongRateInfo(getFinalTeamId(teamId,gudong),teamRateStr,teamFWF));
		
		table.refresh();
		
		
	}
	
	
	/**
	 * 设置单个动态表的数据(团队中的公司部分)
	 * 公司利润计算公式：收回水+水后险，这里计成合利润
	 * 
	 */
	private  void setDynamicTableData_team_company_part(TableView<GudongRateInfo> table,String teamId, List<Record> teamList,String gudong) {
		Double companyProfit = getHelirun(teamList);
		
		//计算团队中公司的占比      公式 = sum（收回水+水后险） / 计算总利润 
		//Double teamRenci = teamList.size() * NumUtil.getNum(getRenci());
		Double companyRate_Double = companyProfit / getComputeTotalProfit(); 
		String companyRateStr = NumUtil.getPercentStr(companyRate_Double);
		table.getItems().add(new GudongRateInfo(getFinalTeamId(teamId,gudong),companyRateStr,companyProfit.intValue()+""));
		
		table.refresh();
	}
	
	
	/**
	 * 获取计算总利润值
	 * @time 2018年1月27日
	 * @return
	 */
	public Double getComputeTotalProfit() {
		return NumUtil.getNum(computeTotalProfit.getText()); 
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

		//生成动态股东表
		dynamicGenerateGDTable();
		
		//刷新总利润对比数据
		refreshDifTatalValue();
		
		//股东奖励值设置数据
		setGudongMoneyBelow();
		
		
	}
	
	/**
	 * 股东奖励值设置数据
	 * 
	 * @time 2018年1月28日
	 */
	private void setGudongMoneyBelow() {
		//1、设置客服股数据
		setTable_KFGu_data();
		
		//2、设置第一次股东原始股数据
		setTable_YSGu_data_first();
		
		//3、设置股东奖励股数据
		setTable_JLGu_data();
	}
	
	/**
	 * 设置股东奖励股数据
	 */
	private void setTable_JLGu_data() {
		ObservableList<GDInputInfo> obList = FXCollections.observableArrayList();
		//股东列表总和：除了银河股东,用于获取各股东的比例（比拼值）
		Double sum = tableGDSum.getItems().stream().filter(info->!info.getGudongName().contains("银河"))
				.map(info->NumUtil.getNum(info.getGudongProfit())).reduce(Double::sum).get();
				
		//获取可分配的奖励池
		Double curragePool = getJLPoolAvailable();
		
		//
		//计算各股东的奖励金额
		tableGDSum.getItems().stream()
			.filter(info->!info.getGudongName().contains("银河"))
			.map(info -> {
				Double rate =  NumUtil.getNum(info.getGudongProfit()) / sum ;
				Double currageMoney = curragePool * rate;
				return new GDInputInfo(
						info.getGudongName(), 
						NumUtil.getPercentStr(rate),
						NumUtil.digit0(currageMoney)
						);
			}).forEach(info -> obList.add(info));
		
		//设置数据并刷新表
		tableEncourageGu.setItems(obList);
		tableEncourageGu.refresh();
	}
	
	/**
	 * 获取可分配的奖励池
	 * 奖励池 = （总利润 - 原始股 — 客服股）* 可分配比例
	 * 奖励池=（总利润 - 财物分红 - 奖罚 - 银河股东）*70%
	 * 可分配比例默认是70%
	 * 
	 * @time 2018年1月28日
	 * @return
	 */
	private Double getJLPoolAvailable() {
		//总利润
		Double totalProfit = getComputeTotalProfit();
		//原始股
		Double totalYSGu = tableYSGu.getItems().stream().map(info->NumUtil.getNum(info.getValue())).reduce(Double::sum).get();
		//客服股
		Double totalKFGu = tablekfGu.getItems().stream().map(info->NumUtil.getNum(info.getValue())).reduce(Double::sum).get();
		//可分配比例,即股东奖励值
		Double currageRate = getCurrageRate();
		//奖励池
		Double curragePool = (totalProfit - totalYSGu - totalKFGu) * currageRate;
		
		return curragePool;
	}
	
	/**
	 * 可分配比例,即股东奖励值比
	 * @time 2018年1月28日
	 * @return
	 */
	private Double getCurrageRate() {
		String currageRate = gd_currage_money.getText();
		if(StringUtil.isBlank(currageRate)) {
			return 0.7d;
		}else {
			if(currageRate.contains("%"))
				return NumUtil.getNumByPercent(currageRate);
			else
				return 0.7d;
		}
	}
	
	/**
	 * 设置第一次股东原始股
	 * 
	 * @time 2018年1月28日
	 */
	private void setTable_YSGu_data_first() {
		ObservableList<GDInputInfo> obList = FXCollections.observableArrayList();
		
		//获取银河股东的利润
		Double yinheProfit = getYinheProfit();
		
		//股东列表：除了银河股东
		if(tableYSGu.getItems()==null || tableYSGu.getItems().size() == 0 || StringUtil.isBlank(tableYSGu.getItems().get(0).getType())) {
			tableGDSum.getItems().stream()
					.filter(info->!info.getGudongName().contains("银河"))
					.map(info -> new GDInputInfo(info.getGudongName(),"",""))
					.forEach(info-> {
						obList.add(info);
					});
			tableYSGu.setItems(obList);
			tableYSGu.refresh();
		}
		
		
		//根据股东比例计算各股东的原始利润
		tableYSGu.getItems().forEach(info -> {
			if(!StringUtil.isAnyBlank(info.getType(),info.getRate()))
				info.setValue(NumUtil.digit0(NumUtil.getNumByPercent(info.getRate()) * yinheProfit));
			else {
				//info.setType("");
				//info.setRate("");
				info.setValue("");
			}
		});
		
		//刷新表
		tableYSGu.refresh();
	}
	
	/**
	 * 获取银河股东的利润
	 * 
	 * @time 2018年1月28日
	 * @return
	 */
	private Double getYinheProfit() {
		GudongRateInfo gudongRateInfo = tableGDSum.getItems().stream().filter(info->info.getGudongName().contains("银河")).findFirst().get();
		return NumUtil.getNum(gudongRateInfo.getGudongProfit());
	}
	
	/**
	 * 设置客服股数据
	 * 
	 * @time 2018年1月28日
	 */
	private void setTable_KFGu_data() {
		tablekfGu.getItems().stream()
			.forEach(info->{
				if(!StringUtil.isAnyBlank(info.getType(),info.getRate()))
					info.setValue(NumUtil.digit0(NumUtil.getNumByPercent(info.getRate()) * getComputeTotalProfit()));
				else {
					info.setType("");
					info.setRate("");
					info.setValue("");
				}
			});
		tablekfGu.refresh();
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
		computeTotalProfit.setText("0.0");
		changciTotalProfit.setText("0.0");
		difTotalProfit.setText("0.0");
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
