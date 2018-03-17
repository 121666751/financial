package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.db.DBUtil;
import com.kendy.entity.ProxyTeamInfo;
import com.kendy.entity.TGCommentInfo;
import com.kendy.entity.TGCompanyModel;
import com.kendy.entity.TGFwfinfo;
import com.kendy.entity.TGKaixiaoInfo;
import com.kendy.entity.TGTeamInfo;
import com.kendy.entity.TGTeamModel;
import com.kendy.entity.TypeValueInfo;
import com.kendy.service.TGFwfService;
import com.kendy.service.TeamProxyService;
import com.kendy.service.TgWaizhaiService;
import com.kendy.util.CollectUtil;
import com.kendy.util.InputDialog;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;

import application.Constants;
import application.DataConstans;
import application.Main;
import application.MyController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Pair;

/**
 * 处理联盟配额的控制器
 * 
 * @author 林泽涛
 * @time 2017年11月24日 下午9:31:04
 */
public class TGController implements Initializable{
	
	
	private static Logger log = Logger.getLogger(TGController.class);

//	//=====================================================================
	@FXML public VBox TG_Company_VBox; // 托管公司（按钮）
	
	@FXML private VBox TG_Team_VBox; // 托管公司的内部托管团队
	
	@FXML private Label currentTGCompanyLabel; //当前托管公司
	@FXML private Label currentTGTeamLabel; //当前托管团队
	
	@FXML private TextField tgTeamHSRate; //托管团队回水比例
	@FXML private TextField tgTeamHBRate; //托管团队回保比例
	@FXML private TextField tgTeamFWF; //托管团队服务费
	
	@FXML private TextField tgCompanyYajin; //托管公司押金
	@FXML private TextField tgCompanyEdu; //托管公司额度
	
	//=====================================================================
	@FXML private TabPane tabs;
	
	//=====================================================================团队战绩表
	@FXML public TableView<TGTeamInfo> tableTGZhanji;
	@FXML private TableColumn<TGTeamInfo,String> tgPlayerId;
	@FXML private TableColumn<TGTeamInfo,String> tgPlayerName;
	@FXML private TableColumn<TGTeamInfo,String> tgYSZJ;
	@FXML private TableColumn<TGTeamInfo,String> tgZJ25;
	@FXML private TableColumn<TGTeamInfo,String> tgZJUnknow;
	@FXML private TableColumn<TGTeamInfo,String> tgProfit;
	@FXML private TableColumn<TGTeamInfo,String> tgHuiBao;
	@FXML private TableColumn<TGTeamInfo,String> tgBaoxian;
	@FXML private TableColumn<TGTeamInfo,String> tgChangci;
	
	//=====================================================================托管团队战绩总和表
	@FXML public TableView<TypeValueInfo> tableZJSum;
	@FXML private TableColumn<TypeValueInfo,String> tgZJSumType;
	@FXML private TableColumn<TypeValueInfo,String> tgZJSumValue;
	
	//=====================================================================托管团队映射表
	@FXML public TableView<TypeValueInfo> tableTGTeamRate;
	@FXML private TableColumn<TypeValueInfo,String> tgTeamId;
	@FXML private TableColumn<TypeValueInfo,String> tgTeamRate;

	//=====================================================================托管开销表
	@FXML public TableView<TGKaixiaoInfo>  tableTGKaixiao;     
	@FXML private TableColumn<TGKaixiaoInfo,String> tgKaixiaoDate;
	@FXML private TableColumn<TGKaixiaoInfo,String> tgKaixiaoPlayerName;
	@FXML private TableColumn<TGKaixiaoInfo,String> tgKaixiaoPayItem;
	@FXML private TableColumn<TGKaixiaoInfo,String> tgKaixiaoMoney;
	@FXML private TableColumn<TGKaixiaoInfo,String> tgKaixiaoCompany;
	@FXML public ListView<String> tgKaixiaoSumView; // 开销合计
	//=====================================================================托管玩家备注表
	@FXML public TableView<TGCommentInfo>  tableTGComment;     
	@FXML private TableColumn<TGCommentInfo,String> tgCommentDate;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentPlayerId;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentPlayerName;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentType;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentId;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentName;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentBeizhu;
	@FXML private TableColumn<TGCommentInfo,String> tgCommentCompany;
	@FXML public ListView<String> tgCommentSumView; // 玩家备注合计
	
	//=====================================================================托管团队外债表
	@FXML public TableView<TypeValueInfo> tgWZTeam;
	@FXML private TableColumn<TypeValueInfo,String> tgWZTeamId;
	@FXML private TableColumn<TypeValueInfo,String> tgWZTeamValue;
	
	//=====================================================================外债数据表
	@FXML private HBox tgWZTeamHBox; // 存储动态的团队外债数据表
	
