package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.db.DBUtil;
import com.kendy.entity.ClubZhuofei;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.GDDetailInfo;
import com.kendy.entity.GDInputInfo;
import com.kendy.entity.GudongRateInfo;
import com.kendy.entity.KaixiaoInfo;
import com.kendy.entity.Player;
import com.kendy.entity.ProfitInfo;
import com.kendy.entity.Record;
import com.kendy.service.MoneyService;
import com.kendy.service.TeamProxyService;
import com.kendy.util.CollectUtil;
import com.kendy.util.MapUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

import application.DataConstans;
import application.Main;
import application.MyController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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

	private static Logger log = Logger.getLogger(GDController.class);
	
	
	//股东贡献值主表
	@FXML private TableView<GudongRateInfo> tableGDSum;
	@FXML private TableColumn<GudongRateInfo,String> gudongName;//股东名称
	@FXML private TableColumn<GudongRateInfo,String> gudongProfit;//股东利润占比
	
	//*************************************************************************//
	@FXML private Label computeTotalProfit;//计算总利润
	@FXML private Label changciTotalProfit;//场次总利润
	@FXML private Label difTotalProfit;//总利润差额 （为实时开销与桌费的差值）
	
	@FXML private TextField personTime_ProfitRate_Text;//1人次等于多少利润
	@FXML private Label totalRenciProfitText;//总人次利润
	@FXML private TextField gd_currage_money;//股东奖励值比例，默认70%
	@FXML private Label gd_currage_money_value;//总股东奖励值
	
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
	@FXML private TableColumn<GDInputInfo,String> KF_salary;//底薪
	//*************************************************************************//明细表
	@FXML private TableView<GDDetailInfo> tableGDDetail;
	@FXML private TableColumn<GDDetailInfo,String> name;
	@FXML private TableColumn<GDDetailInfo,String> ysgu;
	@FXML private TableColumn<GDDetailInfo,String> jl3;
	@FXML private TableColumn<GDDetailInfo,String> jl7;
	@FXML private TableColumn<GDDetailInfo,String> total;
	@FXML private TableColumn<GDDetailInfo,String> salary;
	
	
	public static  boolean has_quotar_oneKey = false;
	
	public static final String TABLE_KF_DATA_KEY = "table_kf_data";
	
	public static final Integer KF_SIZE = 20;
	
	private static final String UN_KNOWN = "未知";
	
	
	//数据来源:某俱乐部的数据
	private static List<Record> dataList = new ArrayList();
	
	//数据来源:股东开销的总数据来源
	private static List<KaixiaoInfo> gudongKaixiao_dataList = new ArrayList();
	
	//将dataList转成特定数据结构
	//{股东ID:{团队ID:List<Record}}
	private static  Map<String,Map<String,List<Record>>> gudongTeamMap = new HashMap<>();
	
	//获取每个股东的开销总和
	private static  Map<String,List<KaixiaoInfo>> gudongKaixiaoMap = new HashMap<>();
	
	//用于计算每个团队与股东客的利润（因为总利润 = 人次 + 团队+股东客 + 联盟桌费）
	//{团队ID:List<Record>}
	private static Map<String,List<Record>> teamMap = new HashMap<>();
	
	//{股东ID:股东利润占比} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsRateMap = new HashMap<>();
	//{股东ID:股东利润值} 注意：找不到股东的放在股东未知中
	private static Map<String,String> gudongProfitsValueMap = new HashMap<>();
	
	//明细数据表中的缓存数据
	private static Map<String,GDDetailInfo> detailMap = new HashMap<>();
	
	public static void initData() {
		//初始化dataList,根据当前的俱乐部去取是否有问题？
		initDataList();
		
		//将原始数据转换成特定的数据结构
		initGudongTeamMap();
		initTeamMap();
		
		// 初始化总开销数据
		initGudongKaixiaoDataList();
		initGudongKaixiaoMap();
	}
	
	
	/**
	 * 初始化dataList
	 * 备注：获取当天保存到数据库的当前俱乐部记录List<Record>
	 * @time 2018年1月18日
	 */
	private static void initDataList() {
		//String currentClubId = PropertiesUtil.readProperty("clubId");
		String currentClubId = MyController.currentClubId.getText();
		log.info("GDController's clubId:"+currentClubId);
		if(!StringUtil.isAnyBlank(currentClubId)) {
			List<Record> list = DBUtil.getRecordsByClubId(currentClubId);
			if(CollectUtil.isHaveValue(list)) {
				dataList = list;
			}
		}
	}
	
	/**
	 * 初始化GudongKaixiaoDataList
	 * 
	 * @time 2018年2月21日
	 */
	private static void initGudongKaixiaoDataList() {
		List<KaixiaoInfo> get_all_gudong_kaixiao = DBUtil.get_all_gudong_kaixiao();
		if(CollectUtil.isHaveValue(get_all_gudong_kaixiao)) {
			gudongKaixiao_dataList = get_all_gudong_kaixiao;
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
						Collectors.groupingBy(info -> { 
							if(StringUtil.isBlank(info.getTeamId())){
								System.out.println("========"+info);
							} 
						return StringUtil.nvl(info.getTeamId(),UN_KNOWN);})
		));//再按团队分
		
		//add 2018-03-01 
		ObservableList<String> gudongList = MyController.getGudongList();
		for(String gudong : gudongList) {
			if(!gudongTeamMap.keySet().contains(gudong)) {
				gudongTeamMap.put(gudong, new HashMap<>());
			}
		}
	}
	
	/**
	 * 将原始数据转换成特定的数据结构
	 * {股东ID:List<KaixiaoInfo>}
	 * 
	 * @time 2018年1月19日
	 */
	private static void initGudongKaixiaoMap() {
		if(CollectUtil.isNullOrEmpty(gudongKaixiao_dataList)) return;
		gudongKaixiaoMap = 
				gudongKaixiao_dataList.stream()
			    .collect(
			    		Collectors.groupingBy(info -> StringUtil.nvl(((KaixiaoInfo)info).getKaixiaoGudong(),UN_KNOWN)));//按团队分
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
	 * 设置计算总利润与场次总利润的差额
	 * @time 2018年1月25日
	 */
	public void refreshDifTatalValue() {
		String computeTotalProfitVal = computeTotalProfit.getText();
		//获取场次信息中的总利润(从锁定的数据中获取)
		String changciTotalProfitVal = getLastProfit();
		changciTotalProfit.setText(NumUtil.digit0(changciTotalProfitVal));
		//计算差值 
		Double difProfitVal = NumUtil.getNum(changciTotalProfitVal) - NumUtil.getNum(computeTotalProfitVal);
		difTotalProfit.setText(NumUtil.digit2(difProfitVal.toString()));
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
		if(CollectUtil.isNullOrEmpty(dataList)) return;
		Map<String,List<Record>> gudongRecordList = dataList.stream()
				.collect(Collectors.groupingBy(record -> getGudongByPlayerId((Record)record)));
		//计算总利润
		Double totalProfits = getTotalProfits();
		if(Double.compare(totalProfits, 0) == 0) {
			ShowUtil.show("总利润计算为0", 2);
			return;
		}else {
			computeTotalProfit.setText(NumUtil.digit2(totalProfits.toString()));
		}
		//每个股东的利润占比
		gudongRecordList.forEach((gudong,eachRecordList) -> {
			Double eachGudong =  getHelirun(eachRecordList);//每个股东的利润
			String gudongRate = NumUtil.getPercentStr( divide(eachGudong, totalProfits) );//每个股东的利润占比
			//System.out.println(String.format("股东%s占比%s", gudong,gudongRate));
			gudongProfitsValueMap.put(gudong, NumUtil.digit0(eachGudong));//股东值占比
			gudongProfitsRateMap.put(gudong, gudongRate);//股东值
		});
		
	}
	
	/**
	 * 获取总利润
	 * 股东的总利润 =  团队(团队利润+团队服务费)+股东客 + 联盟桌费 + 总开销
	 */
	public  Double getTotalProfits() {
		Double totalProfits = 0d;
		
		//获取团队(服务费) and 获取股东客
		for(Map.Entry<String, List<Record>> entry : teamMap.entrySet()) {
			String teamId = entry.getKey();
			List<Record> teamList = entry.getValue();
			if("公司".equals(teamId)) {
				Double companyProfit = getHelirun(teamList);
				totalProfits += companyProfit;
			}else {
				String teamFWF = TeamProxyService.getTeamFWF_GD(teamId,teamList);//获取服务费（根据锁定的存到数据库中的数据）
				totalProfits += (NumUtil.getNum(teamFWF) + getTeamProfit(teamList));
			}
		} 
		
		//获取联盟桌费
		Double LM1Zhuofei = getLM1TotalZhuofei() *(-1);
		totalProfits += LM1Zhuofei;
		
		// 获取总开销
		Double totalKaixiao = getTotalGudongKaixiao();
		totalProfits += totalKaixiao;
		
		return totalProfits;
	}
	
	/**
	 * 获取联盟1的所有桌费
	 * 问题：如果用户在同一天插入相同的记录，存在被覆盖一条的隐患（TODO）
	 * 
	 * @time 2018年2月11日
	 * @return
	 */
	private static Double getLM1TotalZhuofei() {
		List<ClubZhuofei> LM1_all_club_zhuofei = DBUtil.get_LM1_all_club_zhuofei();
		Double totalZhuofei = 
				LM1_all_club_zhuofei
				.stream()
				.filter(info->"联盟1".equals(info.getLmType()))
				.map(ClubZhuofei::getZhuofei)
				.map(NumUtil::getNum)
				.reduce(Double::sum)
				.orElseGet(()->0d);
		return  totalZhuofei;
	}
	private static Double getLM1TotalZhuofei(String gudong) {
		List<ClubZhuofei> LM1_all_club_zhuofei = DBUtil.get_LM1_all_club_zhuofei();
		Double totalZhuofei = 
				LM1_all_club_zhuofei
				.stream()
				.filter(info->"联盟1".equals(info.getLmType()) && info.getGudong().equals(gudong))
				.map(ClubZhuofei::getZhuofei)
				.map(NumUtil::getNum)
				.reduce(Double::sum)
				.orElseGet(()->0d);
		return  totalZhuofei;
	}
	
	
	/**
	 * 获取联盟1的所有桌费
	 * 问题：如果用户在同一天插入相同的记录，存在被覆盖一条的隐患（TODO）
	 * 
	 * @time 2018年2月11日
	 * @return
	 */
	private static Double getTotalGudongKaixiao() {
		Double totalGudongKaixiao = 
				gudongKaixiao_dataList
				.stream()
				.map(KaixiaoInfo::getKaixiaoMoney)
				.map(NumUtil::getNum)
				.reduce(Double::sum)
				.orElseGet(()->0d);
		return  totalGudongKaixiao;
	}
	private static Double getKaixiaoByGudong(String gudong) {
		Double gudongKaixiao = 
				gudongKaixiao_dataList
				.stream()
				.filter(info -> gudong.equals(info.getKaixiaoGudong()))
				.map(KaixiaoInfo::getKaixiaoMoney)
				.map(NumUtil::getNum)
				.reduce(Double::sum)
				.orElseGet(()->0d);
		return  gudongKaixiao;
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
	 * 获取团队（非公司）的个人利润（区别于合利润）
	 * 团队个人利润= 收回水+出回水 + 水后险 - 回保 
	 * 
	 * @time 2018年1月31日
	 * @param record
	 * @return
	 */
	public static Double getTeamPersonProfit(final Record record) {
		String playerId = record.getPlayerId();
		String teamId = getTeamIdWithUperCase(playerId);
		String zhanji = record.getScore();
		String baoxian = record.getInsuranceEach();
		String shishou = MoneyService.getShiShou(record.getScore());
		String chuHuishui = NumUtil.digit1(MoneyService.getChuhuishui(zhanji, teamId));
		String shuihouxian = NumUtil.digit1((-1)*Double.valueOf(baoxian)*0.975+"");
		String shouHuishui = NumUtil.digit1(Math.abs(Double.valueOf(zhanji))*0.025+"");
		String baohui = NumUtil.digit1(MoneyService.getHuiBao(baoxian,teamId));
//		Double personProfit = NumUtil.getNum(NumUtil.getSum(shouHuishui , chuHuishui , shuihouxian )) - Math.abs(NumUtil.getNum(baohui));
		Double personProfit = NumUtil.getNum(NumUtil.getSum(shouHuishui , chuHuishui , shuihouxian )) - (NumUtil.getNum(baohui));
		return  personProfit;
	}
	
	/**
	 * 根据玩家ID获取玩家所属的团队ID
	 * 
	 * @time 2018年1月20日
	 * @param playerId
	 * @return
	 */
	public static String getTeamIdWithUperCase(String playerId) {
		String findFirst = dataList.stream()
				.filter(record->record.getPlayerId()==playerId)
				.findFirst()
				.map(Record::getTeamId)
				.orElseGet(()->UN_KNOWN);
		return findFirst;
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
		
		//明细表
		MyController.bindCellValue(name, ysgu, jl3, jl7, total, salary);
		
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
		YS_gudongName.setStyle("-fx-alignment: CENTER;");
		YS_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		YS_rate.setCellFactory(TextFieldTableCell.forTableColumn());
		YS_rate.setStyle("-fx-alignment: CENTER;");
		YS_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
		YS_value.setStyle("-fx-alignment: CENTER;");
		
		
		//股东奖励股表
		tableEncourageGu.setEditable(true);
		Encourage_gudongName.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("type") );
		Encourage_gudongName.setStyle("-fx-alignment: CENTER;");
		Encourage_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		Encourage_rate.setStyle("-fx-alignment: CENTER;");
		Encourage_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
		Encourage_value.setStyle("-fx-alignment: CENTER;");
		
		//客服占股表
		tablekfGu.setEditable(true);
		KF_gudongName.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("type") );
		KF_gudongName.setCellFactory(TextFieldTableCell.forTableColumn());
		KF_gudongName.setStyle("-fx-alignment: CENTER;");
		KF_rate.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("rate") );
		KF_rate.setCellFactory(TextFieldTableCell.forTableColumn());
		KF_rate.setStyle("-fx-alignment: CENTER;");
		KF_value.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("value") );
		KF_value.setStyle("-fx-alignment: CENTER;");
		KF_salary.setCellValueFactory( new PropertyValueFactory<GDInputInfo,String>("description") );
		KF_salary.setCellFactory(TextFieldTableCell.forTableColumn());
		KF_salary.setStyle("-fx-alignment: CENTER;");
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
				obList.add(new GDInputInfo("客服"+i,getRandomRate()));
			}
		}
		table.setItems(obList);
		table.refresh();
	}
	
	private String getRandomRate() {
		Random random = new Random();
//		String randomString = random.nextInt(10)+"%";
		String randomString = "1%";
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
		List<String> _gudongList = new ArrayList<>(gudongSet);
		Collections.sort(_gudongList);
		
		 TableView<GudongRateInfo> table;
	      
		ObservableList<GudongRateInfo> obList= FXCollections.observableArrayList();
        for(String gudongName : _gudongList) {
        	table = new TableView<GudongRateInfo>();
	 
        	//设置列
	        TableColumn firstNameCol = new TableColumn("股东"+gudongName);
	        firstNameCol.setStyle("-fx-alignment: CENTER;");
	        firstNameCol.setPrefWidth(100);
	        firstNameCol.setCellValueFactory(
	                new PropertyValueFactory<GudongRateInfo, String>("gudongName"));
	 
	        TableColumn lastNameCol = new TableColumn("0%");
	        lastNameCol.setStyle("-fx-alignment: CENTER;");
	        lastNameCol.setPrefWidth(92);
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
	        List<KaixiaoInfo> kaixiaoList = gudongKaixiaoMap.get(gudongName);
	        setDynamicTableData(table,teamMap, kaixiaoList, gudongName);
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
	 * 		2、团队服务费问题：目前是直接引用代理查询表的数据，但最好重新计算！！！已经重新算了
	 * 		3、后期加入联盟桌费！！！！已经加入了
	 * 		4、后期加入股东开销
	 * 
	 * @time 2018年1月20日
	 * @param table  单个动态表
	 * @param teamMap 单个动态表的团队数据，不包括联盟
	 */
	private  void setDynamicTableData(TableView<GudongRateInfo> table,Map<String,List<Record>> teamMap,
			List<KaixiaoInfo> kaixiaoList, String gudong) {
		//设置股东的人次
		//setGudongRenci(table,teamMap);
		
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
		
		//设置单个动态表的数据(股东开销部分)
		setDynamicTableData_gudong_kaixiao_part(table,gudong);
		
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
			.map(rate -> {return NumUtil.getNumByPercent(rate);})
			.reduce(Double::sum)
			.get();
		table.getColumns().get(1).setText(NumUtil.getPercentStr(rateSum));
		//具体数值总和
		Double valSum = table.getItems().stream()
				.map(GudongRateInfo::getDescription)
				.map(val -> { return NumUtil.getNum(val);})
				.reduce(Double::sum)
				.get()
				.doubleValue();
		table.getColumns().get(2).setText(NumUtil.digit2(valSum.toString()));
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
		Double teamRenci_Double =  divide(teamRenci, getComputeTotalProfit_mins_totalSalary()); 
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
		Double LM1Zhuofei = getLM1TotalZhuofei(gudong) *(-1);
		Double LM1Zhuofei_Double =  NumUtil.getNumDivide(LM1Zhuofei , getComputeTotalProfit()); 
		String LM1ZhuofeiStr = NumUtil.getPercentStr(LM1Zhuofei_Double);
		table.getItems().add(new GudongRateInfo("联盟桌费",LM1ZhuofeiStr, LM1Zhuofei+""));
		table.refresh();
	}
	
	/**
	 * 设置单个动态表的数据(股东开销部分)
	 * 
	 * @time 2018年2月21日
	 * @param table
	 * @param gudong
	 */
	private  void setDynamicTableData_gudong_kaixiao_part(TableView<GudongRateInfo> table, String gudong) {
		Double gudongKaixiao = getKaixiaoByGudong(gudong) ; //*(-1);
		Double gudongKaixiao_Double =  NumUtil.getNumDivide(gudongKaixiao , getComputeTotalProfit()); 
		String gudongKaixiaoStr = NumUtil.getPercentStr(gudongKaixiao_Double);
		table.getItems().add(new GudongRateInfo("累计开销",gudongKaixiaoStr, gudongKaixiao+""));
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
		//获取团队利润
		Double teamPersonProfit = getTeamProfit(teamList);
		Double teamProfits = NumUtil.getNum(teamFWF) + teamPersonProfit;
		
		//计算团队占比      公式 = (人次 + 团队服务费) / 当天总利润 
		Double teamRate_Double =  NumUtil.getNumDivide(teamProfits , getComputeTotalProfit()); 
		String teamRateStr = NumUtil.getPercentStr(teamRate_Double);
		table.getItems().add(new GudongRateInfo(getFinalTeamId(teamId,gudong),teamRateStr,NumUtil.digit2(teamProfits+"")));
		
		table.refresh();
		
		
	}
	
	/**
	 * 获取团队利润
	 * 备注：小胖新增
	 * 团队个人利润= 收回水+出回水 + 水后险 - 回保 
	 * @time 2018年1月31日
	 * @param teamList
	 * @return
	 */
	private Double getTeamProfit(List<Record> teamList) {
		return CollectUtil.isNullOrEmpty(teamList) ? 0d : 
			teamList.stream().mapToDouble(item->getTeamPersonProfit(item)).sum();
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
		Double companyRate_Double = NumUtil.getNumDivide(companyProfit , getComputeTotalProfit()); 
		String companyRateStr = NumUtil.getPercentStr(companyRate_Double);
		table.getItems().add(new GudongRateInfo(getFinalTeamId(teamId,gudong),companyRateStr,companyProfit+""));
		
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
	 * 获取分配的总利润值（ = 计算总利润 - 客服工资 ）
	 * @time 2018年1月27日
	 * @return
	 */
	public Double getComputeTotalProfit_mins_totalSalary() {
		return NumUtil.getNum(computeTotalProfit.getText()) - get_KF_total_salary(); 
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
		detailMap.clear();
		
		//清空数据
		clearBtn.fire();
		//准备数据
		prepareAllData();
		if(CollectUtil.isNullOrEmpty(dataList)) {
			tableGDDetail.getItems().clear();
			tableYSGu.getItems().clear();
			tablekfGu.getItems().clear();
			tableEncourageGu.getItems().clear();
			ShowUtil.show("当前数据库无历史数据！",2);
			return;
		}

		//生成动态股东表
		dynamicGenerateGDTable();
		
		//刷新总利润对比数据
		refreshDifTatalValue();
		
		//刷新总人次利润数据
		setTotalRenciProfit();
		
		//股东奖励值设置数据
		setGudongMoneyBelow();
		
		//设置总彩池值
		setTotalPoolText();
		
	}
	
	/**
	 * 设置总彩池值
	 * 备注：在股东奖励值设置数据后执行此代码 
	 * 
	 * @time 2018年2月3日
	 */
	private void setTotalPoolText() {
		Double jlPool = getJLPoolAvailable();
		gd_currage_money_value.setText(NumUtil.digit0(jlPool.toString()));
	}
	
	
	/**
	 * 设置人次总利润（所有数据，包含银河股东）
	 * @time 2018年2月3日
	 */
	private void setTotalRenciProfit() {
		String eachRenciProfit = getRenci();
		Double totalRenciProfits = NumUtil.getNumTimes(eachRenciProfit, dataList.size()+"");
		totalRenciProfitText.setText(NumUtil.digit0(totalRenciProfits));
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
		
		//4、设置第二次股东原始股数据
		setTable_YSGu_data_Second();
		
		//5、将缓存中的数据设置到明细表中 
		setTable_detail();
	}
	
	
	/**
	 * 将缓存中的数据设置到明细表中
	 * 
	 * @time 2018年1月28日
	 */
	private void setTable_detail() {
		ObservableList<GDDetailInfo> obList = FXCollections.observableArrayList();
		detailMap.values().forEach(info -> {
			if(StringUtil.isBlank(info.getTotal())){
				info.setTotal(NumUtil.getSum(info.getYsgu(),info.getJl3(),info.getJl7(), info.getSalary()));
			}
			obList.add(info);
		});
		tableGDDetail.setItems(obList);
		tableGDDetail.refresh();
	}
	
	/**
	 * 设置第二次股东原始股数据
	 */
	private void setTable_YSGu_data_Second() {
		//获取剩下的30%
		Double currage_poor_rest = ( divide(getJLPoolAvailable(), getCurrageRate()) ) * (1 - getCurrageRate());
		//获取原始股比例
		tableYSGu.getItems().forEach(info-> {
			String oldVal = info.getValue();
			String rateString = StringUtil.nvl(info.getRate(),"0%");
			Double rate = NumUtil.getNumByPercent(rateString);
			//与第一次计算结果进行累积
			Double newVal = currage_poor_rest * rate;
			info.setValue(NumUtil.digit0(NumUtil.getNum(oldVal)+newVal));
			
			//缓存明细数据
			GDDetailInfo detail = detailMap.get(info.getType());
			detail.setJl3(NumUtil.digit0(newVal));
		});
		
		//设置数据并刷新表
		tableYSGu.refresh();
	}
	
	
	/**
	 * 获取非银河的股东的所有人次利润
	 * 
	 * @time 2018年2月2日
	 * @return
	 */
	private Double getRenciTotalProfit_not_yinhe() {
		
		//获取非银河的股东的所有人次
		Long count_not_company = dataList.stream().filter(info->{
				Player p = DataConstans.membersMap.get(info.getPlayerId());
				return (p != null && !p.getGudong().contains("银河")) ? true : false;
			}).count();
		
		Double renciProfit = NumUtil.getNumTimes(count_not_company.toString(), getRenci());
		return renciProfit;
	}
	
	/**
	 * 设置股东奖励股数据
	 */
	private void setTable_JLGu_data() {
		//股东及股东的记录数，一个记录数就是一个人次
		Map<String,List<Record>> gudongSizeMap = 
				dataList.stream()
				.collect(Collectors.groupingBy(//按股东分
						record -> getGudongByPlayerId((Record)record)));
		ObservableList<String> gudongList = MyController.getGudongList();
		for(String gudong : gudongList) {
			if(!gudongSizeMap.keySet().contains(gudong)) {
				gudongSizeMap.put(gudong, new ArrayList<>());
			}
		}
		
		//获取非银河的股东的所有人次利润
		Double renciProfit = getRenciTotalProfit_not_yinhe();
		
		ObservableList<GDInputInfo> obList = FXCollections.observableArrayList();
		//股东列表总和：除了银河股东,用于获取各股东的比例（比拼值）,加上了总人次利润（除去银河股东）
		Double sum = tableGDSum.getItems().stream()
				.filter(info->!info.getGudongName().contains("银河"))
				.map(info->NumUtil.getNum(info.getGudongProfit()))
				.reduce(Double::sum).orElseGet(()->0d) + renciProfit;
		
		//获取可分配的奖励池
		Double curragePool = getJLPoolAvailable();
		
		//计算各股东的奖励金额
		tableGDSum.getItems().stream()
			.filter(info->!info.getGudongName().contains("银河"))
			.map(info -> {
				/********************************************************添加相应股东的人次总利润**********/
				String gudongId = info.getGudongName().replace("股东", "");
				Integer size = gudongSizeMap.get(gudongId).size();
				Double gudongTotalRenciProfit = NumUtil.getNumTimes(size.toString(), getRenci());
				/******************************************************************/
				
				Double rate =  divide((NumUtil.getNum(info.getGudongProfit()) + gudongTotalRenciProfit ), sum);
				Double currageMoney = curragePool * rate;
				//明细缓存
				GDDetailInfo gdDetailInfo = detailMap.get(info.getGudongName());
				gdDetailInfo.setJl7(NumUtil.digit0(currageMoney));
				//返回
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
	 * 奖励池 = （总利润 - 原始股 — 客服股）* 可分配比例，原始股实际上就是银河股东的利润
	 * 奖励池=（总利润 - 财物分红 - 奖罚 - 银河股东）*70%
	 * 可分配比例默认是70%
	 * 财物分红 and 奖罚 即客服表的总和
	 * 
	 * @time 2018年1月28日
	 * @return
	 */
	private Double getJLPoolAvailable() {
		//总利润
		Double totalProfit = getComputeTotalProfit_mins_totalSalary();
		//原始股（银河股东）
		//Double totalYSGu = tableYSGu.getItems().stream().map(info->NumUtil.getNum(info.getValue())).reduce(Double::sum).get();
		Double totalYSGu = getYinheProfit();
		//总公司
		//Double Zonggongsi_Profit = get_Zonggongsi_Profit(); //TODO
		
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
		
		//缓存明细数据
		tableYSGu.getItems().forEach(info-> {
			GDDetailInfo entity = new GDDetailInfo(info.getType(),info.getValue());
			entity.setSalary(info.getDescription());//备注：此处的描述字段为实体的底薪
			detailMap.put(info.getType(), entity);
		});
	}
	
	/**
	 * 获取银河股东的利润
	 * 
	 * @time 2018年1月28日
	 * @return
	 */
	private Double getYinheProfit() {
		Optional<GudongRateInfo> gudongRateInfoOpt = tableGDSum.getItems().stream().filter(info->info.getGudongName().contains("银河")).findFirst();
		if(gudongRateInfoOpt.isPresent())
			return NumUtil.getNum(gudongRateInfoOpt.get().getGudongProfit());
		else
			return 0d;
	}
	
	/**
	 * 获取总公司的利润
	 * 
	 * @time 2018年2月21日
	 * @return
	 */
//	@Deprecated
//	private Double get_Zonggongsi_Profit() {
//		Optional<GudongRateInfo> gudongRateInfoOpt = tableGDSum.getItems().stream().filter(info->info.getGudongName().contains("总公司")).findFirst();
//		if(gudongRateInfoOpt.isPresent())
//			return NumUtil.getNum(gudongRateInfoOpt.get().getGudongProfit());
//		else
//			return 0d;
//	}
	
	/**
	 * 设置客服股数据
	 * 
	 * @time 2018年1月28日
	 */
	private void setTable_KFGu_data() {
		tablekfGu.getItems().stream()
			.forEach(info->{
				if(!StringUtil.isAnyBlank(info.getType(),info.getRate())) {
					info.setValue(NumUtil.digit0(NumUtil.getNumByPercent(info.getRate()) * getComputeTotalProfit_mins_totalSalary()));
					//缓存明细数据
					GDDetailInfo detail = new GDDetailInfo(info.getType(),info.getValue());
					detail.setSalary(info.getDescription());
					detail.setTotal(NumUtil.getSum(info.getValue(),info.getDescription()));
					detailMap.put(info.getType(), detail);
				}else {
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
		gudongKaixiao_dataList.clear();
		
		//清空总和表
		if(tableGDSum.getItems() != null)
			tableGDSum.getItems().clear();
		
		//清空三个表
		setTableDataEmpty(tableYSGu, tableEncourageGu, tablekfGu);
		
		//清空明细表
		if(tableGDDetail.getItems() != null)
			tableGDDetail.getItems().clear();
		
		//清空动态表
		contributionHBox.getChildren().clear();
		
		//清空其他数据
		computeTotalProfit.setText("0.0");
		changciTotalProfit.setText("0.0");
		difTotalProfit.setText("0.0");
		gudongProfitsRateMap.clear();
		gudongProfitsValueMap.clear();
		totalRenciProfitText.setText("0.0");//总人次利润清空
		gd_currage_money_value.setText("0.0");//总彩池
	}
	
	/**
	 * 清空三个表
	 * 
	 * @time 2018年1月30日
	 * @param tables
	 */
	private void setTableDataEmpty(TableView<GDInputInfo>... tables) {
		for(TableView<GDInputInfo> table : tables) {
			if(TableUtil.isNullOrEmpty(table)) {
				
			}else {
				if(table.getItems() != null)
					table.getItems().forEach(info->{
						//info.setDescription("");
						info.setId("");
						//info.setRate("");
						//info.setType("");
						info.setValue("");
					});
			}
			
		}
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
	
	/**
	 * 客服栏的底薪总计
	 * 
	 * @time 2018年2月26日
	 * @return
	 */
	private Double get_KF_total_salary() {
		Double KF_Total_Salary = 0d;
		if(TableUtil.isHasValue(tablekfGu)) {
			KF_Total_Salary = tablekfGu.getItems().stream()
				.mapToDouble(info -> NumUtil.getNum(info.getDescription()))
				.sum();
		}
		return KF_Total_Salary;
	}
	
	/**
	 * 加载客服数据（按钮）
	 * 
	 * @time 2018年2月3日
	 * @param even
	 */
	public void load_KF_data_Action(ActionEvent even) {
		//从数据库获取客服数据
		String kfValue = DBUtil.getValueByKey(TABLE_KF_DATA_KEY);
		Map<String,String> kfMap = JSON.parseObject(kfValue, new TypeReference<Map<String,String>>() {});
		
		if(MapUtil.isNullOrEmpty(kfMap)) {
			ShowUtil.show("数据库中无客服数据！");
			return;
		}
		
		//清空客服表数据s
		if(TableUtil.isHasValue(tablekfGu)) {
			tablekfGu.getItems().forEach(info->{
				info.setDescription("");
				info.setId("");
				info.setRate("");
				info.setType("");
				info.setValue("");
			});
		}
		
		
		//保证有20条记录
		ObservableList<GDInputInfo> obList = FXCollections.observableArrayList();
		kfMap.forEach((name,rate) -> {
			obList.add(new GDInputInfo(name,rate));  //本方法最关键的一行
		});
		Integer restEmptyCountRowSize = KF_SIZE - kfMap.size() ;//不满足20行的补空行
		if(restEmptyCountRowSize > 0) {
			for(int i=0 ; i< restEmptyCountRowSize ; i++)
				obList.add(new GDInputInfo());
		}
		
		//刷新客服表
		tablekfGu.setItems(obList);
		tablekfGu.refresh();
	}
	
	
	/**
	 * 保存客服数据（按钮）
	 * 
	 * @time 2018年2月3日
	 * @param even
	 */
	public void save_KF_data_Action(ActionEvent even) {
		if(TableUtil.isHasValue(tablekfGu)) {
			Map<String,String> kfMap = new HashMap<>();
			tablekfGu.getItems().forEach(info -> {
				String kfName = info.getType();
				if(StringUtil.isNotBlank(kfName))
					kfMap.put(kfName, info.getRate());
			});
			//保存到数据库
			DBUtil.saveOrUpdateOthers(TABLE_KF_DATA_KEY, JSON.toJSONString(kfMap));
			//提示
			ShowUtil.show("保存成功，记录数："+kfMap.size(), 2);
		}else {
			ShowUtil.show("保存成功，记录数：0", 2);
		}
	}
	
	private static Double divide(Double d1, double d2) {
		return NumUtil.getNumDivide(d1, d2);
	}
	/**
	 * 手动删除所有Record数据
	 * 
	 * @time 2018年2月25日
	 * @param event
	 */
	public void clear_all_records_Action(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("警告");
		alert.setHeaderText(null);
		alert.setContentText("\r\n确定要手动删除数据库中所有白名单数据？");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//删除缓存？
			clearBtn.fire();
			
			//删除数据库
			DBUtil.del_all_record_and_zhuofei_and_kaixiao();
			
			log.info("客户手动删除数据库中所有白名单数据成功！！");
			ShowUtil.show("手动删除成功！", 2);
		}
	}
	
	/**
	 * 一键配额
	 * 
	 * @time 2018年2月25日
	 * @param event
	 */
	public void quotar_money_oneKey_Action(ActionEvent event) {
		if(has_quotar_oneKey) {
			ShowUtil.show("您已经一键分配过了！");
			return;
		}
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("警告");
		alert.setHeaderText(null);
		alert.setContentText("\r\n 将清空场次信息中的利润栏以及把股东值赋到金额栏, 确定??");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			MyController mc = Main.myController;
			//金额表
			TableView<CurrentMoneyInfo> tableMoney = mc.tableCurrentMoneyInfo;
			//利润表
			TableView<ProfitInfo> tableProfit = mc.tableProfit;
			//清空利润表
			tableProfit.getItems().forEach(info -> info.setProfitAccount("0"));
			//将股东值赋到金额栏
			String date = StringUtil.nvl(DataConstans.Date_Str, "2017-01-01");
			date = date.substring(5);
			for(GDDetailInfo info : tableGDDetail.getItems()) {
				String money = StringUtil.nvl(info.getTotal(),"0");
				String name = date + "#" + info.getName() + "#" + money;
				CurrentMoneyInfo cmi = new CurrentMoneyInfo(name, money, "", ""); //mingzi, shishiJine,String wanjiaId,String cmiEdu
				tableMoney.getItems().add(cmi);
			}
			//添加总利润差额
			String dif = difTotalProfit.getText();
			String name = date + "#贡献值差额#" + dif;
			CurrentMoneyInfo difCMI = new CurrentMoneyInfo(name, dif, "", ""); //mingzi, shishiJine,String wanjiaId,String cmiEdu
			tableMoney.getItems().add(difCMI);
			tableMoney.refresh();
			tableProfit.refresh();
			has_quotar_oneKey = true;
			
		}
	}
	
}
