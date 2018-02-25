package com.kendy.controller;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.Club;
import com.kendy.entity.ClubQuota;
import com.kendy.entity.ClubZhuofei;
import com.kendy.entity.LMDetailInfo;
import com.kendy.entity.LMSumInfo;
import com.kendy.entity.Record;
import com.kendy.excel.ExportAllLMExcel;
import com.kendy.excel.ExportLMExcel;
import com.kendy.interfaces.Entity;
import com.kendy.util.CollectUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.InputDialog;
import com.kendy.util.MapUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TimeUtil;

import application.DataConstans;
import application.MyController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 * 处理联盟对帐的控制器
 * 
 * @author 林泽涛
 * @time 2017年11月24日 下午9:31:04
 */
public class LMController implements Initializable{
	
	

	//=====================================================================
	@FXML private ListView<String>  clubListView;
	@FXML private AnchorPane bigAnchorPane;//展示面板（包含单个详情和所有俱乐部统计）
	@FXML private Label sumOfZF;//合计桌费
	@FXML private Label currentLMLabel;//当前联盟
	
	//=====================================================================单个俱乐部总和表
	@FXML private TableView<LMSumInfo>  tableLMSum;     
	@FXML private TableColumn<LMSumInfo,String> lmSumName;
	@FXML private TableColumn<LMSumInfo,String> lmSumZJ;
	@FXML private TableColumn<LMSumInfo,String> lmSumInsure;
	@FXML private TableColumn<LMSumInfo,String> lmSumPersonCount;
	
	
	//=====================================================================单个俱乐部详情表
	@FXML private TableView<LMDetailInfo>  tableLMDetail;     
	@FXML private TableColumn<LMDetailInfo,String> lmDetailTableId;
	@FXML private TableColumn<LMDetailInfo,String> lmDetailZJ;
	@FXML private TableColumn<LMDetailInfo,String> lmDetailInsure;
	@FXML private TableColumn<LMDetailInfo,String> lmDetailPersonCount;
	
	
	private static Logger log = Logger.getLogger(LMController.class);
	
	private static final String FLOW_PANE_ID = "flowPane";
	
	//导入每场战绩时的所有俱乐部记录
	public static List<Record> currentRecordList = new ArrayList<>();
	
	//{俱乐部ID : 俱乐部信息}
	public static Map<String,Club> allClubMap = new HashMap<>();
	
	//{俱乐部ID : 俱乐部每一场信息}
	public static Map<String,List<Record>> eachClubList = new HashMap<>();
	
	//缓存三个联盟的信息
	public static List<Map<String,List<Record>>> LMTotalList = new ArrayList<>();
	
	public static ListView<String> _clubListView = new ListView<>();
	
	public static final String[] LM = new String[] {"联盟1","联盟2","联盟3"};
	
