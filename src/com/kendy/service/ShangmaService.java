package com.kendy.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.ClubBankInfo;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.ShangmaDetailInfo;
import com.kendy.entity.ShangmaInfo;
import com.kendy.entity.ShangmaNextday;
import com.kendy.entity.WanjiaInfo;
import com.kendy.excel.ExportShangmaExcel;
import com.kendy.util.CollectUtil;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import application.Main;
import application.MyController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

/**
 * 实时上码系统服务类
 * 
 * @author 林泽涛
 * @time 2017年10月28日 下午5:22:28
 */
public class ShangmaService {
	
	private static Logger log = Logger.getLogger(ShangmaService.class);
	
	public static TableView<ShangmaInfo> tableSM;
	public static TableView<ShangmaDetailInfo> tableSMD;
	public static TableView<ShangmaDetailInfo> tableND;//tableNextDay
	public static TableView<WanjiaInfo> tablePJ;
	public static Map<String,CurrentMoneyInfo> cmiMap;//{玩家ID={}}
	public static Label labelZSM;
	public static Label labelZZJ;
	public static VBox shangmaVBox;
	public static Label shangmaTeamIdLabel;
	
	public static Label teamShangmaAvailable;
	public static TextField teamYajin;
	public static TextField teamEdu;
	
	//玩家ID=上码次日列表   ShangmaDetailInfo 存到数据库时对应ShangmaNextday
	public static Map<String,List<ShangmaDetailInfo>> SM_NextDay_Map= new HashMap<>();
	
	/**
	 * 初始化上码相关配置
	 */
	public static void initShangma(VBox shangmaVBox0, final TableView<ShangmaInfo> tableShangma, 
			final Label shangmaTeamIdLabel0, TableView<ShangmaDetailInfo> tableShangmaDetail,
			Label shangmaZSM,Label shangmaZZJ,TableView<WanjiaInfo> tablePaiju,
			TableView<ShangmaDetailInfo> tableShangmaNextDay, Label teamShangmaAvailable0, 
			TextField teamYajin0, TextField teamEdu0) {
		shangmaVBox = shangmaVBox0;
		tableSM = tableShangma;
		tableSMD = tableShangmaDetail;
		labelZSM = shangmaZSM;
		labelZZJ = shangmaZZJ;
		tablePJ = tablePaiju;
		shangmaTeamIdLabel = shangmaTeamIdLabel0;
		tableND = tableShangmaNextDay;
		teamShangmaAvailable = teamShangmaAvailable0;
		teamYajin = teamYajin0;
		teamEdu = teamEdu0;

		//重新初始化所有团队ID按钮
		initShangmaButton();
		
		//加载数据库中玩家的次日信息
		init_SM_NextDay_Map();
	}
	
	/**
	 * 重置团队押金与团队额度，包括团可上码
	 */
	private static void resetTeamYajinAndEdu() {
		//置零
		teamShangmaAvailable.setText("0");
		teamYajin.setText("0");
		teamEdu.setText("0");
		//赋新值
		//获取团队信息
		String teamId = shangmaTeamIdLabel.getText();
		Huishui hs = DataConstans.huishuiMap.get(teamId);
		if(hs != null) {
			String _teamYajin = hs.getTeamYajin();
			String _teamEdu = hs.getTeamEdu();
			teamYajin.setText(_teamYajin);
			teamEdu.setText(_teamEdu);
			//计算团队可上码
			//计算公式：  团队可上码= 押金 + 额度 + 团队战绩 - 团队已上码
			Double teamSMAvailable = NumUtil.getNum(NumUtil.getSum(_teamYajin, _teamEdu, labelZZJ.getText()))
					- NumUtil.getNum(labelZSM.getText());
			
			teamShangmaAvailable.setText(teamSMAvailable.intValue()+"");
			
		}
	}
	
	
	/**
	 * 加载数据库中玩家的次日信息
	 * @time 2018年2月5日
	 */
	public static void init_SM_NextDay_Map() {
		List<ShangmaNextday> allSM_nextday = DBUtil.getAllSM_nextday();
		if(!allSM_nextday.isEmpty()) {
			SM_NextDay_Map = allSM_nextday.stream().map(nextday -> {
		    	String playerId = nextday.getPlayerId();
		    	String playerName = nextday.getPlayerName();
		    	String changci = nextday.getChangci();
		    	String shangma = nextday.getShangma();
		    	ShangmaDetailInfo shangmaDetailInfo = new ShangmaDetailInfo(playerId,playerName,changci,shangma);
		    	return shangmaDetailInfo;
			}).collect(Collectors.groupingBy(ShangmaDetailInfo::getShangmaPlayerId));
		}
	}
	
	/**
	 * 重新初始化所有团队ID按钮
	 * @time 2017年10月23日
	 */
	public static void initShangmaButton() {
		
		if(DataConstans.huishuiMap == null )
			return;
		
		//先删除所有按钮
		shangmaVBox.getChildren().clear();
		
		Set<String> teamIdSet = DataConstans.huishuiMap.keySet();
		if(teamIdSet != null && teamIdSet.size() > 0) {
			List<String> list = new ArrayList<>();
			teamIdSet.forEach(teamId -> {
				list.add(teamId);
			});
			Collections.sort(list);
			for(String teamId : list) {
				final Button btn = new Button(teamId);
				btn.setPrefWidth(100);
				btn.setOnAction((ActionEvent e) -> { 
					shangmaTeamIdLabel.setText(teamId);
					//加载数据
					tableSM.setItems(null);
					loadShangmaTable(teamId,tableSM);
				});
				
				shangmaVBox.getChildren().add(btn);
			}
		}
	}
	