	//=====================================================================托管玩家备注表
	@FXML public TableView<TGFwfinfo>  tableTGFwf;     
	@FXML private TableColumn<TGFwfinfo,String> tgFwfCompany;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfTeamId;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfHuishui;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfHuiBao;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfProfit;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfFanshui;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfFanbao;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfQuanshui;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfQuanbao;
	@FXML private TableColumn<TGFwfinfo,String> tgFwfHeji;
	

	//=====================================================================托管服务费总和表
	@FXML public TableView<TypeValueInfo> tableTGFwfSum;
	@FXML private TableColumn<TypeValueInfo,String> tgFwfType;
	@FXML private TableColumn<TypeValueInfo,String> tgFwfValue;
	
	
	
	
	

	private static final String TG_TEAM_RATE_DB_KEY = "tg_team_rate"; //保存到数据库的key
	
	/**
	 * DOM加载完后的事件
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//绑定列值属性
		MyController.bindCellValue(tgKaixiaoDate,tgKaixiaoPlayerName,tgKaixiaoPayItem,tgKaixiaoMoney,tgKaixiaoCompany);
		MyController.bindCellValue(tgCommentDate,tgCommentPlayerId,tgCommentPlayerName,tgCommentType,tgCommentId,tgCommentName,tgCommentBeizhu,tgCommentCompany);
		MyController.bindCellValue(tgFwfCompany, tgFwfTeamId, tgFwfHuishui, tgFwfHuiBao, tgFwfProfit, tgFwfFanshui, tgFwfFanbao, tgFwfQuanshui, tgFwfQuanbao, tgFwfHeji);
		binCellValueDiff(tgTeamId,"type");
		binCellValueDiff(tgTeamRate,"value");
		binCellValueDiff(tgZJSumType,"type");
		binCellValueDiff(tgZJSumValue,"value");
		binCellValueDiff(tgWZTeamId,"type");
		binCellValueDiff(tgWZTeamValue,"value");
		binCellValueDiff(tgFwfType,"type");
		binCellValueDiff(tgFwfValue,"value");
		MyController.bindCellValue(tgPlayerId,tgPlayerName,tgYSZJ,tgZJ25,tgZJUnknow,tgProfit,tgHuiBao,tgBaoxian,tgChangci);
		bindColorColumn(tgYSZJ, tgZJ25, tgZJUnknow, tgProfit, tgHuiBao, tgBaoxian);
		bindColorColumnTGFwfinfo(tgFwfHuishui, tgFwfHuiBao, tgFwfProfit, tgFwfFanshui, tgFwfFanbao, tgFwfQuanshui, tgFwfQuanbao, tgFwfHeji);
		
		//tabs切换事件
		tabsAction();
		
		//加载托管团队比例数据
		refreshTableTGTeam();
		
		//加载托管公司数据
		loadDataLastest();
		
	}
	
	private <T>  void   binCellValueDiff(TableColumn<T, String> column, String bindName) {
        try {
			column.setStyle("-fx-alignment: CENTER;");
			column.setCellValueFactory(
					new PropertyValueFactory<T, String>(bindName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  void bindColorColumn(TableColumn<TGTeamInfo, String>... columns) {
		for(TableColumn<TGTeamInfo, String> column  : columns)
			column.setCellFactory(MyController.getColorCellFactory(new TGTeamInfo()));
	}
	private  void bindColorColumnTGFwfinfo(TableColumn<TGFwfinfo, String>... columns) {
		for(TableColumn<TGFwfinfo, String> column  : columns)
			column.setCellFactory(MyController.getColorCellFactory(new TGFwfinfo()));
	}
	
	
	/**
	 * tabs切换事件
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void tabsAction() {
		tabs.getSelectionModel().selectedItemProperty().addListener(info-> {
			//Tab tab = (Tab)info;
			refreshSmallTabData();
		});
	}
	
	/**
	 * 选择托管公司按钮 或 手动点击小tab时自动刷新界面数据
	 * 
	 * @time 2018年3月16日
	 */
	private void refreshSmallTabData() {
		String selectedTab = tabs.getSelectionModel().getSelectedItem().getText().trim();
		log.info(" newTab:"+selectedTab);
		if("开销".equals(selectedTab)) {
    		refreshTableTGKaixiao();//刷新
    	}
    	if("玩家备注".equals(selectedTab)) {
    		refreshTableTGComment();//刷新
    	}
    	if("托管外债".equals(selectedTab)) {
    		refreshTabTGWaizhai();//刷新
    	}
    	if("服务费明细".equals(selectedTab)) {
    		TGFwfService tgFwfService = new TGFwfService();
    		tgFwfService.setFwfDetail(StringUtil.nvl(currentTGCompanyLabel.getText(),""), tableTGFwf, tableTGFwfSum);
    	}
	}
	