	//同步俱乐部信息（缓存与数据库）
	public static void refreshClubList() {
		//这里如果allClubMap这个缓存没有数据，尝试从数据库获取（或者在中途中就加载，或者初始化就加载）
		
		//锁定数据后同步到缓存中
		if(currentRecordList != null ) {
			currentRecordList.forEach(record -> {
				String clubId = record.getClubId();
				if(allClubMap.get(clubId) == null) {
					allClubMap.put(clubId, new Club(clubId,record.getClubName(),"0"));
				}
			});
		}
		
		//缓存数据全部更新到数据库
		if(allClubMap == null || allClubMap.size()==0) return;
		allClubMap.values().forEach(club -> {
			try {
				if(!DBUtil.isHasClub(club.getClubId())) {
					DBUtil.addClub(club);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		//刷新每个俱乐部的列表
		refresh_eachClubList();
		
		//这里更新俱乐部列表（静态访问）
//		_clubListView.setItems(null);
//		ObservableList<String> obList = FXCollections.observableArrayList();
//		allClubMap.values().forEach(club -> {
//			obList.add(club.getName()+"=="+club.getClubId()+"=="+club.getEdu());
//		});
//		_clubListView.setItems(obList);
//		
//		//默认选择第一个
//		if(_clubListView.getItems() != null) {
//			_clubListView.getSelectionModel().select(0);
//		}
		//refreshClubListView();
		
		compute3LM();//计算三个联盟的相关信息
		refreshClubListView(1);//1表示加载联盟1的数据（依此类推）
	}
	

	
	/**
	 * 刷新俱乐部列表视图
	 * 
	 * @time 2017年12月14日
	 * @param index 1：联盟1（依此类推）
	 * index 取 [1,2,3]
	 */
	public static void refreshClubListView(int index) { 
		//这里更新俱乐部列表（静态访问）
		_clubListView.setItems(null);
		ObservableList<String> obList = FXCollections.observableArrayList();
		if(!LMTotalList.isEmpty()) {
			LMTotalList.get(index-1).keySet().forEach(clubId -> {
				Club club = allClubMap.get(clubId);
//				obList.add(club.getName()+"=="+club.getClubId()+"=="+club.getEdu());
				obList.add(club.getName()+"=="+club.getClubId()+"=="+get_LM_edu(club, index));
			});
		}
		_clubListView.setItems(obList);
		
		//默认选择第一个
		if(_clubListView.getItems() != null) {
			_clubListView.getSelectionModel().select(0);
		}
	}
	
	/**
	 * 点击俱乐部时自动更新详情表和主表
	 * 设置ListView监听
	 * 
	 * @time 2017年11月25日
	 */
	@SuppressWarnings({ "rawtypes" })
	public  void initSingClubListen() {
		
		//ListView变化时自动更新右边的信息
		clubListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Object>() {
		            @Override
		            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		            	
		            	/*********************设置前的处理 ***************************************/
		            	//清空所有动态表、显示单个俱乐部的两个表
		            	destroyAllView();
		            	//俱乐部所有的信息
		            	if(eachClubList != null && eachClubList.size() == 0) {
		            		refresh_eachClubList();//更新一下俱乐部记录列表
		            	}
		            	
		            	/*********************详情 : 单个俱乐部详情信息设置 ************************/
		            	//获取值
		            	String newVal = (String)newValue;
		            	if(StringUtil.isBlank(newVal)) return;//屏蔽空指针异常
		            	String[] newValArr = newVal.trim().split("==");
		            	String newClubName = newValArr[0];
		            	String newClubId   = newValArr[1];
		            	String newClubEdu  = newValArr[2];
		            	
		            	//1、详情表重新赋值（务必先于主表更新数值）
		            	setDataTableLMDetail(newClubId,true);//true表示要进行求和统计，这个参数用于拓展详情的详情
		            	
		            	/**********************总和 ： 单个俱乐部总和信息设置 ************************/
		            	//2、单个俱乐部主表重新赋值
		            	setDataTableLMSum();
		            	//3、更新所属联盟的合计桌费
						setNewSumOfZF();
		            	

		            }
				});
	}
	
	/**
	 * 点击所有俱乐部总帐按钮后的数据统计
	 * 2017-12-14 新增了单个联盟计算操作
	 * 
	 * @time 2017年11月26日
	 * @return 所有俱乐部总帐Map
	 */
	public  Map<String, List<LMSumInfo>> getAllClubSumMap(Map<String, List<Record>> current_LM_Map){
		 Map<String,List<LMSumInfo>> map = new HashMap<>();
		 
		 for(Map.Entry<String, List<Record>> entry : current_LM_Map.entrySet()) {
			 List<LMSumInfo> tempList = new ArrayList<>();
			 String clubId     = entry.getKey();
			 List<Record> list = entry.getValue();
			 
			 if(list == null) {
				 log.warn("根据详情找不到俱乐部信息:"+clubId);//如果有回查功能就有可能出现这个问题
				 map.put(clubId, tempList);
				 continue;//注意：这个后面要处理
			 }
			 list = computSumList(list,false);//求和统计,但不进行排序!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 
			//初始化数据
			LMSumInfo info1 = new LMSumInfo(); info1.setLmSumName("当天总帐");
			LMSumInfo info2 = new LMSumInfo(); info2.setLmSumName("桌费");
			LMSumInfo info3 = new LMSumInfo(); info3.setLmSumName("玩家战绩");
			
			//统计数据
			double sumOfEachClubZJ = 0d;
			double sumOfEachClubInsure = 0d;//这个要全部全和
			int sumOfEachClubPersonCount = 0;
			for(Record record : list) {
				sumOfEachClubZJ += NumUtil.getNum(record.getScore());
				sumOfEachClubInsure += NumUtil.getNum(record.getInsurance());
				sumOfEachClubPersonCount += NumUtil.getNum(record.getPersonCount());
			}
			info1.setLmSumZJ(NumUtil.digit0(""+sumOfEachClubZJ));
			info1.setLmSumInsure(NumUtil.digit0(""+sumOfEachClubInsure));
			info1.setLmSumPersonCount(""+sumOfEachClubPersonCount);
			 
			//桌费
			String zf = "";
//			if(allClubMap.get(clubId) != null) zf = allClubMap.get(clubId).getZhuoFei();
			if(allClubMap.get(clubId) != null) {
				int lmType = getCurrentLMType();
				zf = get_LM_Zhuofei(allClubMap.get(clubId),lmType);
			}
			info2.setLmSumZJ(getNavigateSumZJ(zf));
			//玩家战绩   联盟总账里的玩家战绩公式 = sum（当天总账-保险）
			info3.setLmSumZJ(NumUtil.digit0(sumOfEachClubZJ - sumOfEachClubInsure));
			
			tempList.add(info1);
			tempList.add(info2);
			tempList.add(info3);
			
			map.put(clubId, tempList);
		 }
		 
		 return map;
	}
	
	//桌费显示负数
	private String getNavigateSumZJ(String zj) {
		Double zhanji = 0d;
		try {
			zhanji = Double.valueOf(zj);
		} catch (NumberFormatException e) {
			ErrorUtil.err("战绩转换数值格式出错！"+zj);
			zhanji = 0d;
		}
		int _zj = zhanji.intValue();
		return _zj <= 0 ? zj : _zj * (-1)+"";
	}
	
	
	/**
	 * 更新单个俱乐部总和表
	 * 备注：需要用到最新的详情表数据
	 * @time 2017年11月25日
	 */
	public void setDataTableLMSum() {
		//初始化数据
		LMSumInfo info4 = new LMSumInfo(); info4.setLmSumName("结余");
		LMSumInfo info2 = new LMSumInfo(); info2.setLmSumName("桌费");
		LMSumInfo info3 = new LMSumInfo(); info3.setLmSumName("已结算");
		LMSumInfo info1 = new LMSumInfo(); info1.setLmSumName("当天总帐");
		tableLMSum.setItems(null);
		ObservableList<LMSumInfo> obList = FXCollections.observableArrayList();
		obList.add(info1);
		obList.add(info2);
		obList.add(info3);
		obList.add(info4);
		tableLMSum.setItems(obList);
		if(tableLMDetail == null && tableLMDetail.getItems() == null)  return;
		
		//桌费
//		info2.setLmSumZJ(getSelectedClub().getZhuoFei());
//		info3.setLmSumZJ(getSelectedClub().getYiJieSuan());
		info2.setLmSumZJ(this.get_LM_Zhuofei());
		info3.setLmSumZJ(this.get_LM_YiJiesuan());
		
		//统计数据
		double sumOfEachClubZJ = 0d;
		double sumOfEachClubInsure = 0d;//这个要全部全和
		int sumOfEachClubPersonCount = 0;
		for(LMDetailInfo detailInfo : tableLMDetail.getItems()) {
			sumOfEachClubZJ += NumUtil.getNum(detailInfo.getLmDetailZJ());
			sumOfEachClubInsure += NumUtil.getNum(detailInfo.getLmDetailInsure());
			sumOfEachClubPersonCount += NumUtil.getNum(detailInfo.getLmDetailPersonCount());
		}
		info1.setLmSumZJ(NumUtil.digit0(""+sumOfEachClubZJ));
		info1.setLmSumInsure(NumUtil.digit0(""+sumOfEachClubInsure));
		info1.setLmSumPersonCount(""+sumOfEachClubPersonCount);
		
		tableLMSum.refresh();//刷新才会显示
		
		//计算结余
		updateTableLMSumOnly();//里面会去刷新表
	}
	
	
	public Map<String, ClubQuota> get_LM_club_info() {
		//{俱乐部ID : 俱乐部配额信息}
		Map<String,ClubQuota> single_LM_map = new HashMap<>();
		//{联盟Index : {俱乐部ID : 俱乐部配额信息}}
		//Map<String,Map<String,List<ClubQuota>>> totalMap = new HashMap<>();
		int lmType = getCurrentLMType()-1;
		Map<String, List<Record>> current_LM_Map =  LMTotalList.get(lmType);//遍历这三个
		Map<String,List<LMSumInfo>> allClubSumMap = getAllClubSumMap(current_LM_Map);
		allClubSumMap.forEach( (clubId,sumList) -> {
			Club club = allClubMap.get(clubId);
			String sumZJ = getSumZJ(sumList);
			String yiJieshan = get_LM_YiJiesuan(club, lmType);
			String zhuoFei = get_LM_Zhuofei(club, lmType);
			String jieyu = NumUtil.digit0(
					NumUtil.getNum(sumZJ)+NumUtil.getNum(yiJieshan)+NumUtil.getNum(zhuoFei));
			List<ClubQuota> list  =  new ArrayList<>();
			ClubQuota quota = new ClubQuota();
			quota.setEuotaClubId(clubId);
			quota.setQuotaClubName(club.getName());
			quota.setQuotaJieyu(jieyu);
			quota.setQuotaRest(jieyu);
			single_LM_map.put(clubId, quota);
		});
		return single_LM_map;
	}
	
	/**
	 * 获取俱乐部的总战绩
	 * @time 2017年12月17日
	 * @param sumList 指导出全部俱乐部总和信息Excel时的某个俱乐部信息
	 * @return
	 */
	public String getSumZJ(List<LMSumInfo> sumList) {
		return sumList.parallelStream()
				.filter(sumInfo -> "总战绩".equals(sumInfo.getLmSumName())
		).collect(Collectors.toList()).get(0).getLmSumZJ();
	}
	
	/**
	 * 根据ID更新详情表
	 * 
	 * @time 2017年11月25日
	 * @param clubId
	 * @param isNeedSum 是否需要进行求和
	 */
	public void setDataTableLMDetail(String clubId,boolean isNeedSum) {
		tableLMDetail.setItems(null);
//		List<Record> list = eachClubList.get(clubId);
		int LMTypeIndex = this.getCurrentLMType()-1;
		Map<String,List<Record>> LMMap = LMTotalList.get(LMTypeIndex);
		List<Record> list = LMMap.get(clubId);
		
		ObservableList<LMDetailInfo> obList = FXCollections.observableArrayList();
		if(list == null) {
			tableLMDetail.setItems(obList);
			log.warn("根据详情表找不到俱乐部信息:"+clubId);//如果有回查功能就有可能出现这个问题
			return;
		}
		if(isNeedSum) 
			list = computSumList(list,true);//求和统计
		
		list.forEach(record -> {
//			 * @param lmDetailTableId
//			 * @param lmDetailZJ
//			 * @param lmDetailInsure
//			 * @param lmDetailPersonCount
			String tableId = record.getTableId();
			String zj = record.getScore();
			String insure = record.getInsurance();
			String personNumbers = record.getPersonCount();
			obList.add(new LMDetailInfo(tableId,zj,insure,personNumbers));
			
		});
		tableLMDetail.setItems(obList);
		
	}
	
	/**
	 * 每场都进行求和统计
	 * 
	 * 规则：每场的战绩公式：=（Sum(当场战绩)+俱乐部保险其中一个）*0.975
	 * 新增了排序功能
	 * 
	 * @time 2017年11月25日
	 * @param list 某个俱乐部的所有场次信息，具体到每一条战绩记录
	 */
	private static List<Record> computSumList(List<Record> list,boolean isNeedSort) {
		List<Record> sumList = new ArrayList<>();
		Map<String,List<Record>> map = new HashMap<>();//key是tableId
		String tableId = "";//以tableId进行分类求和
		for(Record record : list) {
			tableId = record.getTableId();
			List<Record> _list = map.get(tableId);
			if(_list == null) {
				_list = new ArrayList<>();
			}
			_list.add(record);
			map.put(tableId, _list);
		}
		for(Map.Entry<String, List<Record>> entry : map.entrySet()) {
			List<Record> eachClubList = entry.getValue();
			double sumOfEachClubZJ = 0d;
			double sumOfEachClubInsure = 0d;//只取其中一个， 不求和（因为已经求和了）
			int sumOfEachClubPersonCount = 0;
			for(Record record : eachClubList) {
				sumOfEachClubZJ += NumUtil.getNum(record.getScore());
				sumOfEachClubInsure = NumUtil.getNum(record.getInsurance());
				sumOfEachClubPersonCount++;
			}
			if(sumOfEachClubPersonCount == 0) {
				//这里应该清空数据并返回
				continue;
			}
			Record sumRecord = new Record();
			sumRecord.setTableId(entry.getKey());
			sumRecord.setScore(NumUtil.digit0(""+((sumOfEachClubZJ+sumOfEachClubInsure) * 0.975)));
			sumRecord.setInsurance(NumUtil.digit0(""+(sumOfEachClubInsure * 0.975 )));
			sumRecord.setPersonCount(sumOfEachClubPersonCount+"");
			//添加到最后的总和列表中
			sumList.add(sumRecord);
			
		}
		//自定义场次排序（从低到高）
		if(isNeedSort) {
			 Collections.sort(sumList, new Comparator<Record>(){
		            @Override
		            public int compare(Record r1, Record r2){
		            	String o1 = r1.getTableId();
		            	String o2 = r2.getTableId();
		                o1 = o1.replace("第","").replaceAll("局", "");
		                o2 = o2.replace("第","").replaceAll("局", "");
		                Integer index1 ;
						Integer index2 ;
						try {
							index1 = Integer.valueOf(o1);
							index2 = Integer.valueOf(o2);
						} catch (NumberFormatException e) {
							ErrorUtil.err("自定义排序失败，原因：("+o1+")或("+o2+")不是一个合法数字！");
							return 0;
						}
		                return index1.compareTo(index2);
		            }
		     });
		}
		
		return sumList;
	}
	
	
	/**
	 * 隐藏单个俱乐部信息
	 * @time 2017年11月25日
	 */
	private void showAllView() {
		bigAnchorPane.getChildren().forEach(node -> {
			//隐藏单个俱乐部信息
			String nodeId = node.getId();
			if("tableLMSum".equals(nodeId) || "tableLMDetail".equals(nodeId))
				node.setVisible(false);
		});
	}
	
	/**
	 * 删除所有动态表
	 * @time 2017年11月25日
	 */
	private void destroyAllView() {
		//删除所有动态表
		if(FLOW_PANE_ID.equals(bigAnchorPane.getChildren().get(0).getId())) {
			bigAnchorPane.getChildren().remove(0);
		}
		bigAnchorPane.getChildren().forEach(node -> {
			String nodeId = node.getId();
			if("tableLMSum".equals(nodeId) || "tableLMDetail".equals(nodeId))
				node.setVisible(true);
			if(FLOW_PANE_ID.equals(nodeId))
				node.setVisible(false);
		});
	}
	
	
    /**
     * FXML DOM节点加载完毕后的初始化
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		log.info("联盟对帐页面加载数据开始...");
		
		//绑定代理查询中的合计表
		bindCellValue(lmDetailTableId,lmDetailZJ,lmDetailInsure,lmDetailPersonCount);
		lmDetailZJ.setCellFactory(MyController.getColorCellFactory(new LMDetailInfo()));//红色注释
		lmDetailInsure.setCellFactory(MyController.getColorCellFactory(new LMDetailInfo()));
		
		//绑定代理查询中的合计表
		tableLMSum.setEditable(true);
		bindCellValue(lmSumName,lmSumZJ,lmSumInsure,lmSumPersonCount);
		lmSumInsure.setCellFactory(MyController.getColorCellFactory(new LMSumInfo()));//红色注释
		lmSumZJ.setCellFactory(TextFieldTableCell.forTableColumn());
//		shishiJine.setCellFactory(redAndEditCellFactory);
		lmSumZJ.setOnEditCommit(
				new EventHandler<CellEditEvent<LMSumInfo, String>>() {
					@Override
					public void handle(CellEditEvent<LMSumInfo, String> t) {
						String oldValue = t.getOldValue();
						String newValue = t.getNewValue();
						//修改原值
						LMSumInfo sumInfo = (LMSumInfo) t.getTableView().getItems().get(
								t.getTablePosition().getRow());
						List<String> noAllowList = Arrays.asList("结余","当天总帐");
						
						if(sumInfo == null || noAllowList.contains(sumInfo.getLmSumName())) {
							ShowUtil.show("此行不能编辑！");
							sumInfo.setLmSumZJ(oldValue);
							tableLMSum.refresh();
							return;
						}
						if(sumInfo != null && "桌费".equals(sumInfo.getLmSumName()) 
								&& !("0".equals(newValue)) && !(newValue.contains("-"))) {
							ShowUtil.show("桌费只能填写负数！");
							sumInfo.setLmSumZJ(oldValue);
							tableLMSum.refresh();
							return;
						}
						String zf_or_yiJieSuan = sumInfo.getLmSumZJ();//因为桌费和已结算在战绩那列
						try {
							if(!StringUtil.isBlank(newValue))
								Double.valueOf(newValue);
						}catch(Exception e) {
							ShowUtil.show(newValue+"是非法数据！！");
							sumInfo.setLmSumZJ(oldValue);
							tableLMSum.refresh();
							return;
						}
						//新旧两值相等，应该不操作
						
						
						//总和表赋新值
						sumInfo.setLmSumZJ(newValue);
						//缓存赋新值（桌费或已结算）
						Club club = getSelectedClub();
						int lmType = getCurrentLMType();
						if("桌费".equals(sumInfo.getLmSumName())) {
//							club.setZhuoFei(newValue);
							//将新桌费设置到不同联盟当中
							set_LM_Zhuofei(club,lmType,newValue);
							//add 2018-2-11 添加到历史联盟桌费
							String date = StringUtil.isBlank(DataConstans.Date_Str) ? "2017-01-01" : DataConstans.Date_Str;
							ClubZhuofei clubZhuofei = new ClubZhuofei(date,  club.getClubId(),  newValue,  "联盟"+lmType);
							DBUtil.saveOrUpdate_club_zhuofei(clubZhuofei);
							
						}else if("已结算".equals(sumInfo.getLmSumName())) {
							//club.setYiJieSuan(newValue);
							//将新桌费设置到不同联盟当中
							set_LM_YiJiesuan(club,lmType,newValue);
						}
						//更新结余
						updateTableLMSumOnly();
						//同步到数据库
						DBUtil.updateClub(club);
						
						//设置合计桌费（这个没多大影响）
						setNewSumOfZF();
						
					}
					
				}
		);
		
		
		
		
		//设置俱乐部的ListView监听
		initSingClubListen();
		
		//软件一打开就从从数据库中获取所有俱乐部信息
		allClubMap = DBUtil.getAllClub();
		_clubListView = clubListView;
		refreshClubList();
		
		//同步所有俱乐部信息总列表
		refresh_eachClubList();
		
		//设置合计桌费（这个没多大影响）
		setNewSumOfZF();
		log.info("联盟对帐页面加载数据完成！");
	}

	/**
	 * 修改桌费和已结算之后自动更新主表（指单个俱乐部的统计之和）
	 * @time 2017年11月25日
	 */
	private void updateTableLMSumOnly() {
		if(tableLMSum == null || tableLMSum.getItems() == null) return;
		double sum_jieyu = 0d;
		for(LMSumInfo info : tableLMSum.getItems())
			if(!"结余".equals(info.getLmSumName())) 
				sum_jieyu += NumUtil.getNum(info.getLmSumZJ());
			
		for(LMSumInfo info : tableLMSum.getItems())
			if("结余".equals(info.getLmSumName()))
				info.setLmSumZJ(NumUtil.digit0(sum_jieyu));
			
		tableLMSum.refresh();
		
	}
	
	/**
	 * 刷新所有俱乐部总记录（与数据库同步）
	 * 
	 * @time 2017年11月25日
	 * @param list 数据库返回的结果，估计最大会为30*200=6000条数据
	 * @return
	 */
	private static void refresh_eachClubList(){
		
		String maxRecordTime = DBUtil.getMaxRecordTime();//最新一天的战绩记录（也可能是昨天的，是否要做个标记）
		if(!StringUtil.isBlank(maxRecordTime)) {
			List<Record> list = DBUtil.getRecordsByMaxTime(maxRecordTime);
			//处理从数据库返回的结果为Map
			//即把List<Record>转为Map<String,List<Record>>
			Map<String,List<Record>> map = new HashMap<>();
			if(list == null) list = new ArrayList<>();
			list.forEach(record -> {
				String clubId = record.getClubId();
				List<Record>  _list = map.get(clubId);
				if(_list == null) {
					_list = new ArrayList<>();
				}
				_list.add(record);
				map.put(clubId, _list);
			});
			
			
			eachClubList.clear();
			eachClubList = map;
			
		}
	}
	
	/**
	 * 锁定当局后判断俱乐部额度与结余
	 * 导入战绩后选择不同的联盟就弹框提示哪个联盟的额度超出情况 
	 * 
	 * !!!!可能存在的问题：如果中途两次结算同一个团队，可能服务费会累加
	 * 如KK团队在导入01场后点击结算，然后又在导入02场后点击结算
	 * 解决方法：建议在Record表中新增一个字段标志是否已经计算过
	 * 
	 * @time 2017年11月30日
	 */
	public  static void checkOverEdu(String LMType){
		try {
			String maxRecordTime = DBUtil.getMaxRecordTime();//最新一天的战绩记录（也可能是昨天的，是否要做个标记）
			if(!StringUtil.isBlank(maxRecordTime)) {
				List<Record> list = DBUtil.getRecordsByMaxTime(maxRecordTime);
				
				//2018-01-01 add 大概个联盟分别报额度是否超出
				if(!CollectionUtils.isEmpty(list)) {
					list = list.stream().filter(record -> LMType.equals(record.getLmType())).collect(Collectors.toList());
				}
				
				//最新的当天所有战绩记录（包含当局记录）
				Map<String,List<Record>> map = new HashMap<>();
				if(list == null) list = new ArrayList<>();
				list.forEach(record -> {
					String clubId = record.getClubId();
					List<Record>  _list = map.get(clubId);
					if(_list == null) {
						_list = new ArrayList<>();
					}
					_list.add(record);
					map.put(clubId, _list);
				});

				
				//到这里map
				boolean isOver = false;
				//计算总值
				for(Map.Entry<String, List<Record>> entry : map.entrySet()) {
					String clubId = entry.getKey();
					if(allClubMap.get(clubId)==null) continue;
					List<Record> recordList =  entry.getValue();
					recordList = computSumList(recordList,false);//求和统计（针对每一场求和）
					
					Double sumOfZJ = 0d;
					Double sumOfBX = 0d;
					for(Record record : recordList) {
						sumOfZJ += NumUtil.getNum(record.getScore());
						sumOfBX += NumUtil.getNum(record.getInsurance());
					}

					//结余=sum（当天总账+已结算+桌费）
					Club tempClub = allClubMap.get(clubId);
					int LMTYPE = Integer.valueOf(LMType.replace("联盟", ""));
					double zhuoFei = NumUtil.getNum(get_LM_Zhuofei(tempClub,LMTYPE));
//							NumUtil.getNum(tempClub.getZhuoFei())
//							+NumUtil.getNum(tempClub.getZhuoFei2())
//							+NumUtil.getNum(tempClub.getZhuoFei3());

					double yiJiesuan = NumUtil.getNum(get_LM_YiJiesuan(tempClub,LMTYPE));
//							NumUtil.getNum(tempClub.getYiJieSuan())
//							+NumUtil.getNum(tempClub.getYiJieSuan2())
//							+NumUtil.getNum(tempClub.getYiJieSuan3());
					
					double jieyu = sumOfZJ + yiJiesuan + zhuoFei;

					double edu = NumUtil.getNum(get_LM_edu(tempClub,Integer.valueOf(LMType.replace("联盟", ""))));
					//结余和额度进行比较
					if((jieyu+edu) < 0) {
						isOver = true;
						String msg = String.format("%s:%s超过额度！额度：%s,结余%s", LMType,allClubMap.get(clubId).getName(),
								NumUtil.digit0(edu),NumUtil.digit0(jieyu));
						ShowUtil.show(msg);
						log.info(msg);
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * kendy:绑定数据域
	 * @param colums TableColumn 可变参数
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void bindCellValue(TableColumn<? extends Entity,String>... colums){
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

	/**
	 * 新增俱乐部
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public void addNewClubAction(ActionEvent event) {
		InputDialog inputDlg = new InputDialog("增加","待新增的俱乐部ID:","俱乐部名称：");
    	Optional<Pair<String, String>> result = inputDlg.getResult();
    	result.ifPresent(entry -> {
    		
    		//获取到原始的有效的团队ID及团队回水率
    		String clubId = StringUtil.nvl(entry.getKey(),"");
    		String clubName = StringUtil.nvl(entry.getValue(),"");
    		if(StringUtil.isAnyBlank(clubId,clubName)) {
    			ShowUtil.show("操作失败！原因：俱乐部ID或名称不能为空！！");
    			return;
    		}
    		if(allClubMap.get(clubId) != null) {
    			ShowUtil.show("操作失败！原因：俱乐部ID已存在！！！");
    			return;
    		}
    		//正式进入新增的编码
    		Club club = new Club(clubId,clubName,"0");
    		try {
				DBUtil.saveOrUpdateClub(club);//保存到数据库
				allClubMap.put(clubId, club);//刷新到刷新
				refreshClubList();//刷新表
				ShowUtil.show("新增成功",2);
				return;
			} catch (Exception e) {
				log.error("新增俱乐部失败");
			}
    		//新增俱乐部操作
    		ShowUtil.show("新增失败！！");
    	});
	}
	
	/**
	 * 删除俱乐部
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public void delClubAction(ActionEvent event) {
		InputDialog inputDlg = new InputDialog("删除","待删除的俱乐部ID或名称:");
    	Optional<String> result = inputDlg.getTextResult();
    	result.ifPresent(key -> {
    		key = StringUtil.nvl(key,"");
    		if(StringUtil.isBlank(key)) {
    			ShowUtil.show("操作失败！原因：输入项不能为空！！");
    			return;
    		}
    		//删除俱乐部操作
    		ShowUtil.show("模拟删除俱乐部操作...",2);
    		
    	});
	}
	
	/**
	 * 更新俱乐部（额度）
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public void updateClubAction(ActionEvent event) {
		Club club = getSelectedClub();
		if(StringUtil.isBlank(club.getClubId())) {
			ShowUtil.show("请先选择俱乐部！");
			return;
		}
		String clubName = club.getName();
		String clubId = club.getClubId();
		
		InputDialog inputDlg = new InputDialog("修改："+clubName,"联盟"+this.getCurrentLMType()+"的俱乐部新额度：");
    	Optional<String> result = inputDlg.getTextResult();
    	result.ifPresent(newClubEdu -> {
    		
    		//判断
    		if(StringUtil.isBlank(newClubEdu)) {
    			ShowUtil.show("新额度不能为空！！");
    			return;
    		}
    		newClubEdu = newClubEdu.trim();
    		//club.setEdu(newClubEdu);
    		set_LM_edu(club, this.getCurrentLMType(), newClubEdu);
    		
    		//重新刷新俱乐部列表
    		refreshClubList();
    		refreshClubListView(this.getCurrentLMType());
    		
    		//同步到数据库
    		DBUtil.updateClub(club);
    		
    		//更新俱乐部（额度）操作
    		ShowUtil.show("更新俱乐部（额度）操作成功",2);
    	});
	}
	
	/**
	 * 获取不同联盟对应的桌费（for循环中未选择俱乐部）
	 * @time 2017年12月15日
	 * @param club
	 * @param lmType
	 * @return
	 */
	public static String get_LM_Zhuofei(final Club club,int lmType) {
		String zhuoFei = "0";
		if ( 1 == lmType ) {
			zhuoFei = club.getZhuoFei();
		}else if ( 2 == lmType ) {
			zhuoFei = club.getZhuoFei2();
		}else if ( 3 == lmType ) {
			zhuoFei = club.getZhuoFei3();
		}
		return zhuoFei;
	}
	public static String get_LM_YiJiesuan(final Club club,int lmType) {
		String yiJiesuan = "0";
		if ( 1 == lmType ) {
			yiJiesuan = club.getYiJieSuan();
		}else if ( 2 == lmType ) {
			yiJiesuan = club.getYiJieSuan2();
		}else if ( 3 == lmType ) {
			yiJiesuan = club.getYiJieSuan3();
		}
		return yiJiesuan;
	}
	public static String get_LM_edu(final Club club,int lmType) {
		String edu = "0";
		if ( 1 == lmType ) {
			edu = club.getEdu();
		}else if ( 2 == lmType ) {
			edu = club.getEdu2();
		}else if ( 3 == lmType ) {
			edu = club.getEdu3();
		}
		return edu;
	}
	
	/**
	 * 获取不同联盟对应的桌费(已选择俱乐部)
	 * @time 2017年12月15日
	 * @param club
	 * @param lmType
	 * @return
	 */
	public  String get_LM_Zhuofei() {
		
		final Club club = this.getSelectedClub();
		final int lmType = this.getCurrentLMType();
		String zhuoFei = "0";
		if ( 1 == lmType ) {
			zhuoFei = club.getZhuoFei();
		}else if ( 2 == lmType ) {
			zhuoFei = club.getZhuoFei2();
		}else if ( 3 == lmType ) {
			zhuoFei = club.getZhuoFei3();
		}
		return zhuoFei;
	}
	public  String get_LM_YiJiesuan() {
		
		final Club club = this.getSelectedClub();
		final int lmType = this.getCurrentLMType();
		String yiJiesuan = "0";
		if ( 1 == lmType ) {
			yiJiesuan = club.getYiJieSuan();
		}else if ( 2 == lmType ) {
			yiJiesuan = club.getYiJieSuan2();
		}else if ( 3 == lmType ) {
			yiJiesuan = club.getYiJieSuan3();
		}
		return yiJiesuan;
	}
	
	public  String get_LM_edu() {
		final Club club = this.getSelectedClub();
		final int lmType = this.getCurrentLMType();
		String edu = "0";
		if ( 1 == lmType ) {
			edu = club.getEdu();
		}else if ( 2 == lmType ) {
			edu = club.getEdu2();
		}else if ( 3 == lmType ) {
			edu = club.getEdu3();
		}
		return edu;
	}
	/**
	 *设置不同联盟对应的桌费
	 * @time 2017年12月15日
	 * @param club
	 * @param lmType
	 * @return
	 */
	public static void set_LM_Zhuofei(final Club club,int lmType,String zf) {
		if ( 1 == lmType ) {
			club.setZhuoFei(zf);
		}else if ( 2 == lmType ) {
			club.setZhuoFei2(zf);
		}else if ( 3 == lmType ) {
			club.setZhuoFei3(zf);
		}
	}
	public static void set_LM_YiJiesuan(final Club club,int lmType,String yiJiesuan) {
		if ( 1 == lmType ) {
			club.setYiJieSuan(yiJiesuan);
		}else if ( 2 == lmType ) {
			club.setYiJieSuan2(yiJiesuan);
		}else if ( 3 == lmType ) {
			club.setYiJieSuan3(yiJiesuan);
		}
	}
	public static void set_LM_edu(final Club club,int lmType,String edu) {
		if ( 1 == lmType ) {
			club.setEdu(edu);
		}else if ( 2 == lmType ) {
			club.setEdu2(edu);
		}else if ( 3 == lmType ) {
			club.setEdu3(edu);
		}
	}
	
	
	/**
	 * 清空所有统计信息
	 * 
	 * @time 2017年11月26日
	 * @param event
	 */
	public void clearClubAndRecordAction(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n你确定要清空所有统计信息操作吗?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//从数据库中删除相应信息
			DBUtil.del_all_record();
			
			/****************前端展示的相应信息都置空****************/
			
			//导入每场战绩时的所有俱乐部记录
			currentRecordList = new ArrayList<>();
			
			//{俱乐部ID : 俱乐部信息}
			//allClubMap = new HashMap<>();
			
			//{俱乐部ID : 俱乐部每一场信息}
			eachClubList = new HashMap<>();
			
			sumOfZF.setText("0");
			currentLMLabel.setText("");
			LMTotalList.forEach(lm -> {
				lm.clear();
			});
			
			//clubListView.setItems(null);;
			
			tableLMDetail.setItems(null);
			tableLMSum.setItems(null);
			_clubListView.setItems(null);
		}
	}
	
	public void clearTables() {
		sumOfZF.setText("0");
		currentLMLabel.setText("");
		tableLMDetail.setItems(null);
		tableLMSum.setItems(null);
		_clubListView.setItems(null);
	}
	
	/**
	 * 清空桌费和已结算
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public void clear_All_Club_ZhuoFei_Action(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n你确定要清空所有俱乐部的桌费和已结算操作吗?");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			//先去更新数据库
			DBUtil.clearAllClub_ZF_YiJiSuan();
			//更新缓存
			if(allClubMap != null && allClubMap.size() > 0) {
				allClubMap.values().forEach(club -> {
					club.setZhuoFei("0");
					club.setZhuoFei2("0");
					club.setZhuoFei3("0");
					club.setYiJieSuan("0");
					club.setYiJieSuan2("0");
					club.setYiJieSuan3("0");
				});
				_clubListView.getSelectionModel().selectFirst();
			}
			ShowUtil.show("已清空桌费和已结算！",2);
		}
	}
	
	/**
	 * 修改俱乐部的股东
	 * 
	 * @time 2018年1月20日
	 * @param event
	 */
	public void change_club_gudong_Action(ActionEvent event) {
		Club club = getSelectedClub();
		String clubId = club.getClubId();
		if(StringUtil.isBlank(clubId)) {
			ShowUtil.show("请先选择俱乐部！");
			return;
		}
		//输入框
		String title = StringUtil.isBlank(club.getGudong())? "修改" : "修改（当前股东是"+club.getGudong()+"）" ;
		Optional<String> result= new InputDialog(title,"俱乐部"+club.getName()+"的新股东").getTextResult();
		if(result.isPresent()) {
			String newGudong = result.get();
			if(StringUtil.isBlank(newGudong)) {
				ShowUtil.show("股东不能为空，修改失败！",2);
			}else {
				club.setGudong(newGudong.trim().toUpperCase());
				DBUtil.updateClub(club);
				ShowUtil.show("修改成功！",2);
			}
		}
	}
	
	/**
	 * 查看所有俱乐部总帐单
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public void viewAllClubAction(ActionEvent event) {
		//无数据就返回
        int size = allClubMap.size();
        if(allClubMap == null || allClubMap.size() ==0) {
        	ShowUtil.show("无数据可以导出");
        	return;
        }
        
        Map<String, List<Record>> current_LM_Map =  LMTotalList.get(getCurrentLMType()-1);
        if(MapUtil.isNullOrEmpty(current_LM_Map)) {
        	ShowUtil.show("该联盟无数据可以导出");
        	return;
        }
        
        //组装当前要展示的联盟的相应俱乐部
        Map<String,Club> lmClubMap = getLMClub(current_LM_Map);
        
        
		//设置合计桌费（这个没多大影响）
		setNewSumOfZF();

		//隐藏单个所有信息
		showAllView();
		
		//点击所有俱乐部总帐按钮后的单个聪明数据统计 {俱乐部ID  ： 表内容}
		Map<String,List<LMSumInfo>> allClubSumMap = getAllClubSumMap(current_LM_Map);
		
		
		FlowPane flow = new FlowPane();
		flow.setId(FLOW_PANE_ID);
	    flow.setMinWidth(668);
        flow.setBorder(new Border(new BorderStroke(Color.BLUE,BorderStrokeStyle.SOLID,
             null,new BorderWidths(3))));
        flow.setVgap(20);
        flow.setHgap(20);
        flow.setPadding(new Insets(10,10,10,30));
//        int size = allClubMap.size();
//        if(allClubMap == null || allClubMap.size() ==0) {
//        	ShowUtil.show("无数据可以导出");
//        	return;
//        }
        final int talbeWidth = 300;//表示第一列的宽度
        final int With1 = 80;//表示第一列的宽度
        final int With2 = 70;//表示第二列的宽度
        final int With3 = 70;
        final int With4 = 60;
        final int height = 115;
        final String style = "-fx-alignment: CENTER;";
        final String clubNameStyle = "-fx-background-color: #FFFFE0;";
//        for(Map.Entry<String, Club> entry : allClubMap.entrySet()) {
    	for(Map.Entry<String, Club> entry : lmClubMap.entrySet()) {
        	Club club = entry.getValue();
        	String clubId = entry.getKey();
        	
        	TableView table = new TableView();
        	table.setPrefHeight(height);
        	table.setPrefWidth(talbeWidth);
        	
        	//设置列
        	TableColumn col1 = new TableColumn(club.getName());
        	col1.setPrefWidth(With1);
        	col1.setStyle(style);
        	col1.setSortable(false);
        	col1.setCellValueFactory(
	                new PropertyValueFactory<LMSumInfo, String>("lmSumName"));
        	
        	TableColumn col2 = new TableColumn("总战绩");
        	col2.setSortable(false);
        	col2.setStyle(style);
        	col2.setPrefWidth(With2);
        	col2.setCellValueFactory(
	                new PropertyValueFactory<LMSumInfo, String>("lmSumZJ"));
        	col2.setCellFactory(MyController.getColorCellFactory(new LMSumInfo()));//红色注释
        	
        	TableColumn col3 = new TableColumn("总保险");
        	col3.setSortable(false);
        	col3.setStyle(style);
        	col3.setPrefWidth(With3);
        	col3.setCellValueFactory(
	                new PropertyValueFactory<LMSumInfo, String>("lmSumInsure"));
        	col3.setCellFactory(MyController.getColorCellFactory(new LMSumInfo()));//红色注释
        	
        	TableColumn col4 = new TableColumn("总人数");
        	col4.setSortable(false);
        	col4.setStyle(style);
        	col4.setPrefWidth(With4);
        	col4.setCellValueFactory(
	                new PropertyValueFactory<LMSumInfo, String>("lmSumPersonCount"));
        	
        	table.getColumns().addAll(col1, col2,col3,col4);
        	
        	//设置数据
        	setDynamicTableData(table,clubId,allClubSumMap);
        	flow.getChildren().add(table);
        		
        };
        bigAnchorPane.getChildren().add(0, flow);
		
	}
	
	/**
	 * 获取联盟对应的俱乐部
	 * @time 2017年12月14日
	 * @param current_LM_Map
	 * @return
	 */
	private  Map<String,Club> getLMClub( Map<String, List<Record>> current_LM_Map){
		 Map<String, Club> _map = new HashMap<>();
		 current_LM_Map.forEach( (clubId,list) -> {
			 _map.put(clubId, allClubMap.get(clubId));
		 });
		 return _map;
	}
	
	private void setDynamicTableData(TableView table ,String clubId , Map<String,List<LMSumInfo>> allClubSumMap) {
		List<LMSumInfo> list = allClubSumMap.get(clubId);
		if(list != null && list.size() > 0) {
			ObservableList obList = FXCollections.observableArrayList();
			for(LMSumInfo info : list) {
				obList.add(info);
			}
			table.setItems(obList);
		}
	}

	/**
	 * 获取已选择的俱乐部信息(String)
	 */
	private String getSelectedItemString() {
		String selectedItemString =  StringUtil.nvl(clubListView.getSelectionModel().getSelectedItem(),"");
		return selectedItemString;
	}
	
	/**
	 * 获取已选择的俱乐部信息(Club Bean)
	 * 
	 * @time 2017年11月26日
	 * @return Club
	 */
	public Club getSelectedClub() {
		Club club = new Club();
		String selectedItemString =  getSelectedItemString();
		if(StringUtil.isBlank(selectedItemString)) {
			return club;
		}
		String[] newValArr = selectedItemString.trim().split("==");
    	String newClubName = newValArr[0];
    	String newClubId   = newValArr[1];
    	String newClubEdu  = newValArr[2];
    	if(!StringUtil.isBlank(newClubName))
    		club = allClubMap.get(newClubId);
		return club;
	}
	
	
	/**
	 * 刷新合计桌费
	 * @time 2017年11月26日
	 */
//	private void setNewSumOfZF() {
//		sumOfZF.setText("0");
//		if(allClubMap != null && allClubMap.size() > 0) {
//			 double sum = 0d;
//			 for(Map.Entry<String, Club> entry : allClubMap.entrySet()) 
//				sum += NumUtil.getNum(entry.getValue().getZhuoFei());
//			 
//			 sumOfZF.setText(NumUtil.digit0((-1)*sum));
//		}
//	}
	private void setNewSumOfZF() {
		try {
			sumOfZF.setText("0");
			
			if(LMTotalList.isEmpty()) {
				return;
			}
			
			int lmType = getCurrentLMType();
			
			Map<String, List<Record>> current_LM_Map =  LMTotalList.get(getCurrentLMType()-1);
			if(MapUtil.isNullOrEmpty(current_LM_Map))  return;
			
			//组装当前要展示的联盟的相应俱乐部
			Map<String,Club> lmClubMap = getLMClub(current_LM_Map);
			
			if(MapUtil.isHavaValue(lmClubMap)) {
				 double sum = 0d;
				 String zhuoFei = "";
				 for(Map.Entry<String, Club> entry : lmClubMap.entrySet()) {
					 zhuoFei = get_LM_Zhuofei(entry.getValue(), lmType);
					sum += NumUtil.getNum(zhuoFei);
				 }
				 sumOfZF.setText(NumUtil.digit0((-1)*sum));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//这里添加小胖要求：桌费为负数时修改为正数（桌费之和不能为正）
	private Double getSumOfZF() {
		
		int lmType = getCurrentLMType();
		
        Map<String, List<Record>> current_LM_Map =  LMTotalList.get(getCurrentLMType()-1);
        if(MapUtil.isNullOrEmpty(current_LM_Map))  return 0d;
        
        //组装当前要展示的联盟的相应俱乐部
        Map<String,Club> lmClubMap = getLMClub(current_LM_Map);
        
    	if(MapUtil.isHavaValue(lmClubMap)) {
			 Double sum = 0d;
			 String zhuoFei = "";
			 for(Map.Entry<String, Club> entry : lmClubMap.entrySet()) {
				 zhuoFei = get_LM_Zhuofei(entry.getValue(), lmType);
				sum += NumUtil.getNum(zhuoFei);
			 }
			 
			 return (-1)*sum;
		}else {
			return 0D;
		}
	}
	
	
	
	
	/**********************************************************************************
	 * 
	 *                                     导出Excel
	 *  
	 ***********************************************************************************/
	
	/**
	 * 导出单个俱乐部帐单
	 * 
	 * @time 2017年11月22日
	 * @param event
	 */
	public  void exportSingleClubAction(ActionEvent event) {
		
		Club club = getSelectedClub();
		if(StringUtil.isBlank(club.getClubId())) {
			ShowUtil.show("请先选择俱乐部！");
			return;
		}
		String clubName = club.getName();
		String clubId = club.getClubId();
		
		String time = DataConstans.Date_Str;
		if(StringUtil.isBlank(time)) {
//			ShowUtil.show("导出失败! 您今天还没导入01场次的战绩，无法确认时间!!");
//			return;
		}
		List<LMDetailInfo> list = new ArrayList<>();
		ObservableList<LMDetailInfo> obList = tableLMDetail.getItems();
		if(obList != null && obList.size() > 0) {
			for(LMDetailInfo info : obList) {
				list.add(info);
			}
		}else {
			ShowUtil.show(clubName+"没有需要导出的数据!!");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
  	    String title = clubName + "帐单-"+sdf.format(new Date());
  	    log.info(title);
  	  
	      String[] rowsName = new String[]{"场次","战绩","保险","人数"};
	      List<Object[]>  dataList = new ArrayList<Object[]>();
	      Object[] objs = null;
	      for(LMDetailInfo info : list) {
	          objs = new Object[rowsName.length];
	          objs[0] = info.getLmDetailTableId();
	          objs[1] = info.getLmDetailZJ();
	          objs[2] = info.getLmDetailInsure();
	          objs[3] = info.getLmDetailPersonCount();
	          dataList.add(objs);
	      }
	      
	      String[] rowsName2 = new String[]{"名称","总战绩","总保险","总人数"};
	      List<Object[]> sumList = new ArrayList<>();
	      Object[] sumObjs = null;
		  ObservableList<LMSumInfo> ob_List = tableLMSum.getItems();
			if(ob_List != null && ob_List.size() > 0) {
				for(LMSumInfo info : ob_List) {
					sumObjs= new Object[rowsName2.length];
					sumObjs[0] = StringUtil.nvl(info.getLmSumName(),"");
					sumObjs[1] = StringUtil.nvl(info.getLmSumZJ(),"");
					sumObjs[2] = StringUtil.nvl(info.getLmSumInsure(),"");
					sumObjs[3] = StringUtil.nvl(info.getLmSumPersonCount(),"");
					sumList.add(sumObjs);
				}
			}
	      
	      String out = "D:/"+title+System.currentTimeMillis();
	      ExportLMExcel ex = new ExportLMExcel(title,rowsName, dataList,out,rowsName2,sumList);
	      try {
			ex.export();
			log.info("导出单个联盟帐单完成！");
		} catch (Exception e) {
			ErrorUtil.err("导出单个联盟帐单失败", e);
		}
	}
	
	/**
	 * 一键导出俱乐部帐单
	 * 
	 * @param event
	 */
	public void exportClubOneKeyAction(ActionEvent event) {
		//循环遍历每一个有数据的团队并导出
		ObservableList<String> obList = clubListView.getItems();
		if(!CollectUtil.isHaveValue(obList)) {
			ShowUtil.show("小林提示：没有俱乐部数据！不导出Excel哦！",2);
			return;
		}else {
			//1 删除今日文件夹
			
			//2 遍历
			for(String teamId : obList) {
				clubListView.getSelectionModel().select(teamId);
				exportSingleClubAction(new ActionEvent());
				log.info("一键导出俱乐部帐单Excel: "+ teamId);
			}
			
		}
		
	}
	
	
	/**
	 * 导出所有俱乐部总帐单
	 * 由沈美女负责提供接口
	 * 
	 * @author 沈佳琳
	 * @param event
	 */
	public void exportAllClubAction(ActionEvent event) {
		
		//事先判断
		int size = allClubMap.size();
        if(allClubMap == null || allClubMap.size() ==0) {
        	ShowUtil.show("无数据可以导出");
        	return;
        }
        
        Map<String, List<Record>> current_LM_Map =  LMTotalList.get(getCurrentLMType()-1);
        if(MapUtil.isNullOrEmpty(current_LM_Map)) {
        	ShowUtil.show("该联盟无数据可以导出");
        	return;
        }
        
        //组装当前要展示的联盟的相应俱乐部
        Map<String,Club> lmClubMap = getLMClub(current_LM_Map);
        
		//获取所有俱乐部统计结果
		Map<String, List<LMSumInfo>> sumMap = getAllClubSumMap(current_LM_Map);
		//计算总桌费
		int sumOfZF = getSumOfZF().intValue();
		try {
			//调用佳琳的接口
	    	ExportAllLMExcel exportAllLMExcel = new ExportAllLMExcel();
	    	boolean hasExported = exportAllLMExcel.export(sumMap,lmClubMap,sumOfZF);
	    	if(hasExported)
	    		log.info("导出所有联盟总帐单完成！");
		}catch(Exception e) {
			ErrorUtil.err("导出Excel失败",e);
		}
		
	}
	
	
	/**
	 * 计算三个联盟的相关信息
	 * @time 2017年12月13日
	 */
	public static void compute3LM() {
		
		//数据结构：[{LMType,List<LM_Record>}]
		List<Map<String,List<Record>>> tempTotalList = new ArrayList<>();
		Map<String,List<Record>> map1 = new HashMap<>();//代理联盟1，依此类推
		Map<String,List<Record>> map2 = new HashMap<>();
		Map<String,List<Record>> map3 = new HashMap<>();
		tempTotalList.add(map1);
		tempTotalList.add(map2);
		tempTotalList.add(map3);
		
		//数据来源 eachClubList = new HashMap<>() {俱乐部ID : 俱乐部每一场信息} 
		String lmType ;
		String clubId ;
		for(Map.Entry<String, List<Record>> entry : eachClubList.entrySet()) {
			clubId = entry.getKey();
			List<Record> singleClubList = entry.getValue();
			for(Record record : singleClubList) {
				lmType = record.getLmType();
				if(LM[0].equals(lmType)) {//"联盟1"
					List<Record> _list = map1.getOrDefault(clubId, new ArrayList<>());
					_list.add(record);
					map1.put(clubId, _list);
				}
				if(LM[1].equals(lmType)) {//"联盟2"
					List<Record> _list = map2.getOrDefault(clubId, new ArrayList<>());
					_list.add(record);
					map2.put(clubId, _list);
				}
				if(LM[2].equals(lmType)) {//"联盟3"
					List<Record> _list = map3.getOrDefault(clubId, new ArrayList<>());
					_list.add(record);
					map3.put(clubId, _list);
				}
			}
		}//到此eachClubList已经被三个俱乐部瓜分完毕
		
		//赋值
		LMTotalList.clear();
		LMTotalList = tempTotalList;
	}
	
	/**
	 * 获取联盟1的所有桌费
	 * 
	 * @time 2018年1月20日
	 * @return
	 */
	public static Double getLM1TotalZhuofei(String gudong) {
		Optional<Double> totalZhuofei = 
		allClubMap.values()
			.stream()
			.filter(info -> gudong.equals(info.getGudong()))
			.map(Club::getZhuoFei)
			.map(NumUtil::getNum)
			.reduce(Double::sum);
		totalZhuofei.orElse(0d);
		if(totalZhuofei.isPresent()) {
			return  totalZhuofei.get();
		}else {
			return 0d;
		}
	}
	/**
	 * 获取联盟1的所有桌费
	 * 此方法已经由GDController.getLM1TotalZhuofei方法代替
	 * @time 2018年1月20日
	 * @return
	 */
	@Deprecated
	public static Double getLM1TotalZhuofei() {
		Optional<Double> totalZhuofei = 
				allClubMap.values()
				.stream()
				.map(Club::getZhuoFei)
				.map(NumUtil::getNum)
				.reduce(Double::sum);
		totalZhuofei.orElse(0d);
		if(totalZhuofei.isPresent()) {
			return  totalZhuofei.get();
		}else {
			return 0d;
		}
	}
	
	/**
	 * 联盟1按钮
	 * 
	 * @time 2017年12月13日
	 * @param event
	 */
    public void LM1_Btn_Action(ActionEvent event) {
    	clearTables();
    	currentLMLabel.setText(LM[0]);
    	refreshClubListView(1);
    }
    
	/**
	 * 联盟2按钮
	 * 
	 * @time 2017年12月13日
	 * @param event
	 */
    public void LM2_Btn_Action(ActionEvent event) {
    	clearTables();
    	currentLMLabel.setText(LM[1]);
    	refreshClubListView(2);
    }
    
	/**
	 * 联盟3按钮
	 * 
	 * @time 2017年12月13日
	 * @param event
	 */
    public void LM3_Btn_Action(ActionEvent event) {
    	clearTables();
    	currentLMLabel.setText(LM[2]);
    	refreshClubListView(3);
    }

    /**
     * 获取当前联盟的索引
     * 索引1表示联盟1
     * 
     * @time 2017年12月14日
     * @return
     */
    public  int getCurrentLMType() {
    	clubListView.getId();
    	String currentType =  currentLMLabel.getText();
    	int index =  StringUtil.isBlank(currentType) ? 1 : Integer.valueOf(currentType.replaceAll("联盟", ""));
    	return index;
    }
}