	/**
	 * 加载上码主表
	 * 
	 * @param teamId
	 * @param tableShangma
	 */
	public static void loadShangmaTable(String teamId,TableView<ShangmaInfo> tableShangma) {
		double teamSumYiSM, teamSumZJ;
		try {
			ObservableList<ShangmaInfo> obList = FXCollections.observableArrayList();
			List<String> wanjiaIdList = DataConstans.teamWanjiaIdMap.get(teamId);
			String playerName,edu,yicunJifen,sumAvailableEdu,sumYiSM,sumZJ;
			teamSumYiSM = 0d;
			teamSumZJ = 0d;
			if(wanjiaIdList != null) {
				if(cmiMap == null)
					refresh_cmiMap_if_null();//加载cmiMap估计
				ShangmaInfo smInfo;
				for(String playerId : wanjiaIdList) {
					//根据玩家ID找名称和额度和已存积分
					CurrentMoneyInfo cmiInfo = cmiMap.get(playerId);
					if(cmiInfo == null) {
						Player player = DataConstans.membersMap.get(playerId);
						if(player == null) {
							int a = 0;
						}
						playerName = player.getPlayerName();
						edu = player.getEdu();
						yicunJifen = "";//最关键的区别
						
					}else {
						playerName = cmiInfo.getMingzi();
						edu = cmiInfo.getCmiEdu();
						yicunJifen = cmiInfo.getShishiJine();//实时金额就是已存积分
					}
					//根据玩家ID找个人详情
					Double[] sumArr = getSumDetail(playerId,edu,yicunJifen);
					sumAvailableEdu = MoneyService.digit0(sumArr[0]);
					sumYiSM = MoneyService.digit0(sumArr[1]);
					sumZJ = MoneyService.digit0(sumArr[2]);
					//组装实体
					smInfo = new ShangmaInfo(playerName,edu,sumAvailableEdu,sumYiSM,sumZJ,playerId,yicunJifen,"");
					obList.add(smInfo);
					//设置团队总和
					teamSumYiSM += sumArr[1];
					teamSumZJ +=sumArr[2];
				}
			}
			//刷新表
			tableShangma.setItems(obList);
			tableShangma.refresh();
			
			//重新加载合并ID进去
			render_Shangma_info_talbe_0();
			
			//设置团队总和
			labelZSM.setText(MoneyService.digit0(teamSumYiSM));
			labelZZJ.setText(MoneyService.digit0(teamSumZJ));
			
			//add 2018-2-19 设置团队押金与团队额度
			resetTeamYajinAndEdu();
			
			
		} catch (Exception e1) {
			ErrorUtil.err("加载上码主表",e1);
		}
	}
	
	
	
	/**
	 * 添加合并ID
	 * @param srcList
	 * @throws Exception
	 */

	@SuppressWarnings("unchecked")
	public static void render_Shangma_info_talbe_0() throws Exception {
		LinkedList<ShangmaInfo> srcList = new LinkedList<>();
		if(tableSM == null || tableSM.getItems() == null || tableSM.getItems().size() == 0) {
			return;
		}else {
			for(ShangmaInfo info : tableSM.getItems())
				srcList.add(info);
		}
		
		//组装数据为空
		//对组装的数据列表进行处理，空行直接删除，父ID删除，子ID删除后缓存
		ListIterator<ShangmaInfo> it = srcList.listIterator();
		Map<String,ShangmaInfo> superIdInfoMap = new HashMap<>();//存放父ID那条记录的信息
    	Map<String,List<ShangmaInfo>> superIdSubListMap = new HashMap<>();//存放父ID下的所有子记录信息
    	String playerId = "";
    	while(it.hasNext()) {  
    		ShangmaInfo item = it.next();  
    	    //删除空行
    	    if(item == null || StringUtil.isBlank(item.getShangmaPlayerId())) {
    	    	it.remove();//没有玩家ID的就是空行
    	    	continue;
    	    }
    	    //删除父ID
    	    playerId  = item.getShangmaPlayerId();
    	    if(DataConstans.Combine_Super_Id_Map.get(playerId) != null) {
    	    	superIdInfoMap.put(playerId, item);
    	    	it.remove();
    	    	continue;
    	    }
    	    //删除子ID
    	    if(DataConstans.Combine_Sub_Id_Map.get(playerId) != null) {
    	    	//是子ID节点
    	    	String parentID = DataConstans.Combine_Sub_Id_Map.get(playerId);
    	    	List<ShangmaInfo> childList = superIdSubListMap.get(parentID);
    	    	if(childList == null) {
    	    		childList = new ArrayList<>();
    	    	}
    	    	childList.add(item);
    	    	superIdSubListMap.put(parentID, childList);
    	    	it.remove();
    	    	continue;
    	    }
    	} 
    	
    	//添加子ID不在表中的父ID记录（单条父记录）
    	superIdInfoMap.forEach((superId,info) -> {
    		if(!superIdSubListMap.containsKey(superId)) {
    			srcList.add(info);//添加父记录
    		}
    	});
    	
    	//添加父ID不在表中的子ID记录
        Iterator<Entry<String, List<ShangmaInfo>>> ite = superIdSubListMap.entrySet().iterator();  
        while(ite.hasNext()){  
            Entry<String, List<ShangmaInfo>> entry = ite.next();  
            String superId = entry.getKey();
    		ShangmaInfo superInfo = superIdInfoMap.get(superId);
    		if(superInfo == null) {
    			for(ShangmaInfo info : entry.getValue()) {
    				srcList.add(info);//添加子记录
    			}
    			ite.remove();//删除
    		}
        } 
    	
    	//计算总和并填充父子节点和空行
    	String superId = "";
    	srcList.add(new ShangmaInfo());//空开一行
    	for(Map.Entry<String, List<ShangmaInfo>> entry : superIdSubListMap.entrySet()) {
    		superId = entry.getKey();
    		ShangmaInfo superInfo = superIdInfoMap.get(superId);
    		if(superInfo == null) {
//    			throw new Exception("实时上码系统：找不到父ID对应的信息！原因，名单无此ID值："+superId);//原因，可能该玩家没有ID值
    			throw new Exception("该父ID与子ID不在同一个团队内："+superId);//原因，可能该玩家没有ID值
    		}
    		//计算父节点总和
    		List<ShangmaInfo> subInfoList = entry.getValue();
    		Double lianheEdu = 0d;//合并ID的联合额度
    		Double sumOfYicunJifen = 0d;//合并ID的已存积分
    		Double sumOfYiSM = 0d;//合并ID的总已上码
    		Double sumOfZZJ = 0d;//合并ID的总战绩
    		Double sumOfZZJ_hasPayed = 0d;//合并ID的总战绩(忆剔除支付后的战绩）
    		for(ShangmaInfo info : subInfoList) {
    			sumOfYicunJifen += NumUtil.getNum(info.getShangmaYCJF());
    			sumOfYiSM += NumUtil.getNum(info.getShangmaYiSM());
    			sumOfZZJ += NumUtil.getNum(info.getShangmaSumOfZJ());
    			//add
    			Double[] subIdSingleSum = getSumDetail(info.getShangmaPlayerId(),info.getShangmaEdu(),info.getShangmaYCJF());
    			sumOfZZJ_hasPayed += NumUtil.getNum(NumUtil.digit0(subIdSingleSum[3]));
    		}
    		sumOfYicunJifen += NumUtil.getNum(superInfo.getShangmaYCJF());
			sumOfYiSM += NumUtil.getNum(superInfo.getShangmaYiSM());
			sumOfZZJ += NumUtil.getNum(superInfo.getShangmaSumOfZJ());
			//
			Double[] subIdSingleSum = getSumDetail(superInfo.getShangmaPlayerId(),superInfo.getShangmaEdu(),superInfo.getShangmaYCJF());
			sumOfZZJ_hasPayed += NumUtil.getNum(NumUtil.digit0(subIdSingleSum[3]));
			
			//计算父节点的联合ID
			//lianheEdu = NumUtil.getNum(superInfo.getShangmaEdu()) + sumOfYicunJifen + sumOfZZJ - sumOfYiSM;
			lianheEdu = NumUtil.getNum(superInfo.getShangmaEdu()) + sumOfYicunJifen + sumOfZZJ_hasPayed - sumOfYiSM;
			
    		superInfo.setShangmaLianheEdu(NumUtil.digit0(lianheEdu));
    		//添加空行和子节点
//    		srcList.add(new CurrentMoneyInfo());
    		srcList.add(superInfo);//先添加父节点
    		for(ShangmaInfo info : subInfoList) {//再添加子节点
    			srcList.add(info);
    		}
    		srcList.add(new ShangmaInfo());//空开一行
    	}
    	//更新
    	ObservableList<ShangmaInfo> obList = FXCollections.observableArrayList();
    	for(ShangmaInfo cmi : srcList) {
    		obList.add(cmi);
    	}
    	tableSM.setItems(obList);
    	tableSM.refresh();
	}
	