	/**
	 * 获取当前托管公司的值 
	 * 
	 * @time 2018年3月4日
	 * 
	 * @return
	 */
	public String getCurrentTGCompany() {
		return currentTGCompanyLabel.getText();
	}
    /**
     * 打开对话框
     * @param path fxml名称
     * @param title 对话框标题
     * @param windowName 对话框关闭时的名称
     */
    public void openBasedDialog(String path,String title,String windowName) {
    	try {
    		if(DataConstans.framesNameMap.get(windowName) == null){
    			//打开新对话框
    			String filePath = "/com/kendy/dialog/"+path;
	    		Parent root = FXMLLoader.load(getClass().getResource(filePath));
	    		Stage addNewPlayerWindow=new Stage();  
	    		Scene scene=new Scene(root);  
	    		addNewPlayerWindow.setTitle(title);  
	    		addNewPlayerWindow.setScene(scene);
	    		try {
	    			addNewPlayerWindow.getIcons().add(new javafx.scene.image.Image("file:resource/images/icon.png"));
				} catch (Exception e) {
					log.debug("找不到icon图标！");
					e.printStackTrace();
				}
	    		addNewPlayerWindow.show();  
	    		//缓存该对话框实例
	    		DataConstans.framesNameMap.put(windowName, addNewPlayerWindow);
	    		addNewPlayerWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
	                @Override
	                public void handle(WindowEvent event) {
	                    DataConstans.framesNameMap.remove(windowName);
	                }
	            });

    		}
    	
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	/**
	 * 新增托管公司
	 * 
	 * @time 2018年3月2日
	 * @param event
	 */
	public void addNewTGCompanyAction(ActionEvent event) {
		openBasedDialog("TG_add_company_frame.fxml","新增托管公司",Constants.ADD_COMPANY_FRAME);
	}
	
	/**
	 * 导出当前托管公司的所有数据
	 * 
	 * @time 2018年3月2日
	 * @param event
	 */
	public void exportTGExcelAction(ActionEvent event) {
		
	}
	
	/**
	 * 新增托管开销
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addKaixiaoAction(ActionEvent event) {
		openBasedDialog("TG_add_kaixiao_frame.fxml","新增托管开销",Constants.ADD_TG_KAIXIAAO_FRAME);
	}
	
	/**
	 * 新增托管开销
	 * 
	 * @time 2018年3月3日
	 * @param event
	 */
	public void addPlayerCommentAction(ActionEvent event) {
		openBasedDialog("TG_add_player_comment_frame.fxml","新增玩家备注",Constants.ADD_TG_KAIXIAAO_FRAME);
	}
	
	/**
	 * 删除开销记录
	 * @time 2018年3月4日
	 * @param event
	 */
	public void delTGKaixiaoAction(ActionEvent event) {
		TGKaixiaoInfo selectedItem = tableTGKaixiao.getSelectionModel().getSelectedItem();
		if(selectedItem == null) {
			ShowUtil.show("先选择要删除的托管开销记录！");
		}else {
			//同步到数据库
			DBUtil.del_tg_kaixiao_by_id(selectedItem.getTgKaixiaoEntityId());
			refreshTableTGKaixiao();
			ShowUtil.show("操作完成 ", 1);
		}
	}
	
	
	/**
	 * 删除玩家备注记录
	 * @time 2018年3月4日
	 * @param event
	 */
	public void delTGCommentAction(ActionEvent event) {
		TGCommentInfo selectedItem = tableTGComment.getSelectionModel().getSelectedItem();
		if(selectedItem == null) {
			ShowUtil.show("先选择要删除的托管玩家备注记录！");
		}else {
			//同步到数据库
			DBUtil.del_tg_comment_by_id(selectedItem.getTgCommentEntityId());
			refreshTableTGComment();
			ShowUtil.show("操作完成 ", 1);
		}
	}
	
