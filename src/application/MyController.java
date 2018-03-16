package application;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.controller.CombineIDController;
import com.kendy.controller.GDController;
import com.kendy.controller.LMController;
import com.kendy.controller.QuotaController;
import com.kendy.controller.TGController;
import com.kendy.db.DBConnection;
import com.kendy.db.DBUtil;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.DangjuInfo;
import com.kendy.entity.DangtianHuizongInfo;
import com.kendy.entity.HistoryRecord;
import com.kendy.entity.Huishui;
import com.kendy.entity.JiaoshouInfo;
import com.kendy.entity.JifenInfo;
import com.kendy.entity.KaixiaoInfo;
import com.kendy.entity.MemberZJInfo;
import com.kendy.entity.PingzhangInfo;
import com.kendy.entity.Player;
import com.kendy.entity.ProfitInfo;
import com.kendy.entity.ProxySumInfo;
import com.kendy.entity.ProxyTeamInfo;
import com.kendy.entity.ShangmaDetailInfo;
import com.kendy.entity.ShangmaInfo;
import com.kendy.entity.TeamHuishuiInfo;
import com.kendy.entity.TeamInfo;
import com.kendy.entity.TotalInfo;
import com.kendy.entity.UserInfos;
import com.kendy.entity.WaizhaiInfo;
import com.kendy.entity.WanjiaInfo;
import com.kendy.entity.ZijinInfo;
import com.kendy.entity.ZonghuiInfo;
import com.kendy.entity.ZonghuiKaixiaoInfo;
import com.kendy.excel.ExcelReaderUtil;
import com.kendy.interfaces.Entity;
import com.kendy.other.Wrap;
import com.kendy.service.JifenService;
import com.kendy.service.MemberService;
import com.kendy.service.MoneyService;
import com.kendy.service.ShangmaService;
import com.kendy.service.TeamProxyService;
import com.kendy.service.WaizhaiService;
import com.kendy.service.ZonghuiService;
import com.kendy.util.ClipBoardUtil;
import com.kendy.util.CollectUtil;
import com.kendy.util.ConsUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.InputDialog;
import com.kendy.util.NumUtil;
import com.kendy.util.RandomUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TableUtil;
import com.kendy.util.Text2ImageUtil;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Pair;

/**
 * 财务软件主界面的组件控制器
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:55:48
 */
public class MyController implements Initializable{
    
	Logger log = Logger.getLogger(MyController.class);
	
	private static final String ZERO = "0";
	private static final String INDEX_ZERO = "第0局";
	
	public static DataConstans cons1 = new DataConstans();
	//自动导入下一场战绩Excel的缓存队列
	private static Queue<File> excelQueue = new ArrayBlockingQueue<>(1000);
	
	@FXML private TextField sysCode; //系统编码
	@FXML private TextField membersDir; //人员名单Excel路径
	@FXML private TextField huishuiDir; //回水Excel路径
	@FXML private TextField preDataDir; //回水Excel路径
    @FXML private TextField excelDir; //excel文件夹路径
    @FXML private Label lable_currentClubId; //当前俱乐部ID
    @FXML public static Label currentClubId; //当前俱乐部ID
    @FXML private TextField teamIdField; //新增团队ID
    @FXML private TextField teamNameField; //新增团队名称 
    @FXML  private TextField huishui; //新增团队回水
    @FXML private TextField insuranceRate; //新增保险比例
    @FXML private TextField gudongInput; //新增股东名称
    @FXML  private ListView gudongListView; //所有股东名称
    @FXML private Label indexLabel;//第几局
    @FXML  private Label dbConnectionState;//数据库连接状态
    @FXML  private TextField searchText;//
    @FXML private Button importHuishuiBtn;
    @FXML private Button importMembersBtn;
    @FXML private Button importPreDataBtn;
    @FXML private Hyperlink delCurrentMoneyBtn;
    
    @FXML private TextField combineIdDir;
    
    @FXML private RadioButton whiteVersionOld;
    @FXML private RadioButton whiteVersionNew;
    
    @FXML private RadioButton radio_autoTest_yes;
    @FXML private RadioButton radio_autoTest_no;
    @FXML private HBox hbox_autoTestMode;
    
    
    //=================================================TabPane
    @FXML private TabPane tabs;
    
    //=================================================第一个tableView
    @FXML private TableView<TotalInfo> tableTotalInfo;
    
	@FXML private TableColumn<TotalInfo,String> tuan;//团
	@FXML private TableColumn<TotalInfo,String> wanjiaId;//ID
	@FXML private TableColumn<TotalInfo,String> wanjia;//玩家
	@FXML private TableColumn<TotalInfo,String> jifen;//计分
	@FXML private TableColumn<TotalInfo,String> shishou;//实收
	@FXML private TableColumn<TotalInfo,String> baoxian;//保险
	@FXML private TableColumn<TotalInfo,String> chuHuishui;//出回水
	@FXML private TableColumn<TotalInfo,String> baohui;//保回
	
	@FXML private TableColumn<TotalInfo,String> shuihouxian;//水后险
	@FXML private TableColumn<TotalInfo,String> shouHuishui;//收回水
	@FXML private TableColumn<TotalInfo,String> heLirun;//合利润

    //=================================================实时金额表tableView
    @FXML
	public TableView<CurrentMoneyInfo> tableCurrentMoneyInfo;
    
    @FXML private TableColumn<CurrentMoneyInfo,String> cmSuperIdSum;//总和
	@FXML private TableColumn<CurrentMoneyInfo,String> mingzi;//名字
	@FXML private TableColumn<CurrentMoneyInfo,String> shishiJine;//实时金额
	@FXML private TableColumn<CurrentMoneyInfo,String> cmiEdu;//实时金额
	
	//=================================================资金表tableView
	@FXML private TableView<ZijinInfo> tableZijin;
	
	@FXML private TableColumn<ZijinInfo,String> zijinType;
	@FXML private TableColumn<ZijinInfo,String> zijinAccount;
	//=================================================利润表tableView
	@FXML public TableView<ProfitInfo> tableProfit;
	
	@FXML private TableColumn<ProfitInfo,String> profitType;
	@FXML private TableColumn<ProfitInfo,String> profitAccount;
	//=================================================开销表tableView
	@FXML private TableView<KaixiaoInfo> tableKaixiao;
	
	@FXML private TableColumn<KaixiaoInfo,String> kaixiaoType;
	@FXML private TableColumn<KaixiaoInfo,String> kaixiaoMoney;
	//=================================================当局表tableView
	@FXML private TableView<DangjuInfo> tableDangju;
	
	@FXML private TableColumn<DangjuInfo,String> type;
	@FXML private TableColumn<DangjuInfo,String> money;
	//=================================================交收表tableView
	@FXML private TableView<JiaoshouInfo> tableJiaoshou;
	
	@FXML private TableColumn<JiaoshouInfo,String> jiaoshouType;
	@FXML private TableColumn<JiaoshouInfo,String> jiaoshouMoney;
	//=================================================交收表tableView
	@FXML private TableView<PingzhangInfo> tablePingzhang;
	
	@FXML private TableColumn<PingzhangInfo,String> pingzhangType;
	@FXML private TableColumn<PingzhangInfo,String> pingzhangMoney;
	
	//=================================================牌局表tableView
	@FXML private TableView<WanjiaInfo> tablePaiju;
	
	@FXML private TableColumn<WanjiaInfo,String> paiju;//名字
	@FXML private TableColumn<WanjiaInfo,String> wanjiaName;//实时金额
	@FXML private TableColumn<WanjiaInfo,String> zhangji;//实时金额
	@FXML private TableColumn<WanjiaInfo,String> yicunJifen;//实时金额
	@FXML private TableColumn<WanjiaInfo,String> heji;//实时金额
	@FXML private TableColumn<WanjiaInfo,String> pay;//支付
	@FXML private TableColumn<WanjiaInfo,String> copy;//复制
	
	//=================================================牌局表tableView
	@FXML public TableView<TeamInfo> tableTeam;
	
	@FXML private TableColumn<TeamInfo,String> teamID;//团ID
	@FXML private TableColumn<TeamInfo,String> teamZJ;//团战绩
	@FXML private TableColumn<TeamInfo,String> teamHS;//团回水
	@FXML private TableColumn<TeamInfo,String> teamBS;//团保险
	@FXML private TableColumn<TeamInfo,String> teamSum;//行总和
	@FXML private TableColumn<TeamInfo,String> teamJiesuan;//结算按钮
	
	
	//===========================================联盟对帐
	@FXML private Button lianmengBtn;
	@FXML private Label LMLabel;
	
	//===========================================分页控件
	@FXML private TextField pageInput;
	
	@FXML private Button refreshBtn;
	@FXML private Button lockDangjuBtn;
	@FXML private Button openKaixiaoDialogBtn;
	@FXML private Button importZJBtn;
	@FXML private Button delKaixiaoBtn;
	@FXML private HBox importZJHBox;
	
//	@FXML private Button checkExcelBtn;
	@FXML private Hyperlink addCurrentMoneyLink;
	@FXML private Hyperlink delCurrentMoneyLink;
	@FXML private Label lockedLabel;
	@FXML private Label dateLabel;
	//===============================================================代理查询Tab
	@FXML private TableView<ProxyTeamInfo> tableProxyTeam;
	@FXML private HBox proxySumHBox;//每列上的总和
	@FXML private TableColumn<ProxyTeamInfo,String> proxyTeamId ;//团ID
	@FXML private TableColumn<ProxyTeamInfo,String> proxyPlayerId;//玩家ID
	@FXML private TableColumn<ProxyTeamInfo,String> proxyPlayerName;//玩家名称
	@FXML private TableColumn<ProxyTeamInfo,String> proxyYSZJ;//原始战绩
	@FXML private TableColumn<ProxyTeamInfo,String> proxyZJ;//战绩
	@FXML private TableColumn<ProxyTeamInfo,String> proxyBaoxian;//保险
	@FXML private TableColumn<ProxyTeamInfo,String> proxyHuishui;//回水
	@FXML private TableColumn<ProxyTeamInfo,String> proxyHuiBao;//回保
	@FXML private TableColumn<ProxyTeamInfo,String> proxyTableId;//场次

	@FXML private ComboBox<String> teamIDCombox;//团队ID下拉框
	@FXML private CheckBox isZjManage;//团对应的战绩是否被管理
	@FXML private CheckBox hasTeamBaoxian;//导出是否团队无保险
	@FXML private Label proxyDateLabel;
	
	@FXML private TableView<ProxySumInfo> tableProxySum;
	@FXML private TableColumn<ProxySumInfo,String> proxySumType;
	@FXML private TableColumn<ProxySumInfo,String> proxySum;
	@FXML private TextField proxyHSRate;//回水比例
	@FXML private TextField proxyHBRate;//回保比例
	@FXML private TextField proxyFWF;//服务费大于多少有效
	//===============================================================汇总Tab
	@FXML private TableView<ZonghuiInfo> tableZonghui;
	@FXML private TableColumn<ZonghuiInfo,String> zonghuiTabelId;
	@FXML private TableColumn<ZonghuiInfo,String> zonghuiFuwufei;
	@FXML private TableColumn<ZonghuiInfo,String> zonghuiBaoxian;
	@FXML private TableColumn<ZonghuiInfo,String> zonghuiHuishui;
	@FXML private TableColumn<ZonghuiInfo,String> zonghuiHuiBao;
	@FXML private ListView<String> juTypeListView;//局类型ListView
	
	@FXML private TableView<DangtianHuizongInfo> tableDangtianHuizong;
	@FXML private TableColumn<DangtianHuizongInfo,String> huizongType;
	@FXML private TableColumn<DangtianHuizongInfo,String> huizongMoney;
	
	@FXML private TableView<ZonghuiKaixiaoInfo> tableZonghuiKaixiao;
	@FXML private TableColumn<ZonghuiKaixiaoInfo,String> zonghuiKaixiaoType;
	@FXML private TableColumn<ZonghuiKaixiaoInfo,String> zonghuiKaixiaoMoney;
	//===============================================================会员查询Tab
	@FXML private TableView<MemberZJInfo> tableMemberZJ;
	@FXML private TableColumn<MemberZJInfo,String> memberJu;
	@FXML private TableColumn<MemberZJInfo,String> memberZJ;
	@FXML private Label memberDateStr;//会员当天战绩的时间
	@FXML private Label memberPlayerId;//会员当天战绩的会员ID
	@FXML private Label memberPlayerName;//会员当天战绩的会员名称
	@FXML private Label memberSumOfZJ;//会员当天战绩的战绩总和
	@FXML private Label memberTotalZJ;//会员历史战绩的战绩总和
	@FXML private TextField memberSearchName;//会员名称
	@FXML private ListView<String> memberListView;//模糊搜索的人名列表
	
	//===============================================================上码查询Tab
	@FXML private TableView<ShangmaInfo> tableShangma;
	@FXML private TableColumn<ShangmaInfo,String> shangmaName;
	@FXML private TableColumn<ShangmaInfo,String> shangmaEdu;
	@FXML private TableColumn<ShangmaInfo,String> shangmaAvailableEdu;
	@FXML private TableColumn<ShangmaInfo,String> shangmaYCJF;//已存积分=实时金额
	@FXML private TableColumn<ShangmaInfo,String> shangmaYiSM;//已上码
	@FXML private TableColumn<ShangmaInfo,String> shangmaSumOfZJ;
	@FXML private TableColumn<ShangmaInfo,String> shangmaPlayerId;
	@FXML private TableColumn<ShangmaInfo,String> shangmaLianheEdu;
//	@FXML private TableColumn<ShangmaInfo,String> shangmaShishou;
//	@FXML private TableColumn<ShangmaInfo,String> shangmaJu;//第多少局(自己初始化单元格)
	@FXML private VBox shangmaVBox;//用于初始化团队ID按钮
	@FXML private Label shangmaTeamId;//显示栏上的团队ID
	@FXML private Label shangmaZSM;//团队总上码
	@FXML private Label shangmaZZJ;//团队总战绩
	@FXML private TextField shangmaSearch;//上码的输入框
	
	@FXML private TextField teamYajin;//团队押金
	@FXML private TextField teamEdu;//团队额度
	@FXML private Label teamShangmaAvailable;//团队可上码
	
	@FXML private TableView<ShangmaDetailInfo> tableShangmaDetail;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaDetailName;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaJu;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaSM;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaShishou;
	
	@FXML private TableView<ShangmaDetailInfo> tableShangmaNextDay;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaNextDayName;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaNextDayJu;
	@FXML private TableColumn<ShangmaDetailInfo,String> shangmaNextDaySM;
	
	//===============================================================外债
	@FXML private TableView<WaizhaiInfo> tableWaizhai;
	@FXML private TableColumn<WaizhaiInfo,String> waizhaiType;
	@FXML private TableColumn<WaizhaiInfo,String> waizhaiMoney;
	@FXML private HBox waizhaiHBox;//里面包含多个表
	//============================================================================积分查询
	@FXML private TableView<JifenInfo> tableJifen;
	@FXML private TableColumn<JifenInfo,String> jfRank;
	@FXML private TableColumn<JifenInfo,String> jfPlayerName;
	@FXML private TableColumn<JifenInfo,String> jfValue;
	@FXML private DatePicker jfStartTime;
	@FXML private DatePicker jfEndTime;
	@FXML private ComboBox<String> jfTeamIDCombox;//团队ID下拉框	
	@FXML private TextField jifenInput;//团队积分值
	@FXML private TextField jifenRankLimit;//前50名
	
	
	public static LMController lmController = new LMController();
	public static TableView<ProfitInfo> table_Profit;
	
	public static TGController tgController; //托管控制类
	
	
	/* 每点击结算按钮就往这个静态变更累加（只针对当局）
	 * 撤销时清空为0
	 * 锁定时清空为0
	 * 平帐时与上场的总团队服务费相加
	 */
	public static Double current_Jiesuaned_team_fwf_sum = 0d;
	
    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
	public MyController() {
		super();
		log.info("MyController 构造方法完成");
	}

