package com.kendy.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.kendy.entity.CurrentMoneyInfo;
import com.kendy.entity.Player;
import com.kendy.entity.ShangmaDetailInfo;
import com.kendy.entity.UserInfos;

import application.DataConstans;

/**
 * 常用工具类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:58:04
 */
public class ConsUtil {

	/**
	 * 获取LinkedHashMap的最后一个元素
	 * @param map
	 * @return
	 */
	public static <K, V> Entry<K, V> getTail(LinkedHashMap<K, V> map) {
	    Iterator<Entry<K, V>> iterator = map.entrySet().iterator();
	    Entry<K, V> tail = null;
	    while (iterator.hasNext()) {
	        tail = iterator.next();
	    }
	    return tail;
	}

	public static Entry<String, List<UserInfos>> getTail(Map<String, List<UserInfos>> zjMap) {
		 Iterator<Entry<String, List<UserInfos>>> iterator = zjMap.entrySet().iterator();
		    Entry<String, List<UserInfos>> tail = null;
		    while (iterator.hasNext()) {
		        tail = iterator.next();
		    }
		    return tail;
	}
	

	
	//同步SM_Detail_Map
	
	//获取最新的SM_Detail_Map(上码表的个人详情）{玩家ID=List<ShangmaDetailInfo>}
	public static void refresh_SM_Detail_Map() {
		if(DataConstans.membersMap == null) 
			return;
		
		DataConstans.membersMap.forEach((playerId,player) -> {
			if(!StringUtil.isBlank(playerId) && 
					DataConstans.SM_Detail_Map.get(playerId) == null) {
				DataConstans.SM_Detail_Map.put(playerId, new ArrayList<ShangmaDetailInfo>());
			}
		});	
	}
	
	//在实时金额那里临时添加
	public static void refresh_SM_Detail_Map(CurrentMoneyInfo info) {
		String playerId = info.getWanjiaId();
		if(!StringUtil.isBlank(playerId)) {
			List<ShangmaDetailInfo> list = DataConstans.SM_Detail_Map.get(playerId);
			if(list == null) {
				list = new ArrayList<>();
				DataConstans.SM_Detail_Map.put(playerId,list);
			}
			
		}
	}
	
	/**
	 * 根据名称找玩家ID
	 * @param name
	 */
	@SuppressWarnings("unused")
	public static String getPlayerIdByName(String name) {
		final Map<String,Player> memberMap = DataConstans.membersMap;
		if(memberMap != null && memberMap.size()>0) {
			String playerName = "";
			for(Map.Entry<String, Player> entry : memberMap.entrySet()) {
				Player wanjia = entry.getValue();
				playerName = wanjia.getPlayerName();
				if(!StringUtil.isBlank(playerName) && playerName.equals(name)) {
					return entry.getKey();//playerId
				}
			}
		}
		return "";
	}
	public static Player getPlayerByName(String name) {
		String pId = getPlayerIdByName(name);
		if(!StringUtil.isBlank(pId)) {
			return DataConstans.membersMap.get(pId);
		}
		return null;
	}
	
	//锁定当局备份上码表的个人详情
	public static void lock_SM_Detail_Map() {
		//复制当前上码的人个详情表,缓存到锁定中
		//开启线程异步执行
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String,List<ShangmaDetailInfo>> map = new HashMap<>();
				List<ShangmaDetailInfo> list = null;
				List<ShangmaDetailInfo> srcList = null;
				ShangmaDetailInfo info = null;
				for(Map.Entry<String, List<ShangmaDetailInfo>> entry : DataConstans.SM_Detail_Map.entrySet()) {
					srcList = entry.getValue();
					if(srcList == null || srcList.size() == 0) {
						list = new ArrayList<ShangmaDetailInfo>();
						map.put(entry.getKey(), list);
					}else {
						list = new ArrayList<ShangmaDetailInfo>();
						for(ShangmaDetailInfo detail : srcList) {
							info = new ShangmaDetailInfo();
							info.setShangmaDetailName(detail.getShangmaDetailName());
							info.setShangmaJu(detail.getShangmaJu());
							info.setShangmaPlayerId(detail.getShangmaPlayerId());
							info.setShangmaShishou(detail.getShangmaShishou());
							info.setShangmaSM(detail.getShangmaSM());
							info.setShangmaPreSM(detail.getShangmaPreSM());
							info.setShangmaHasPayed(detail.getShangmaHasPayed());
							list.add(info);
						}
						map.put(entry.getKey(), list);
					}
				}
				
				DataConstans.SM_Detail_Map_Locked = null;
				DataConstans.SM_Detail_Map_Locked = map;
			}
		}).start();
	}
	
	//撤销时当局恢复上码表的个人详情
	public static void recovery_SM_Detail_Map() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Map<String,List<ShangmaDetailInfo>> map = new HashMap<>();
				List<ShangmaDetailInfo> list = null;
				List<ShangmaDetailInfo> srcList = null;
				ShangmaDetailInfo info = null;
				for(Map.Entry<String, List<ShangmaDetailInfo>> entry : DataConstans.SM_Detail_Map_Locked.entrySet()) {
					srcList = entry.getValue();
					if(srcList == null || srcList.size() == 0) {
						list = new ArrayList<ShangmaDetailInfo>();
						map.put(entry.getKey(), list);
					}else {
						list = new ArrayList<ShangmaDetailInfo>();
						for(ShangmaDetailInfo detail : srcList) {
							info = new ShangmaDetailInfo();
							info.setShangmaDetailName(detail.getShangmaDetailName());
							info.setShangmaJu(detail.getShangmaJu());
							info.setShangmaPlayerId(detail.getShangmaPlayerId());
							info.setShangmaShishou(detail.getShangmaShishou());
							info.setShangmaSM(detail.getShangmaSM());
							info.setShangmaPreSM(detail.getShangmaPreSM());
							info.setShangmaHasPayed(detail.getShangmaHasPayed());
							list.add(info);
						}
						map.put(entry.getKey(), list);
					}
				}
				
				DataConstans.SM_Detail_Map = null;
				DataConstans.SM_Detail_Map = map;
			}
		}).start();
	}
	
	
	
}