	/**
	 * 获取个人详情总和
	 * 
	 * @time 2017年10月28日
	 * @param playerId
	 * @param edu
	 * @param yicunJifen
	 * @return
	 */
	public static Double[] getSumDetail(String playerId,String edu,String yicunJifen) {
		Double[] sumDetail = {0d,0d,0d,0d};
//		List<ShangmaDetailInfo> detailList = DataConstans.SM_Detail_Map.get(playerId);
		//add 2018-2-5 新增次日
		List<ShangmaDetailInfo> detailList = new ArrayList<>();
		List<ShangmaDetailInfo> _detailList = DataConstans.SM_Detail_Map.get(playerId);
		detailList.addAll(_detailList);
		detailList.addAll(SM_NextDay_Map.getOrDefault(playerId,new ArrayList<>()));
		
		if(detailList != null  ) {
			Double sumAvailableEdu = 0d,sumYiSM = 0d,sumZJ = 0d,sumZJ_hasPayed=0d;
			for(ShangmaDetailInfo info : detailList) {
				if("否".equals(info.getShangmaHasPayed())){//如果该战绩导入后按了支付按钮，则不计算(即未支付)
					sumZJ_hasPayed += MoneyService.getNum(info.getShangmaShishou());
				}
				sumZJ += MoneyService.getNum(info.getShangmaShishou());
				sumYiSM += MoneyService.getNum(info.getShangmaSM());
			}
			sumAvailableEdu = getSumAvailableEdu(edu,sumZJ_hasPayed+"",sumYiSM+"",playerId,yicunJifen);
			sumDetail[0] = sumAvailableEdu;
			sumDetail[1] = sumYiSM;
			sumDetail[2] = sumZJ;
			sumDetail[3] = sumZJ_hasPayed;
		}
		return sumDetail;
	}
	
	//如果该战绩导入后按了支付按钮，则不计算
	/**
	 * 可上码额度=额度-已上码+实收+已存积分。
	 * 但是如果那一场结束，已存积分已经包含实收这个数值	
	 * 
	 */
	public static Double getSumAvailableEdu(String edu,String sumZJ,String sumYiSM,String playerId,String yicunJifen) {
		Double sumAvailableEdu = 0d;
//		boolean hasPayed = isHasPayedByPlayerId(playerId);
//		sumZJ = hasPayed ? "" : sumZJ;
		sumAvailableEdu = 
				MoneyService.getNum(edu) + 
				MoneyService.getNum(sumZJ) + 
				MoneyService.getNum(yicunJifen) -
				MoneyService.getNum(sumYiSM);
		return sumAvailableEdu;
	}
	
	/**
	 * 点击支付时更改SM_Detail_Map中的支付状态
	 * 它会影响到可上码额度的计算
	 * 
	 * @time 2017年10月28日 add
	 * @param playerId
	 * @param paiju
	 */
	public static void update_SM_Detail_Map_byPlayerIdAndPaiju(String playerId,String paiju) {
		List<ShangmaDetailInfo> detailList = DataConstans.SM_Detail_Map.get(playerId);
		if(detailList != null  ) {
			for(ShangmaDetailInfo info : detailList) {
				String dangjuIndex = info.getShangmaJu();
				if(!StringUtil.isBlank(dangjuIndex) && dangjuIndex.equals(paiju)) {
					info.setShangmaHasPayed("是");
					return;
				}
			}
		}
	}
	