	/**
	 * 节点加载完后需要进行的一些初始化操作
	 * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		table_Profit = this.tableProfit;
		//第一次打开主窗口时设置当前俱乐部ID值
		String clubIdValue = PropertiesUtil.readProperty("clubId");
		currentClubId = lable_currentClubId;
		if(clubIdValue != null)
			lable_currentClubId.setText(clubIdValue);
		
		//第一次打开主窗口时显示所有股东
//		//股东
		String gudongs = PropertiesUtil.readProperty("gudong");
		DataConstans.gudongList = new ArrayList<>();
		if(!StringUtil.isBlank(gudongs)){
			for(String gudong : gudongs.split(",")){
				DataConstans.gudongList.add(gudong);
			}
		}
		for(String gd : DataConstans.gudongList)
			gudongListView.getItems().add(gd);
		
		//模拟初始化第一个tableView
		//kendy:绑定数据域
		pay.setCellFactory(cellFactory);//支付按钮
		//绑定玩家信息表
		bindCellValue(tuan,wanjiaId,wanjia,jifen,shishou,baoxian,
				chuHuishui,baohui,shuihouxian,heLirun,shouHuishui);
		jifen.setCellFactory(getColorCellFactory(new TotalInfo()));
		shishou.setCellFactory(getColorCellFactory(new TotalInfo()));
		baoxian.setCellFactory(getColorCellFactory(new TotalInfo()));
		shuihouxian.setCellFactory(getColorCellFactory(new TotalInfo()));
		chuHuishui.setCellFactory(getColorCellFactory(new TotalInfo()));
		//绑定牌局表
		bindCellValue(paiju,wanjiaName,zhangji,yicunJifen,heji);
		pay.setCellFactory(cellFactory);//支付按钮：单独出来 
		pay.setStyle("-fx-alignment: CENTER;");
		copy.setCellFactory(cellFactoryCopy);//复制按钮：单独出来 
		copy.setStyle("-fx-alignment: CENTER;");
		zhangji.setCellFactory(getColorCellFactory(new WanjiaInfo()));
		yicunJifen.setCellFactory(getColorCellFactory(new WanjiaInfo()));
		heji.setCellFactory(getColorCellFactory(new WanjiaInfo()));

		//绑定实时金额表
		tableCurrentMoneyInfo.setEditable(true);
		bindCellValue(cmSuperIdSum,mingzi,shishiJine,cmiEdu);
		cmSuperIdSum.setStyle("-fx-alignment: CENTER;-fx-font-weight: bold;");
                        
		shishiJine.setCellFactory(TextFieldTableCell.forTableColumn());
//		shishiJine.setCellFactory(redAndEditCellFactory);
		shishiJine.setOnEditCommit(
				new EventHandler<CellEditEvent<CurrentMoneyInfo, String>>() {
					@Override
					public void handle(CellEditEvent<CurrentMoneyInfo, String> t) {
						String oldValue = t.getOldValue();
						//修改原值
						CurrentMoneyInfo cmInfo = (CurrentMoneyInfo) t.getTableView().getItems().get(
								t.getTablePosition().getRow());
						
						if(cmInfo != null && !StringUtil.isBlank(cmInfo.getMingzi())){
							//更新到已存积分
							boolean isChangedOK = MoneyService.changeYicunJifen(tablePaiju,cmInfo.getMingzi(),t.getNewValue());
							if(isChangedOK) {
								cmInfo.setShishiJine(t.getNewValue());
							}else {
								cmInfo.setShishiJine(t.getOldValue());
								String ss = t.getTableView().getItems().get(
										t.getTablePosition().getRow()).getShishiJine();
								tableCurrentMoneyInfo.refresh();
							}
							MoneyService.flush_SSJE_table();//最后刷新实时金额表
						}else if(cmInfo != null) {
							cmInfo.setShishiJine(null);
							ShowUtil.show("空行不能输入", 1);
							tableCurrentMoneyInfo.refresh();
						}
					}
					
				}
		);
		
		//绑定资金表
		tableZijin.setEditable(true);
		bindCellValue(zijinType,zijinAccount);
		//zijinAccount.setCellFactory(TextFieldTableCell.forTableColumn());
		zijinType.setCellFactory(zijinCellFactory);
		zijinAccount.setCellFactory(getColorCellFactory(new ZijinInfo()));
		
		//绑定利润表
		bindCellValue(profitType,profitAccount);
		profitAccount.setCellFactory(getColorCellFactory(new ProfitInfo()));
		//绑定实时开销表
		bindCellValue(kaixiaoType,kaixiaoMoney);
		kaixiaoMoney.setCellFactory(getColorCellFactory(new KaixiaoInfo()));
		//绑定实时当局表
		bindCellValue(type,money);
		money.setCellFactory(getColorCellFactory(new DangjuInfo()));
		//绑定交收表
		bindCellValue(jiaoshouType,jiaoshouMoney);
		jiaoshouMoney.setCellFactory(getColorCellFactory(new JiaoshouInfo()));
		//绑定平帐表
		bindCellValue(pingzhangType,pingzhangMoney);
		pingzhangMoney.setCellFactory(getColorCellFactory(new PingzhangInfo()));
		//绑定团队表
		bindCellValue(teamID,teamZJ,teamHS,teamBS,teamSum);
		teamJiesuan.setCellFactory(cellFactoryJiesuan);
		teamJiesuan.setStyle("-fx-alignment: CENTER;");
		teamZJ.setCellFactory(getColorCellFactory(new TeamInfo()));
		teamBS.setCellFactory(getColorCellFactory(new TeamInfo()));
		teamHS.setCellFactory(getColorCellFactory(new TeamInfo()));
		teamSum.setCellFactory(getColorCellFactory(new TeamInfo()));
		//绑定代理查询表（团队当天查询）
		bindCellValue(proxyPlayerId,proxyPlayerName,proxyYSZJ,proxyZJ,proxyBaoxian,proxyHuishui,proxyHuiBao,proxyTableId);
		proxyYSZJ.setCellFactory(getColorCellFactory(new ProxyTeamInfo()));
		proxyZJ.setCellFactory(getColorCellFactory(new ProxyTeamInfo()));
		proxyBaoxian.setCellFactory(getColorCellFactory(new ProxyTeamInfo()));
		
		//绑定代理查询中的合计表
		bindCellValue(proxySumType,proxySum);
		proxySum.setCellFactory(getColorCellFactory(new ProxySumInfo()));
		//绑定汇总信息表（当天每一局的团队汇总查询）
		bindCellValue(zonghuiTabelId,zonghuiFuwufei,zonghuiBaoxian,zonghuiHuishui,zonghuiHuiBao);
		zonghuiBaoxian.setCellFactory(getColorCellFactory(new ZonghuiInfo()));
		zonghuiHuishui.setCellFactory(getColorCellFactory(new ZonghuiInfo()));
		zonghuiHuiBao.setCellFactory(getColorCellFactory(new ZonghuiInfo()));
		//绑定汇总查询中的当天汇总表
		bindCellValue(huizongType,huizongMoney);
		huizongMoney.setCellFactory(getColorCellFactory(new DangtianHuizongInfo()));
		//绑定汇总查询中的开销表表
		bindCellValue(zonghuiKaixiaoType,zonghuiKaixiaoMoney);
		zonghuiKaixiaoMoney.setCellFactory(getColorCellFactory(new ZonghuiKaixiaoInfo()));
		//绑定会员查询中的会员当天战绩表
		bindCellValue(memberJu,memberZJ);
		memberZJ.setCellFactory(getColorCellFactory(new MemberZJInfo()));
		//绑定实时上码表
		bindCellValue(shangmaLianheEdu,shangmaName,shangmaEdu,shangmaAvailableEdu,shangmaYCJF,shangmaYiSM,shangmaSumOfZJ,shangmaPlayerId);//,shangmaShishou,shangmaJu
		tableShangma.setRowFactory(new Callback<TableView<ShangmaInfo>, TableRow<ShangmaInfo>>() {  
            @Override  
            public TableRow<ShangmaInfo> call(TableView<ShangmaInfo> param) {  
                return new TableRowControl(tableShangma);  
            }  
        });  
//		shangmaPlayerId.setCellFactory(shangmaLeftNameCellFactory);
//		shangmaName.setCellFactory(shangmaLeftNameCellFactory);
//		shangmaEdu.setCellFactory(shangmaLeftNameCellFactory);
//		shangmaSumOfZJ.setCellFactory(shangmaLeftNameCellFactory);
//		shangmaYiSM.setCellFactory(shangmaLeftNameCellFactory);
		//shangmaYCJF.setCellFactory(shangmaLeftNameCellFactory);
		
		shangmaLianheEdu.setCellFactory(getColorCellFactory(new ShangmaInfo()));
		shangmaAvailableEdu.setCellFactory(getColorCellFactory(new ShangmaInfo()));//red_NotEdit_CellFactory
		shangmaYCJF.setCellFactory(getColorCellFactory(new ShangmaInfo()));
		
		//绑定外债信息表
		bindCellValue(waizhaiType,waizhaiMoney);
		waizhaiMoney.setCellFactory(getColorCellFactory(new WaizhaiInfo()));
		
		
		//绑定上码个人信息表
		tableShangmaDetail.setEditable(true);
		bindCellValue(shangmaDetailName,shangmaJu,shangmaSM,shangmaShishou);
		shangmaDetailName.setCellFactory(ShangmaNameCellFactory);
		shangmaJu.setCellFactory(ShangmaNameCellFactory);
		shangmaSM.setCellFactory(ShangmaNameCellFactory);
		shangmaShishou.setCellFactory(ShangmaNameCellFactory);
		tableShangma.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {    
			        @Override    
			        public void changed(ObservableValue observable,Object oldValue, Object newValue) {
			        	ShangmaInfo smInfo = (ShangmaInfo)newValue;
			        	//加载右边的个人详情
			        	if(smInfo != null) {
			        		String playerId = smInfo.getShangmaPlayerId();
			        		if(!StringUtil.isBlank(playerId)) {
			        			ShangmaService.loadSMDetailTable(playerId);
			        			ShangmaService.loadSMNextDayTable(playerId);
			        		}
			        	}
			        	else
			        		tableShangmaDetail.setItems(null);
			        }    
			}); 
		
		//绑定次日信息表
		shangmaNextDayName.setCellValueFactory(new PropertyValueFactory<ShangmaDetailInfo,String>("shangmaDetailName"));
		shangmaNextDayName.setCellFactory(ShangmaNameNextdayCellFactory);
		shangmaNextDayName.setStyle("-fx-alignment: CENTER;");
		shangmaNextDayJu.setCellValueFactory(new PropertyValueFactory<ShangmaDetailInfo,String>("shangmaJu"));
		shangmaNextDayJu.setCellFactory(ShangmaNameNextdayCellFactory);
		shangmaNextDayJu.setStyle("-fx-alignment: CENTER;");
		shangmaNextDaySM.setCellValueFactory(new PropertyValueFactory<ShangmaDetailInfo,String>("shangmaSM"));
		shangmaNextDaySM.setCellFactory(ShangmaNameNextdayCellFactory);
		shangmaNextDaySM.setStyle("-fx-alignment: CENTER;");

		
		//绑定积查询表
		bindCellValue(jfRank,jfPlayerName,jfValue);
		
		
		//////初始化实时金额表
		MoneyService.iniitMoneyInfo(tableCurrentMoneyInfo);
		
//		tableTotalInfo.setItems(tableTotalInfoList);
		
//		tableKaixiao.setItems(FXCollections.observableArrayList(
//				new KaixiaoInfo("测试列","12")
//				));
		
		
		////////////////////总汇表中的初始化
		juTypeListView.getItems().add("合局");
		
		LMLabel.setTextFill(Color.web("#CD3700"));
		indexLabel.setTextFill(Color.web("#0076a3"));// 设置Label 的文本颜色。
		indexLabel.setFont(new Font("Arial", 30));
		
		////////////////////代理查询中的团队回水选择
		TeamProxyService.initTeamProxy(
				tableProxyTeam,proxySumHBox,teamIDCombox,isZjManage,proxyDateLabel,tableProxySum,proxyHSRate,proxyHBRate,proxyFWF,hasTeamBaoxian);
		////////////////////代理查询中的团队回水选择
		TeamProxyService.initTeamSelectAction(teamIDCombox,isZjManage,tableProxyTeam,proxySumHBox);
		////////////////////会员服务类
		MemberService.initMemberQuery(memberListView,tableMemberZJ,memberDateStr,memberPlayerId,memberPlayerName,memberSumOfZJ,memberTotalZJ);
		
		tabsAction();
		////////////////////实时上马系统
		initShanagma();
		
		///////////////合并ID
		CombineIDController.initCombineIdController(tableCurrentMoneyInfo);
		
		////////////积分查询
		JifenService.initJifenService(jfTeamIDCombox);
		
		//是否启动测试模式
		initAutoTestMode();
		
		//选择导入白名单的版本
		initWhiteVersion();
		
		
//		try {
//			FXMLLoader loader = new FXMLLoader();
//			Parent root = loader.load(getClass().getResource("/com/kendy/dialog/team_proxy_frame.fxml").openStream());
//			loader.setController(new TeamProxyController());
////			loader.setController(ControllerManager.teamProxyController);
//			//lmController = loader.getController();
//			Tab tab1 = new Tab();
//			tab1.setText("代理2查询");
//			tab1.setClosable(false);
//			tab1.setContent(root);
//			tabs.getTabs().add(tab1);  
//			
//		} catch (IOException e) {
//			ErrorUtil.err("代理2查询加载失败", e);
//		}
		
	    try {
	    	FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource("/com/kendy/dialog/LM_Tab_Fram.fxml").openStream());
			//loader.setController(lmController);
			lmController = loader.getController();
			Tab tab1 = new Tab();
		    tab1.setText("联盟对账");
		    tab1.setClosable(false);
			tab1.setContent(root);
			tabs.getTabs().add(tab1);  
			
		} catch (IOException e) {
			ErrorUtil.err("联盟tab加载失败", e);
		}
	    
	    
	    try {
	    	FXMLLoader loader = new FXMLLoader();
			Parent root = loader.load(getClass().getResource("/com/kendy/dialog/Quota_Tab_Fram.fxml").openStream());
			loader.setController(new QuotaController());
			Tab quotaTab = new Tab();
			quotaTab.setText("联盟配账");
			quotaTab.setClosable(false);
			quotaTab.setContent(root);
			tabs.getTabs().add(quotaTab);  
			
		} catch (IOException e) {
			ErrorUtil.err("联盟配账tab加载失败", e);
		}
	    
	    
	    try {
	    	FXMLLoader loader = new FXMLLoader();
	    	Parent root = loader.load(getClass().getResource("/com/kendy/dialog/gudong_contribution.fxml").openStream());
	    	loader.setController(new GDController());
	    	Tab gdTab = new Tab();
	    	gdTab.setText("股东贡献值");
	    	gdTab.setClosable(false);
	    	gdTab.setContent(root);
	    	tabs.getTabs().add(gdTab);  
	    	
	    } catch (IOException e) {
	    	ErrorUtil.err("股东贡献值tab加载失败", e);
	    }
	    
	    
	    try {
	    	FXMLLoader loader = new FXMLLoader();
	    	Parent root = loader.load(getClass().getResource("/com/kendy/dialog/TG_toolsss.fxml").openStream());
//	    	loader.setController(new TGController());
	    	//loader.setController(new TGController());
	    	Tab gdTab = new Tab();
	    	gdTab.setText("托管工具");
	    	gdTab.setClosable(false);
	    	gdTab.setContent(root);
	    	tabs.getTabs().add(gdTab);  
	    	
	    	tgController = (TGController) loader.getController();
	    	
	    } catch (IOException e) {
	    	ErrorUtil.err("托管小工具tab加载失败", e);
	    }
	 
//	    try {
//	    	FXMLLoader loader = new FXMLLoader();
//	    	Parent root = loader.load(getClass().getResource("/com/kendy/dialog/team_proxy_frame2.fxml").openStream());
//	    	loader.setController(new TeamProxyController());
//	    	Tab gdTab = new Tab();
//	    	gdTab.setText("托管小工具");
//	    	gdTab.setClosable(false);
//	    	gdTab.setContent(root);
//	    	tabs.getTabs().add(gdTab);  
//	    	
//	    } catch (IOException e) {
//	    	ErrorUtil.err("托管小工具tab加载失败", e);
//	    }
	    
	}
		
	/**
	 * 初始化白名单版本
	 * 
	 * @time 2018年1月4日
	 */
	private void initWhiteVersion() {
	    ToggleGroup group = new ToggleGroup();
	    whiteVersionOld.setToggleGroup(group);
	    whiteVersionNew.setToggleGroup(group);
		whiteVersionOld.setUserData(0);//"旧名单"
		whiteVersionNew.setUserData(1);//"新名单"
		whiteVersionOld.setSelected(true);
		log.info("默认导入版本：新名单");
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
		    public void changed(ObservableValue<? extends Toggle> ov,
		        Toggle toggle, Toggle new_toggle) {
		    	Integer version =  (Integer)group.getSelectedToggle().getUserData();
		    	if(version == 1) {
		    		log.info("导入版本：新名单");
		    		//ShowUtil.show("新名单版本未开发！！！");
		    	}else {
		    		log.info("导入版本：旧名单");
		    	}
		    }
		});
	}
	
	/**
	 * 获取版本类型 0：旧版本  1：新版本
	 * @time 2018年1月5日
	 * @return
	 */
	private int getVersionType() {
		return whiteVersionOld.isSelected() ? 0 : 1;
	}
	/**
	 * 启动测试模式
	 * 
	 * @time 2017年11月11日
	 */
	private void initAutoTestMode() {
		hbox_autoTestMode.setVisible(false);
		ToggleGroup group = new ToggleGroup();
		radio_autoTest_yes.setToggleGroup(group);
		radio_autoTest_no.setToggleGroup(group);
		radio_autoTest_yes.setUserData("是");
		radio_autoTest_no.setUserData("否");
		radio_autoTest_no.setSelected(true);
		group.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
			public void changed(ObservableValue<? extends Toggle> ov,
					Toggle toggle, Toggle new_toggle) {
				String autoTestMode =  (String)group.getSelectedToggle().getUserData();
				if("是".equals(autoTestMode)) {
					hbox_autoTestMode.setVisible(true);
				}else {
					hbox_autoTestMode.setVisible(false);
				}
			}
		});
	}
	
	/**
	 * tabs切换事件
	 * 
	 * @time 2017年11月12日
	 */
	@SuppressWarnings("unchecked")
	private void tabsAction() {
//		importMembersBtn.fire();
//		importHuishuiBtn.fire();//已经导入数据库
//		importPreDataBtn.fire();
//		tabs.getSelectionModel().select(1);
		tabs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            	Tab tab = (Tab)newValue;
            	log.info(" newTab:"+tab.getText());
            	if("场次信息".equals(tab.getText())) {
            		MoneyService.flush_SSJE_table();
            		MoneyService.update_Table_CMI_Map();//更新{玩家ID=CurrentMoneyInfo},感觉没什么用
            	}
            	if("代理查询".equals(tab.getText())) {
            		String dateStr = DataConstans.Date_Str;
            		if(!StringUtil.isBlank(dateStr)) {
            			proxyDateLabel.setText(dateStr);
            		}
            		//查询最新数据
            		String teamId = teamIDCombox.getSelectionModel().getSelectedItem();
            		if(!StringUtil.isBlank(teamId))
            			TeamProxyService.refresh_TableTeamProxy_TableProxySum(teamId);
            		
            		//TeamProxyService.allTeamDataMap = TeamProxyService.getTotalTeamHuishuiMap();
            	}
            	if("实时上码系统".equals(tab.getText())) {
            		//刷新上码中的teamWanjiaIdMap
    				ShangmaService.refreshTeamIdAndPlayerId(); // update 2018-3-16
            		
            		//获取最新的实时金额Map {玩家ID={}}
            		Map<String,CurrentMoneyInfo> lastCMIMap = new HashMap<>();;
            		ObservableList<CurrentMoneyInfo> obList = tableCurrentMoneyInfo.getItems();
            		if(obList != null ) {
            			String pId = "";
            			for(CurrentMoneyInfo cmiInfo : obList) {
            				pId = cmiInfo.getWanjiaId();
            				if(!StringUtil.isBlank(pId)) {
            					lastCMIMap.put(pId, cmiInfo);
            				}
            			}
            		}
            		ShangmaService.cmiMap = lastCMIMap;
            		
            		ConsUtil.refresh_SM_Detail_Map();
            		
            		//加载数据
            		String shangmaTeamIdValue = shangmaTeamId.getText();
            		if(!StringUtil.isBlank(shangmaTeamIdValue)) {
            			//ShangmaService.loadShangmaTable(shangmaTeamIdValue,tableShangma);
            		}else {
            			if(DataConstans.huishuiMap.containsKey("公司")){
            				shangmaTeamIdValue = "公司";
            			}else {
            				shangmaTeamIdValue = ((Button)shangmaVBox.getChildren().get(0)).getText();
            			}
            		}
            		ShangmaService.loadShangmaTable(shangmaTeamIdValue,tableShangma);
            	}
            	if("积分查询".equals(tab.getText())) {
            		
            	}
            	if("联盟对帐".equals(tab.getText())) {
            		LMController.refreshClubList();
            	}
            	if("联盟配账".equals(tab.getText())) {
            		LMController.refreshClubList();
            		//导入每场战绩时的所有俱乐部记录
            		QuotaController.currentRecordList = LMController.currentRecordList;
            		//{俱乐部ID : 俱乐部信息}
            		QuotaController.allClubMap = LMController.allClubMap;
            		//{俱乐部ID : 俱乐部每一场信息}
            		QuotaController.eachClubList = LMController.eachClubList;
            		//缓存三个联盟的信息
            		QuotaController.LMTotalList = LMController.LMTotalList;
            		//自动加载联盟1的数据
            		QuotaController.autoSelectLM1();
            	}
            	if("托管工具".equals(tab.getText())) {
            		tgController.loadDataLastest();
            	}
            }
		});
	}
	
	/**
	 * 初使化上码表的信息
	 */
	public void initShanagma() {
		ShangmaService.initShangma(shangmaVBox,tableShangma,shangmaTeamId,
				tableShangmaDetail,shangmaZSM,shangmaZZJ,tablePaiju,tableShangmaNextDay,
				teamShangmaAvailable,teamYajin,teamEdu);
	}
	
	/**
	 * kendy:绑定数据域
	 * @param colums TableColumn 可变参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static  void bindCellValue(TableColumn<? extends Entity,String>... colums){
		try {
			for(TableColumn column : colums){
				String fxId = column.getId();
				column.setCellValueFactory(
						new PropertyValueFactory<Entity,String>(fxId)
						);
				column.setStyle("-fx-alignment: CENTER;");
				column.setSortable(false);//禁止排序
			}
		} catch (Exception e) {
			throw new RuntimeException("小林：绑定列值失败");
		}
	}
	
	
	

	//=======================  打开文件选择 Excel ==============================================================
	/**
	 * 导入excel文件选择器
	 * @param excelDir
	 * @param title
	 */
	public void openBasicExcelDialog(TextField excelDir,String title) {
		String rootPathName = !StringUtil.isBlank(DataConstans.Root_Dir) ? DataConstans.Root_Dir : System.getProperty("user.home");
		FileChooser fileChooser = new FileChooser();//文件选择器
		fileChooser.setTitle(Constants.TITLE+": "+title);//标题
		fileChooser.setInitialDirectory(new File(rootPathName));//初始化根目标  
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("excel", "*.xls?")
//				new FileChooser.ExtensionFilter("2007Excel", "*.xlsx")
				);
		File file = null;
		try {
			file = fileChooser.showOpenDialog(Main.primaryStage0);
		} catch (Exception e) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));//初始化根目标
			file = fileChooser.showOpenDialog(Main.primaryStage0);
		}
		if(file != null) {
			String filePath = file.getAbsolutePath();
			excelDir.setText(filePath);
			DataConstans.Root_Dir = filePath.substring(0,filePath.lastIndexOf("\\"));
		}
	}
	/**
	 * 打开人员名单excel
	 */
	public void openMembersExcelAction(ActionEvent event){
		openBasicExcelDialog(membersDir,"选择人员名单Excel");
	}
	/**
	 * 打开战绩excel
	 */
    public void openZJExcelAction(ActionEvent event){
    	openBasicExcelDialog(excelDir,"请选择战绩Excel");
    }
    /**
     * 打开回水比例excel
     */
    public void openHuishuiExcelAction(ActionEvent event){
    	openBasicExcelDialog(huishuiDir,"请选择回水比例Excel");
    }
    /**
     * 打开昨日留底excel
     */
    public void openPreDataExcelAction(ActionEvent event){
    	openBasicExcelDialog(preDataDir,"请选择回水比例Excel");
    }
	/**
	 * 打开合并ID的模板xcel
	 */
	public void opentBatchCombineIDDialogAction(ActionEvent event) {
		openBasicExcelDialog(combineIdDir,"请选择合并ID的模板xcel");
	}
    
    
  //=======================  导入 Excel ==============================================================
	/**
	 * 导入人员名单文件
	 * 
	 */
	public void importMembersExcelAction(ActionEvent even){
		String membersFilePath = membersDir.getText();
		if(!StringUtil.isBlank(membersFilePath)){
			//将人员名单文件缓存起来
			try {
				Map<String,Player> allPlayers = ExcelReaderUtil.readMembersRecord(new File(membersFilePath));
				DataConstans.membersMap.putAll(allPlayers);//求并集,key相同的会被替换掉
				//插入到数据库
				DBUtil.insertMembers(allPlayers);
				
				ShowUtil.show("导入人员名单成功", 2);
				//刷新相关缓存
//				ConsUtil.refreshTeamIdAndPlayerId();
				ConsUtil.refresh_SM_Detail_Map();//加载测试数据
			} catch (Exception e) {
				ErrorUtil.err("导入人员名单失败",e);
			}
		}else {
			ShowUtil.warn("您未选择需要导入的Excel!");
		}
	}
	
	/**
	 * 导入回水比例文件
	 */
	public void importHuishuiExcelAction(ActionEvent even){
		String huishuiFilePath = huishuiDir.getText();
		if(!StringUtil.isBlank(huishuiFilePath)){
			//将人员名单文件缓存起来
			Wrap wrap = ExcelReaderUtil.readHuishuiRecord(new File(huishuiFilePath));
			if(wrap.resultSuccess){
				DataConstans.huishuiMap.putAll((Map<String,Huishui>)wrap.obj);
				DBUtil.insertTeamHS((Map<String,Huishui>) wrap.obj);
				ShowUtil.show("导入回水比例成功", 2);
//			    importHuishuiBtn.setDisable(true);//导入后不再导了
			    //代理查询初始化团队ID
				TeamProxyService.initTeamSelectAndZjManage(teamIDCombox);
				//积分查询初始化团队ID
				JifenService.init_Jifen_TeamIdCombox();
				//上码系统中的团队ID按钮
				ShangmaService.initShangmaButton();
			}else{
				ShowUtil.show("导入回水比例失败", 2);
			}
		}
	}
	
	/**
	 * 导入昨日留底文件
	 * 备注：后期要添加不要删除所有信息！！！！
	 */
	public void importPreDataExcelAction(ActionEvent even){
		String preDataFilePath = preDataDir.getText();
		if(!StringUtil.isBlank(preDataFilePath)){
			//将人员名单文件缓存起来
			try {
				DataConstans.preDataMap = ExcelReaderUtil.readPreDataRecord(new File(preDataFilePath));
				if(DataConstans.preDataMap.size() == 0){
					ShowUtil.show("导入昨日留底失败!");
				}else{
					ShowUtil.show("导入昨日留底成功", 2);
					//备份到01场次
					//LMLabel.setText(getLMValFirstTime());
					//MoneyService.fillTableCurrentMoneyInfo(tableCurrentMoneyInfo, tableZijin, tableProfit,tableKaixiao,LMLabel);
					
					//保存到数据库、
					String dataTime = "2017-01-01";//第一次导入时是没有时间的，故到时可以改,注意这里没有匹配人员ID
					String json_preData = JSON.toJSONString(DataConstans.preDataMap);
					DBUtil.insertPreData(dataTime,json_preData);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				ShowUtil.show("导入昨日留底出错,原因："+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 导入战绩文件
	 */
	public void importZJExcelAction(ActionEvent even){
		String zjFilePath = excelDir.getText();
		if(!StringUtil.isBlank(zjFilePath)){
			String tableId = zjFilePath.substring(zjFilePath.lastIndexOf("-")+1, zjFilePath.lastIndexOf("."));
			if(!NumUtil.isNumeric(tableId)) {
				ErrorUtil.err(zjFilePath+"不是一个合法的Excel文件名称，请检查！");
				return;
			}
			if(hbox_autoTestMode.isVisible()) {
				final_selected_LM_type = "联盟1";
				selected_LM_type = "联盟1";
			}else {
				selectLM();
				if(StringUtil.isBlank(selected_LM_type)) {
					String msg = "导入战绩时没有选择对应的联盟，场次："+tableId+" Excel不准导入！";
					ShowUtil.show(msg);
					log.warn(msg);
					return;
				}
			}
			
			//将人员名单文件缓存起来
			Wrap wrap = ExcelReaderUtil.readZJRecord(new File(zjFilePath),lable_currentClubId.getText(),selected_LM_type,getVersionType());
			dateLabel.setText(DataConstans.Date_Str);
			if(wrap.resultSuccess){
				indexLabel.setText("第"+tableId+"局");
//				//1 填充总信息表
//				MoneyService.fillTablerAfterImportZJ(tableTotalInfo, tablePaiju,tableDangju,tableJiaoshou,tableTeam,(List<UserInfos>)wrap.obj,tableId);
//				//2填充当局表和交收表和团队表的总和
//				MoneyService.setTotalNumOnTable(tableDangju, DataConstans.SumMap.get("当局"));
//				MoneyService.setTotalNumOnTable(tableJiaoshou, DataConstans.SumMap.get("交收"));
//				tableTeam.getColumns().get(4).setText(MoneyService.digit0(DataConstans.SumMap.get("团队回水及保险总和")));
				importExcelData(tableId,(List<UserInfos>)wrap.obj);
				
				importZJBtn.setDisable(true);//导入不可用
				ShowUtil.show("导入战绩文件成功", 2);
			}else{
				ShowUtil.show("导入失败!!!原因:"+wrap.obj, 2);
			}
		}
	}
	
	private void importExcelData(String tableId, List<UserInfos> userInfoList) {
		//1 填充总信息表
		MoneyService.fillTablerAfterImportZJ(tableTotalInfo, tablePaiju,tableDangju,tableJiaoshou,tableTeam, userInfoList, tableId);
		//2填充当局表和交收表和团队表的总和
		MoneyService.setTotalNumOnTable(tableDangju, DataConstans.SumMap.get("当局"));
		MoneyService.setTotalNumOnTable(tableJiaoshou, DataConstans.SumMap.get("交收"));
		tableTeam.getColumns().get(4).setText(MoneyService.digit0(DataConstans.SumMap.get("团队回水及保险总和")));
	}
	
	/**
	 * 导入战绩后选择联盟
	 */
	private static String selected_LM_type = "";//选择后会被清空
	private static String final_selected_LM_type = "";//选择后不会被清空，用于检测额度是否超出
	private void selectLM() {
		Dialog dialog = new Dialog<>();
     	dialog.setTitle("请选择联盟:");
     	dialog.setHeaderText(null);
     	
     	//添加联盟按钮
     	GridPane grid = new GridPane();
     	grid.setPrefHeight(150);
     	grid.setPrefWidth(200);
     	grid.setHgap(10);
     	grid.setVgap(20);
     	grid.setPadding(new Insets(20, 15, 10, 10));
     	for(int i=0;i<3;i++) {
     		Button btn = new Button("联盟"+(i+1));
     		btn.setPrefWidth(200);
     		btn.setOnAction(event -> {
     			selected_LM_type = btn.getText(); 
     			dialog.setTitle(selected_LM_type);
     			System.out.println(selected_LM_type);
     		});
     		grid.add(btn, 0, i);
     	}
     	//添加取消按钮
     	HBox hbox = new HBox();
     	hbox.setPadding(new Insets(0,0,0,70));
     	hbox.setSpacing(10);
     	hbox.setStyle("-fx-background-color:#FFFFFF;");
     	
     	Hyperlink cancleLink = new Hyperlink("取消");
     	cancleLink.setPrefWidth(100);
     	cancleLink.setOnAction(event -> {
     		selected_LM_type = "";
     		System.out.println("selected_LM_type:"+selected_LM_type);
     		dialog.close();
     	});
     	hbox.getChildren().addAll(cancleLink);
     	grid.add(hbox, 0, 3);
     	
     	//添加确定按钮
     	ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
     	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
     	
     	dialog.setOnCloseRequest(event -> {
     		final_selected_LM_type = StringUtil.nvl(selected_LM_type, "联盟1");
     		if("".equals(selected_LM_type)) {
     			return;
     		}
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle("提示");
    		alert.setHeaderText(null);
    		alert.setContentText("\r\n==== "+selected_LM_type+" ===, 确定??");
    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() != ButtonType.OK){
    			selected_LM_type = "";
    			System.out.println("selected_LM_type:"+selected_LM_type);
    		}else {
    			System.out.println("最终选择:"+selected_LM_type);
    		}
     	});

     	dialog.getDialogPane().setContent(grid);
     	dialog.showAndWait();
	}
	
	/**
	 * 导入合并ID模板
	 * 备注：后期要判断是否在父子ID是否在同一个团队里面
	 */
    public void importBatchCombineIdExcelAction(ActionEvent event) {
    	
    	String combineIdPath = combineIdDir.getText();
		if(StringUtil.isBlank(combineIdPath)){
			ShowUtil.show("哥，你还没选择要导入合并ID的Excel文件!!"); return;
		}
		try {
			//读取模板数据
			final Map<String,String> excelCombineIdMap = ExcelReaderUtil.readCombineIdRecord(new File(combineIdPath));
			
			//将模板数据读取结果转化为缓存中合并ID相同的数据结果
			Map<String,Set<String>> tempCombineIdMap = new HashMap<>();
			excelCombineIdMap.forEach((parentId,subIdStr) -> {
				Set<String> subSet = new HashSet<>();
				for(String subId : subIdStr.split("#")) {
					subSet.add(subId);
				}
				tempCombineIdMap.put(parentId, subSet);
			});
			//与现有的合并ID缓存进行求并集（更新缓存）
			tempCombineIdMap.forEach((_parentId,_subIdSet) -> {
				Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(_parentId);
				if(subIdSet == null) {//缓存中没有此父节点，可直接入库
					//更新缓存
					DataConstans.Combine_Super_Id_Map.put(_parentId, _subIdSet);//更新父节点
					_subIdSet.forEach(_subID -> {
						DataConstans.Combine_Sub_Id_Map.put(_subID, _parentId);//更新子节点
					});
					//入库
					DBUtil.saveOrUpdateCombineId(_parentId);
				}else {//缓存中存在此父节点，需要进行比较
					boolean isNeedSave = false;
					for(String _subId : _subIdSet) {
						if(!subIdSet.contains(_subId)) {
							isNeedSave = true;
							subIdSet.add(_subId);//更新父节点
							DataConstans.Combine_Sub_Id_Map.put(_subId, _parentId);//更新子节点
						}
					}
					if(isNeedSave) {
						//入库
						DBUtil.saveOrUpdateCombineId(_parentId);
					}
				}
			});
			
			ShowUtil.show("导入合并ID模板成功！", 2);
		} catch (Exception e) {
			ShowUtil.show("导入合并ID模板失败！原因："+e.getMessage());
			e.printStackTrace();
		}
    }
	
	
    /**
     * 检查excel是否重复
     */
    public void checkExcelAction(ActionEvent event){
    	String filePath = excelDir.getText();
    	if(!StringUtil.isBlank(filePath)){
    		String tableId = filePath.substring(filePath.lastIndexOf("-")+1, filePath.lastIndexOf("."));
    		if(DataConstans.Index_Table_Id_Map.containsValue(tableId)){
    			ShowUtil.show("场次："+tableId+"已经导入过，请勿重复操作！");
    		}else{
    			ShowUtil.show("校验通过，即将导入场次"+tableId+"的战。",2);
    		}
    	}else{
    		ShowUtil.show("先选择需要导入的Excel", 2);
    	}
    }
    
    /**
     * 修改当前俱乐部ID
     */
    public void configCurrentClueIdAction(ActionEvent event){
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("修改");
		dialog.setHeaderText(null);
		// Set the button types.
		ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20,20, 20));

		TextField shangmaJu = new TextField();
		TextField shangmaVal = new TextField();

		grid.add(new Label("新俱乐部ID:"), 0, 0);
		grid.add(shangmaJu, 1, 0);

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		shangmaJu.textProperty().addListener((observable, oldValue, newValue) -> {
		    loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> shangmaJu.requestFocus());

		// Convert the result to a username-password-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == loginButtonType) {
		        return new Pair<>(shangmaJu.getText(), shangmaVal.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();

		result.ifPresent(shangmaJuAndVal -> {
			String newClubId = shangmaJuAndVal.getKey();
	    	if(newClubId != null && !"".equals(newClubId)){
	    		String oddClubId = PropertiesUtil.readProperty("clubId");
	    		PropertiesUtil.writeProperty("clubId", newClubId);
	    		log.info("修改了当前俱乐部ID：旧值"+oddClubId + "  新值"+newClubId);
	    		lable_currentClubId.setText(newClubId);
	    		ShowUtil.show("俱乐部ID修改成功！", 2);
	    	}
		});
    }
    
    /**
     * 增加股东
     */
    public void addGudongAction(ActionEvent event){
    	String newGudongName = gudongInput.getText();
    	if(!StringUtil.isBlank(newGudongName)){
    		if(!gudongListView.getItems().contains(newGudongName)){
    			gudongListView.getItems().add(newGudongName);
    			DataConstans.gudongList.add(newGudongName);
    			gudongInput.setText("");
    			
    			//持久化
    			final String GD = Constants.GU_DONG;
    			String gudongs = PropertiesUtil.readProperty(GD);
    			if(!StringUtil.isBlank(gudongs)){
    				PropertiesUtil.writeProperty(GD, gudongs+","+newGudongName);
    				log.debug(PropertiesUtil.readProperty(GD));
    			}else{
    				PropertiesUtil.writeProperty(GD, newGudongName);
    			}
    		}
    	}
    }
    
    /**
     * 删除股东
     */
    public void delGudongAction(ActionEvent event){
    	String selectedGudongName = (String) gudongListView.getFocusModel().getFocusedItem(); 	
    	if(!StringUtil.isBlank(selectedGudongName)){
    		gudongListView.getItems().remove(selectedGudongName);
    		DataConstans.gudongList.remove(selectedGudongName);
    		
    		//持久化
			String gudongs = PropertiesUtil.readProperty("gudong");
			if(!StringUtil.isBlank(gudongs)){
				if(gudongs.contains(","+selectedGudongName)) {
					PropertiesUtil.writeProperty("gudong", gudongs.replace(","+selectedGudongName, ""));
				}else if(gudongs.contains(selectedGudongName+",")){
					PropertiesUtil.writeProperty("gudong", gudongs.replace(selectedGudongName+",", ""));
				}else {
					PropertiesUtil.writeProperty("gudong", "");
				}
				log.info(PropertiesUtil.readProperty("gudong"));
			}else{
//				PropertiesUtil.writeProperty("gudong", newGudongName);
			}
    	}

    }
    
    /**
     * 获取股东列表
     * @time 2018年2月21日
     * @return
     */
    public static ObservableList<String>  getGudongList(){
    	ObservableList gudongItems = Main.myController.gudongListView.getItems();
    	if(CollectUtil.isHaveValue(gudongItems)) {
//    		List<String> gudongList = new ArrayList<>();
//    		gudongItems.forEach(gudong -> gudongList.add((String)gudong));
//    		return gudongList;
    		return gudongItems;
    	}else {
    		return FXCollections.observableArrayList();
    	}
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
     * 打开增加人员名单对话框
     */
    public void openNewPlayerDialogAction(ActionEvent event){
    	openBasedDialog("add_new_player_frame.fxml","新增人员名单",Constants.ADD_NEWPALYER_FRAME);
    }
    
	/**
     * 打开增加团队回水对话框
     */
    public void addHuishuiAction(ActionEvent event){
    	openBasedDialog("add_huishui_frame.fxml","新增团队回水",Constants.ADD_TEAM_HUISHUI_FRAME);
    }
    
    /**
     * 打开增加实时开销对话框
     */
    public void openKaixiaoDialogAction(ActionEvent event){
    	openBasedDialog("add_kaixiao_fram.fxml","新增开销",Constants.ADD_KAIXIAO_FRAME);
    }
    /**
     * 打开删除人员对话框
     */
    public void openDelMemberDialogAction(ActionEvent event){
    	openBasedDialog("del_member_framesss.fxml","人员删除或修改",Constants.DEL_MEMBER_FRAME);
    }
    /**
     * 打开新增实时金额对话框
     */
    public void openAddCurrentMoneyAction(ActionEvent event){
    	openBasedDialog("add_current_money_frame4.fxml","新增实时金额",Constants.ADD_CURRENT_MONEY_FRAME);
    }
    /**
     * 打开新增合并ID对话框
     */
    public void opentCombineIDDialogAction(ActionEvent event){
    	openBasedDialog("combine_player_id_framess.fxml","合并ID",Constants.COMBINE_ID_FRAME);
    }
    
    /**
     * 打开会员战绩查询对话框(没用到)
     */
    public void openMemberZJQueryAction(ActionEvent event){
    }
    
    /**
     * 牌局表列中添加支付按钮
     */
    Callback<TableColumn<WanjiaInfo, String>, TableCell<WanjiaInfo, String>> cellFactory = //
	    new Callback<TableColumn<WanjiaInfo, String>, TableCell<WanjiaInfo, String>>() {
		@Override
		public TableCell call(final TableColumn<WanjiaInfo, String> param) {
		    final TableCell<WanjiaInfo, String> cell = new TableCell<WanjiaInfo, String>() {
		        
		    	final Button btn = new Button("支付");
		
		        @Override
		        public void updateItem(String item, boolean empty) {
		            super.updateItem(item, empty);
		            if (empty) {
		                setGraphic(null);
		                setText(null);
		            } else {
		                btn.setOnAction(event -> {
		                    WanjiaInfo wanjiaInfo = getTableView().getItems().get(getIndex());
		                    if("1".equals(wanjiaInfo.getHasPayed())) {
		                    	ShowUtil.show("抱歉，已支付过！！");
		                    	return;
		                    }
		                    if(getTableRow() != null){  
		                    	try {
//		                    		//支付时修改玩家实时金额  或 添加 实时金额
		                    		MoneyService.updateOrAdd_SSJE_after_Pay(wanjiaInfo);
		                    		btn.setText("已支付");
		                    		wanjiaInfo.setHasPayed("1");
		                    		ShowUtil.show("已将"+wanjiaInfo.getWanjiaName()+"的实时金额修改为   "+wanjiaInfo.getHeji(),2);
		                    		//moneyInfo.setShishiJine(wj.getHeji());//设置新的实时金额的值 
		                    		MoneyService.flush_SSJE_table();//刷新实时金额表
		                    		
		                    		//点击支付时更改SM_Detail_Map中的支付状态
		                    		ShangmaService.update_SM_Detail_Map_byPlayerIdAndPaiju(wanjiaInfo.getWanjiaId(),indexLabel.getText());
		                    	} catch (Exception e) {
		                    		ErrorUtil.err("支付失败", e);
		                    	}
                            } 
		                });
		                WanjiaInfo wj = getTableView().getItems().get(getIndex());
		                //解决不时本应支付确显示成已支付的bug
		                if(!"1".equals(wj.getHasPayed())){
		                	btn.setText("支付");
		                }else {
		                	btn.setText("已支付");
		                }
		                setGraphic(btn);//小林：这一行解决了支付按钮消失的问题
		                //在此处增加是否要显示该按钮(如果玩家从属于某个非空或非公司的团队，则无需显示按钮)
		                String tempTeamId = wj.getHasPayed();//这个tempTeamId是hasPayed的内容,这里没有公司的人
		                if(!StringUtil.isBlank(tempTeamId) && !"0".equals(tempTeamId)) {
		                	//获取团队信息
		                	Huishui hs = DataConstans.huishuiMap.get(tempTeamId);
		                	//情况一：有从属团队的玩家，再分两种情况
		                	if(hs != null) {
		                		//A:若团队战绩要管理，需要显示支付按钮
		                		if("是".equals(hs.getZjManaged())) {
//		                			log.debug("====teamId为不为空，要显示，要战绩管理：是");
		                			setGraphic(btn);
		                		//B:若团队战绩不要管理，无须显示支付按钮
		                		}else {
//		                			log.debug("hsPayed:====================hs为空："+hs.getZjManaged());
		                			setGraphic(null);
		                		}
		                	}
		                }else {
//		                	log.debug("====teamId为空或为0，要显示+"+DataConstans.membersMap.get(wj.getWanjiaId()).getTeamName());
		                	//情况二：对于没有从从属的团队的玩家或者团队是公司的玩家，一定需要需要显示支付按钮
		                	setGraphic(btn);
		                }
//		                setGraphic(btn);
		                setText(null);
		            }
		        }
		    };
		    return cell;
		}
	};
	/**
	 * 在列中添加按钮
	 */
	Callback<TableColumn<WanjiaInfo, String>, TableCell<WanjiaInfo, String>> cellFactoryCopy
	= //
	new Callback<TableColumn<WanjiaInfo, String>, TableCell<WanjiaInfo, String>>() {
		@Override
		public TableCell call(final TableColumn<WanjiaInfo, String> param) {
			final TableCell<WanjiaInfo, String> cell = new TableCell<WanjiaInfo, String>() {
				
				final Button btn = new Button("copy");
				
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setGraphic(null);
						setText(null);
					} else {
						btn.setOnAction(event -> {
							WanjiaInfo wanjiaInfo = getTableView().getItems().get(getIndex());
//							log.debug(wanjiaInfo.toString());
							try {
								clip2QQ(wanjiaInfo);
								log.debug("已经复制到剪切板");
							} catch (Exception e) {
								ShowUtil.show("复制失败", 1);
								e.printStackTrace();
							}
						});
						setGraphic(btn);
						setText(null);
					}
				}
			};
			return cell;
		}
	};
	
	
	 /**
     * 结算按钮
     */
    Callback<TableColumn<TeamInfo, String>, TableCell<TeamInfo, String>> cellFactoryJiesuan = //
	    new Callback<TableColumn<TeamInfo, String>, TableCell<TeamInfo, String>>() {
		@Override
		public TableCell call(final TableColumn<TeamInfo, String> param) {
		    final TableCell<TeamInfo, String> cell = new TableCell<TeamInfo, String>() {
		
		        final Button btn = new Button("结算");
		
		        @Override
		        public void updateItem(String item, boolean empty) {
		            super.updateItem(item, empty);
		            if (empty) {
		                setGraphic(null);
		                setText(null);
		            } else {
		                btn.setOnAction(event -> {
		                	//
		                	if(!importZJBtn.isDisabled()) {
		                		ShowUtil.show("只能在导入战绩后才能结算！");
		                		return;
		                	}
		            		Alert alert = new Alert(AlertType.CONFIRMATION);
		            		alert.setTitle("提示");
		            		alert.setHeaderText(null);
		            		alert.setContentText("\r\n只能在当天最后一场才能结算！你确定了吗?");
		            		Optional<ButtonType> result = alert.showAndWait();
		            		if (result.get() != ButtonType.OK){
		            			return;
		            		}
		                	TeamInfo teamInfo = getTableView().getItems().get(getIndex());
		                    if(getTableRow() != null && teamInfo != null && "0".equals(teamInfo.getHasJiesuaned())){  
		                    	TeamInfo tempTeamInfo = copyTeamInfo(teamInfo);
//		                    	log.debug(teamInfo.toString());
		                    	btn.setText("已结算");
		                    	String rowTeamSum = teamInfo.getTeamSum();
		                    	teamInfo.setHasJiesuaned("1");
		                    	teamInfo.setTeamZJ(ZERO);
		                    	teamInfo.setTeamBS(ZERO);
		                    	teamInfo.setTeamHS(ZERO);
		                    	teamInfo.setTeamSum(ZERO);
		                    	//结算后自动删除总和中部分数字
		                    	String key = "团队回水及保险总和";
		                    	if(DataConstans.SumMap.get(key)!=null) {
		                    		double totalSum = DataConstans.SumMap.get(key);
		                    		DataConstans.SumMap.put(key, Double.valueOf(MoneyService.digit0(totalSum - MoneyService.getNum(rowTeamSum))));
		                    	};
		                    	//缓存中清空之前所加的团队回水，以便下次团队累计重新从0开始
		                    	DataConstans.Team_Huishui_Map.remove(teamInfo.getTeamID());
		                    	DataConstans.Team_Info_Pre_Map.remove(teamInfo.getTeamID());
		                    	
		                    	//2018-01-04 在实时金额栏中新增该团队减去团队服务费的记录
		                    	add2SSJE(tempTeamInfo);
                            } 
		                });
		                //解决不时本应支付确显示成已支付的bug
		                TeamInfo teamInfo = getTableView().getItems().get(getIndex());
		                if(!"1".equals(teamInfo.getHasJiesuaned())){
		                	btn.setText("结算");
		                }
		                setGraphic(btn);
		                setText(null);
		            }
		        }
		    };
		    return cell;
		}
	};
	
	public TeamInfo copyTeamInfo(TeamInfo info) {
		TeamInfo temp = new TeamInfo(info.getTeamID(),info.getTeamZJ(),info.getTeamHS(),info.getTeamBS(),info.getTeamSum());
		return temp;
	}
	
	/**
	 * 点击结算按钮后往实时金额新增一条团队记录（减去该团队服务费）
	 * @time 2018年1月4日
	 * @param teamInfo
	 */
	public void add2SSJE(TeamInfo teamInfo) {
		Platform.runLater(new Runnable() { //更新JavaFX的主线程的代码放在此处
				@Override
				public void run() {
					String teamID = teamInfo.getTeamID();
					String teamName = "团队#"+teamID;//自定义添加到金额表中的名称
					String teamSum = teamInfo.getTeamSum();
					Double _teamSum = NumUtil.getNum(teamSum);
					if( _teamSum == 0) {
						//这里是否直接返回？？......
						//return;
					}
					//获取代理查询中该团队的服务费
					String fwfString = TeamProxyService.get_TeamFWF_byTeamId(teamID);
					Double teamFWF = NumUtil.getNum(fwfString);//团队服务费  : 待累加到总团队服务费中
					Double _tempSSJE = _teamSum - teamFWF;//此处已减去该 团队服务费
					String tempSSJE = NumUtil.digit0(_tempSSJE);//此处已减去该 团队服务费
					current_Jiesuaned_team_fwf_sum += teamFWF;
					//获取新记录
					CurrentMoneyInfo cmiInfo = MoneyService.getInfoByName(teamName);
					if(cmiInfo == null) {//cmiInfo为null表示该团队不存在于实时金额表中
						cmiInfo = new CurrentMoneyInfo(teamName,tempSSJE,"","");//玩家ID和额度为空
						MoneyService.addInfo(cmiInfo);
						log.info(String.format("点击结算按钮:新增一条团队记录进金额表,团队ID=%s,团队服务费=%s,金额=%s",teamID,fwfString,tempSSJE));
					}else {
						//如果在实时金额中已经存在该团队记录，则更新该条实时金额
						String oldTeamSSJE = cmiInfo.getShishiJine();
						Double _newTeamSSJE = NumUtil.getNum(oldTeamSSJE) + _tempSSJE;//此处已减去该 团队服务费
						String newTeamSSJE = NumUtil.digit0(_newTeamSSJE);
						cmiInfo.setShishiJine(newTeamSSJE);
						log.info(String.format("点击结算按钮:修改金额表原团队记录,团队ID=%s,团队服务费=%s,金额=%s",teamID,fwfString,newTeamSSJE));
					}
					//并刷新表
					MoneyService.refreshRecord();
					//利润表修改总团队服务费(累积该团队的服务费)
					//add2AllTeamFWF_from_tableProfit(tableProfit,teamFWF);//由于改用current_Jiesuaned_team_fwf_sum，故此方法不调用
					//自动平帐按钮
					refreshBtn.fire();//这个是为了让当局的团队总服务费能累加到利润表中的总团队服务费中
				}
			}	
		);
		
		
	}
	
	
	/**
	 * 利润表修改总团队服务费(累积该团队的服务费)
	 * @time 2018年1月5日
	 * @param teamFWF
	 */
	public static void add2AllTeamFWF_from_tableProfit(TableView<ProfitInfo> table,Double teamFWF) {
		try {
			ProfitInfo profitInfo = TableUtil.getItem(table)
					.filtered(info -> "总团队服务费".equals(info.getProfitType())).get(0);
			String allTeamFWF = NumUtil.digit0(NumUtil.getNum(profitInfo.getProfitAccount()) + teamFWF);
			profitInfo.setProfitAccount(allTeamFWF);
			table.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
    
    
    /**
     * 右下表：次日上码详情表添加双击名称事件
     */
    Callback<TableColumn<ShangmaDetailInfo, String>, TableCell<ShangmaDetailInfo,String>> ShangmaNameNextdayCellFactory = 
    	new Callback<TableColumn<ShangmaDetailInfo, String>, TableCell<ShangmaDetailInfo, String>> () {  
    	    @Override  
    	    public TableCell<ShangmaDetailInfo, String> call(TableColumn<ShangmaDetailInfo, String> param) {  
    	        TextFieldTableCell<ShangmaDetailInfo, String> cell = new TextFieldTableCell<>();  
//    	        cell.setTextFill(Color.RED);
    	        cell.setEditable(false);//不让其可编辑
    	        cell.setOnMouseClicked((MouseEvent t) -> {  
    	        	//鼠标双击事件
    	            if (t.getClickCount() == 2 && cell.getIndex() < tableShangmaNextDay.getItems().size()) { 
    	               //双击执行的代码  
    	            	ShangmaDetailInfo smDetail =  tableShangmaNextDay.getItems().get(cell.getIndex());
    	            	ShangmaService.openAddNextdayShangSMDiag(smDetail);
    	            }  
    	        }); 
    	        return cell;  
    	    } 
    };
    /**
     * 上码详情表添加双击名称事件
     */
    Callback<TableColumn<ShangmaDetailInfo, String>, TableCell<ShangmaDetailInfo,String>> ShangmaNameCellFactory = 
    		new Callback<TableColumn<ShangmaDetailInfo, String>, TableCell<ShangmaDetailInfo, String>> () {  
    	@Override  
    	public TableCell<ShangmaDetailInfo, String> call(TableColumn<ShangmaDetailInfo, String> param) {  
    		TextFieldTableCell<ShangmaDetailInfo, String> cell = new TextFieldTableCell<>();  
//    	        cell.setTextFill(Color.RED);
    		cell.setEditable(false);//不让其可编辑
    		cell.setOnMouseClicked((MouseEvent t) -> {  
    			//鼠标双击事件
    			if (t.getClickCount() == 2 && cell.getIndex() < tableShangmaDetail.getItems().size()) { 
    				//双击执行的代码  
    				ShangmaDetailInfo smDetail =  tableShangmaDetail.getItems().get(cell.getIndex());
    				ShangmaService.openAddShangSMDiag(smDetail);
    			}  
    		}); 
    		return cell;  
    	} 
    };
    
    /**
     * 解决上码主表行双击事件
     * 
     */
    private class  TableRowControl<T> extends TableRow<T> {  
    	private TableView<T> tableView;
    	public TableRowControl(TableView<T> tableView) {  
    		super();  
    		this.tableView = tableView;
    		this.setOnMouseClicked(new EventHandler<MouseEvent>() {  
    			@Override  
    			public void handle(MouseEvent event) {  
    				int index = TableRowControl.this.getIndex();
    				if (event.getButton().equals(MouseButton.PRIMARY)  
    						&& event.getClickCount() == 2  
    						&& index < tableView.getItems().size()
    						) {  
    					//双击执行的代码  
//        				ShangmaInfo smInfo =  tableShangma.getItems().get(cell.getIndex());
    					ShangmaInfo smInfo =  tableShangma.getItems().get(index);
        				ShangmaService.openNewShangSMDiag(smInfo);
    				}  
    			}  
    		});  
    	}  
    }  
    
    
    
    /**
     * 资金表添加双击名称事件
     */
    Callback<TableColumn<ZijinInfo, String>, TableCell<ZijinInfo,String>> zijinCellFactory = 
    		new Callback<TableColumn<ZijinInfo, String>, TableCell<ZijinInfo, String>> () {  
    	@Override  
    	public TableCell<ZijinInfo, String> call(TableColumn<ZijinInfo, String> param) {  
    		
    		TextFieldTableCell<ZijinInfo, String> cell = new TextFieldTableCell<>();  
    		cell.setEditable(false);//不让其可编辑
    		cell.setOnMouseClicked((MouseEvent t) -> {  
    			//鼠标双击事件
    			if (t.getClickCount() == 2) { 
    				if(lockedLabel.isVisible()) {
    					return;
    				}
    				//双击执行的代码  
    				ZijinInfo info =  tableZijin.getItems().get(cell.getIndex());
    				MoneyService.openAddZijinDiag(tableZijin, info);
    			}  
    		}); 
    		return cell;  
    	} 
    };
    
	
	/**
	 * 复制玩家信息到剪切板，并转成图片发到QQ
	 * @param wj 玩家信息
	 * @throws Exception
	 */
	private void clip2QQ(WanjiaInfo wj) throws Exception {
		String code = sysCode.getText();
		if(StringUtil.isBlank(code)) {
			code = "GBK";
		}
		String html = Text2ImageUtil.getHtml(wj);
		BufferedImage img = Text2ImageUtil.toImage(html,code);
		ClipBoardUtil.setClipboardImage((Image)img);
	}
	
	/**
	 * 更新开销表
	 * @param data
	 */
	public void updateKaixiaoTable(KaixiaoInfo  info){
		//获取ObserableList
		ObservableList<KaixiaoInfo> list = tableKaixiao.getItems();
		list.add(info);
		tableKaixiao.setItems(list);
	}
	/**
	 * 更新实时金额表
	 * @param data
	 */
	public void updateCurrentMoneyTable(Player player,String SSJE){
		//获取ObserableList
		ObservableList<CurrentMoneyInfo> list = tableCurrentMoneyInfo.getItems();
		list.add(new CurrentMoneyInfo(player.getPlayerName(),SSJE,player.getgameId(),player.getEdu()));
		tableCurrentMoneyInfo.setItems(list);
	}
	
	/**
	 * 刷新同步,即平帐按钮
	 * @param data
	 */
	public void refreshAction(ActionEvent event) {
		MoneyService.refreshSumPane(
				tableTeam,
				tableZijin,
				tableKaixiao,
				tableProfit,
				tableCurrentMoneyInfo,
				tablePingzhang,
				LMLabel
				);
	}
	
	/**
	 * 搜索按钮
	 */
	public void searchRowAction(ActionEvent event) {
		String keyWord = searchText.getText();
		MoneyService.searchRowAction(tableCurrentMoneyInfo,keyWord);
	}
	public void searchRowByEnterEvent(KeyEvent event) {
		String keyWord = searchText.getText();
		if(KeyCode.ENTER == event.getCode() && !StringUtil.isBlank(keyWord)) {
			MoneyService.searchRowAction(tableCurrentMoneyInfo,keyWord);
		}
	}
	
	/**
	 * 锁定当局按钮
	 */
	public void lockDangjuAction(ActionEvent event) {
		boolean isOK = false;
		String JS = "交收";
		String PZ = "平帐";
		if(DataConstans.SumMap.get(JS) != null && DataConstans.SumMap.get(PZ) != null) {
			double sumOfJS = DataConstans.SumMap.get(JS);
			double sumOfPZ = DataConstans.SumMap.get(PZ);
			double diff = Math.abs(sumOfJS + sumOfPZ);
			if(diff < 10) {
				//若交收与平帐大体相等，则更新联盟对帐
				/*
				 * 联盟对帐 = 上一局的联盟对帐 + 本局的联盟对帐
				 * 当清空上一局的联盟对帐时，联盟对帐设置为0.00
				 */
				if(!"0.00".equals(LMLabel.getText())) {
					LMLabel.setText(getNewLMVal(sumOfJS*(-1)+""));
				}else {
					LMLabel.setText(MoneyService.digit0(sumOfJS*(-1))+"");
				}
				// 锁定并缓存十个表数据
				Map<String, String> mapOf10Tables;
				try {
					mapOf10Tables = MoneyService.lock10Tables(tableTotalInfo,tablePaiju,tableTeam,
							tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit,tableDangju,tableJiaoshou,tablePingzhang,LMLabel);
				} catch (Exception e) {
					ShowUtil.show("锁定失败\r\n相关表数据不能为空");
					log.debug("锁定失败============相关表数据不能为空");
					return;
				}
				
				// 汇总信息保存到数据库
				int paijuIndex = DataConstans.Paiju_Index.getAndIncrement();
				DataConstans.Index_Table_Id_Map.put(paijuIndex+"", indexLabel.getText());//记录局与场次映射
				
				//缓存到总团队回水中(不会从中减少)
				setDangjuTeamInfo();
				
				Map<String,String> totalMap = new HashMap<>();
				totalMap.putAll(mapOf10Tables);
				DataConstans.All_Locked_Data_Map.put(paijuIndex+"", totalMap);//总缓存数据
				
				//清空表数据
				clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
						//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
				//清空相关缓存
				Double teamData = DataConstans.SumMap.get("团队回水及保险总和");//这个值必须保留
				Double shangchangKaixiao = DataConstans.SumMap.get("上场开销");//这个值必须保留
				DataConstans.SumMap = new HashMap<String,Double>();
				DataConstans.SumMap.put("团队回水及保险总和", teamData);
				DataConstans.SumMap.put("上场开销", shangchangKaixiao);
				//控制分页
				pagePaneOpration();
				
				indexLabel.setText(INDEX_ZERO);
				importZJBtn.setDisable(false);
				
				//锁定当局备份上码表的个人详情
				ConsUtil.lock_SM_Detail_Map();
				
				
				//插入当前导入的战绩表
				try {
					saveHistoryRecord();
				} catch (Exception e) {
					ErrorUtil.err("保存历史数据失败",e);
				}
				DataConstans.Dangju_Team_Huishui_List = new LinkedList<>();
				
				//保存所有缓存数据进数据库(与上面的数据顺序不要变换），不然会中途加载时想查看以前的数据会报请撤销或锁定数据
				//DBUtil.saveLastLockedData();//IO耗时长
				Platform.runLater(() -> {
						DBUtil.saveLastLockedData();//IO耗时长
					}
				);
				
				//联盟对帐：保存当场所有战绩到数据库
				setLMRecords();
				DBUtil.addRecordList(LMController.currentRecordList);//是否反回结果？？
				LMController.refreshClubList();
				LMController.checkOverEdu(final_selected_LM_type);//检查俱乐部额度
				
				
				//当局已结算的团队服务费之和 要置为0
				current_Jiesuaned_team_fwf_sum = 0d;
				
				ShowUtil.show("锁定成功！", 2);
			}else {
				ShowUtil.show("平帐与交收的差值大于10，不能锁定！！！");
			}
		}else {
			ShowUtil.show("平帐失败，不能锁定！！！", 2);
		}
	}
	
	/**
	 * 锁定时设置导入的Excel到对应的联盟中
	 * @time 2017年12月13日
	 */
	private void setLMRecords() {
		if(StringUtil.isBlank(selected_LM_type)) {
			ErrorUtil.err("锁定时没有选择所属联盟！");
			return;
		}
		LMController.currentRecordList.forEach(record -> {
			record.setLmType(selected_LM_type);
		});
		selected_LM_type = "";
	}
	
	/**
	 * 保存当前导入的战绩表，用于会员查询和积分查询
	 * @throws Exception
	 */
	public void saveHistoryRecord() throws Exception {
		List<TeamHuishuiInfo> list = DataConstans.Dangju_Team_Huishui_List;
		List<HistoryRecord> ls = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		if(list != null && list.size() > 0 ) {
			for(TeamHuishuiInfo hs : list ) {
				/**
				 * @param playerId
				 * @param playerName
				 * @param teamId
				 * @param shishou
				 * @param yszj
				 * @param chuHuishui
				 * @param shouHuishui
				 * @param updateTime
				 */
				ls.add(new HistoryRecord(hs.getWanjiaId(),hs.getWanjia(),hs.getTuan(),
						hs.getShishou(),hs.getZj(),hs.getChuHuishui(),hs.getShouHuishui(),hs.getUpdateTime()));
			}
			if(!ls.isEmpty()) {
				DBUtil.saveHistoryRecord(ls);
			}
		}
		
	}
	

	
	//缓存到总团队回水中(不会从中减少)
	public void setDangjuTeamInfo() {
		DataConstans.Dangju_Team_Huishui_List.forEach(info -> {
			String teamId = info.getTuan();
			List<TeamHuishuiInfo> teamHuishuiList = DataConstans.Total_Team_Huishui_Map.get(teamId);
			if( teamHuishuiList == null ) {
				teamHuishuiList = new ArrayList<>();
			}
			teamHuishuiList.add(info);
			DataConstans.Total_Team_Huishui_Map.put(teamId,teamHuishuiList);
		});
	}
	//获取上一个联盟对帐与本局联盟对帐的总值
	public String getNewLMVal(String dangjuLMVal) {
		int preJuIndex = DataConstans.All_Locked_Data_Map.size();
		if(DataConstans.All_Locked_Data_Map.get(preJuIndex+"") != null) {
			String preJuLMVal = DataConstans.All_Locked_Data_Map.get(preJuIndex+"").get("联盟对帐");
			return MoneyService.digit0(MoneyService.getNum(dangjuLMVal)+MoneyService.getNum(preJuLMVal));
		}
		return MoneyService.digit0(dangjuLMVal);
	}
	
	//获取最新的利润总和
	public static String getChangciTotalProfit() {
		String totalProfit = "0";
		int paijuIndex = DataConstans.All_Locked_Data_Map.size();
		if(paijuIndex == 0) return totalProfit;
		Map<String,String> lastLockedMap = DataConstans.All_Locked_Data_Map.get(paijuIndex+"");
		if(lastLockedMap != null ) {
			totalProfit = lastLockedMap.get("利润总和");
		}
		return totalProfit;
	}
	
	
	public void clearDataConstansCache() {
		//DataConstans.SumMap.clear();
	}
	
	@SuppressWarnings("unchecked")
	public void clearData(TableView tableTotalInfo,TableView tablePaiju,TableView tableTeam,
			TableView tableDangju,TableView tableJiaoshou,TableView tablePingzhang) {
		//清空相关界面表数据
		tableTotalInfo.setItems(null);
		tablePaiju.setItems(null);
//		tableTeam.setItems(null);
		tableDangju.setItems(null);
		tableJiaoshou.setItems(null);
		tablePingzhang.setItems(null);
		//清空相关界面表总数据
		MoneyService.setTotalNumOnTable(tableDangju, 0d);
		MoneyService.setTotalNumOnTable(tableJiaoshou, 0d);
		MoneyService.setTotalNumOnTable(tablePingzhang, 0d);
//		MoneyService.setTotalNumOnTable(tableTeam, 0d,4);
		
		//2017-12-31注释
		// if("2017-01-01".equals(DBUtil.Load_Date)) {
		// tableTeam.setItems(null);
		// MoneyService.setTotalNumOnTable(tableTeam, 0d,4);
		// }
		
		//清空缓存中的数据
		clearDataConstansCache();
	}
	public void clear10Tables(TableView tableTotalInfo,TableView tablePaiju,TableView tableTeam,
			TableView tableDangju,TableView tableJiaoshou,TableView tablePingzhang) {
		//清空相关界面表数据
		tableTotalInfo.setItems(null);
		tablePaiju.setItems(null);
//		tableTeam.setItems(null);
		tableDangju.setItems(null);
		tableJiaoshou.setItems(null);
		tablePingzhang.setItems(null);
		//清空相关界面表总数据
		MoneyService.setTotalNumOnTable(tableDangju, 0d);
		MoneyService.setTotalNumOnTable(tableJiaoshou, 0d);
		MoneyService.setTotalNumOnTable(tablePingzhang, 0d);
//		MoneyService.setTotalNumOnTable(tableTeam, 0d,4);
		
		if("2017-01-01".equals(DBUtil.Load_Date)) {
			tableTeam.setItems(null);
			MoneyService.setTotalNumOnTable(tableTeam, 0d,4);
		}
		//清空缓存中的数据
		clearDataConstansCache();
	}
	
	
	public void teamIDSelectedAction(ActionEvent event) {
		
	}
	
	/**********************************************************自定义 分页控件代码   开始  *********/
	//每一场锁定时添加一个页
	public void pagePaneOpration() {
		int index = DataConstans.Paiju_Index.get();
		pageInput.setText(index+"");
	}
	//第一页按钮
	public void pageFirstAction(ActionEvent event) {
		if(isHalfImport()) 
			return;
		
		pageInput.setText("1");
		//调用
		getResultByPage(1);
	}
	//前一页按钮
	public void pagePreAction(ActionEvent event) {
		if(isHalfImport()) 
			return;
		
		String page = pageInput.getText();
		int pageIndex = getPageIndex(page,false);
		pageInput.setText(pageIndex+"");
		//调用
		if(DataConstans.All_Locked_Data_Map.size() > 0 && pageIndex >= 1) {
			getResultByPage(pageIndex);
		}
	}
	//下一页按钮
	public void pageNextAction(ActionEvent event) {
		if(isHalfImport()) 
			return;
		
		String page = pageInput.getText();
		int pageIndex = getPageIndex(page,true);
		pageInput.setText(pageIndex+"");
//		//调用
//		getResultByPage(pageIndex);
//		
		//调用
		if(DataConstans.All_Locked_Data_Map.size() > 0 && pageIndex >= 1) {
			getResultByPage(pageIndex);
		}
	}
	
	//最后一页页按钮
	public void pageLastAction(ActionEvent event) {
		if(isHalfImport()) 
			return;
		
		pageInput.setText(DataConstans.Paiju_Index.get()+"");
		//调用
		getResultByPage(DataConstans.Paiju_Index.get());
	}
	//输入页码
	public void turn2PageEnterAction(KeyEvent event) {
		String pageText = pageInput.getText();
		int pageMax = DataConstans.Paiju_Index.get();
		if(KeyCode.ENTER == event.getCode() && !StringUtil.isBlank(pageText)) {
			try {
				int page = Integer.valueOf(pageText.trim()); 
				if(page > pageMax || page < 0) {
					ShowUtil.show("值必须界于1到"+pageMax+"之间",1);
					return;
				}
				//调用
				getResultByPage(page);
				
			}catch(Exception e) {
				ShowUtil.show("您输入的不是一个数值",1);
				pageInput.setText("");
			}
		}
	}
	
	/**
	 * 判断是否只导到一半就撤销（通过页码）
	 * 
	 * @time 2017年10月17日
	 * @return
	 */
	private boolean isHalfImport() {
		if(DataConstans.Dangju_Team_Huishui_List.size() > 0) {
			ShowUtil.show("请把本局撤销或锁定再去查看锁定信息！");
			return true;
		}
		return false;
	}
	
	//根据用户输入的页码或不断点击前后页时获取可用的页数
	public int getPageIndex(String oldPage,boolean addOrDel) {
		int newPage = 1;
		int currentMaxPage = DataConstans.Paiju_Index.get();
		try {
			if(!StringUtil.isBlank(oldPage)) {
				if(addOrDel) {//加
					newPage = Integer.valueOf(oldPage)+1;
					if(newPage > currentMaxPage) {
						newPage--;
					}
				}else {//减
					newPage = Integer.valueOf(oldPage)-1;
					if(newPage < 1) {
						newPage++;
					}
				}
			}else{
				newPage = currentMaxPage -1;
			}
		}catch(Exception e) {//出现异常默认跳到最后一页
			newPage = currentMaxPage -1;
		}
		return newPage;
	}
	
	/**
	 * 根据页码查询
	 * 
	 * @time 2017年10月17日
	 * @param pageIndex
	 */
	public void getResultByPage(int pageIndex) {
		try {
			//add 2017-11-11 若是开始新的一天则最后一场和第一场不做重新加载
			if(DataConstans.All_Locked_Data_Map.size() == 0) {
				return;
			}
			
			//对于已缓存的数据的查询
			int cacheMaxPage = DataConstans.Paiju_Index.get();
			if(pageIndex > 0 && pageIndex < cacheMaxPage) {
				//恢复十个表数据
				reCovery10TablesByPage(pageIndex);
				//获取对应的场次
				//锁定的数据不能再被修改
				fobiddenChangeData();
				indexLabel.setVisible(true);
				log.debug("DataConstans.Index_Table_Id_Map.get(pageIndex):"+DataConstans.Index_Table_Id_Map.get(pageIndex+""));
				indexLabel.setText(DataConstans.Index_Table_Id_Map.get(pageIndex+""));
				
			}else {//对于缓存之外的数据查询（属于新增）
				//恢复锁定的数据
				changableData();
				//加载前一场数据
				pageIndex -= 1;
				reCovery10TablesByPage(pageIndex);//恢复十个表数据
				
				//清空相关表数据（保留类似昨日留底的表数据）
				clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
						//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
				indexLabel.setText(INDEX_ZERO);
			}
			//如果尾页有改动数据，需要同步缓存
			//reCoveryRelatedCache();

		} catch (Exception e) {
			ShowUtil.show("查询失败！！", 1);
			e.printStackTrace();
		}
	}
	
	public void clear10Tables() {
		//清空相关表数据（保留类似昨日留底的表数据）
		clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
				//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
		indexLabel.setText(INDEX_ZERO);
	}
	
	//恢复十个表数据
	public void reCovery10TablesByPage(int pageIndex) throws Exception {
		
		MoneyService.reCovery10TablesByPage(tableTotalInfo,tablePaiju,tableTeam,tableCurrentMoneyInfo,
				tableZijin,tableKaixiao,tableProfit,tableDangju,tableJiaoshou,tablePingzhang,LMLabel,
				pageIndex);
	}
	/********************************************************** 分页控件代码    结束  *********/
	//对于锁定的数据禁止被修改
	public void fobiddenChangeData() {
		lockedLabel.setVisible(true);
		lianmengBtn.setDisable(true);
		refreshBtn.setVisible(false);
		lockDangjuBtn.setVisible(false);
		delKaixiaoBtn.setVisible(false);
		delCurrentMoneyBtn.setVisible(false);
		openKaixiaoDialogBtn.setVisible(false);
		addCurrentMoneyLink.setVisible(false);
		importZJHBox.setVisible(false);
		tableCurrentMoneyInfo.setEditable(false);
		tableTeam.getColumns().get(5).setVisible(false);
		tableTeam.refresh();
//		tablePaiju.getColumns().get(0).setVisible(false);//copy
		tablePaiju.getColumns().get(6).setVisible(false);
		tablePaiju.refresh();
		tableZijin.setEditable(false);
		
	}
	//恢复数据（可见，可修改）
	public void changableData() {
		lockedLabel.setVisible(false);
		lianmengBtn.setDisable(false);
		refreshBtn.setVisible(true);
		lockDangjuBtn.setVisible(true);
		delKaixiaoBtn.setVisible(true);
		delCurrentMoneyBtn.setVisible(true);
		openKaixiaoDialogBtn.setVisible(true);
		addCurrentMoneyLink.setVisible(true);
		importZJHBox.setVisible(true);
		tableCurrentMoneyInfo.setEditable(true);
		tableTeam.getColumns().get(5).setVisible(true);
		tableTeam.refresh();
		//tablePaiju.getColumns().get(0).setVisible(false);//copy
		tablePaiju.getColumns().get(6).setVisible(true);
		tablePaiju.refresh();
		tableZijin.setEditable(true);
	}
	
	public void dbConnectAction(ActionEvent event) {
		if(DBConnection.getConnection() != null ) {
			dbConnectionState.setText("已成功连接！");
			dbConnectionState.setTextFill(Color.web("#0076a3"));
		}
	}
	//第一次获取获取联盟对账（原始数据）
	private static String getLMValFirstTime() {
		String lm = DataConstans.preDataMap.get("联盟对帐");
		try {
			Double.valueOf(lm);
			return lm;
		}catch(Exception e){
			Map<String,String> map = JSON.parseObject(lm, new TypeReference<Map<String,String>>() {});
			return MoneyService.nvl(map.get("联盟对帐"),"0");
		}
	}
	
	/**
	 * 撤销当局信息
	 */
	public void openCancelAlertAction(ActionEvent event){
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n你确定要撤销本局所有操作吗?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			importZJBtn.setDisable(false);
		    log.debug("确定撤销本局所有操作");
		    //情况一：第一场还没锁定就撤销
		    //直接从昨日留底中加载数据
		    //备份到01场次
		    try {
		    	//如果以下三个有没有数据则可判断还没导入最新的Excel
		    	ObservableList<TotalInfo> obListTotal = tableTotalInfo.getItems();
		    	ObservableList<WanjiaInfo> obListPaiju = tablePaiju.getItems();
		    	selected_LM_type = "";
		    	if(tableTotalInfo.getItems() == null
		    			|| tablePaiju.getItems() == null
		    			|| tableTotalInfo.getItems().size() == 0
		    			|| tablePaiju.getItems().size() == 0) {
		    		ShowUtil.show("您还没有导入数据", 1);
		    		return;
		    	}
		    	//当局已结算的团队服务费之和  要置为0
		    	current_Jiesuaned_team_fwf_sum = 0d;
		    	
			    if(DataConstans.All_Locked_Data_Map.size() == 0) {
			    	LMLabel.setText(getLMValFirstTime());
			    	//MoneyService.fillTableCurrentMoneyInfo(tableCurrentMoneyInfo, tableZijin, tableProfit,tableKaixiao,LMLabel);
			    	fillTables(tableCurrentMoneyInfo, tableZijin, tableProfit,tableKaixiao,LMLabel);
			    	//清空相关表数据（保留类似昨日留底的表数据）
			    	DataConstans.SumMap = new LinkedHashMap<>();//这里不缓存团队回水了，直接清空
			    	DataConstans.Team_Huishui_Map  = new LinkedHashMap<>();
			    	DataConstans.Total_Team_Huishui_Map  = new LinkedHashMap<>();
			    	DataConstans.Dangju_Team_Huishui_List  = new LinkedList<>();
			    	
					clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
					//获取第一次加载的上码表的个人详情{玩家ID=List<ShangmaDetailInfo>}
					ConsUtil.refresh_SM_Detail_Map();
					return;
			    }
			    
			    
			    //情况二：锁定后再撤销
			    //加载前一场数据
				int pageIndex = DataConstans.Index_Table_Id_Map.size();
				//撤销的代码
				cancelDangju();
				
				log.debug("=====================缓存中的数据恢复成功！");
				ShowUtil.show("撤销成功", 1);
			} catch (Exception e) {
				ShowUtil.show("撤销失败！！！", 1);
				e.printStackTrace();
			}
		} else {
			log.debug("取消撤销本局所有操作");
		}
	}
	/**
	 * 清空联盟对帐的信息
	 */
	public void openLMDialogAction(ActionEvent event){
		// if(!importZJBtn.isDisabled()) {
		// ShowUtil.show("只能在导入战绩后才能清空联盟对帐！");
		// return;
		// }
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n你确定要清空联盟对称信息么?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			LMLabel.setText("0.00");
			log.info("确定清空联盟对帐信息");
			
		} else {
			log.info("取消清空联盟对帐信息");
		}
	}
	
	/**
	 * 抽取出撤销的共同代码
	 * 
	 * @time 2017年10月17日
	 * @throws Exception
	 */
	public void cancelDangju() throws Exception {
		//加载前一场数据
		int pageIndex = DataConstans.Index_Table_Id_Map.size();
		//恢复十个表数据
		reCovery10TablesByPage(pageIndex);
		//清空相关表数据（保留类似昨日留底的表数据）
		clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
		//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
		
		//恢复缓存中的数据
		reCoveryRelatedCache();
		
	}
	
	/**
	 * 恢复缓存中的数据
	 */
	public void reCoveryRelatedCache() {
		String tableId = indexLabel.getText();//如第08局，是多少就多少，这个IndexLabel的值必须正确
		
		if(DataConstans.Dangju_Team_Huishui_List.size() > 0) {//说明需要恢复到前一场次的缓存状态
			if(DataConstans.Index_Table_Id_Map.containsValue(tableId)){
				//说明用户还没有导战绩进来，但是可能修改了其他地方
				//最好是保证场次是新的tableId
			}
			//恢复玩家战绩信息
			Map.Entry<String, List<UserInfos>> map = ConsUtil.getTail(DataConstans.zjMap);
			DataConstans.zjMap.remove(tableId);
			//恢复团队回水信息====原先是注释那一部分的代码，现在注释掉，改为从数据库加载上一场锁定的团队回水信息
//			Map<String,List<TeamHuishuiInfo>> teamHSMap = DataConstans.Team_Huishui_Map;
//			log.debug("撤销前："+JSON.toJSONString(teamHSMap));
//			log.debug("删除"+tableId);
//			DataConstans.Dangju_Team_Huishui_List.forEach( info -> {
//				String teamId = info.getTuan().toUpperCase();
//				String tabelId = info.getTableId();
//				List<TeamHuishuiInfo> tempList = teamHSMap.get(teamId);
//				for(TeamHuishuiInfo hsInfo : tempList) {
//					if(hsInfo.getTableId().equals(tabelId)) {
//						tempList.remove(hsInfo);
//						break;
//					}
//				}
//			});
//			log.debug("撤销后："+JSON.toJSONString(DataConstans.Team_Huishui_Map));
			Map<String, String> maps = DBUtil.getLastLockedData();
			if(maps != null && maps.size() > 0) {
				DataConstans.Team_Huishui_Map = JSON.parseObject(maps.get("Team_Huishui_Map"), new TypeReference<Map<String, List<TeamHuishuiInfo>>>() {});
				DataConstans.Total_Team_Huishui_Map = JSON.parseObject(maps.get("Total_Team_Huishui_Map"), new TypeReference<Map<String, List<TeamHuishuiInfo>>>() {});
			}
			//初始化当局回水
			DataConstans.Dangju_Team_Huishui_List = new LinkedList<>();
			
			//恢复上一场的团队累计 getLockedInfo
			int currentPage = Integer.parseInt(pageInput.getText());
			int size = DataConstans.All_Locked_Data_Map.size();
			if(currentPage - size == 1) {
				//此情况下要从上一场加载==团队回水总和
				DataConstans.SumMap = new HashMap<String,Double>();
				String sumOfTeam = MoneyService.getLockedInfo(size+"", "团队回水总和");
				String shangchangKaixiao = MoneyService.getLockedInfo(size+"", "实时开销总和");
				if("".equals(sumOfTeam)) {
					log.debug("从上一场加载==团队回水总和失败！！！！");
				}else {
					DataConstans.SumMap.put("团队回水及保险总和", Double.valueOf(sumOfTeam));
					DataConstans.SumMap.put("上场开销", Double.valueOf(shangchangKaixiao));//add 9-1
				}
			}
		}
		//上码表恢复数据
		ConsUtil.recovery_SM_Detail_Map();
	}
	
	//unused
	public void recoverySumMap() {
		//恢复上一场的团队累计 getLockedInfo
		int currentPage = Integer.parseInt(pageInput.getText());
		int size = DataConstans.All_Locked_Data_Map.size();
		if(currentPage - size == 1) {
			//此情况下要从上一场加载==团队回水总和
			DataConstans.SumMap = new HashMap<String,Double>();
			String sumOfTeam = MoneyService.getLockedInfo(size+"", "团队回水总和");
			String shangchangKaixiao = MoneyService.getLockedInfo(size+"", "实时开销总和");
			if("".equals(sumOfTeam)) {
				log.debug("从上一场加载==团队回水总和失败！！！！");
			}else {
				DataConstans.SumMap.put("团队回水及保险总和", Double.valueOf(sumOfTeam));
				DataConstans.SumMap.put("上场开销", Double.valueOf(shangchangKaixiao));//add 9-1
			}
		}
	}
	
	public  int getCurrentPage() {
		return Integer.parseInt(pageInput.getText());
	}
	
	/**
	 * 隐藏今日无数据的团队
	 * @time 2018年1月1日
	 * @param event
	 */
	public void proxyHideNoDataTeamAction(ActionEvent event) {
		TeamProxyService.proxyHideNoDataTeam();
	}
	
	/**
	 * 总汇刷新按钮
	 */
	public void zonghuiRefreshAction(ActionEvent event) {
		ZonghuiService.refreHuizongTable(tableZonghui,tableDangtianHuizong,tableZonghuiKaixiao,tableProfit);
	}
	/**
	 * 外债刷新按钮
	 */
	public void waizhaiRefreshAction(ActionEvent event) {
		WaizhaiService.generateWaizhaiTables(tableWaizhai,waizhaiHBox,tableCurrentMoneyInfo,tableTeam);
	}
	/**
	 * 会员搜索(按钮)
	 */
	public void memberSearchAction(ActionEvent event) {
		MemberService.setResult2ListView(memberSearchName,memberListView);
	}
	/**
	 * 会员搜索(回车)
	 */
	public void searchMemberByEnterEvent(KeyEvent event) {
		String keyWord = memberSearchName.getText();
		if(KeyCode.ENTER == event.getCode() && !StringUtil.isBlank(keyWord)) {
			MemberService.setResult2ListView(memberSearchName,memberListView);
		}
	}
	/**
	 * 上码搜索(按钮)
	 */
	public void shangmaSearchAction(ActionEvent event) {
		String keyWord = shangmaSearch.getText();
		if(!StringUtil.isBlank(keyWord)) {
			ShangmaService.shangmaSearch(keyWord, shangmaTeamId);
		}
	}

	/**
	 * 上码搜索(回车)
	 */
	public void shangmaSearchByEnter(KeyEvent event) {
		String keyWord = shangmaSearch.getText();
		if(KeyCode.ENTER == event.getCode() && !StringUtil.isBlank(keyWord)) {
			ShangmaService.shangmaSearch(keyWord, shangmaTeamId);
		}
	}

	/**
	 * 代理查询导出为Excel
	 */
	public void exportExcelAction(ActionEvent event) {
		TeamProxyService.exportExcel();
	}
	
	/**
	 * 代理查询一键导出为Excel
	 */
	public void exportExcelBatchAction(ActionEvent event) {
		TeamProxyService.exportTeamHasDataOneKey();
	}
	
	/**
	 * 实时上码导出为Excel
	 */
	public void exportSMExcelAction(ActionEvent event) {
		//ShowUtil.show("未开发",2);
		ShangmaService.exportShangmaExcel();
	}
	
	
	/**
	 * 代理查询刷新按钮 
	 */
	public void proxyRefreshAction(ActionEvent event) {
		TeamProxyService.proxyRefresh();
	}
	/**
	 * 删除实时开销按钮
	 */
	public void delKaixiaoAction(ActionEvent event) {
		int kaixiaoIndex = tableKaixiao.getSelectionModel().getFocusedIndex();
		KaixiaoInfo info = tableKaixiao.getSelectionModel().getSelectedItem();
		if(info != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("提示");
			alert.setHeaderText(null);
			alert.setContentText("实时开销名称："+info.getKaixiaoType()+" 金额："+info.getKaixiaoMoney()
				+"\r\n你确定要删除所选中的开销记录吗?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				tableKaixiao.getItems().remove(kaixiaoIndex);
				tableKaixiao.refresh();
				//删除数据库中的开销数据
				String kaixiaoID = info.getKaixiaoID();
				String kaixiaoGudong = info.getKaixiaoGudong();
				if(!StringUtil.isAnyBlank(kaixiaoID, kaixiaoGudong)) {
					DBUtil.del_gudong_kaixiao_by_id(kaixiaoID);
				}
			}
		}else {
			ShowUtil.show("请选中要删除的实时开销记录!");
			return;
		}
	}
	/**
	 * 删除实时金额
	 */
	public void delCurrentMoneyAction(ActionEvent event) {
		int index = tableCurrentMoneyInfo.getSelectionModel().getFocusedIndex();
		CurrentMoneyInfo info = tableCurrentMoneyInfo.getSelectionModel().getSelectedItem();
		if(info != null) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("提示");
			alert.setHeaderText(null);
			alert.setContentText("实时金额名称："+info.getMingzi()+" 金额："+info.getShishiJine()
			+"\r\n你确定要删除所选中的实时金额吗?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
				//add 删除前先判断这记录是子或父ID，若是则先解除父子ID关系
//				String playerId = info.getWanjiaId();
//				if(!StringUtil.isBlank(playerId) && (DataConstans.Combine_Sub_Id_Map.containsKey(playerId)
//						|| DataConstans.Combine_Super_Id_Map.containsKey(playerId))) {
//					ShowUtil.show("删除前请先解除本记录的父子ID关系!");
//					return;
//				}
				tableCurrentMoneyInfo.getItems().remove(index);
				tableCurrentMoneyInfo.refresh();
				MoneyService.flush_SSJE_table();
			}
		}else {
			ShowUtil.show("请选中要删除的实时金额记录!");
			return;
		}
	}
	
    /**
     * 开始新一天的统计
     */
    public void openTimeInputAction(ActionEvent event){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n即将初始化所有配置项，点击确定开始操作新一天报表!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//从数据库中判断是否符合从中途继续条件
			if(2 == DBUtil.newStaticOrContinue()) {
				ShowUtil.show("开始新一天的统计条件不满足，请点击中途继续按钮！");
				return;
			}
			
			
			//清空所有缓存数据
			DataConstans.clearAllData();
			//加载必要的原始数据（人员和回水）
			DataConstans.initMetaData();
			//加载昨日数据
			DataConstans.loadPreData();
			
			//渲染表格数据
			LMLabel.setText(getLMValFirstTime());
			fillTables(tableCurrentMoneyInfo, tableZijin, tableProfit,tableKaixiao,LMLabel);
			ShowUtil.show("加载数据成功", 2);
			//转到场次信息页面
			tabs.getSelectionModel().select(1);
			
			//代理查询：
			List<Huishui> list= DBUtil.getAllTeamHS();
			list.forEach(hs -> {
				DataConstans.huishuiMap.put(hs.getTeamId(), hs);
			});
			TeamProxyService.initTeamSelectAndZjManage(teamIDCombox);
			
			//积分查询初始化团队ID
			JifenService.init_Jifen_TeamIdCombox();
			
			//实时上码表：清空所有SM_Detail
			DataConstans.SM_Detail_Map= new HashMap<>();//新一天统计时应该清空昨日数据
			//实时上码表：获取最新的团队ID与玩家ID列表的映射
//			ConsUtil.refreshTeamIdAndPlayerId();
			//ConsUtil.refresh_SM_Detail_Map();//新一天统计时应该清空昨日数据
			
			//初始化股东列表
			DataConstans.initGudong();
		}
    }
    

    

    
    /**
     * 把渲染表格的数据加载出来，要判断是否2017-01-01，分成两步
     * @param table
     * @param tableZijin
     * @param tableProfit
     * @param tableKaixiao
     * @param LMLabel
     */
    public void fillTables(TableView<CurrentMoneyInfo> table,
			TableView<ZijinInfo> tableZijin,
			TableView<ProfitInfo> tableProfit,
			TableView<KaixiaoInfo> tableKaixiao,
			Label LMLabel) {
    	//清空十个表数据
    	clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
		//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
    	indexLabel.setText(INDEX_ZERO);

    	if(DBUtil.isPreData2017VeryFirst()) {
			MoneyService.fillTableCurrentMoneyInfo(tableCurrentMoneyInfo, tableZijin, tableProfit,tableKaixiao,LMLabel);
		}else {
			MoneyService.fillTableCurrentMoneyInfo2(tableTeam, tableCurrentMoneyInfo, tableZijin, tableProfit, tableKaixiao, LMLabel);
			//缓存战绩文件夹中多份excel中的数据 {团队ID=List<TeamHuishuiInfo>...}这个可能会被修改，用在展示每场的tableTeam信息
			Map<String, String> map = DBUtil.getLastLockedData();
			if(map != null && map.size() > 0) {
				DataConstans.Team_Huishui_Map = JSON.parseObject(map.get("Team_Huishui_Map"), new TypeReference<Map<String, List<TeamHuishuiInfo>>>() {});
			}
		}
    }
    
	
    /**
     * 中途继续按钮
     */
    public void middle2ContinueAction(ActionEvent event){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n即将加载关闭之前的最后锁定数据，确认要从中途加载吗？");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//从数据库中判断是否符合从中途继续条件
			if(1 == DBUtil.newStaticOrContinue()) {
				ShowUtil.show("中途继续条件不满足，请点击开始新一天的统计！");
				return;
			}
			
			//恢复所有缓存数据
			DataConstans.recoveryAllCache();
			DataConstans.initMetaData();
			//add 2017-10-21 代理类初始化团队ID
			TeamProxyService.initTeamSelectAndZjManage(teamIDCombox);
			//add 2017-10-21 代理类初始化团队ID
			JifenService.init_Jifen_TeamIdCombox();
			
			//加载十个表格数据
			int pageIndex = DataConstans.Paiju_Index.get();
			pageInput.setText(pageIndex+"");
//			indexLabel.setText(value);
//			getResultByPage(pageIndex);//切不可调用该方法，因为它会清空已经锁定的最后一场团队数据导致总和计算不对
			try {
				dateLabel.setText(DataConstans.Date_Str);
				//恢复锁定的数据
				changableData();
				//加载前一场数据
				pageIndex -= 1;
				reCovery10TablesByPage(pageIndex);//恢复十个表数据
				MoneyService.flush_SSJE_table();
				
				//清空相关表数据（保留类似昨日留底的表数据）
				clearData(tableTotalInfo,tablePaiju,tableTeam,tableDangju,tableJiaoshou,tablePingzhang);
						//tableCurrentMoneyInfo,tableZijin,tableKaixiao,tableProfit
				System.out.println(DataConstans.membersMap.size());
				indexLabel.setText(INDEX_ZERO);
			} catch (Exception e) {
				ShowUtil.show("中途继续失败：原因："+e.getMessage());
				e.printStackTrace();
			}
			ShowUtil.show("中途继续成功，请继续操作", 2);
			//转到场次信息页面
			tabs.getSelectionModel().select(1);
		}
    }
    
    /**
     * 结束今天统计按钮
     */
    public void endStaticAction(ActionEvent event){
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("提示");
    	alert.setHeaderText(null);
    	alert.setContentText("\r\n即将结束今天所有操作并将数据保存到数据库，确定？");
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		//将最后一场的锁定数据保存到数据库，只保留实时金额和昨日利润等
    		endOneDayStaticAndSave();
    		//处理锁定数据
    		DBUtil.handle_last_locked_data();
    		
    		//从数据库中删除相应信息
			//DBUtil.del_club_and_record();
    		
    		DBUtil.reset_clubZhuofei_to_0();//重置联盟中俱乐部的桌费和已结算为0
			
    		ShowUtil.show("已更新到数据库，即将关闭此软件。", 2);
    		Main.primaryStage0.close();
    		System.exit(0);
    	}
    }
    
    
	/**缓存到昨日留底总Map中**/