	/**
	 * 刷新托管开销表
	 * 
	 * @time 2018年3月4日
	 */
	public void refreshTableTGKaixiao() {
		
		//从数据库获取最新数据
		List<TGKaixiaoInfo> tgKaixiaoList = DBUtil.get_all_tg_kaixiao();
		String company = currentTGCompanyLabel.getText();
		if(StringUtil.isAnyBlank(company)) {
			ShowUtil.show("请选择托管公司！");
		}else if(CollectUtil.isHaveValue(tgKaixiaoList)) {
			tgKaixiaoList  = tgKaixiaoList.stream().filter(info -> company.equals(info.getTgKaixiaoCompany())).collect(Collectors.toList());
		}
		
		//过滤某个托管公司 TODO 
		
		//赋值
		ObservableList<TGKaixiaoInfo> obList ;
		ObservableList<String> sumObList ;
		if(CollectUtil.isNullOrEmpty(tgKaixiaoList)) {
			obList = FXCollections.observableArrayList();
			sumObList = FXCollections.observableArrayList();
		}else {
			obList = FXCollections.observableArrayList(tgKaixiaoList);
			/****计算合计****/
			//1 按类别划分 （金币、打牌奖励、推荐奖励...）
			Map<String, List<TGKaixiaoInfo>> typeMap = tgKaixiaoList.stream().collect(Collectors.groupingBy(TGKaixiaoInfo::getTgKaixiaoPayItem));
			List<String> sumList = new ArrayList<>();
			typeMap.forEach((name,list) -> {sumList.add(name + ": " + list.size() 
				   + "	" + list.stream().mapToInt(info -> NumUtil.getNum(info.getTgKaixiaoMoney()).intValue()).sum()
					);
			});
			// 添加为0的情况
			List<String> payItems = TGAddKaixiaoController.payItems;
			if(CollectUtil.isHaveValue(payItems)) {
				payItems.stream().filter(item -> typeMap.get(item) == null).forEach(item -> sumList.add(item + ": 0"));
			}
			//2 计算服务费
			sumObList = FXCollections.observableArrayList(sumList);
		}
		tableTGKaixiao.setItems(obList);
		tableTGKaixiao.refresh();
		tgKaixiaoSumView.setItems(sumObList);
		tgKaixiaoSumView.refresh();
		
	}
	
	
	/**
	 * 刷新托管玩家备注表
	 * 
	 * @time 2018年3月4日
	 */
	public void refreshTableTGComment() {
		//从数据库获取最新数据
		List<TGCommentInfo> tgCommentList = DBUtil.get_all_tg_comment();
		//过滤某个托管公司 TODO 
		String company = currentTGCompanyLabel.getText();
		if(StringUtil.isAnyBlank(company)) {
			ShowUtil.show("请选择托管公司！");
		}else if(CollectUtil.isHaveValue(tgCommentList)) {
			tgCommentList  = tgCommentList.stream().filter(info -> company.equals(info.getTgCommentCompany())).collect(Collectors.toList());
		}
		
		//赋值
		ObservableList<TGCommentInfo> obList ;
		ObservableList<String> sumObList ;
		if(CollectUtil.isNullOrEmpty(tgCommentList)) {
			obList = FXCollections.observableArrayList();
			sumObList = FXCollections.observableArrayList();
		}else {
			obList = FXCollections.observableArrayList(tgCommentList);
			/****计算合计****/
			//按类别划分 （改号、更换帐号、推荐玩家...）
			Map<String, List<TGCommentInfo>> typeMap = tgCommentList.stream().collect(Collectors.groupingBy(TGCommentInfo::getTgCommentType));
			List<String> sumList = new ArrayList<>();
			typeMap.forEach((name,list) -> {sumList.add(name + ": " + list.size() 
				   //+ "	" + list.stream().mapToInt(info -> NumUtil.getNum(info.getTgKaixiaoMoney()).intValue()).sum()
					);
			});
			// 添加为0的情况
			List<String> items = TGAddCommentController.typeItems;
			if(CollectUtil.isHaveValue(items)) {
				items.stream().filter(item -> typeMap.get(item) == null).forEach(item -> sumList.add(item + ": 0"));
			}
			
			sumObList = FXCollections.observableArrayList(sumList);
		}
		tableTGComment.setItems(obList);
		tableTGComment.refresh();
		tgCommentSumView.setItems(sumObList);
		tgCommentSumView.refresh();
		
	}
	
	
	/**
	 * 清空界面数据
	 */
	private void clearUIData() {
		
	}
	
	/**
	 * 加载最新的数据
	 * 
	 * @time 2018年3月5日
	 */
	public void loadDataLastest() {
		//清空数据
		
		//获取数据
		List<TGCompanyModel> tgCompanys = DBUtil.get_all_tg_company();
		
		if(CollectUtil.isHaveValue(tgCompanys)) {
			// 获取特定结构 {托管公司 ：｛ 团队名称 ： 团队数据列表 ｝｝ TODO
			
			
			ObservableList<Node> companyButtons = TG_Company_VBox.getChildren();
			TG_Company_VBox.setPrefWidth(120);
			if(CollectUtil.isHaveValue(companyButtons)) {
				companyButtons.clear();
			}
			tgCompanys.forEach(company -> {
				Button companyBtn = getCompanyButton(company);
				companyButtons.add(companyBtn);
			});
		}
	}
	
	/**
	 * 获取一个动态的公司按钮
	 * @time 2018年3月7日
	 * @param companyEntity
	 * @return
	 */
	private Button getCompanyButton(TGCompanyModel companyEntity ) {
		String company = companyEntity.getTgCompanyName();
		List<String> teamList = companyEntity.getTgTeamList();
		Button companyBtn = new Button(company);
		companyBtn.setPrefWidth(110);
		companyBtn.setOnAction(event -> {
			//改前景颜色
			//TG_Company_VBox.getStylesheets().add("-fx-background-color:red");
			//修改当前托管公司名称
			currentTGCompanyLabel.setText(company);
			currentTGTeamLabel.setText("");
			
			//清空表数据
			clearTableTGTeamDataAndSum();
			clearWhenChangeCompanyBtn();
			
			//加载托管公司名下的团队按钮数据  {托管公司 ：｛ 团队名称 ： 团队数据列表 ｝｝
			if(CollectUtil.isHaveValue(teamList)) {
				loadTeamBtnView(teamList);
			}
			//设置托管公司的相关信息
			setTGCompanyInfo(company);
			//设置下面那个小Tab的当前Tab数据
			refreshSmallTabData();
		});
		
		return companyBtn;
	}
	
