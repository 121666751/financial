package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.db.DBUtil;
import com.kendy.entity.TGCommentInfo;
import com.kendy.entity.TGCompanyModel;
import com.kendy.entity.TGKaixiaoInfo;
import com.kendy.entity.TypeValueInfo;
import com.kendy.util.CollectUtil;
import com.kendy.util.InputDialog;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.Constants;
import application.DataConstans;
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
import javafx.scene.control.cell.PropertyValueFactory;
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
	
	//=====================================================================
	@FXML private TabPane tabs;
	
	
	//=====================================================================托管团队映射表
	@FXML public TableView<TypeValueInfo> tableTGTeamRate;
	@FXML private TableColumn<TypeValueInfo,String> tgTeamId;
	@FXML private TableColumn<TypeValueInfo,String> tgTeamRate;
	//=====================================================================表

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
	@FXML public ListView<String> tgCommentSumView; // 玩家备注合计

	private static final String TG_TEAM_RATE_DB_KEY = "tg_team_rate"; //保存到数据库的key
	
	/**
	 * DOM加载完后的事件
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//绑定列值属性
		MyController.bindCellValue(tgKaixiaoDate,tgKaixiaoPlayerName,tgKaixiaoPayItem,tgKaixiaoMoney,tgKaixiaoCompany);
		MyController.bindCellValue(tgCommentDate,tgCommentPlayerId,tgCommentPlayerName,tgCommentType,tgCommentId,tgCommentName,tgCommentBeizhu);
		binCellValueDiff(tgTeamId,"type");
		binCellValueDiff(tgTeamRate,"value");
		
		//tabs切换事件
		tabsAction();
		
		//加载托管团队比例数据
		refreshTableTGTeam();
		
		//加载托管公司数据
		loadDataLastest();
		
	}
	
	private <T>  void   binCellValueDiff(TableColumn<T, String> column, String bindName) {
        column.setStyle("-fx-alignment: CENTER;");
        column.setCellValueFactory(
        		new PropertyValueFactory<T, String>(bindName));
	}
	
	
	/**
	 * tabs切换事件
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void tabsAction() {
		tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            	Tab tab = (Tab)newValue;
            	log.info(" newTab:"+tab.getText());
            	if("开销".equals(tab.getText().trim())) {
            		refreshTableTGKaixiao();//刷新
            	}
            	if("玩家备注".equals(tab.getText().trim())) {
            		refreshTableTGComment();//刷新
            	}
            }
		});
	}
	
	/**
	 * 获取当前托管公司的值 
	 * 
	 * @time 2018年3月4日
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
			//加载托管公司名下的团队按钮数据  {托管公司 ：｛ 团队名称 ： 团队数据列表 ｝｝
			if(CollectUtil.isHaveValue(teamList)) {
				loadTeamBtnView(teamList);
			}
		});
		
		return companyBtn;
	}
	
	private void loadTeamBtnView(List<String> teamList) {
		ObservableList<Node> teamBtns = TG_Team_VBox.getChildren();
		TG_Team_VBox.setPrefWidth(100);
		if(CollectUtil.isHaveValue(teamBtns)) {
			teamBtns.clear();
		}
		teamList.forEach(teamId -> {
			Button teamBtn = getTeamButton(teamId);
			teamBtns.add(teamBtn);
		});
	}
	
	
	private Button getTeamButton(String teamId) {
		Button teamBtn = new Button(teamId);
		teamBtn.setPrefWidth(90);
		teamBtn.setOnAction(event -> {
			currentTGTeamLabel.setText(teamId);
//			//修改当前托管公司名称
//			currentTGCompanyLabel.setText(company);
//			//加载托管公司名下的团队按钮数据  {托管公司 ：｛ 团队名称 ： 团队数据列表 ｝｝
//			if(CollectUtil.isHaveValue(teamList)) {
//				
//			}
		});
		
		return teamBtn;
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
	 * 获取托管团队表内容
	 * @time 2018年3月6日
	 * @return
	 */
	private List<TypeValueInfo> getTableTGTeams(){
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
			list = new ArrayList<>();
		}
		tableTGTeamRate.setItems(FXCollections.observableArrayList(list));
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
	 * 删除托管团队
	 */
	public void delTGTeamRateBtnAction(ActionEvent event){
		
	}
	
	
	
	

    
    
    
    
	
}
