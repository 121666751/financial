package com.kendy.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Player;
import com.kendy.service.MoneyService;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.sun.corba.se.impl.orbutil.closure.Constant;

import application.DataConstans;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 合并ID控制器
 * @author 林泽涛
 *
 */
public class CombineIDController implements Initializable{

	private static Logger log = Logger.getLogger(CombineIDController.class);
	
	public static TableView<CurrentMoneyInfo> tableCurrentMoneyInfo;
	
    //=====================================================================合并ID对话框	
	@FXML
	private TextField combineName; //模糊查询的名称
	
	@FXML
	private TextField parentIdField; 
	
	@FXML
	private ListView leftPlayerListView; 
	
	@FXML
	private ListView rightPlayerListView; 
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}
	
	public static void initCombineIdController(TableView<CurrentMoneyInfo> table) {
		tableCurrentMoneyInfo = table;
	}
	
	//清空按钮
	public void combineIdClearAction(ActionEvent event) {
		parentIdField.setText("");
		combineName.setText("");
		leftPlayerListView.setItems(FXCollections.observableArrayList());
		rightPlayerListView.setItems(FXCollections.observableArrayList());
	}
	
	//根据父ID解散该父子ID的合并关系
	public void CancelcombineIdAction(ActionEvent event) {
		String parentNameAndId = parentIdField.getText();
    	if(StringUtil.isBlank(parentNameAndId)) {
    		ShowUtil.show("请先选择父ID!");
    		return;
    	}else {
    		Alert alert = new Alert(AlertType.CONFIRMATION);
    		alert.setTitle(parentNameAndId);
    		alert.setHeaderText(null);
    		alert.setContentText("你确定要解散该合并ID吗?\r\n父ID："+parentNameAndId);
    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == ButtonType.OK){
    			String parentId = getIdFromStr(parentNameAndId);
    			Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(parentId);
    			if(subIdSet == null) {
    				ShowUtil.show(parentNameAndId+"无子ID,不需要解除哦");
            		return;
    			}else {
    				//删除父节点
    				DataConstans.Combine_Super_Id_Map.remove(parentId);
    				//删除子节点
    				for(String subId : subIdSet) {
    					DataConstans.Combine_Sub_Id_Map.remove(subId);
    				}
    				ShowUtil.show("已解散该父子ID的合并关系",3);
    				//刷新实时金额表
    				CurrentMoneyInfo info = MoneyService.getInfoById(parentId);
    				if(info != null) {
    					info.setCmSuperIdSum("");
    					MoneyService.refreshRecord();
    				}
    				MoneyService.flush_SSJE_table();
    				//刷新上码表
    				
    				//更新到数据库
    				DBUtil.cancelCombineId(parentId);
    			}
    			
    		}
    	}
	}
	
	
	//删除子ID
	public void delSubIdAction(ActionEvent event) {
		String selectedMemberName = (String) rightPlayerListView.getFocusModel().getFocusedItem(); 
		if(!StringUtil.isBlank(selectedMemberName)) {
			if(leftPlayerListView != null) {
				if(leftPlayerListView.getItems().size() == 0) {
					leftPlayerListView.getItems().add(selectedMemberName);
				}else {
					boolean isExist = false;
					for(Object info : leftPlayerListView.getItems()) {
						if(selectedMemberName.equals((String)info)) {
							isExist = true;
						}
					}
					if(!isExist) {
						leftPlayerListView.getItems().add(selectedMemberName);
					}
				}
			}else {
				//左边为空
				leftPlayerListView.setItems(FXCollections.observableArrayList(selectedMemberName));
			}
			//刷新左右表
			rightPlayerListView.getItems().remove(rightPlayerListView.getFocusModel().getFocusedIndex());
			rightPlayerListView.refresh();
		}
	}
	
	
	
	 /**
     * 搜索可能符合的人员
     */
	public  void handleSearch() {
    	String name = combineName.getText();
    	Set<Player> set = new HashSet<>();
    	if(!StringUtil.isBlank(name)) {
    		DataConstans.membersMap.forEach((mId,mPlayer) ->{
    			if(mPlayer.getPlayerName().contains(name)||
    			   mPlayer.getPlayerName().toLowerCase().contains(name.trim().toLowerCase())||	
    			   mPlayer.getPlayerName().toUpperCase().contains(name.trim().toUpperCase())	
    					) {
    				set.add(mPlayer);
    			}
    		});
    	}
    	ObservableList<String> list = FXCollections.observableArrayList();
    	leftPlayerListView.setItems(list);
    	if(set.size() >0) {
    		set.forEach(play -> {
    			leftPlayerListView.getItems().add(play.getPlayerName()+" "+play.getgameId());
    		});
    		
    	}
	}
    /**
     * 根据名称或玩家ID搜索可能符合的人员
     */
    public void playerQueryAction(ActionEvent event){
    	handleSearch();
    }
    
    /**
     * 根据名称或玩家ID搜索可能符合的人员
     */
    public void combineIdEnter(KeyEvent event) {
    	String keyWord = combineName.getText();
		if(KeyCode.ENTER == event.getCode() && !StringUtil.isBlank(keyWord)) {
			handleSearch();
		}
    }
    
    /**
     * 加载父ID和名称
     * @param event
     */
    public void select2parentAction(ActionEvent event){
    	String selectedMemberName = (String) leftPlayerListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择父ID!");
    		return;
    	}else {
    		if(DataConstans.Combine_Sub_Id_Map.containsKey(getIdFromStr(selectedMemberName))) {
    			ShowUtil.show(selectedMemberName+"本身是子ID!请先解除与其他父ID("+
    					DataConstans.Combine_Sub_Id_Map.get(getIdFromStr(selectedMemberName))
    					+")的关系！");
        		return;
    		}
    	}
    	parentIdField.setText(selectedMemberName);
    	//根据父ID加载所有子节点    大傻哥111 543999226
    	String superId = getIdFromStr(selectedMemberName);
    	Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(superId);
    	rightPlayerListView.setItems(null);
    	if(subIdSet == null) {
    		return;
    	}
    	ObservableList<String> obList = FXCollections.observableArrayList();
    	Player player;
    	String playerName;
    	for(String subId : subIdSet) {
    		player = DataConstans.membersMap.get(subId);
    		if(player != null) {
	    		playerName = player.getPlayerName();
	    		obList.add(playerName+" "+subId);
    		}else {
    			ShowUtil.show("匹配不到子ID信息，子ID为："+subId);
    		}
    	}
    	rightPlayerListView.setItems(obList);
    	rightPlayerListView.refresh();
    }
    
    
    /**
     * 选择子ID
     */
    public void select2ChildAction(ActionEvent event){
    	String parentStr = parentIdField.getText();
    	if(StringUtil.isBlank(parentStr)) {
    		ShowUtil.show("请先选择父ID!!!");
			return;
    	}
    	String selectedMemberName = (String) leftPlayerListView.getFocusModel().getFocusedItem(); 
    	if(!StringUtil.isBlank(selectedMemberName)) {
	    	ObservableList<String> list = rightPlayerListView.getItems();
	    	if(list == null ) {
	    		list = FXCollections.observableArrayList();
	    	}else {
	    		boolean isSubIdExist = rightPlayerListView.getItems().contains(selectedMemberName);
	    		boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(getIdFromStr(selectedMemberName));
	    		String superId = DataConstans.Combine_Sub_Id_Map.get(getIdFromStr(selectedMemberName));
	    		if(isSubIdExist) {
	    			ShowUtil.show(selectedMemberName+"该子ID已经存在，请勿重复添加");
	    			return;
	    		}else if(isSuperId){
	    			ShowUtil.show(selectedMemberName+"该记录本身是父ID,不能同时作为子ID，操作非法!");
	    			return;
	    		}else if(superId != null && !getIdFromStr(parentStr).equals(superId)) {
	    			ShowUtil.show(selectedMemberName+"本身是子ID!请先解除与其他父ID的关系！");
	        		return;
	    		}
	    	}
	    	rightPlayerListView.setItems(list);
	    	rightPlayerListView.getItems().add(selectedMemberName);
    	}
    }
    
    /**
     * 合并父子ID
     * @param event
     */
    public void combinePlayerIDAction(ActionEvent event){
    	try {
			String parentIdStr = parentIdField.getText();
			ObservableList<String> obSubIdlist = rightPlayerListView.getItems();
			if(obSubIdlist == null || obSubIdlist.size() == 0 || StringUtil.isBlank(parentIdStr)) {
				ShowUtil.show("合并失败：没有父ID或子ID,请确认!!");
				return;
			}
			String parentId = getIdFromStr(parentIdStr);
			//add 合并前先判断父子ID是否在同一个团队
			String teamId = DataConstans.membersMap.get(parentId).getTeamName();
			for(String subIdStr : obSubIdlist) {
				String subId = getIdFromStr(subIdStr);
				String _teamId = DataConstans.membersMap.get(subId).getTeamName();
				if(!teamId.equalsIgnoreCase(_teamId)) {
					ShowUtil.show("合并失败：父子ID不在同一个团队中，请确认！");
					return;
				}
			}//判断是否同个团队结束
			
			
			Set<String> subIdSets = DataConstans.Combine_Super_Id_Map.get(parentId);
			if(subIdSets == null ) {
				subIdSets = new HashSet<>();
			}else {
				//删除原有数据
				for(String subId : subIdSets) {
					DataConstans.Combine_Sub_Id_Map.remove(subId);
				}
				DataConstans.Combine_Super_Id_Map.remove(parentId);
				subIdSets.clear();
			}
			List<String> tempSubIdList = new ArrayList<>();
			for(String subIdStr : obSubIdlist) {
				String subId = getIdFromStr(subIdStr);
				subIdSets.add(subId);
				tempSubIdList.add(subId);
				DataConstans.Combine_Sub_Id_Map.put(subId, parentId);//缓存子ID
			}
			DataConstans.Combine_Super_Id_Map.put(parentId, subIdSets);//缓存父ID
			//刷新实时金额表
			MoneyService.flush_SSJE_table();
			
			//将合并ID同步到数据库
			DBUtil.saveOrUpdateCombineId(parentId);
			
			ShowUtil.show("合并ID成功！", 2);
		} catch (Exception e) {
			ShowUtil.show("合并ID失败！原因："+e.getMessage());
			e.printStackTrace();
		}
    }
    
    /**
     * 获取玩家ID
     * 备注：名称可能含有空格，名称与ID也是用空格隔开。所以单独成方法。
     * @param nameAndId 名称和ID 传过来肯定是不为空的，固不作空判断
     */
    public static String getIdFromStr(String nameAndId) {
    	String[] arr = nameAndId.split(" ");
    	return arr[arr.length-1];
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
	 * 判断一个ID是否有合并ID关系
	 * 
	 * @time 2017年11月12日
	 * @param id 玩家ID
	 * @return 父ID
	 */
	public static String hasCombineIdRelation(String id) {
		String superId = null;
		if(DataConstans.Combine_Sub_Id_Map.containsKey(id)) {
			superId = DataConstans.Combine_Sub_Id_Map.get(id);
			return superId;
		}
		if(DataConstans.Combine_Super_Id_Map.containsKey(id)) {
			superId =  id;
			return superId;
		}
		return superId;
	}
	
	/**
	 * 检测合并ID
	 * @time 2017年12月2日
	 */
	public static List<String> checkCombineId() {
		//获取所有成员ID
		Map<String,Player> memberIdMap = DataConstans.membersMap;
		
		//获取所模板ID
		Map<String,String> subIdMap = DataConstans.Combine_Sub_Id_Map;
		Map<String,Set<String>> superIdMap = DataConstans.Combine_Super_Id_Map;
		
		//待返回的结果
		List<String> resList = new ArrayList<>();
		
		//判断所有父子ID是否都有对应的成员信息
		String playerId = "";
		for(Map.Entry<String, String> entry : subIdMap.entrySet()) { //子ID
			playerId = entry.getKey(); 
			if(StringUtil.isBlank(playerId)) continue;
			if(!memberIdMap.containsKey(playerId)) {
				resList.add("检测到子ID("+playerId+")没有对应的人员信息,请检查！其父ID是"+entry.getValue());
			}
			//检测即是子ID又是父ID
			if(superIdMap.containsKey(playerId)) {
				resList.add("检测到"+playerId+"同时为父ID和子ID,将严格影响统计,请检查！");
			}
		}
		for(Map.Entry<String, Set<String>> entry : superIdMap.entrySet()) { //父ID
			playerId = entry.getKey();
			if(StringUtil.isBlank(playerId)) continue;
			if(!memberIdMap.containsKey(playerId)) {
				resList.add("检测到父ID("+playerId+")没有对应的人员信息,请检查！");
			}
		}
		
		return resList;
		
	}
    

}
