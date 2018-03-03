package com.kendy.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import application.Constants;
import application.DataConstans;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 处理联盟配额的控制器
 * 
 * @author 林泽涛
 * @time 2017年11月24日 下午9:31:04
 */
public class TGController implements Initializable{
	
	
	private static Logger log = Logger.getLogger(TGController.class);

//	//=====================================================================
	@FXML public VBox TG_Company_VBox; // 托管公司
	
	@FXML private VBox TG_Team_VBox; // 托管公司的内部托管团队
	
	//=====================================================================
	@FXML private TabPane tabs;
	
//	//=====================================================================自动配额表
//	@FXML private TableView<ClubQuota>  tableQuota;     
//	@FXML private TableColumn<ClubQuota,String> quotaClubName;
//	@FXML private TableColumn<ClubQuota,String> quotaJieyu;
//	@FXML private TableColumn<ClubQuota,String> quotaRest;
//	@FXML private TableColumn<ClubQuota,String> quotaHedgeFirst;
//	@FXML private TableColumn<ClubQuota,String> quotaHedgeSecond;
//	@FXML private TableColumn<ClubQuota,String> quotaHedgeThree;
//	@FXML private TableColumn<ClubQuota,String> quotaHedgeFour;
//	@FXML private TableColumn<ClubQuota,String> quotaHedgeFive;
//	
//	
//	//=====================================================================俱乐部结账表
//	@FXML private TableView<QuotaMoneyInfo>  tableQuotaPay;     
//	@FXML private TableColumn<QuotaMoneyInfo,String> quotaMoneyPaytor;
//	@FXML private TableColumn<QuotaMoneyInfo,String> quotaMoney;
//	@FXML private TableColumn<QuotaMoneyInfo,String> quotaMoneyGather;
//	
//	//=====================================================================银行信息表
//	@FXML private TableView<ClubBankInfo>  tableQuotaBank;    
//	@FXML private TableColumn<ClubBankInfo,String> clubName;
//	@FXML private TableColumn<ClubBankInfo,String> mobilePayType;
//	@FXML private TableColumn<ClubBankInfo,String> personName;
//	@FXML private TableColumn<ClubBankInfo,String> phoneNumber;
//	@FXML private TableColumn<ClubBankInfo,String> bankType;
//	@FXML private TableColumn<ClubBankInfo,String> bankAccountInfo;
//	
//	//引用联盟控制类
//	public  LMController lmController ;
//	
//	private static Button _LM_Btn1;//联盟1
//	//缓存所有俱乐部的银行卡信息
//	public static Map<String, ClubBankModel> allClubBankModels = new HashMap<>();
//	
//	//从数据库中初始化俱乐部
//	static{
//		allClubBankModels = DBUtil.getAllClubBanks();//加载 俱乐部银行卡信息数据
//		if(allClubBankModels.isEmpty()) {
//			LMController.allClubMap.values().forEach(club -> {
//				ClubBankModel model = new ClubBankModel();
//				model.setClubId(club.getClubId());
//				model.setClubName(club.getName());
//				//刷新缓存
//				allClubBankModels.put(club.getClubId(), model);
//				//插入到数据库
//				DBUtil.addOrUpdateClubBank(model);
//			});
//		}
//	}
//	
//	public TGController() {
//		super();
//	}
//
//
//	/**
//	 * @param lmController
//	 */
//	public TGController(LMController lmController) {
//		super();
//		this.lmController = lmController;
//	}
//
//
//
//	Map<String,ClubQuota> single_LM_map = new HashMap<>();
//	
//	//导入每场战绩时的所有俱乐部记录
//	public static List<Record> currentRecordList = LMController.currentRecordList;
//	
//	//{俱乐部ID : 俱乐部信息}
//	public static Map<String,Club> allClubMap = LMController.allClubMap;
//	
//	//{俱乐部ID : 俱乐部每一场信息}
//	public static Map<String,List<Record>> eachClubList = LMController.eachClubList;
//	
//	//缓存三个联盟的信息
//	public static List<Map<String,List<Record>>> LMTotalList = LMController.LMTotalList;
//	
//	public static final String[] LM = LMController.LM;
//
//	/**
//	 * DOM加载完后的事件
//	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
//		
//		_LM_Btn1 = LM_Btn1;
//		lmController = MyController.lmController;
//		
//		//绑定列值属性
//		bindCellValue(quotaClubName,quotaJieyu,quotaRest,quotaHedgeFirst,quotaHedgeSecond,quotaHedgeThree,quotaHedgeFour,quotaHedgeFive);
//		bindCellValue(quotaMoneyPaytor,quotaMoney,quotaMoneyGather);
//		bindCellValue(clubName,mobilePayType,personName,phoneNumber,bankType,bankAccountInfo);
//		//绑定列值红色属性
//		bindRedColor(quotaJieyu,quotaRest,quotaHedgeFirst,quotaHedgeSecond,quotaHedgeThree,quotaHedgeFour,quotaHedgeFive);
//		quotaJieyu.setStyle("-fx-font-weight: bold;-fx-alignment: CENTER;");
//		
//
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
//	
//	public static void autoSelectLM1() {
//		if(_LM_Btn1 != null) {
//			_LM_Btn1.fire();
//		}
//	}
//	
//	/**
//	 * 设置单个联盟最新的结余
//	 * 
//	 * @time 2017年12月18日
//	 */
//	public void set_LM_club_info() {
//		try {
//			if(LMTotalList.isEmpty()) {
//				return;
//			}
//			//{俱乐部ID : 俱乐部配额信息}
//			Map<String,ClubQuota> _single_LM_map = new HashMap<>();
//			//{联盟Index : {俱乐部ID : 俱乐部配额信息}} //后期如果性能慢的话，可以修改为这定个
//			//Map<String,Map<String,List<ClubQuota>>> totalMap = new HashMap<>();
//			int lmType = getCurrentLMType()-1;
//			Map<String, List<Record>> current_LM_Map =  LMTotalList.get(lmType);//遍历这三个
//			//LMController lmController= new LMController();
//			Map<String,List<LMSumInfo>> allClubSumMap = lmController.getAllClubSumMap(current_LM_Map);
//			allClubSumMap.forEach( (clubId,sumList) -> {
//				Club club = allClubMap.get(clubId);
//				String sumZJ = getSumZJ(sumList);
//				String yiJieshan = lmController.get_LM_YiJiesuan(club, getCurrentLMType());
//				String zhuoFei = lmController.get_LM_Zhuofei(club, getCurrentLMType());
//				String jieyu = NumUtil.digit0(
//						NumUtil.getNum(sumZJ)+NumUtil.getNum(yiJieshan)+NumUtil.getNum(zhuoFei));
//				// System.out.println(String.format("俱乐部：%s,总战绩：%s,桌费：%s,已结算：%s,===结余：%s",
//				// allClubMap.get(clubId).getName(),sumZJ,zhuoFei,yiJieshan,jieyu
//				// ));
//				List<ClubQuota> list  =  new ArrayList<>();
//				ClubQuota quota = new ClubQuota();
//				quota.setEuotaClubId(clubId);
//				quota.setQuotaClubName(club.getName());
//				quota.setQuotaJieyu(jieyu);
//				quota.setQuotaRest(jieyu);
//				_single_LM_map.put(clubId, quota);
//			});
//			single_LM_map.clear();
//			single_LM_map = _single_LM_map;
//		} catch (Exception e) {
//			ErrorUtil.err("设置单个联盟最新的结余出错，建议重启软件！",e);
//		}
//		
//	}
//	
//	/**
//	 * 获取俱乐部的总战绩
//	 * @time 2017年12月17日
//	 * @param sumList 指导出全部俱乐部总和信息Excel时的某个俱乐部信息
//	 * @return
//	 */
//	public String getSumZJ(List<LMSumInfo> sumList) {
//		return sumList.parallelStream()
//				.filter(sumInfo -> "当天总帐".equals(sumInfo.getLmSumName()))
//				.collect(Collectors.toList()).get(0).getLmSumZJ();
//	}
//	
//	/**
//	 * 设置配额表数据
//	 * @time 2017年12月18日
//	 */
//	public void setTableData() {
//		ObservableList<ClubQuota> obList = FXCollections.observableArrayList();
//		single_LM_map.forEach((clubId, clubQuota) -> {
//			obList.add(clubQuota);
//		});
//		tableQuota.setItems(obList);
//		//设置银行卡信息表
//		autoSetBankData();
//	}
//	
//	public void setTableQuota() {
//		
//	}
//	
//	/**
//	 * 点击不同联盟时清空之前的表视图数据
//	 * @time 2017年12月18日
//	 */
//	private void clearTables() {
//		tableQuota.setItems(null);
//		tableQuotaPay.setItems(null);
//		tableQuotaBank.setItems(null);
//	}
//	
//	
//	/**
//	 * 联盟1按钮
//	 * 
//	 * @time 2017年12月13日
//	 * @param event
//	 */
//    public void LM1_Btn_Action(ActionEvent event) {
//    	clearTables();
//    	currentLMLabels.setText(LM[0]);
//    	set_LM_club_info();
//    	setTableData();
//    	autoQuota();
//    }
//    
//
//
//	/**
//	 * 联盟2按钮
//	 * 
//	 * @time 2017年12月13日
//	 * @param event
//	 */
//    public void LM2_Btn_Action(ActionEvent event) {
//    	clearTables();
//    	currentLMLabels.setText(LM[1]);
//    	set_LM_club_info();
//    	setTableData();
//    	autoQuota();
//    }
//    
//	/**
//	 * 联盟3按钮
//	 * 
//	 * @time 2017年12月13日
//	 * @param event
//	 */
//    public void LM3_Btn_Action(ActionEvent event) {
//    	clearTables();
//    	currentLMLabels.setText(LM[2]);
//    	set_LM_club_info();
//    	setTableData();
//    	autoQuota();
//    }
//    
//    /**
//     * 联盟自动配额
//     * 这是本控制类最核心的代码
//     * 算法：找剩余值中的两个最大最小值进行一方清零，不断循环
//     * 
//     * @time 2017年12月18日
//     */
//    public void autoQuota() {
//    	
//    	if(tableQuota.getItems() == null || tableQuota.getItems().size()==0) {
//    		return;
//    	}
//    	
//    	boolean isDone = false;
//    	int count = 0;
//    	while(!isDone) {
//    		count++;
//    		ClubQuota row1 = getRecord(1);
//			ClubQuota row2 = getRecord(0);
//			
//			//log.info(String.format("最大值：%s::%s", row1.getQuotaClubName(),row1.getQuotaRest()));
//			//log.info(String.format("最小值：%s::%s", row2.getQuotaClubName(),row2.getQuotaRest()));
//			
//			Double first = NumUtil.getNum(row1.getQuotaRest());
//			Double  second= NumUtil.getNum(row2.getQuotaRest());
//			if(first * second >= 0) {
//				isDone = true;
//				log.info("=====================联盟配额结束！count:"+(count-1));
//				break;
//			}
//			//转换（row1永远是绝对值的大数，row2是绝对值的小数	）
//			if( Double.compare(first, second) > 0 ) {
//				if( Double.compare(Math.abs(first), Math.abs(second)) < 0 ) {
//					ClubQuota tempRow ;
//					tempRow = row1;
//					row1 = row2;
//					row2 = tempRow;
//					Double tempVal ;
//					tempVal = first;
//					first = second;
//					second = tempVal;
//					//log.info(String.format("转换最大值：%s::%s", row1.getQuotaClubName(),row1.getQuotaRest()));
//					//log.info(String.format("转换最小值：%s::%s", row2.getQuotaClubName(),row2.getQuotaRest()));
//				}
//			}
//			
//			//绝对值大数行设值
//			if(StringUtil.isBlank(row1.getQuotaHedgeFirst())) {
//				row1.setQuotaHedgeFirst(row2.getQuotaRest());
//			}else if(StringUtil.isBlank(row1.getQuotaHedgeSecond())) {
//				row1.setQuotaHedgeSecond(row2.getQuotaRest());
//			}else if(StringUtil.isBlank(row1.getQuotaHedgeThree())) {
//				row1.setQuotaHedgeThree(row2.getQuotaRest());
//			}else if(StringUtil.isBlank(row1.getQuotaHedgeFour())) {
//				row1.setQuotaHedgeFour(row2.getQuotaRest());
//			}else if(StringUtil.isBlank(row1.getQuotaHedgeFive())) {
//				row1.setQuotaHedgeFive(row2.getQuotaRest());
//			}
//			row1.setQuotaRest(NumUtil.digit0(first+second));
//			//绝对值小数行设值
//			String small = NumUtil.digit0(second * (-1));
//			if(StringUtil.isBlank(row2.getQuotaHedgeFirst())) {
//				row2.setQuotaHedgeFirst(small);
//			}else if(StringUtil.isBlank(row2.getQuotaHedgeSecond())) {
//				row2.setQuotaHedgeSecond(small);
//			}else if(StringUtil.isBlank(row2.getQuotaHedgeThree())) {
//				row2.setQuotaHedgeThree(small);
//			}else if(StringUtil.isBlank(row2.getQuotaHedgeFour())) {
//				row2.setQuotaHedgeFour(small);
//			}else if(StringUtil.isBlank(row2.getQuotaHedgeFive())) {
//				row2.setQuotaHedgeFive(small);
//			}
//			row2.setQuotaRest("0");
//			//以下做其他逻辑
//			//输出钱由输者转给赢者
//			String from , to , money = "";
//			ClubQuota winner ;
//			if(small.contains("-")) {
//				from  = row1.getQuotaClubName();
//				to = row2.getQuotaClubName();
//				money = Integer.valueOf(small.replace("-", "")).toString();
//				winner = row2;
//			}else {
//				from  = row2.getQuotaClubName();
//				to = row1.getQuotaClubName();
//				money = small;
//				winner = row1;
//			}
//			log.info(String.format("%s转%s到%s", from,money,to));
//			addRecord2TableQuotaPay(new QuotaMoneyInfo(winner.getQuotaClubId(),from,money,to));
//			
//		}
//    	tableQuotaPay.refresh();
//		tableQuota.refresh();
//		//配额最后还有剩余为负数的则全部结转到当前俱乐部
//		addNegativeRest2CurrentClub();
//    }
//    
//    /**
//     * 配额最后还有剩余为负数的则全部结转到当前俱乐部
//     * @time 2017年12月18日
//     */
//    private void addNegativeRest2CurrentClub() {
//    	String currentClubId = MyController.currentClubId.getText();
////    	Club winnerClub = allClubMap.get("555551");//555551为银河ATM的俱乐部ID
//    	Club winnerClub = allClubMap.get(currentClubId);//555551为银河ATM的俱乐部ID
//    	if(winnerClub == null) {
//    		//ErrorUtil.err("当前俱乐部"+currentClubId+"不存在！！！请添加！！");
//    		return;
//    	}
//    	tableQuota.getItems()
//    		.parallelStream()
//    		.filter( info -> NumUtil.getNum(info.getQuotaRest()) < 0)
//    		.forEach( info -> {
//    			String from , to , money = "";
//    			to = winnerClub.getName();		
//    			money = NumUtil.digit0((-1) * NumUtil.getNum(info.getQuotaRest()));
//    			from = info.getQuotaClubName();
//    			//log.info(String.format("%s转%s到%s", from,money,to));
//    			addRecord2TableQuotaPay(new QuotaMoneyInfo(winnerClub.getClubId(),from,money,to));
//    		});
//    	tableQuotaPay.refresh();
//    }
//    
//    /**
//     * 往结账表新增一条记录
//     * 应用场景：自动配额时，每配额一次就产生一条记录
//     * @time 2017年12月18日
//     * @param info
//     */
//    private void addRecord2TableQuotaPay(QuotaMoneyInfo info) {
//    	ObservableList<QuotaMoneyInfo> obList = tableQuotaPay.getItems();
//    	if(obList == null)  obList = FXCollections.observableArrayList();
//    	obList.add(info);
//    	tableQuotaPay.setItems(obList);
//    }
//    
//	/**
//	 * 获取剩余最大和最小的两行
//	 * @time 2017年12月16日
//	 * @param type 0:最小值  1：最大值 
//	 * @return
//	 */
//    private ClubQuota getRecord(int type) {
//    	if(1 ==  type )
//	    	return tableQuota.getItems().parallelStream().max(new Comparator<ClubQuota>() {  
//	            @Override  
//	            public int compare(ClubQuota o1, ClubQuota o2) {  
//	                 return NumUtil.getNum(o1.getQuotaRest()).compareTo(NumUtil.getNum(o2.getQuotaRest()));  
//	            }  
//	        }).get();  
//    	else
//	    	return tableQuota.getItems().parallelStream().min(new Comparator<ClubQuota>() {  
//	            @Override  
//	            public int compare(ClubQuota o1, ClubQuota o2) {  
//	                 return NumUtil.getNum(o1.getQuotaRest()).compareTo(NumUtil.getNum(o2.getQuotaRest()));  
//	            }  
//	        }).get();  
//    }
//    
//    
//    /**
//     * 设置该联盟所有俱乐部的银行卡信息
//     * 
//     * @time 2017年12月19日
//     */
//    private void autoSetBankData() {
//    	ObservableList<ClubBankInfo> obList = FXCollections.observableArrayList();
//    	single_LM_map.forEach( (clubId,quota) -> {
//    		ClubBankModel model = allClubBankModels.get(clubId);
//    		String clubName = quota.getQuotaClubName();
//    		if(model == null) {
//    			model = new ClubBankModel();
//    			model.setClubId(clubId);
//    			model.setClubName(clubName);
//    			model.setMobilePayType("支付宝");
//    			model.setBankType("银行卡");
//    			allClubBankModels.put(clubId, model);
//    		}
//    		ClubBankInfo bank = convert2ClubBankInfo(model);
//    		obList.add(bank);
//    	});
//    	tableQuotaBank.setItems(obList);
//    }
//
//    /**
//     * 获取当前联盟的索引
//     * 索引1表示联盟1
//     * 
//     * @time 2017年12月14日
//     * @return
//     */
//    public  int getCurrentLMType() {
//    	String currentType =  currentLMLabels.getText();
//    	int index =  StringUtil.isBlank(currentType) ? 1 : Integer.valueOf(currentType.replaceAll("联盟", ""));
//    	return index;
//    }
//    
//	/**
//	 * kendy:绑定数据域
//	 * @param colums TableColumn 可变参数
//	 */
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	private void bindCellValue(TableColumn<? extends Entity,String>... colums){
//		try {
//			for(TableColumn column : colums){
//				String fxId = column.getId();
//				column.setCellValueFactory(
//						new PropertyValueFactory<Entity,String>(fxId)
//						);
//				column.setStyle("-fx-alignment: CENTER;");
//				column.setSortable(false);//禁止排序
//			}
//		} catch (Exception e) {
//			String errMsg = "小林：绑定列值失败";
//			log.error(errMsg);
//			throw new RuntimeException(errMsg);
//		}
//	}
//	
//	/**
//	 * kendy:绑定列为红色
//	 * @time 2017年12月18日
//	 * @param colums
//	 */
//	private void bindRedColor(TableColumn<? extends Entity,String>... colums){
//		try {
//			for(TableColumn column : colums){
//				column.setCellFactory(MyController.getColorCellFactory(new ClubQuota()));
//			}
//		} catch (Exception e) {
//			String errMsg = "小林：绑定列值红色属性失败";
//			log.error(errMsg);
//			throw new RuntimeException(errMsg);
//		}
//	}
//	
//	/**
//	 * 添加一个俱乐部银行卡记录
//	 * 
//	 * @time 2017年12月19日
//	 * @param event
//	 */
//    public void addClubBankAction(ActionEvent event) {
//    	List<String> list = Arrays.asList("俱乐部ID","移动类型","姓名","手机","银行类型","银行信息");
//    	InputDialog dlg = new InputDialog();
//    	dlg.InputMultyDialog("新增",list);
//    	Optional<Map<String,String>> resultMapOpt = dlg.getMultyResult();
//    	System.out.println(resultMapOpt.toString());
//    	if(resultMapOpt.isPresent()) {
//    		Map<String,String> map = resultMapOpt.get();
//    		String clubID = StringUtil.nvl(map.get(list.get(0)),"");
//    		String mobileType = StringUtil.nvl(map.get(list.get(1)),"");
//    		String personName = StringUtil.nvl(map.get(list.get(2)),"");
//    		String phoneNumber = StringUtil.nvl(map.get(list.get(3)),"");
//    		String bankType = StringUtil.nvl(map.get(list.get(4)),"");
//    		String bankAccountInfo = StringUtil.nvl(map.get(list.get(5)),"");
//    		if(StringUtil.isAnyBlank(clubID)) {
//    			ShowUtil.show("俱乐部ID不能为空！");
//    			return;
//    		}
//    		//判断是否有该ID信息
//    		Club club = allClubMap.get(clubID);
//    		if( club == null ){
//    			ShowUtil.show("不存在此俱乐部ID:"+clubID);
//    			return;
//    		}
//    		//入库操作
//    		ClubBankModel bank = new ClubBankModel();
//    		bank.setClubId(clubID);
//    		bank.setMobilePayType(mobileType);
//    		bank.setClubName(club.getName());
//    		bank.setBankType(bankType);
//    		bank.setPersonName(personName);
//    		bank.setPhoneNumber(phoneNumber);
//    		bank.setBankAccountInfo(bankAccountInfo);
//    		boolean isAdd2DB = DBUtil.addOrUpdateClubBank(bank);
//    		
//    		//往银行信息表添加一条记录
//    		addClubBank2Table(bank);
//    		//更新缓存
//    		allClubBankModels.put(clubID,bank);
//    		ShowUtil.show("添加成功",2);
//    		
//    	}
//
//    }
//	
//    /**
//     * 往银行信息表添加一条记录
//     * @time 2017年12月19日
//     * @param model
//     */
//    private void addClubBank2Table(ClubBankModel model) {
//    	//转为实体
//    	ClubBankInfo bank = convert2ClubBankInfo(model);
//    	
//    	//加进表中
//		ObservableList<ClubBankInfo> obList = tableQuotaBank.getItems();
//		if( obList == null) {
//			obList = FXCollections.observableArrayList();
//		}
//		obList.add(bank);
//		tableQuotaBank.refresh();
//    }
//    
//    
//   //转为实体(传入参数)
//    private ClubBankInfo convert2ClubBankInfo(ClubBankModel model,ClubBankInfo bank) {
//		bank.setClubId(model.getClubId());
//		bank.setMobilePayType(model.getMobilePayType());
//		bank.setClubName(model.getClubName());
//		bank.setBankType(model.getBankType());
//		bank.setPersonName(model.getPersonName());
//		bank.setPhoneNumber(model.getPhoneNumber());
//		bank.setBankAccountInfo(model.getBankAccountInfo());
//		return bank;
//    }
//    //转为实体
//    private ClubBankInfo convert2ClubBankInfo(ClubBankModel model) {
//    	ClubBankInfo bank = new ClubBankInfo();
//		bank.setClubId(model.getClubId());
//		bank.setMobilePayType(model.getMobilePayType());
//		bank.setClubName(model.getClubName());
//		bank.setBankType(model.getBankType());
//		bank.setPersonName(model.getPersonName());
//		bank.setPhoneNumber(model.getPhoneNumber());
//		bank.setBankAccountInfo(model.getBankAccountInfo());
//		return bank;
//    }
//    
//    //转为模型
//    private ClubBankModel convert2ClubBankModel(ClubBankInfo info) {
//    	ClubBankModel model = new ClubBankModel();
//    	model.setClubId(info.getClubId());
//    	model.setMobilePayType(info.getMobilePayType());
//    	model.setClubName(info.getClubName());
//    	model.setBankType(info.getBankType());
//    	model.setPersonName(info.getPersonName());
//    	model.setPhoneNumber(info.getPhoneNumber());
//    	model.setBankAccountInfo(info.getBankAccountInfo());
//    	return model;
//    }
//    
//    /**
//     * 获取选中的俱乐部银行卡记录
//     * @time 2017年12月19日
//     * @return
//     */
//    private ClubBankInfo getSelectClubBank() {
//    	if(tableQuotaBank.getItems() != null )
//    			return tableQuotaBank.getSelectionModel().getSelectedItem();
//    	
//    	return null;
//    }
//    
//    /**
//     * 修改俱乐部银行卡记录
//     * @time 2017年12月19日
//     * @param event
//     */
//    public void updateClubBankAction(ActionEvent event) {
//    	ClubBankInfo item = getSelectClubBank();
//    	if(item == null){
//    		ShowUtil.show("请先选择要修改的银行卡记录！");
//    		return;
//    	}
//    	Map<String,String> paramMap = new LinkedHashMap<>();
//    	paramMap.put("俱乐部名称", item.getClubName());
//    	paramMap.put("移动类型", item.getMobilePayType());
//    	paramMap.put("姓名", item.getPersonName());
//    	paramMap.put("手机", item.getPhoneNumber());
//    	paramMap.put("银行类型", item.getBankType());
//    	paramMap.put("银行信息", item.getBankAccountInfo());
//    	
//    	InputDialog dlg = new InputDialog();
//    	dlg.InputMultyDialog("修改",paramMap);
//    	Optional<Map<String,String>> resultMapOpt = dlg.getMultyResult();
//    	System.out.println(resultMapOpt.toString());
//    	if(resultMapOpt.isPresent()) {
//    		Map<String,String> map = resultMapOpt.get();
//    		String clubName = StringUtil.nvl(map.get("俱乐部名称"),"");
//    		String mobileType = StringUtil.nvl(map.get("移动类型"),"");
//    		String personName = StringUtil.nvl(map.get("姓名"),"");
//    		String phoneNumber = StringUtil.nvl(map.get("手机"),"");
//    		String bankType = StringUtil.nvl(map.get("银行类型"),"");
//    		String bankAccountInfo = StringUtil.nvl(map.get("银行信息"),"");
//    		if(StringUtil.isAnyBlank(clubName)) {
//    			ShowUtil.show("俱乐部名称不能为空！");
//    			return;
//    		}
//    		//修改银行信息表
//    		ClubBankModel model = new ClubBankModel();
//    		model.setClubId(item.getClubId());
//    		model.setMobilePayType(mobileType);
//    		model.setClubName(clubName);
//    		model.setBankType(bankType);
//    		model.setPersonName(personName);
//    		model.setPhoneNumber(phoneNumber);
//    		model.setBankAccountInfo(bankAccountInfo);
//    		boolean isUpdate2DB = DBUtil.addOrUpdateClubBank(model);
//    		
//    		//更新缓存
//    		allClubBankModels.put(item.getClubId(),model);
//    		//刷新表
//    		convert2ClubBankInfo(model,item);
//    		tableQuotaBank.refresh();
//    		ShowUtil.show("修改成功",2);
//    	}
//    }
//    
//	
//	/**********************************************************************************
//	 * 
//	 *                                     导出Excel
//	 *  
//	 ***********************************************************************************/
//    public void exportClubPayExcel(ActionEvent event) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//    	// 标题
//		String title = currentLMLabels.getText() + "俱乐部结账单-"+sdf.format(new Date());
//    	// 列名
//    	String[] rowName = new String[]{"付款方","收款方","转账","支付类型","联系人","手机","银行卡信息"};;
//    	// 输出
//    	String out = "D:/"+title+System.currentTimeMillis();
//    	// 数据
//    	ObservableList<QuotaMoneyInfo> obList = tableQuotaPay.getItems();
//    	if(CollectUtil.isNullOrEmpty(obList)) {
//    		ShowUtil.show("没有需要导出的数据！");
//    		return;
//    	}
//    	List<Object[]>  dataList = new ArrayList<Object[]>();
//	    Object[] objs = null;
//	    String clubId = "";
//	    for(QuotaMoneyInfo info : obList) {
//	    	ClubBankModel model = allClubBankModels.get(info.getQuotaMoneyClubId());
//	        objs = new Object[rowName.length];
//	        objs[0] = info.getQuotaMoneyPaytor();
//	        objs[1] = info.getQuotaMoneyGather();
//	        objs[2] = "转" + info.getQuotaMoney() + "到";
//	        objs[3] = model.getMobilePayType();
//	        objs[4] = model.getPersonName();
//	        objs[5] = model.getPhoneNumber();
//	        objs[6] = model.getBankAccountInfo();
//	        dataList.add(objs);
//	    }
//    	ExportQuotaPayExcel excel = new ExportQuotaPayExcel(title, rowName,  dataList, out);
//    	try {
//    		excel.export();
//			log.info("导出单个联盟俱乐部结账单完成！");
//		} catch (Exception e) {
//			ErrorUtil.err("导出单个联盟俱乐部结账单失败", e);
//		}
//    	
//    }
    
    
    
    
	
}