//	preDataMap.put("资金", zijinMap);
//	preDataMap.put("实时开销", presentPayoutMap);
//	preDataMap.put("实时金额", presentMoneyMap);
//	preDataMap.put("昨日利润", yesterdayProfitMap);//注意，不是整数LM
//	preDataMap.put("联盟对帐", LMMap);//
    
    public void endOneDayStaticAndSave() {
    	final Map<String, String>  lockedMap = getLastLockedData();
    	List<String> list = Arrays.asList("团队回水总和","团队回水","实时金额","实时金额总和","资金","资金总和","利润","利润总和","实时开销","实时开销总和","联盟对帐");
    	Map<String,String> map = new HashMap<>();
    	for(String key : list) {
    		map.put(key, lockedMap.get(key));
    	}
    	//锁定最后一场时默认实时开销只保留在利润中的总开销，具体记录清空
    	map.put("实时开销", JSON.toJSONString(Arrays.asList()));//new KaixiaoInfo()
    	map.put("实时开销总和", JSON.toJSONString(0));//new KaixiaoInfo()
    	String time = DataConstans.Date_Str;
    	DBUtil.insertPreData(time,JSON.toJSONString(map));
    }
    
	/**
	 * 获取最新的锁定数据
	 */
	public Map<String, String>  getLastLockedData() {
		int paijuIndex = DataConstans.All_Locked_Data_Map.size();
		return DataConstans.All_Locked_Data_Map.get(paijuIndex+"");//总缓存数据
//		Map<String,String> map = new HashMap<>();
//		map.put("战绩", JSON.toJSONString(TotalInfoList));
//		map.put("玩家", JSON.toJSONString(WanjiaInfoList));
//		map.put("团队回水", JSON.toJSONString(TeamInfoList));
//		map.put("团队回水总和", sumOfTeam);
//		map.put("实时金额", JSON.toJSONString(list));
//		map.put("实时金额总和", sumOfCurrentMoney);
//		map.put("资金", JSON.toJSONString(listZijinInfo));
//		map.put("资金总和", sumOfZijin);
//		map.put("利润", JSON.toJSONString(listProfitInfo));
//		map.put("利润总和", sumOfProfit);
//		map.put("实时开销", JSON.toJSONString(listKaixiaoInfo));
//		map.put("实时开销总和", sumOfKaixiao);
//		map.put("当局", JSON.toJSONString(listDangjuInfo));
//		map.put("当局总和", sumOfDangjuInfo);
//		map.put("交收", JSON.toJSONString(listJiaoshouInfo));
//		map.put("交收总和", sumOfJiaoshouInfo);
//		map.put("平帐", JSON.toJSONString(listPingzhangInfo));
//		map.put("平帐总和", sumOfPingzhangInfo);
//		map.put("联盟对帐", getLMLabelText(LMLabel));
	}
	
	
	/**
	 * 清空数据库
	 * 
	 * @time 2017年10月17日
	 * @param event
	 */
	public void clearAllDataAction(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("即将清空桌费、已结算、已锁定的历史数据，是否继续?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			if(DBUtil.clearAllData()) {
				ShowUtil.show("清空成功。",1);
			}else {
				ShowUtil.show("清空失败");
			}
		}
	}
	
	
	/**
	 * 查询会员积分排名
	 * @param event
	 */
	public void jfQueryAciton(ActionEvent event) {
		JifenService.jifenQuery(tableJifen,jfStartTime,
				jfEndTime,jifenInput,jifenRankLimit,jfTeamIDCombox);
	}
	
	
	/**
	 * 导出人员表
	 */
	public void exportMembersExcelAction(ActionEvent event) {
		MoneyService.exportMemberExcel();
	}
	
	/**
	 * 导出回水表
	 */
	public void exportHuishuiExcelAction(ActionEvent event) {
		MoneyService.exportTeamhsExcel();
	}
	
	/**
	 * 导出合并ID表
	 */
	public void exportCombineIDAction(ActionEvent event) {
		MoneyService.exportCombineIDAction();
	}
	
    /**
     * 小林：解决红色列的问题
     * @param t 列实体
     * @return
     */
    public static <T> Callback<TableColumn<T,String>, TableCell<T,String>> getColorCellFactory(T t){
    	return new Callback<TableColumn<T,String>, TableCell<T,String>>() {  
    	    public TableCell<T,String> call(TableColumn<T,String> param) {  
    	    	TableCell<T,String> cell = new TableCell<T,String>() {  
    	            ObservableValue ov;  
    	            @Override  
    	            public void updateItem(String item, boolean empty) {  
    	                super.updateItem(item, empty);  
    	                this.setTextFill(null); 
    	                if (!isEmpty() && item != null) {  
    	                    ov = getTableColumn().getCellObservableValue(getIndex()); 
    	                     //log.debug(ov.getValue());  
    	                     if(item.contains("-")) {
    	                    	 //this.setStyle("-fx-font-weight: bold;-fx-alignment: CENTER;");
 	                             //this.setTextFill(Color.web("#CD3700"));
    	                    	 this.setTextFill(Color.RED);  
    	                     }else {
    	                    	 this.setTextFill(Color.BLACK);  
    	                     }
    	                    setText(item);  
    	                }  
    	            }  
    	        }; 
    	        cell.setEditable(false);//不让其可编辑
    	        cell.setOnMouseClicked((MouseEvent t) -> {  
    	        	//鼠标双击事件
					// if (t.getClickCount() == 2 && cell.getIndex() < myTable.getItems().size()) {
					// log.debug("鼠标双击事件"+atomic.getAndIncrement());
					// //双击执行的代码
					// TABLEDATA INFO = MYTABLE.GETITEMS().GET(CELL.GETINDEX());
					// OPENADDDIAG(INFO);
					// MYTABLE.REFRESH();
					// }
    	        }); 
    	        return cell;
    	    }
        };  
    }
    
    /**
     * 导出实时金额表
     * 
     * @time 2017年10月28日
     * @param event
     */
    public void exportSSJEAction(ActionEvent event) {
    	try {
			MoneyService.exportSSJEAction(tableCurrentMoneyInfo);
			ShowUtil.show("导出实时金额表Excel成功",2);
		} catch (Exception e) {
			ShowUtil.show("导出实时金额表Excel失败，原因："+e.getMessage());
		}
    }
    
    /**
     * 刷新下一场的缓存按钮
     * 
     * @time 2017年11月10日
     * @param event
     */
    public void refreshNextExcelAction(ActionEvent event) {
    	//先给个提示
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n你确定要强制刷新自动导入下一场的缓存操作吗?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() != ButtonType.OK)
			return;
		
    	//获取导入的值
    	String zjFilePath = excelDir.getText();
		if(StringUtil.isBlank(zjFilePath)){
			ShowUtil.show("战绩栏要有初始Excel地址！");
			return;
		}
    	//获取父级目录下的所有Excel文件
    	File rootFile = new File(zjFilePath.substring(0,zjFilePath.lastIndexOf("\\")));
    	File[] excelList = rootFile.listFiles();
    	//清空并重置待导入队列
    	excelQueue.clear();
    	for(File file : excelList) {
    		excelQueue.add(file);
    	}
    }
    
    /**
     * 导入下一场（测试使用）
     * 
     * @time 2017年11月10日
     * @param event
     */
    public void importNextZJExcelAction(ActionEvent event) {
    	if(excelQueue.size() > 0) {
    		try {
				File excelFile = excelQueue.poll();
				excelDir.setText(excelFile.getAbsolutePath());
				importZJBtn.fire();
			} catch (Exception e) {
				e.printStackTrace();
			}
    	}else {
    		ShowUtil.show("没有下一场的数据了",2);
    	}
    }
    
    /**
     * 支付全部（有空再去实现吧）
     * 
     * @time 2017年11月11日
     * @param event
     */
    public void payedAllAction(ActionEvent event) {
    	if(tablePaiju != null && tablePaiju.getItems() != null ) {
    		
    		TableColumn<WanjiaInfo, ?> col = tablePaiju.getColumns().get(tablePaiju.getColumns().size()-1);
    		
    		ObservableList<TableColumn<WanjiaInfo, ?>> columns = tablePaiju.getColumns();
    		
    		for(WanjiaInfo info : tablePaiju.getItems()) {
    			System.out.println(info.toString());
    		}
    	}else {
    		ShowUtil.show("全支付失败：因为牌局表为空！");
    	}
    }
    

    /**
     * 删除团队ID
     * 备注：此方法的界面按钮已经删除，但后台代码仍保留着（以备后期要重新开发）
     * 
     * @time 2017年11月14日
     * @param event
     */
    public void delTeamAction(ActionEvent event) {

		//注册界面
		TextInputDialog dialog = new TextInputDialog("");
		dialog.setTitle("注册");
		dialog.setHeaderText(null);
		dialog.setContentText("删除团队ID:");
		//dialog.getDialogPane().getButtonTypes().removeAll(ButtonType.CANCEL);
		Optional<String> result = dialog.showAndWait();

		result.ifPresent(_teamId -> {
			try {
				String teamId = _teamId.trim().toUpperCase();
				//判断是否有此团队
				Huishui hs = DataConstans.huishuiMap.get(teamId);
				if(hs == null) {
					ShowUtil.show("哥，不存在此团队ID,请检查！");
					return;
				}
				
				//清空数据库与缓存中回水表中的团队记录
				DBUtil.delHuishui(teamId);
				DataConstans.huishuiMap.remove(teamId);
				
				//清空数据库与缓存中属于该团队的人员记录
				DBUtil.delMembers_after_delTeam(teamId);
				synchronized(DataConstans.membersMap) {
				  Iterator<Map.Entry<String, Player>> ite = DataConstans.membersMap.entrySet().iterator();  
				    while(ite.hasNext()){  
				        Map.Entry<String,Player> entry = ite.next();  
				        Player player = entry.getValue();
				        if(player.getTeamName().trim().toUpperCase().equals(teamId)) {
				        	ite.remove();//删除
				        }
				    }
				}
				
				//以下清除代码 
				//A 清空代理查询中的团队下拉框
				String selected_team = teamIDCombox.getSelectionModel().getSelectedItem();
				teamIDCombox.getItems().remove(teamId);
				if(teamId.equals(selected_team) && teamIDCombox.getItems().size() > 0) {
					teamIDCombox.getSelectionModel().select(0);
				}
				//B 积分查询中的团队下拉框
				String selected_jifen = jfTeamIDCombox.getSelectionModel().getSelectedItem();
				jfTeamIDCombox.getItems().remove(teamId);
				if(teamId.equals(selected_jifen) && jfTeamIDCombox.getItems().size() > 0) {
					jfTeamIDCombox.getSelectionModel().select(0);
				}
				//C 实时上码中的团队下拉框
				ShangmaService.initShangmaButton();
				((Button)shangmaVBox.getChildren().get(0)).fire();
				
				ShowUtil.show("删除成功！",2);
			} catch (Exception e) {
				log.error("删除团队ID失败",e);
			}
		});
    }
    
    /**
     * 修改团队回水
     * 
     * @time 2017年11月17日
     * @param event
     */
    public void updateTeamHsRateAction(ActionEvent event) {

    	InputDialog inputDlg = new InputDialog("修改","待修改的团队ID:","团队新回水比例：");
    	Optional<Pair<String, String>> result = inputDlg.getResult();
    	result.ifPresent(teamId_and_hsRate -> {
    		
    		//获取到原始的有效的团队ID及团队回水率
    		String teamID = teamId_and_hsRate.getKey();
    		String teamHsRate = teamId_and_hsRate.getValue();
    		if(StringUtil.isBlank(teamID) || StringUtil.isBlank(teamHsRate)) {
    			ShowUtil.show("修改失败！原因：团队ID或团队回水不能为空！！");
    			return;
    		}
    		teamID = teamID.trim().toUpperCase();
    		teamHsRate = teamHsRate.trim();
			Huishui hs = DataConstans.huishuiMap.get(teamID);//判断是否有此团队
			if(hs == null) {
				ShowUtil.show("修改失败！原因：不存在此团队ID,请检查！");
				return;
			}
    		if(!teamHsRate.endsWith("%")) {
    			ShowUtil.show("修改失败！原因：团队回水必须中有百分号！");
    			return;
    		}
    		//开始修改
    		//1修改数据库
    		Double _hsRate = NumUtil.getNumByPercent(teamHsRate);
    		int size = (_hsRate+"").length();
    		if(size > 6) {
    			_hsRate = NumUtil.getNum(NumUtil.digit4(_hsRate.toString()));
    		}
    		if(!DBUtil.updateTeamHsRate(teamID, _hsRate+"")) {
    			return;
    		}
    		
    		//2修改缓存
    		hs.setHuishuiRate(NumUtil.getNumByPercent(teamHsRate)+"");//地址引用，会修改值
    		
    	    ShowUtil.show("修改成功！",2);
    	});
    }
    
    
    
    /**
     * 修改团队保险比例
     * 
     * @time 2018年2月8日
     * @param event
     */
    public void updateTeamHsBaoxianRateAction(ActionEvent event) {
    	
    	InputDialog inputDlg = new InputDialog("修改团队保险比例","待修改的团队ID:","团队新保险比例：");
    	Optional<Pair<String, String>> result = inputDlg.getResult();
    	result.ifPresent(teamId_and_hsRate -> {
    		
    		//获取到原始的有效的团队ID及团队回水率
    		String teamID = teamId_and_hsRate.getKey();
    		String teamHsRate = teamId_and_hsRate.getValue();
    		if(StringUtil.isBlank(teamID) || StringUtil.isBlank(teamHsRate)) {
    			ShowUtil.show("修改失败！原因：团队ID或团队回水不能为空！！");
    			return;
    		}
    		teamID = teamID.trim().toUpperCase();
    		teamHsRate = teamHsRate.trim();
    		Huishui hs = DataConstans.huishuiMap.get(teamID);//判断是否有此团队
    		if(hs == null) {
    			ShowUtil.show("修改失败！原因：不存在此团队ID,请检查！");
    			return;
    		}
    		if(!teamHsRate.endsWith("%")) {
    			ShowUtil.show("修改失败！原因：团队保险必须中有百分号！");
    			return;
    		}
    		//开始修改
    		//1修改数据库
    		Double _hsRate = NumUtil.getNumByPercent(teamHsRate);
    		int size = (_hsRate+"").length();
    		if(size > 6) {
    			_hsRate = NumUtil.getNum(NumUtil.digit4(_hsRate.toString()));
    		}
    		if(!DBUtil.updateTeamHsBaoxianRate(teamID, _hsRate+"")) {
    			return;
    		}
    		
    		//2修改缓存
    		hs.setInsuranceRate(NumUtil.getNumByPercent(teamHsRate)+"");//地址引用，会修改值
    		
    		ShowUtil.show("修改成功！",2);
    	});
    }


    /**
     * 修改团队股东
     * 
     * @param event
     */
    public void updateTeamHsGudongAction(ActionEvent event) {
    	
    	InputDialog inputDlg = new InputDialog("修改团队股东","待修改的团队ID:","团队新股东：");
    	Optional<Pair<String, String>> result = inputDlg.getResult();
    	result.ifPresent(teamId_and_teamGD -> {
    		
    		//获取到原始的有效的团队ID及团队回水率
    		String teamID = teamId_and_teamGD.getKey();
    		String teamGD = teamId_and_teamGD.getValue();
    		if(StringUtil.isBlank(teamID) || StringUtil.isBlank(teamGD)) {
    			ShowUtil.show("修改失败！原因：团队ID或团队股东不能为空！！");
    			return;
    		}
    		teamID = teamID.trim().toUpperCase();
    		teamGD = teamGD.trim();
    		Huishui hs = DataConstans.huishuiMap.get(teamID);//判断是否有此团队
    		if(hs == null) {
    			ShowUtil.show("修改失败！原因：不存在此团队ID,请检查！");
    			return;
    		}
    		//开始修改
    		//1修改数据库
    		if(!DBUtil.updateTeamHsGudong(teamID, teamGD)) {
    			ShowUtil.show("修改失败");
    			return;
    		}
    		
    		//2修改缓存
    		hs.setGudong(teamGD);//地址引用，会修改值
    		
    		ShowUtil.show("修改成功！",2);
    	});
    }
    
    /**
     * 检测合并ID
     * @time 2017年12月2日
     * @param even
     */
    public void checkCombineIdAction(ActionEvent even) {
    	List<String> resList = CombineIDController.checkCombineId();
    	if(CollectUtil.isNullOrEmpty(resList)) {
    		ShowUtil.show("检测通过",2);
    		return;
    	}
    	StringBuilder sb = new StringBuilder();
    	int i = 0;
    	for(String msg : resList) {
    		sb.append(++i).append("、").append(msg).append("\r\n\r\n");
    	}
    	Dialog<?> dialog = new Dialog<>();
    	dialog.setTitle("检测结果");
    	dialog.setHeaderText(null);
    	
    	ButtonType loginButtonType = new ButtonType("我知道了", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
    	
    	
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 15, 5, 10));

    	
    	TextArea textArea = new TextArea();
    	textArea.setText(sb.toString());
    	grid.add(textArea, 0, 0);

    	dialog.getDialogPane().setContent(grid);
    	
    	dialog.show();

    }


    /**
     * 实时上码开始新的一天由用户自行点击加载次日的数据
     * 
     * @time 2018年2月4日
     * @param event
     */
    public void loadNextDayDataAction(ActionEvent event) {
    	ShangmaService.loadNextDayDataAction();
    }
    
    /**
     * 实时上码新增次日上码
     * 
     * @time 2018年2月4日
     * @param event
     */
    public void addNextDaySMDetailAction(ActionEvent event) {
    	ShangmaService.addNextDaySMDetailAction();
    }
    
    /**
     * 保存实时上码中的团队押金与团队额度修改
     * @param event
     */
    public void saveTeamYajinAndEduAction(ActionEvent event) {
    	ShangmaService.updateTeamYajinAndEdu();
    }
    
    /**
     * 导入空白表
     * @time 2018年2月25日
     * @param event
     */
    public void importBlankExcelAction(ActionEvent event) {
    	String tableId = RandomUtil.getRandomNumber(10000,20000) + "";//随机生成ID
    	List<UserInfos> blankDataList = new ArrayList<UserInfos>();
    	//存储数据  {场次=infoList...}
		DataConstans.zjMap.put(tableId, blankDataList);
		final_selected_LM_type = "联盟1";
		selected_LM_type = "联盟1";
		LMController.currentRecordList = new ArrayList<>();

    	indexLabel.setText("第"+tableId+"局");
//		//1 填充总信息表
//		MoneyService.fillTablerAfterImportZJ(tableTotalInfo, tablePaiju,tableDangju,tableJiaoshou,tableTeam,(List<UserInfos>)wrap.obj,tableId);
//		//2填充当局表和交收表和团队表的总和
//		MoneyService.setTotalNumOnTable(tableDangju, DataConstans.SumMap.get("当局"));
//		MoneyService.setTotalNumOnTable(tableJiaoshou, DataConstans.SumMap.get("交收"));
//		tableTeam.getColumns().get(4).setText(MoneyService.digit0(DataConstans.SumMap.get("团队回水及保险总和")));
		importExcelData(tableId, blankDataList);
		
		importZJBtn.setDisable(true);//导入不可用
		ShowUtil.show("导入空白战绩文件成功", 2);
    }

}