	/**
	 * 加载动态的按钮列表图
	 * @time 2018年3月7日
	 * @param teamList
	 */
	private void loadTeamBtnView(List<String> teamList) {
		ObservableList<Node> teamBtns = TG_Team_VBox.getChildren();
		TG_Team_VBox.setPrefWidth(100);
		if(CollectUtil.isHaveValue(teamBtns)) {
			teamBtns.clear();
		}
		//排序
		try {
			if(teamList != null && teamList.size() > 1) {
				teamList = teamList.stream().sorted((x,y) -> { 
					String a = x.replaceFirst("[a-zA-Z]+", "");
					String b = y.replaceFirst("[a-zA-Z]+", "");
					if(StringUtil.isAnyBlank(a,b)) {
						return 1;
					}else {
						return Integer.valueOf(a).compareTo(Integer.valueOf(b));
					}
				})
				.collect(Collectors.toList());
			}
		}catch(Exception e) {e.printStackTrace();}
		
		teamList.forEach(teamId -> {
			Button teamBtn = getTeamButton(teamId);
			teamBtns.add(teamBtn);
		});
	}
	
	/**
	 * 获取动态的团队按钮
	 * 
	 * @time 2018年3月7日
	 * @param teamId
	 * @return
	 */
	private Button getTeamButton(String teamId) {
		Button teamBtn = new Button(teamId);
		teamBtn.setPrefWidth(90);
		teamBtn.setOnAction(event -> {
			//清空数据
			clearWhenChangeTeamBtn();
			
			currentTGTeamLabel.setText(teamId);
			//获取代理查询的团队数据
			final List<ProxyTeamInfo> proxyTeamInfoList = getProxyTeamInfoList(teamId);
			//转化为托管公司的团队数据
			List<TGTeamInfo> tgTeamList = convert2TGTeamInfo(teamId, proxyTeamInfoList);
			tableTGZhanji.setItems(FXCollections.observableArrayList(tgTeamList));
			//设置团队合计
			refreshTableTGTeamSum();
			//设置团队的托管回水比例、回保比例、服务费
			setTGTeamRateInfo(teamId);
		});
		
		return teamBtn;
	}
	
	
	/**
	 * 点击托管公司按钮后先清空相应值
	 * @time 2018年3月11日
	 */
	private void clearWhenChangeCompanyBtn() {
		clearWhenChangeTeamBtn();
		
		tgCompanyYajin.setText("");
		tgCompanyEdu.setText("");
	}
	
	/**
	 * 点击托管团队按钮后先清空相应值
	 * @time 2018年3月11日
	 */
	private void clearWhenChangeTeamBtn() {
		tgTeamHSRate.setText("");
		tgTeamHBRate.setText("");
		tgTeamFWF.setText("");
	}
	
	/**
	 * 设置公司的托管押金、额度。其他？
	 * @time 2018年3月11日
	 * @param teamId
	 */
	private void setTGCompanyInfo(String tgCompany) {
		TGCompanyModel tgCompanyModel = DBUtil.get_tg_company_by_id(tgCompany);
		if(tgCompanyModel == null) {
			return;
		}
		//设置
		tgCompanyYajin.setText(tgCompanyModel.getYajin());
		tgCompanyEdu.setText(tgCompanyModel.getEdu());
		//其他更新
		
	}
	
	/**
	 * 设置团队的托管回水比例、回保比例、服务费
	 * @time 2018年3月11日
	 * @param teamId
	 */
	private void setTGTeamRateInfo(String teamId) {
		TGTeamModel tgTeamModel = DBUtil.get_tg_team_by_id(teamId);
		if(tgTeamModel == null) {
			return;
		}
		//设置
		tgTeamHSRate.setText(tgTeamModel.getTgHuishui());
		tgTeamHBRate.setText(tgTeamModel.getTgHuiBao());
		tgTeamFWF.setText(tgTeamModel.getTgFWF());
		//其他更新
		
	}
	