	/**
	 * 如果该战绩导入后按了支付按钮，则不计算
	 * 该方法逻辑不对，已将正确逻辑移至getSumDetail方法中由ShangmaHasPayed去判断
	 */
	@Deprecated
	public static boolean isHasPayedByPlayerId(String playerId) {
		String hasPayedStr = "";
		ObservableList<WanjiaInfo> obList = tablePJ.getItems();
		if(obList != null && obList.size() > 0) {
			for(WanjiaInfo info : obList) {
				if(info.getWanjiaId() != null && info.getWanjiaId().equals(playerId) && "1".equals(info.getHasPayed())) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	public static void scrollByPlayerId(String playerId , TableView<ShangmaInfo> tableShangma) {
		if(!StringUtil.isBlank(playerId)) {
			ObservableList<ShangmaInfo> list = tableShangma.getItems();
			ShangmaInfo shangmaInfo = null;//待转到第一个的行数据
			boolean isExist = false;//检查该玩家是否存在
			String pId = "";
			for(ShangmaInfo info : list) {
				pId = info.getShangmaPlayerId();
				if(!StringUtil.isBlank(pId)){
					if(playerId.equals(pId)) {
						int index = list.indexOf(info);
						tableShangma.scrollTo(index);//
//						table.getSelectionModel().focus(index);
						tableShangma.getSelectionModel().select(index);
						//加载右边的个人详情表
						loadSMDetailTable(playerId);
						
						isExist = true;
						break;
					}
				}
			};
			if(!isExist) {
				ShowUtil.show("查无结果！", 1);
			}else {
				//ShowUtil.show("OK,请下拉", 1);
			}
		}
	}
	
	/**
	 * 上码搜索
	 * @param keyWord
	 * @param shangmaTeamId
	 */
	public static void shangmaSearch(String keyWord,Label shangmaTeamId) {
		//1根据模糊人名匹配到人员信息获取玩家ID
//		Player player = getPlayerIdByName(keyWord);
		Player player = getNextSelectedPlayer(keyWord);
		if(player == null) {
			ShowUtil.show("查无数据", 1);
			return ;
		}
		//2从人员信息中获取团队ID
		String playerId = player.getgameId();
		String teamId = player.getTeamName();
		//3加载数据
		shangmaTeamId.setText(teamId);
		ShangmaService.loadShangmaTable(teamId, tableSM);
		//4匹配人名（ID）后置为选中样式
		ShangmaService.scrollByPlayerId(playerId, tableSM);
		//根据个人ID加载个人信息
		ShangmaService.loadSMDetailTable(playerId);
	}
	
	
	/**
	 * 根据玩家名称获取对应的玩家ID
	 * @param searchText
	 * @return
	 */
	public static Player getNextSelectedPlayer(String searchText) {
		TableView<ShangmaInfo> table = tableSM;
		if(!StringUtil.isBlank(searchText)) {
			if(table == null || table.getItems() == null) {
				return null;
			}
			Player p;
			String pId = "";
			ShangmaInfo selectedInfo = table.getSelectionModel().getSelectedItem();
			if(selectedInfo != null) {
				pId = selectedInfo.getShangmaPlayerId();
			}
			List<String> idList =  new LinkedList<>();
			
			final Map<String,Player> tempMap = new LinkedHashMap<>();
			DataConstans.membersMap.forEach((playerId,player) -> {
				tempMap.put(playerId, player);
			}); 
			
			for(Map.Entry<String, Player> entry : tempMap.entrySet()) {
				p = entry.getValue();
				if(!StringUtil.isBlank(p.getPlayerName()) && 
						(p.getPlayerName().contains(searchText)
						||p.getPlayerName().toLowerCase().contains(searchText.toLowerCase())
						||p.getPlayerName().toUpperCase().contains(searchText.toUpperCase())
						
						||p.getgameId().contains(searchText)
								)
						){
					idList.add(entry.getKey());
				}
			}
			
			int size = idList.size();
			//返回排序序号
			if(size == 0) {
				return null;
			}else if(size == 1) {
				return tempMap.get(idList.get(0));
			}else  {
				if(idList.contains(pId)) {
					int i = idList.indexOf(pId);
					if(i ==  (size-1)) {
						//返回第一个
						return tempMap.get(idList.get(0));
					}else {
						//返回下一个
						return tempMap.get(idList.get(i+1));
					}
				}else {
					//返回第一个
					return tempMap.get(idList.get(0));
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据玩家名称获取对应的玩家ID
	 * 替代方法是getNextSelectedPlayer()
	 * @param searchText
	 * @return
	 */
	@Deprecated
	public static Player getPlayerIdByName(String searchText) {
		if(DataConstans.membersMap != null) {
			String pId = "";
			Player p;
			for(Map.Entry<String, Player> entry : DataConstans.membersMap.entrySet()) {
				p = entry.getValue();
				if(!StringUtil.isBlank(p.getPlayerName()) && 
						(p.getPlayerName().contains(searchText)
						||p.getPlayerName().toLowerCase().contains(searchText.toLowerCase())
						||p.getPlayerName().toUpperCase().contains(searchText.toUpperCase())
								)
						
						){
					return p;
				}
			}
		}
		return null;
	}
	
	/**
	 * 根据玩家ID加载个人上码详情表
	 * @param playerId
	 */
	public static void loadSMDetailTable(String playerId) {
		Map<String,List<ShangmaDetailInfo>> detailMap = DataConstans.SM_Detail_Map;
		final ObservableList<ShangmaDetailInfo> obList = FXCollections.observableArrayList();
		List<ShangmaDetailInfo> list =detailMap.get(playerId);
		if(list == null ) {
			list = new ArrayList<>();
			String playerName = DataConstans.membersMap.get(playerId).getPlayerName();
			if(StringUtil.isBlank(playerName)) {
				ShowUtil.show("ID:"+playerId+"找不到对应的玩家名称", 1);
			}
			detailMap.put(playerId, list);
		}else {
			list.forEach( detail -> {
				obList.add(detail);
			});
		}
		tableSMD.setItems(obList);
		tableSMD.refresh();
	}
	/**
	 * 根据玩家ID加载个人上码次日信息表
	 * @param playerId
	 */
	public static void loadSMNextDayTable(String playerId) {
		Map<String,List<ShangmaDetailInfo>> detailMap = SM_NextDay_Map;
		final ObservableList<ShangmaDetailInfo> obList = FXCollections.observableArrayList();
		List<ShangmaDetailInfo> list =detailMap.get(playerId);
		if(list == null ) {
			list = new ArrayList<>();
			String playerName = DataConstans.membersMap.get(playerId).getPlayerName();
			if(StringUtil.isBlank(playerName)) {
				ShowUtil.show("ID:"+playerId+"找不到对应的玩家名称", 1);
			}
			detailMap.put(playerId, list);
		}else {
			list.forEach( detail -> {
				obList.add(detail);
			});
		}
		tableND.setItems(obList);
		tableND.refresh();
	}
	
	/**
	 * 根据玩家ID保存个人详情表
	 * @param playerId
	 */
	public static void saveSMDetail(String playerId) {
		ObservableList<ShangmaDetailInfo> obList = tableSMD.getItems();
		Map<String,List<ShangmaDetailInfo>> detailMap = DataConstans.SM_Detail_Map;
		List<ShangmaDetailInfo> list =new ArrayList<>();
		obList.forEach(detail -> {
			list.add(detail);
		});
		detailMap.put(playerId, list);
		
	}
	
	
	/**
	 * 右下表：名称鼠标双击事件：打开对话框增加上码值
	 * 
	 * @time 2018年2月9日
	 * @param detail
	 */
	public static void openAddNextdayShangSMDiag(ShangmaDetailInfo detail) {
		if(detail != null && detail.getShangmaDetailName() != null) {
			String oddSM = StringUtil.isBlank(detail.getShangmaSM()) ? "0" : detail.getShangmaSM();
			String newSM = "";
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("添加");
			dialog.setHeaderText(null);
			dialog.setContentText("续增上码值(Enter):");
			
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
				detail.setShangmaSM(MoneyService.digit0(MoneyService.getNum(oddSM)+MoneyService.getNum(result.get())));
			}
			
			String playerId= detail.getShangmaPlayerId();
			String changci = detail.getShangmaJu();
			
			// 1保存到数据库
		    ShangmaNextday nextday = new ShangmaNextday();
		    nextday.setPlayerId(playerId);
		    nextday.setPlayerName(detail.getShangmaDetailName());
		    nextday.setChangci(detail.getShangmaJu());
		    nextday.setShangma(detail.getShangmaSM());
			DBUtil.saveOrUpdate_SM_nextday(nextday);
			
	    	// 2保存到缓存
	    	List<ShangmaDetailInfo> currentNextdayList = SM_NextDay_Map.getOrDefault(playerId, new ArrayList<>());
	    	ShangmaDetailInfo shangmaDetailInfo = currentNextdayList.stream()
	    			.filter(info->changci.equals(info.getShangmaJu())).findFirst().get();
	    	shangmaDetailInfo.setShangmaSM(detail.getShangmaSM());
	    	
	    	// 3刷新到当前的玩家次日表
	    	tableND.refresh();
	    	
	    	//4 修改主表的可上码额度 TODO
//	    	refreshTableSM();
			
			//刷新左表对应记录
			try {
				updateRowByPlayerId(playerId,result.get());
			} catch (Exception e) {
				ErrorUtil.err("刷新左表对应记录失败",e);
			}
		}
	}
	
	
	//右表：名称鼠标双击事件：打开对话框增加上码值
	public static void openAddShangSMDiag(ShangmaDetailInfo detail) {
		if(detail != null && detail.getShangmaDetailName() != null) {
			String oddSM = StringUtil.isBlank(detail.getShangmaSM()) ? "0" : detail.getShangmaSM();
			String newSM = "";
			TextInputDialog dialog = new TextInputDialog();
//			dialog.setGraphic(null);
			dialog.setTitle("添加");
			dialog.setHeaderText(null);
			dialog.setContentText("续增上码值(Enter):");

			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
			    detail.setShangmaSM(MoneyService.digit0(MoneyService.getNum(oddSM)+MoneyService.getNum(result.get())));
			}
			tableSMD.refresh();
			String playerId= detail.getShangmaPlayerId();
		    //save
			saveSMDetail(playerId);
			//刷新左表对应记录
			try {
				updateRowByPlayerId(playerId,result.get());
			} catch (Exception e) {
			}
		}
	}
	//左表：名称鼠标双击事件：打开对话框增加第X局上码值
	public static void openNewShangSMDiag(ShangmaInfo smInfo) {
		if(smInfo != null && smInfo.getShangmaName() != null) {
			Dialog<Pair<String, String>> dialog = new Dialog<>();
//			dialog.setTitle("添加新上码记录");
			dialog.setTitle(smInfo.getShangmaName());
			dialog.setHeaderText(null);
			// Set the button types.
			ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
			// Create the username and password labels and fields.
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 20,20, 20));

			TextField shangmaJu = new TextField();
			TextField shangmaVal = new TextField();

			grid.add(new Label("第X局:"), 0, 0);
			grid.add(shangmaJu, 1, 0);
			grid.add(new Label("上码:"), 0, 1);
			grid.add(shangmaVal, 1, 1);

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
			    log.info("shangmaJu=" + shangmaJuAndVal.getKey() + ", shangmaVal=" + shangmaJuAndVal.getValue());
			    try { 
			    	Integer.valueOf(shangmaJuAndVal.getKey());
			    	Integer.valueOf(shangmaJuAndVal.getValue());
				} catch (NumberFormatException e) {  
					ShowUtil.show("非法数值："+shangmaJuAndVal.getKey()+"或"+shangmaJuAndVal.getValue()+"!");
					return ;
				}
			    addNewShangma2DetailTable(smInfo,shangmaJuAndVal.getKey(),shangmaJuAndVal.getValue());
			});
		}
	}
	
	/**
	 * 新增个人上码详情记录到详情表
	 * @param smInfo
	 * @param shangmaJu 第几局
	 * @param shangmaVal 上码值
	 */
	public static void addNewShangma2DetailTable(ShangmaInfo smInfo,String shangmaJu,String shangmaVal) {
		//判断是否重复
		String playerId = smInfo.getShangmaPlayerId();
		String palyerName = smInfo.getShangmaName();
		if(checkIfDuplicate(playerId,shangmaJu)) {
			ShowUtil.show("请勿重复添加第"+shangmaJu+"场次!!,该场次已存在!");
			return;
		}
		//根据ID加载个人详细数据
		ShangmaService.loadSMDetailTable(playerId);
		//添加表记录
		shangmaJu = getShangmaPaiju(shangmaJu);
		if(tableSMD.getItems() == null) {
			ObservableList<ShangmaDetailInfo> obList = FXCollections.observableArrayList();
			obList.add(new ShangmaDetailInfo(palyerName,shangmaVal,"",playerId,shangmaJu,"","否"));
			tableSMD.setItems(obList);
		}else {
			tableSMD.getItems().add(new ShangmaDetailInfo(palyerName,shangmaVal,"",playerId,shangmaJu,"","否"));
		}
		tableSMD.refresh();
		//save
		saveSMDetail(playerId);
		//刷新左表对应记录
		updateRowByPlayerId(playerId,shangmaVal);
	}
	
	//输入1，获取第01局
	private static String getShangmaPaiju(String shangmaJu) {
		String paiju = "";
		if(!StringUtil.isBlank(shangmaJu)) {
			shangmaJu = shangmaJu.trim();
			Integer ju = Integer.valueOf(shangmaJu);
			if(ju>0 && ju <10) {
				paiju = "0"+(ju+"");
			}else if(ju >= 10){
				paiju = ju+"";
			}
		}
		return "第"+paiju+"局";
	}
	
	//判断加入的新牌局是否跟之前的有重复
	private static boolean checkIfDuplicate(String playerId,String ju) {
		boolean ifDuplicate=  false;
		List<ShangmaDetailInfo> list = DataConstans.SM_Detail_Map.get(playerId);
		if(list != null && list.size() > 0) {
			for(ShangmaDetailInfo info : list) {
				if(info.getShangmaJu() != null && info.getShangmaJu().equals(getShangmaPaiju(ju))) {
					return true;
				}
			}
		}
		return ifDuplicate;
	}
	
	/**
	 * 修改上码表后更新行
	 * @param playerId
	 * @param addedYiShangmaVal
	 */
	public static void updateRowByPlayerId(String playerId,String addedYiShangmaVal) {
		if(StringUtil.isBlank(playerId)) {
			ShowUtil.show("修改上码表后更新行失败，原因：程序错误，玩家ID没有检测到。");
			return;
		}
		if(!StringUtil.isBlank(addedYiShangmaVal)) {
			double addedYSMVal = MoneyService.getNum(addedYiShangmaVal);
			ObservableList<ShangmaInfo> obList = tableSM.getItems();
			for(ShangmaInfo info : obList) {
				if(playerId.equals(info.getShangmaPlayerId())) {
					String old_YSM_val = info.getShangmaYiSM();
					String old_available_edu_val = info.getShangmaAvailableEdu();
					info.setShangmaYiSM(MoneyService.digit0(MoneyService.getNum(old_YSM_val)+addedYSMVal));
					info.setShangmaAvailableEdu(MoneyService.digit0(MoneyService.getNum(old_available_edu_val)-addedYSMVal));
					labelZSM.setText(MoneyService.digit0(MoneyService.getNum(labelZSM.getText())+addedYSMVal));
					tableSM.refresh();
					break;
				}
			}
			try {
				//重新加载合并ID进去
				render_Shangma_info_talbe_0();
			} catch (Exception e) {
				ShowUtil.show("处理合并ID失败，原因："+e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 导入战绩时更新上码系统的个人信息
	 */
	public static void updateShangDetailMap(TableView<WanjiaInfo> table) {
//		final List<TeamHuishuiInfo> list = DataConstans.Dangju_Team_Huishui_List;
		ObservableList<WanjiaInfo> obList = table.getItems();
		Map<String,List<ShangmaDetailInfo>> detailMap = DataConstans.SM_Detail_Map;
		List<ShangmaDetailInfo> detailList= null;
		String playerId="",preYSM,yiSM,tableId,ju;
		if(obList != null && obList.size() > 0) {
			//遍历当局
			for(WanjiaInfo info : obList) {
				playerId = info.getWanjiaId();
				detailList = detailMap.get(playerId);
				if(detailList != null) {
					tableId = info.getPaiju();//getRealTableId(info.getPaiju());//不是第X局了，而是X
					
					//遍历个人信息
					for(ShangmaDetailInfo sdi : detailList) {
						ju = sdi.getShangmaJu();
						if(!StringUtil.isBlank(ju) && ju.equals(tableId)) {
							//设置原先上码值，以及清空当局上码
							preYSM = sdi.getShangmaSM();
							sdi.setShangmaPreSM(preYSM);
							sdi.setShangmaSM("");
							//设置实收
							sdi.setShangmaShishou(info.getZhangji());
							//add2017-09-24 加载主表
							loadShangmaTable(DataConstans.membersMap.get(playerId).getTeamName(),tableSM);
//							ShangmaInfo smif = tableSM.getSelectionModel().getSelectedItem();
//							if(smif != null && !StringUtil.isBlank(smif.getShangmaPlayerId()) 
//									&& smif.getShangmaPlayerId().equals(tableSMD.getSelectionModel().getSelectedItem().getShangmaPlayerId())) {
//								loadShangmaTable(DataConstans.membersMap.get(playerId).getTeamName(),tableSM);
//								scrollByPlayerId(playerId, tableSM);
//							}
							break;
						}
					}
					detailMap.put(playerId, detailList);
				}
			}
		}
	}
	
	public static String getRealTableId(String tableIdStr) {
		String tableId = "";
		if(!StringUtil.isBlank(tableIdStr)) {
			tableId = tableIdStr.trim().replace("第", "").replaceAll("局", "").trim();
			Integer intTableId = Integer.valueOf(tableId);
			tableId = intTableId.toString();
		}
		return tableId;
	}
	

	/**
	 * 重新加载cmiMap如果为空
	 * @time 2017年12月4日
	 */
	public  static void refresh_cmiMap_if_null() {
		MyController mc = Main.myController;
		if(mc == null) {
			ErrorUtil.err("获取MyCtroller为空");
			return;
		}
		//获取最新的实时金额Map {玩家ID={}}
		Map<String,CurrentMoneyInfo> lastCMIMap = new HashMap<>();;
		ObservableList<CurrentMoneyInfo> obList = mc.tableCurrentMoneyInfo.getItems();
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
	}
	
	
	/**********************************************************************************
	 * 
	 *                                     导出Excel
	 *  
	 ***********************************************************************************/
    public static void exportShangmaExcel() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	// 标题
		String title = shangmaTeamIdLabel.getText() + "团队实时上码-"+sdf.format(new Date());
    	// 列名
    	String[] rowName = new String[]{"联合额度","玩家ID","玩家名称","可上码额度","额度","已在积分","已上码","战绩总结算"};
    	// 输出
    	String out = "D:/"+title+System.currentTimeMillis();
    	// 数据
    	ObservableList<ShangmaInfo> obList = tableSM.getItems();
    	if(CollectUtil.isNullOrEmpty(obList)) {
    		ShowUtil.show("没有需要导出的数据！");
    		return;
    	}
    	List<Object[]>  dataList = new ArrayList<Object[]>();
	    Object[] objs = null;
	    String clubId = "";
	    for(ShangmaInfo info : obList) {
	        objs = new Object[rowName.length];
	        objs[0] = info.getShangmaLianheEdu();
	        objs[1] = info.getShangmaPlayerId();
	        objs[2] = info.getShangmaName();
	        objs[3] = info.getShangmaAvailableEdu();
	        objs[4] = info.getShangmaEdu();
	        objs[5] = info.getShangmaYCJF();
	        objs[6] = info.getShangmaYiSM();
	        objs[7] = info.getShangmaSumOfZJ();
	        dataList.add(objs);
	    }
    	ExportShangmaExcel excel = new ExportShangmaExcel(title, rowName,  dataList, out);
    	try {
    		excel.export();
			log.info("导出团队实时上码完成！");
		} catch (Exception e) {
			ErrorUtil.err("导出团队实时上码失败", e);
		}
    	
    }
    
    /**
     * 获取选中的俱乐部银行卡记录
     * @time 2017年12月19日
     * @return
     */
    private static ShangmaInfo getSelectShangma() {
    	if(tableSM.getItems() != null )
    			return tableSM.getSelectionModel().getSelectedItem();
    	return null;
    }
    
    /**
     * 实时上码开始新的一天由用户自行点击加载次日的数据
     * 
     * @time 2018年2月4日
     */
    public static void loadNextDayDataAction() { 
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("提示");
		alert.setHeaderText(null);
		alert.setContentText("\r\n只有开始新一天的统计才可以加载次日数据哦");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			Map<String,List<ShangmaDetailInfo>> detailMap = DataConstans.SM_Detail_Map;
			boolean isHasValue = detailMap.values().stream().anyMatch(list->list.size()>0);
			if(isHasValue) {
				ShowUtil.show("中途不能加载次日数据！！！");
				return;
			}else {
				int playerCount = SM_NextDay_Map.size();
				//将次日数据的值复制给DataConstans.SM_Detail_Map
				SM_NextDay_Map.forEach((playerId,nextdayList) -> {
					List<ShangmaDetailInfo> detailList = new ArrayList<>();
					for(ShangmaDetailInfo nextday : nextdayList) {
						detailList.add(copyShangmaDetailInfo(nextday));
					}
					detailMap.put(playerId, detailList);
				});
				
				//将数据表中的删除
				DBUtil.setNextDayLoaded();
				
				//清空SM_NextDay_Map
				SM_NextDay_Map.clear();
				
				//清空当前的次日信息
				tableND.setItems(null);
				
				//重新加载主表 TODO
		    	refreshTableSM();
		    	
		    	//提示加载成功
		    	ShowUtil.show("加载次日数据成功！加载了"+playerCount+"个玩家数据", 4);
			}
		}
    }
    
    /**
     * 复制
     * @time 2018年2月5日
     * @param source
     * @return
     */
    private static ShangmaDetailInfo copyShangmaDetailInfo(ShangmaDetailInfo source) {
    	ShangmaDetailInfo target = new ShangmaDetailInfo();
    	target.setShangmaDetailName(source.getShangmaDetailName());
    	target.setShangmaSM(source.getShangmaSM());
    	target.setShangmaPlayerId(source.getShangmaPlayerId());
    	target.setShangmaHasPayed(source.getShangmaHasPayed());
    	target.setShangmaPreSM(source.getShangmaPreSM());
    	target.setShangmaJu(source.getShangmaJu());
    	return target;
    }

    /**
     * 实时上码新增次日上码
     * 
     * @time 2018年2月4日
     */
    public static void addNextDaySMDetailAction() {
    	ShangmaInfo smInfo = getSelectShangma();
    	if(smInfo == null){
    		ShowUtil.show("请先选择要增加次日的玩家记录！");
    		return;
    	}
    	if(smInfo != null && smInfo.getShangmaName() != null) {
			Dialog<Pair<String, String>> dialog = new Dialog<>();
			dialog.setTitle("次日上码："+smInfo.getShangmaName());
			dialog.setHeaderText(null);
			ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
			dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);
			GridPane grid = new GridPane();
			grid.setHgap(10);
			grid.setVgap(10);
			grid.setPadding(new Insets(20, 20,20, 20));

			TextField shangmaJu = new TextField();
			TextField shangmaVal = new TextField();

			grid.add(new Label("第X局:"), 0, 0);
			grid.add(shangmaJu, 1, 0);
			grid.add(new Label("上码:"), 0, 1);
			grid.add(shangmaVal, 1, 1);

			Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
			loginButton.setDisable(true);

			shangmaJu.textProperty().addListener((observable, oldValue, newValue) -> {
			    loginButton.setDisable(newValue.trim().isEmpty());
			});

			dialog.getDialogPane().setContent(grid);

			Platform.runLater(() -> shangmaJu.requestFocus());

			dialog.setResultConverter(dialogButton -> {
			    if (dialogButton == loginButtonType) {
			        return new Pair<>(shangmaJu.getText(), shangmaVal.getText());
			    }
			    return null;
			});

			Optional<Pair<String, String>> result = dialog.showAndWait();

			result.ifPresent(shangmaJuAndVal -> {
			    log.info("新增次日上码：shangmaJu=" + shangmaJuAndVal.getKey() + ", shangmaVal=" + shangmaJuAndVal.getValue());
			    try { 
			    	Integer.valueOf(shangmaJuAndVal.getKey());
			    	Integer.valueOf(shangmaJuAndVal.getValue());
				} catch (NumberFormatException e) {  
					ShowUtil.show("非法数值："+shangmaJuAndVal.getKey()+"或"+shangmaJuAndVal.getValue()+"!");
					return ;
				}
			    
			    ShangmaNextday nextday = new ShangmaNextday();
			    nextday.setPlayerId(smInfo.getShangmaPlayerId());
			    nextday.setPlayerName(smInfo.getShangmaName());
			    nextday.setChangci(getShangmaPaiju(shangmaJuAndVal.getKey()));
			    nextday.setShangma(shangmaJuAndVal.getValue());
			    
			    //新增玩家的次日数据
			    addNewRecord_nextday(tableND,nextday);
			    
			});
		}
    }
    
    /**
     * 新增玩家的次日数据
     * 
     * @time 2018年2月5日
     * @param table
     * @param nextday
     */
    private static void addNewRecord_nextday(TableView<ShangmaDetailInfo> table, ShangmaNextday nextday) {
    	String playerId = nextday.getPlayerId();
    	String playerName = nextday.getPlayerName();
    	String changci = nextday.getChangci();
    	String shangma = nextday.getShangma();
    	ShangmaDetailInfo shangmaDetailInfo = new ShangmaDetailInfo(playerId,playerName,changci,shangma);
    	//先判断是否重复
		if(checkIfDuplicateInNextday(playerId,changci)) {
			ShowUtil.show("请勿重复添加"+changci+"!,该场次已存在!");
			return;
		}
    	
    	//1 保存到数据库
    	DBUtil.saveOrUpdate_SM_nextday(nextday);
    	
    	//2 保存到缓存
    	List<ShangmaDetailInfo> currentNextdayList = SM_NextDay_Map.getOrDefault(playerId, new ArrayList<>());
    	currentNextdayList.add(shangmaDetailInfo);
    	SM_NextDay_Map.put(playerId, currentNextdayList);
    	
    	//3 刷新到当前的玩家次日表
    	tableND.getItems().add(shangmaDetailInfo);
    	tableND.refresh();
    	
    	//4 修改主表的可上码额度 TODO
    	refreshTableSM();
    	
    }
    
    /**
     * 判断次日是否重复添加
     * 
     * @time 2018年2月13日
     * @param playerId
     * @param changci
     * @return
     */
    private static boolean checkIfDuplicateInNextday(String playerId, String changci) {
    	if(StringUtil.isBlank(playerId)) {
    		return false;
    	}else {
    		return tableND.getItems().stream()
    		    	.anyMatch(info->playerId.equals(info.getShangmaPlayerId()) && changci.equals(info.getShangmaJu()));
    	}
    }
    
    
    /**
     * 加载数据
     * @time 2018年2月6日
     */
    private static void refreshTableSM() {
//    	String teamId = shangmaTeamIdLabel.getText();
//    	//tableSM.setItems(null);
//		loadShangmaTable(teamId,tableSM);
		
		
		String shangmaTeamIdValue = shangmaTeamIdLabel.getText();
		if(!StringUtil.isBlank(shangmaTeamIdValue)) {
			//ShangmaService.loadShangmaTable(shangmaTeamIdValue,tableShangma);
		}else {
			if(DataConstans.huishuiMap.containsKey("公司")){
				shangmaTeamIdValue = "公司";
			}else {
				shangmaTeamIdValue = ((Button)shangmaVBox.getChildren().get(0)).getText();
			}
		}
		ShangmaService.loadShangmaTable(shangmaTeamIdValue,tableSM);
    }
    
    /**
     * 保存实时上码中的团队押金与团队额度修改
     */
    public static void updateTeamYajinAndEdu() {
    	String teamId = shangmaTeamIdLabel.getText();
    	if(StringUtil.isNotBlank(teamId)) {
    		boolean updateOK = DBUtil.updateTeamYajinAndEdu(teamId, teamYajin.getText(), teamEdu.getText());
    		if(updateOK) {
    			//更新缓存
    			Huishui team = DataConstans.huishuiMap.get(teamId);
    			if(team != null ) {
    				team.setTeamYajin(teamYajin.getText());
    				team.setTeamEdu(teamEdu.getText());
    			}
    			ShowUtil.show("保存成功！", 2);
    		}else {
    			ShowUtil.show("保存失败！");
    		}
    	}else {
    		ShowUtil.show("保存失败,当前团队ID为空！");
    	}
    }
    
    
    
	
}
