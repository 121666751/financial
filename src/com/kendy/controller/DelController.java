package com.kendy.controller;

import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

import com.kendy.db.DBUtil;
import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Player;
import com.kendy.service.MoneyService;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * 删除控制器
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:55:02
 */
public class DelController implements Initializable{

	private static Logger log = Logger.getLogger(CombineIDController.class);
	
    //=====================================================================删除人员对话框	
	@FXML
	private TextField memberName; 
	
	@FXML
	private TextField memberId; 
	
	@FXML
	private ListView delMemberListView; 
	
	@FXML
	private ListView detailListView; 
	
	
	@FXML
	private TextField newPlayerName; 
	
	@FXML
	private TextField newPlayerEdu; 
	
    @FXML private TextField newTeamId;
    @FXML private TextField newGudong;
    //=====================================================================对话框
    
    
    //=====================================================================对话框
    
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		//ListView变化时自动更新右边的信息
		delMemberListView.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener() {
		            @Override
		            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
		            	//清空详细信息框
		            	ObservableList<String> obList = detailListView.getItems();
		            	if(obList != null) { 
		            		obList.clear();
		            	}else {
		            		obList = FXCollections.observableArrayList();
		            	}
		            	if(StringUtil.isBlank((String)newValue)) {
		            		return;
		            	}
		            	/****获取玩家的详细信息，包括个人详情和实时金额以及父子ID ***/
		            	String playerId = getIdFromStr((String)newValue);
		            	Player player = DataConstans.membersMap.get(playerId);
		            	if(player == null) {return;}
		            	obList.add("玩家名称："+player.getPlayerName());
		            	obList.add("玩家ID："+player.getgameId());
		            	obList.add("所属团队："+player.getTeamName());
		            	obList.add("所属股东："+player.getGudong());
		            	obList.add("额度："+(StringUtil.isBlank(player.getEdu()) ? "无" : player.getEdu()));
		            	obList.add("实时金额："+MoneyService.get_SSJE_byId(playerId));
		            	obList.add("是否父ID："+ (DataConstans.Combine_Super_Id_Map.containsKey(playerId) ? "是" : "否"));
		            	obList.add("是否子ID："+ (DataConstans.Combine_Sub_Id_Map.containsKey(playerId) ? "是" : "否"));
		            }
				});
	}
	

	
	public void queryDelMember() {
    	String name = memberName.getText();
    	String id = memberId.getText();
		Set<Player> set = new HashSet<>();
    	if(!StringUtil.isBlank(name)) {
    		DataConstans.membersMap.forEach((mId,mPlayer) ->{
    			if(mPlayer.getPlayerName().contains(name)) {
    				set.add(mPlayer);
    			}
    		});
    	}
    	if(!StringUtil.isBlank(id)) {
    		DataConstans.membersMap.forEach((mId,mPlayer) ->{
    			if(mId.contains(id)) {
    				set.add(mPlayer);
    			}
    		});
    	}
    	ObservableList<String> list = FXCollections.observableArrayList();
    	delMemberListView.setItems(list);
    	if(set.size() >0) {
    		set.forEach(play -> {
    			delMemberListView.getItems().add(play.getPlayerName()+" "+play.getgameId());
    		});
    		
    	}
	}
    
    /**
     * 根据名称或玩家ID搜索可能符合的人员
     */
    public void queryDelMemberAction(ActionEvent event){
    	queryDelMember();
    }
    
    public void queryDelMemberByEnterEvent(KeyEvent event) {
		if(KeyCode.ENTER == event.getCode()) {
			queryDelMember();
		}
	}
    
    /**
     * 删除人员
     * 
     * @time 2017年11月12日
     * @param event
     */
    public void delMemberAction(ActionEvent event){
    	String selectedMemberName = (String) delMemberListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择要删除的人员！！");
    		return;
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("警告");
		alert.setHeaderText(null);
		alert.setContentText(selectedMemberName+" 你确定要删除该人员吗?\r\n若是父节点会连同所有子节点都删除!");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			if(!StringUtil.isBlank(selectedMemberName)){
				String playerId = getIdFromStr(selectedMemberName);//selectedMemberName.split(" ")[1];
				delMemberListView.getItems().remove(selectedMemberName);
				//判断移除子节点还是父节点
				boolean isSubId = DataConstans.Combine_Sub_Id_Map.containsKey(playerId);
				boolean isSuperId = DataConstans.Combine_Super_Id_Map.containsKey(playerId);
				//情况一：不是子ID也不是父ID
				if(!isSubId && !isSuperId) {
					DataConstans.membersMap.remove(playerId);//从人员表中移动
					MoneyService.del_SSJE_byId(playerId);//更新实时金额表
					//ConsUtil.refreshTeamIdAndPlayerId();//获取最新的团队ID与玩家ID列表的映射
					DBUtil.delMember(playerId);//从数据库中删除
				}
				//情况二：是子ID
				if(isSubId) {
					DataConstans.membersMap.remove(playerId);//从人员表中移动
					MoneyService.del_SSJE_byId(playerId);//更新实时金额表
					//ConsUtil.refreshTeamIdAndPlayerId();//获取最新的团队ID与玩家ID列表的映射
					
					//删除相关合并ID关系
					//更新父节点所拥有的子节点
					String superId = DataConstans.Combine_Sub_Id_Map.get(playerId);
					Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(superId);
					subIdSet.remove(playerId);
					//删除子节点
					DataConstans.Combine_Sub_Id_Map.remove(playerId);
					DBUtil.delMember(playerId);//从数据库中删除
					DBUtil.saveOrUpdateCombineId(superId);//删除合并ID(实际上是更新合并ID)
				}
				//情况三：是父ID
				if(isSuperId) {
					//删除相关合并ID关系
					Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(playerId);
					if(subIdSet != null && subIdSet.size() > 0) {
						//循环删除所有子节点
						for(String subId : subIdSet) {
							DataConstans.Combine_Sub_Id_Map.remove(subId);//删除子节点
							MoneyService.del_SSJE_byId(subId);//更新实时金额表
							DataConstans.membersMap.remove(subId);//从人员表中移动
							DBUtil.delMember(subId);//从数据库中删除
						}
						//最后再删除父节点
						DataConstans.membersMap.remove(playerId);//从人员表中移动
						MoneyService.del_SSJE_byId(playerId);//更新实时金额表
						DataConstans.Combine_Super_Id_Map.remove(playerId);
						DBUtil.delMember(playerId);//从数据库中删除
						DBUtil.cancelCombineId(playerId);//删除合并ID
						//ConsUtil.refreshTeamIdAndPlayerId();//获取最新的团队ID与玩家ID列表的映射,跟删除人员的顺序不可调换
					}
				}
				ShowUtil.show("删除成功", 2);
			}
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
     * 新增修改用户名称功能
     * 
     * @time 2017年10月29日
     * @param event
     */
    public void updatePlayerNamerAction(ActionEvent event){
    	String newName = newPlayerName.getText();
    	newName = StringUtil.isBlank(newName) ? "" : newName.trim();
    	if(StringUtil.isBlank(newName)) {
    		ShowUtil.show("请先输入玩家的新名称！！");
    		return;
    	}
    	
    	String selectedMemberName = (String) delMemberListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择要修改名称的玩家！！");
    		return;
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("警告");
		alert.setHeaderText(null);
		alert.setContentText(selectedMemberName+" 哥，你确定要修改该玩家名称为"+newName+"??");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK){
			try {
				String playerId = getIdFromStr(selectedMemberName);
				
				//1 理新常量表中的member
				Player player = DataConstans.membersMap.get(playerId);
				if(player != null) {
					player.setPlayerName(newName);
				}else {
					ShowUtil.show("用户不存在！");
					return;
				}
				
				//2 更新实时金额表
				String oldName = selectedMemberName.replace(playerId, "");
				oldName = oldName.trim();
				MoneyService.updatePlayerName(playerId,oldName,newName);
				
				//3 更新数据库
				DBUtil.updateMember(player);
				
				//4 更新其他（特补充）
				MoneyService.flush_SSJE_table();
				
				//5 提示成功
				ShowUtil.show("修改成功",2);
			} catch (Exception e) {
				ShowUtil.show("修改失败，原因："+e.getMessage());
				e.printStackTrace();
			}
		}
    }
    /**
     * 修改用户额度功能
     * 
     * @time 2017年10月29日
     * @param event
     */
    public void updateNewPlayerEduAction(ActionEvent event){
    	String newEdu = newPlayerEdu.getText();
    	newEdu = StringUtil.isBlank(newEdu) ? "" : newEdu.trim();
    	if(StringUtil.isBlank(newEdu)) {
    		ShowUtil.show("请先输入玩家的新额度！！");
    		return;
    	}
    	
    	String selectedMemberName = (String) delMemberListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择要修改名称的玩家！！");
    		return;
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("警告");
    	alert.setHeaderText(null);
    	alert.setContentText(selectedMemberName+" 哥，你确定要修改该玩家额度为"+newEdu+"??");
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		try {
    			String playerId = getIdFromStr(selectedMemberName);
    			
    			//1 理新常量表中的member
    			Player player = DataConstans.membersMap.get(playerId);
    			if(player != null) {
    				player.setEdu(newEdu);
    			}else {
    				ShowUtil.show("用户不存在！");
    				return;
    			}
    			
    			//2 更新实时金额表
    			CurrentMoneyInfo info = MoneyService.getInfoById(playerId);
    			if(info != null) {
    				info.setCmiEdu(newEdu);
    				MoneyService.saveOrUpdate(info);
    				MoneyService.refreshRecord();
    				MoneyService.flush_SSJE_table();
    			}
    			
    			//3 更新数据库
    			DBUtil.updateMember(player);
    			
    			//4 更新其他（特补充）
    			//修改实时上码额度
    			
    			//5 提示成功
    			ShowUtil.show("修改成功",2);
    		} catch (Exception e) {
    			ShowUtil.show("修改失败，原因："+e.getMessage());
    			e.printStackTrace();
    		}
    	}
    }
    /**
     * 修改用户团队ID
     * 
     * @time 2017年10月29日
     * @param event
     */
    public void updateNewTeamIdAction(ActionEvent event){
    	String newTeam = newTeamId.getText();
    	newTeam = StringUtil.isBlank(newTeam) ? "" : newTeam.trim();
    	if(StringUtil.isBlank(newTeam)) {
    		ShowUtil.show("请先输入玩家的新团队ID！！");
    		return;
    	}
    	
    	String selectedMemberName = (String) delMemberListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择要修改名称的玩家！！");
    		return;
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("警告");
    	alert.setHeaderText(null);
    	alert.setContentText(selectedMemberName+" 哥，你确定要修改该玩家团队为为"+newTeam+"??");
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		try {
    			//1,判断是否 已经合并了父子ID关系
    			//若是，则提示先解除,结束程序
    			String playerId = getIdFromStr(selectedMemberName);
    			String combineParentId = CombineIDController.hasCombineIdRelation(playerId);
    			if(!StringUtil.isBlank(combineParentId)) {
    				ShowUtil.show("修改失败！请先解除合并ID关系，其父ID是："+combineParentId);
    				return;
    			}
    			
    			//2，修改缓存中的人员信息
    			Player player = DataConstans.membersMap.get(playerId);
    			player.setTeamName(newTeam);
    			
    			//3，同步到数据库
    			DBUtil.updateMember(player);
    			
    			//4，同步实时上码中的数据(实时金额不用)
    			
    			//5 add 2018-3-4 修改贡献值中的teamID
    			DBUtil.updateRecordTeamId(playerId, newTeam);
    			
    			ShowUtil.show("修改成功",2);
    			
    		} catch (Exception e) {
    			ShowUtil.show("修改失败，原因："+e.getMessage());
    			e.printStackTrace();
    		}
    	}
    }
    
    
    public void updateNewGudongAction(ActionEvent event) {
    	String newGD = newGudong.getText();
    	newGD = StringUtil.isBlank(newGD) ? "" : newGD.trim();
    	if(StringUtil.isBlank(newGD)) {
    		ShowUtil.show("请先输入玩家的新股东！！");
    		return;
    	}
    	
    	String selectedMemberName = (String) delMemberListView.getFocusModel().getFocusedItem(); 
    	if(StringUtil.isBlank(selectedMemberName)) {
    		ShowUtil.show("请先选择要修改股东的玩家！！");
    		return;
    	}
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("警告");
    	alert.setHeaderText(null);
    	alert.setContentText(selectedMemberName+" 哥，你确定要修改该玩家的股东为"+newGD+"??");
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		try {
    			//1,获取玩家ID
    			String playerId = getIdFromStr(selectedMemberName);
    			
    			//2，修改缓存中的人员信息
    			Player player = DataConstans.membersMap.get(playerId);
    			player.setGudong(newGD);
    			
    			//3，同步到数据库
    			DBUtil.updateMember(player);
    			
    			//4，同步实时上码中的数据(实时金额不用)
    			
    			//5 修改其他所设及的玩家或数据缓存，如父子ID TODO
    			
    			
    			ShowUtil.show("修改成功",2);
    			
    		} catch (Exception e) {
    			ShowUtil.show("修改玩家股东失败，原因："+e.getMessage());
    			e.printStackTrace();
    		}
    	}
    }
	

}