	/**
	 * 代理查询中的数据转成托管中的团队信息数据
	 * @time 2018年3月7日
	 * @param teamId
	 * @param proxyTeamInfoList
	 * @return
	 */
	private List<TGTeamInfo> convert2TGTeamInfo(String teamId, List<ProxyTeamInfo> proxyTeamInfoList){
		List<TGTeamInfo> list = new ArrayList<>();
		TGController tgController = MyController.tgController;
		Map<String, TGTeamModel> tgTeamRateMap = tgController.getTgTeamModelMap();
		
		TGTeamModel tgTeamModel = tgTeamRateMap.get(teamId);
		String teamUnknowValue = tgTeamModel == null ? "0.0" : tgTeamModel.getTgHuishui();
		
		String teamHuibaoRateValue = tgTeamModel == null ? "0.0" : tgTeamModel.getTgHuiBao();
		
		//更改列名称
		changeColumnName_TeamUnknowRate(NumUtil.getNum(teamUnknowValue));
		
		if(CollectUtil.isHaveValue(proxyTeamInfoList)) {
			list = proxyTeamInfoList.stream().map(info -> {
				TGTeamInfo tgTeam = new TGTeamInfo();
				tgTeam.setTgPlayerId(info.getProxyPlayerId());
				tgTeam.setTgPlayerName(info.getProxyPlayerName());
				tgTeam.setTgYSZJ(info.getProxyYSZJ());
				tgTeam.setTgBaoxian(info.getProxyBaoxian());
				tgTeam.setTgChangci(info.getProxyTableId());
				//设置战绩2.5% 
				String percent25Str = NumUtil.digit2(NumUtil.getNum(info.getProxyYSZJ()) * 0.025 + "");
				tgTeam.setTgZJ25(percent25Str);
				//设置战绩未知%
				String teamUnknowStr = NumUtil.digit2(NumUtil.getNumTimes(info.getProxyYSZJ(), teamUnknowValue) + "");
				tgTeam.setTgZJUnknow(teamUnknowStr);
				//设置回保
				String teamHuibaoRateStr =  NumUtil.digit2((-1) * 0.975 * NumUtil.getNumTimes(tgTeam.getTgBaoxian(), teamHuibaoRateValue) + "");
				tgTeam.setTgHuiBao(StringUtil.nvl(teamHuibaoRateStr, "0.00"));
				
				//设置利润
				String profit = getRecordProfit(tgTeam);
				tgTeam.setTgProfit(profit);
				
				return tgTeam;
			}).collect(Collectors.toList());
		}
		return list;
	}
	
	
	/**
	 * 获取每一行的利润
	 * 公式 = 原始战绩 * （2.5% - unknow%） + 保险 * （-0.975） - 回保
	 * 
	 * @time 2018年3月7日
	 * @param info
	 * @return
	 */
	public String getRecordProfit(TGTeamInfo info) {
		String teamRate25 = info.getTgZJ25();
		String teamRateUnknow = info.getTgZJUnknow();
		String baoxian = info.getTgBaoxian();
		String huibao = info.getTgHuiBao();
		Double recordProfit = NumUtil.getNum(teamRate25) - NumUtil.getNum(teamRateUnknow) 
		 + ( NumUtil.getNum(baoxian) * (-0.975)  - NumUtil.getNum(huibao) ); 
		return NumUtil.digit2(recordProfit + "");
	}
	
	/**
	 * 模拟获取代理查询中的团队数据
	 * 
	 * @time 2018年3月6日
	 * @param teamId
	 * @return
	 */
	public List<ProxyTeamInfo> getProxyTeamInfoList(String teamId){

		List<ProxyTeamInfo> proxyTeamList = new ArrayList<>();
		ObservableList<String> obList = TeamProxyService.teamIDCombox.getItems();
		if(!CollectUtil.isHaveValue(obList)) {
			ShowUtil.show("小林提示：代理查询团队下拉框没有数据！",2);
			return proxyTeamList;
		}else {
			if(StringUtil.isNotBlank(teamId) && obList.contains(teamId.toUpperCase())) {
				for(String _teamId : obList) {
					if(_teamId.equals(teamId.toUpperCase())) {
						TeamProxyService.teamIDCombox.getSelectionModel().select(_teamId);
						log.info("模拟获取代理查询中的团队数据: "+ teamId);
						ObservableList<ProxyTeamInfo> teamList = TeamProxyService.tableProxyTeam.getItems();
						if(CollectUtil.isHaveValue(teamList)) {
							for(ProxyTeamInfo info : teamList) {
								proxyTeamList.add(info);
							}
						}
					}
				}
			}
		}
		return proxyTeamList;
	}
	
	
	/** 
     * 获取十六进制的颜色代码.例如  "#6E36B4" , For HTML , 
     * @return String 
     */  
	public static String getRandColorCode(){  
		  String r,g,b;  
		  Random random = new Random();  
		  r = Integer.toHexString(random.nextInt(256)).toUpperCase();  
		  g = Integer.toHexString(random.nextInt(256)).toUpperCase();  
		  b = Integer.toHexString(random.nextInt(256)).toUpperCase();  
		    
		  r = r.length()==1 ? "0" + r : r ;  
		  g = g.length()==1 ? "0" + g : g ;  
		  b = b.length()==1 ? "0" + b : b ;  
		    
		  return r+g+b;  
	 }
	
	/**
	 * 获取团队比例映射
	 * 使用getTgTeamModelMap方法代替
	 * 
	 * @time 2018年3月7日
	 * @return
	 */
	@Deprecated
	public Map<String,Double> getTgTeamRateMap(){
		List<TypeValueInfo> tableTGTeams = getTableTGTeams();
		//toMap方法，当key相同时会报错
		Map<String,Double> map = tableTGTeams.stream().distinct()
				.collect(Collectors.toMap(TypeValueInfo::getType, info -> 
						NumUtil.getNumByPercent(info.getValue())));
		return map == null ? new HashMap<>() : map;
	}
	
	/**
	 * 获取托管团队的托管回水比例和回保比例
	 * 
	 * @time 2018年3月17日
	 * @return
	 */
	public Map<String,TGTeamModel> getTgTeamModelMap(){
		List<TGTeamModel> tableTGTeams = DBUtil.get_all_tg_team();
		if(CollectUtil.isNullOrEmpty(tableTGTeams)) {
			tableTGTeams = new ArrayList<>();
		}
		//toMap方法，当key相同时会报错
		Map<String,TGTeamModel> map = tableTGTeams.stream().distinct()
				.collect(Collectors.toMap(TGTeamModel::getTgTeamId, Function.identity()));
		return map == null ? new HashMap<>() : map;
	}
	
	private void changeColumnName_TeamUnknowRate(Double teamUnknowRate) {
		String newColumnName = "0%";
		if(teamUnknowRate.intValue() >= 0) {
			newColumnName = NumUtil.getPercentStr(teamUnknowRate);
		}
		tableTGZhanji.getColumns().get(5).setText("战绩"+newColumnName);
	}
	
	
	/**
	 * 获取托管团队表内容
	 * @time 2018年3月6日
	 * @return
	 */
	public List<TypeValueInfo> getTableTGTeams(){
		ObservableList<TypeValueInfo> tgTeamRates = tableTGTeamRate.getItems();
		return CollectUtil.isNullOrEmpty(tgTeamRates) ? FXCollections.observableArrayList() : tgTeamRates;
	}
	
	/**
	 * 刷新托管团队表
	 * 
	 * @time 2018年3月3日
	 */
	private void refreshTableTGTeam() {
		List<TypeValueInfo> list ;
		String teamsJson = DBUtil.getValueByKey(TG_TEAM_RATE_DB_KEY);
		if(StringUtil.isNotBlank(teamsJson) && !"{}".equals(teamsJson)) {
			list = JSON.parseObject(teamsJson, new TypeReference<List<TypeValueInfo>>() {});
		}else {
			//list = new ArrayList<>();
			list = getMoniTGTeamRate();
			String teamJson = JSON.toJSONString(list);
			DBUtil.saveOrUpdateOthers(TG_TEAM_RATE_DB_KEY, teamJson);
		}
		tableTGTeamRate.setItems(FXCollections.observableArrayList(list));
	}
	
	
	private List<TypeValueInfo> getMoniTGTeamRate(){
		//DBUtil.delValueByKey(TG_TEAM_RATE_DB_KEY);
		List<TypeValueInfo> list = new ArrayList<>();
		Random random = new Random();
		for(int i=1; i<=40; i++ ) {
			double nextDouble = random.nextDouble();
			list.add(new TypeValueInfo("S"+i, NumUtil.digit1(1+nextDouble+"")+"%"));
		}
		return list;
	}
	
	
	/**
	 * 添加托管团队
	 */
	public void AddTGTeamRateBtnAction(ActionEvent event){
		InputDialog dialog = new InputDialog("添加托管团队","托管团队","团队比例");
		
		Optional<Pair<String, String>> result = dialog.getResult();
		if (result.isPresent()){
			try {
				Pair<String, String> map = result.get();
				String teamId = map.getKey().trim();
				String teamRate = map.getValue().trim();
				//是否重复
				List<TypeValueInfo> tableTGTeams = getTableTGTeams();
				boolean repeatTeamId = tableTGTeams.stream().anyMatch(info->teamId.equals(info.getType()));
				if(repeatTeamId) {
					ShowUtil.show(teamId + "团队已经存在！");
					return;
				}
				if(!teamRate.endsWith("%")) {
					ShowUtil.show("比例必须包含百分比符号%");
					return;
				}
				//添加
				tableTGTeams.add(new TypeValueInfo(teamId, teamRate));
				String teamsJson = JSON.toJSONString(tableTGTeams);
				DBUtil.saveOrUpdateOthers(TG_TEAM_RATE_DB_KEY, teamsJson);
				//刷新当前表(战绩) TODO
				refreshTableTGTeam();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 删除托管团队比例
	 */
	public void delTGTeamRateBtnAction(ActionEvent event){
		TypeValueInfo selectedItem = tableTGTeamRate.getSelectionModel().getSelectedItem();
		if(selectedItem == null ) {
			ShowUtil.show("请先选择记录!");
			return;
		}
		tableTGTeamRate.getItems().remove(selectedItem);
		ObservableList<TypeValueInfo> items = tableTGTeamRate.getItems();
		String teamsJson = JSON.toJSONString(items);
		DBUtil.saveOrUpdateOthers(TG_TEAM_RATE_DB_KEY, teamsJson);
		//刷新当前表(战绩) TODO
		refreshTableTGTeam();
		
	}
	
	/**
	 * 刷新托管团队总和数据（合计）
	 */
	public void refreshTableTGTeamSum() {
		// 1 战绩2.5%：
		double zjRate25Sum = tableTGZhanji.getItems().stream()
			.mapToDouble(info-> NumUtil.getNum(info.getTgZJ25()))
			.sum();
		
		// 2 战绩未知
		String columnName = tableTGZhanji.getColumns().get(5).getText();
		double zjRateUnknowSum = tableTGZhanji.getItems().stream()
				.mapToDouble(info-> NumUtil.getNum(info.getTgZJUnknow()))
				.sum();
		
		// 3 保险
		double zjBaoxianSum = tableTGZhanji.getItems().stream()
				.mapToDouble(info-> NumUtil.getNum(info.getTgBaoxian()))
				.sum();
		
		// 4 回保
		double zjHuibaoSum = tableTGZhanji.getItems().stream()
				.mapToDouble(info-> NumUtil.getNum(info.getTgHuiBao()))
				.sum();
		
		// 5 总和
		double zjProfitSum = zjRate25Sum - zjRateUnknowSum + zjBaoxianSum - zjHuibaoSum;
		
		List<TypeValueInfo> list = new ArrayList<TypeValueInfo>();
		list.add(new TypeValueInfo("战绩2.5%合计", NumUtil.digit2(zjRate25Sum+"")));
		list.add(new TypeValueInfo(columnName + "合计", NumUtil.digit2(zjRateUnknowSum+"")));
		list.add(new TypeValueInfo("保险合计", NumUtil.digit2(zjBaoxianSum+"")));
		list.add(new TypeValueInfo("回保合计", NumUtil.digit2(zjHuibaoSum+"")));
		list.add(new TypeValueInfo("总利润合计", NumUtil.digit2(zjProfitSum+"")));
				
		ObservableList<TypeValueInfo> obList = FXCollections.observableArrayList(list);
		tableZJSum.setItems(obList);

	}
	
	/**
	 * 刷新托管团队战绩以及总和数据（合计）
	 */
	private void clearTableTGTeamDataAndSum() {
		tableTGZhanji.getColumns().get(5).setText("战绩0%");
		if(TableUtil.isHasValue(tableTGZhanji)) {
			tableTGZhanji.getItems().clear();
		}
		if(TableUtil.isHasValue(tableZJSum)) {
			tableZJSum.getItems().clear();
		}
	}
	
	
	/**
	 * 刷新外债Tab
	 * @time 2018年3月8日
	 */
	public void refreshTabTGWaizhai() {
		MyController myController = Main.myController;
		TgWaizhaiService.generateWaizhaiTables(tgWZTeam, tgWZTeamHBox 
				,myController.tableCurrentMoneyInfo, myController.tableTeam);
	}
	
	/**
	 * 保存托管团的各个比例
	 * @time 2018年3月11日
	 */
	public void saveTGTeamAction() {
		String tgTeamId = currentTGTeamLabel.getText();
		if(StringUtil.isBlank(tgTeamId)) {
			ShowUtil.show("请先选择托管团队！");
			return;
		}
		//托管团队回水比例
		String teamHSRate = StringUtil.nvl(tgTeamHSRate.getText(),"0%");
		//托管团队回保比例
		String teamHBRate = StringUtil.nvl(tgTeamHBRate.getText(),"0%");
		//托管团队服务费
		String teamFWF = StringUtil.nvl(tgTeamFWF.getText(),"0");
		
		TGTeamModel team = new TGTeamModel(tgTeamId, teamHSRate, teamHBRate, teamFWF);
		
		DBUtil.saveOrUpdate_tg_team(team);
		ShowUtil.show("保存成功", 2);
	}
	
	/**
	 * 保存或修改公司信息（押金和额度）
	 * 
	 * @time 2018年3月11日
	 * @param event
	 */
	public void saveCompanyInfoAction(ActionEvent event) {
		String tgCompany = currentTGCompanyLabel.getText();
		if(StringUtil.isBlank(tgCompany)) {
			ShowUtil.show("请先选择托管公司！");
			return;
		}
		TGCompanyModel db_company = DBUtil.get_tg_company_by_id(tgCompany);
		if(db_company == null) {
			ShowUtil.show("数据库中没有存储此托管公司！");
			return;
		}
		//托管公司押金
		String companyYajin = StringUtil.nvl(tgCompanyYajin.getText(),"0");
		//托管公司额度
		String companyEdu = StringUtil.nvl(tgCompanyEdu.getText(),"0");
		
		db_company.setYajin(companyYajin);
		db_company.setEdu(companyEdu);
		
		DBUtil.saveOrUpdate_tg_company(db_company);
		ShowUtil.show("保存成功", 2);
	}
	
	
	
	
	

    
    
    
    
	
}
