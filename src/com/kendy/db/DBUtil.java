package com.kendy.db;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.Club;
import com.kendy.entity.ClubBankModel;
import com.kendy.entity.ClubZhuofei;
import com.kendy.entity.HistoryRecord;
import com.kendy.entity.Huishui;
import com.kendy.entity.JifenInfo;
import com.kendy.entity.Player;
import com.kendy.entity.Record;
import com.kendy.entity.ShangmaNextday;
import com.kendy.util.ErrorUtil;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;
import com.kendy.util.TimeUtil;

import application.DataConstans;



/**
 * 数据库操作类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:54:17
 */
public class DBUtil {
	private static Logger log = Logger.getLogger(DBUtil.class);
	
	private static Connection con = null;
	private static PreparedStatement ps = null;
	private static String sql;
	
	/**
	 * 积分查询
	 */
	public static List<JifenInfo> getJifenQuery(String jifenValue,String teamId,String startTime,String endTime, String limit){
		List<JifenInfo> list = new LinkedList<>();
		try {
			con = DBConnection.getConnection();
			String sql = 
					"SELECT " + 
					"	(@i :=@i + 1) AS jfRankNo, " + 
					"	hh.* " + 
					"   FROM " + 
					"	( " + 
					"		SELECT DISTINCT " + 
					"			playerName, " + 
					"			floor( " + 
					"				( " + 
					"					sum(shouHuishui) - sum(chuHuishui) " + 
					"				) / ? " + 
					"			) AS jifenValue " + 
					"		FROM " + 
					"			( " + 
					"				SELECT " + 
					"					* " + 
					"				FROM " + 
					"					historyrecord " + 
					"				WHERE " + 
					"					teamId = ? " + 
					"				AND updateTime >= ? " + 
					"				AND updateTIme <= ? " + 
					"			) h " + 
					"		GROUP BY " + 
					"			playerId " + 
					"		ORDER BY " + 
					"			jifenValue DESC " + 
					"	) hh, " + 
					"	(SELECT @i := 0) b " + 
					"LIMIT ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, Integer.valueOf(jifenValue));
			ps.setString(2, teamId);
			ps.setString(3, startTime);
			ps.setString(4, endTime);
			ps.setInt(5, Integer.valueOf(limit));
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				list.add(new JifenInfo(rs.getString(1),rs.getString(2),rs.getString(3)));
			}
			return list;
		}catch(Exception e) {
			ErrorUtil.err("积分查询失败", e);
		}finally {
			close(con,ps);
		}
		return list;
	}
	
	/**
	 * 会员历史战绩查询
	 * @param playerId
	 * @return
	 */
	public static String getTotalZJByPId(String playerId) {
		try {
			con = DBConnection.getConnection();
			String sql = "SELECT DISTINCT hr.playerName,sum(hr.shishou) as sum from historyrecord hr where hr.playerId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, playerId);
			ResultSet rs = ps.executeQuery();
			String res = "";
			while(rs.next()){
				res = rs.getString(2);
				break;
			}
			return StringUtil.isBlank(res) ? "0.0" : NumUtil.digit0(res);
		}catch(Exception e) {
			ErrorUtil.err("会员历史战绩查询失败", e);
		}finally {
			close(con,ps);
		}
		return "0.0";
	}
	
	
	/**
	 * 保存导入的战绩表，供会员和积分查询
	 * @param list
	 */
	public static void saveHistoryRecord(final List<HistoryRecord> list) {
		try {
			con = DBConnection.getConnection();
			String sql = "";
			for(HistoryRecord hr : list) {
				sql = "insert into historyrecord values(?,?,?,?,?,?,?,?)";
				ps = con.prepareStatement(sql);
				ps.setString(1, hr.getPlayerId());
				ps.setString(2, hr.getPlayerName());
				ps.setString(3, hr.getTeamId().toUpperCase());
				ps.setString(4, hr.getYszj());
				ps.setString(5, hr.getShishou());
				ps.setString(6, hr.getChuHuishui());
				ps.setString(7, hr.getShouHuishui());
//				ps.setDate(8,  new Date(hr.getUpdateTime().getTime()));
				ps.setString(8,  hr.getUpdateTime().toString());
				ps.execute();
			}
		}catch(Exception e) {
			ErrorUtil.err("保存导入的战绩表失败", e);
		}finally {
			close(con,ps);
		}
	}
	
	
	//删除一条人员名单---未测试
	public static void delMember(final String playerId) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(playerId)) {
				sql = "delete from members where playerId = '"+playerId+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err("根据ID("+playerId+")删除人员失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 删除团队时顺带删除所有该团队的人
	 * 
	 * @time 2017年11月14日
	 * @param playerId
	 */
	public static void delMembers_after_delTeam(final String teamId) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(teamId)) {
				sql = "delete from members where teamId = '"+teamId.toUpperCase()+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err(teamId+",删除此团队时顺带删除所有该团队的人失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	//删除一条团队
	public static void delHuishui(final String teamId) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(teamId)) {
				sql = "delete from teamhs where teamId = '"+teamId.toUpperCase()+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err("根据团队ID("+teamId+")删除回水失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	//插入一条人员名单---未测试
	public static void addMember(final Player player) {
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into members values(?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, player.getgameId());
			ps.setString(2, player.getPlayerName());
			ps.setString(3, player.getGudong());
			ps.setString(4, player.getTeamName());
			ps.setString(5, player.getEdu());
			ps.setString(6, "0");
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err(player.toString()+",插入一条人员名单失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 根据玩家ID查询玩家信息
	 * 
	 * @time 2017年10月26日
	 * @param playerId
	 * @return
	 */
	public static Player getMemberById(String playerId) {
		Player player = new Player();
		if(StringUtil.isBlank(playerId))
			return player;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "select m.playerId,m.playerName,m.teamId,m.gudong,m.edu,m.isParent from members m where playerId = ? ";
			ps = con.prepareStatement(sql);
			ps.setString(1,playerId);
			ResultSet rs = ps.executeQuery();
			String res = "";
			while(rs.next()){
				player.setGameId(playerId);
				player.setPlayerName(rs.getString(2));
				player.setTeamName(rs.getString(3));
				player.setGudong(rs.getString(4));
				player.setEdu(rs.getString(5));
				break;
			}
			log.info("================根据玩家ID("+playerId+")查询玩家信息，获得玩家是否为空："+StringUtil.isBlank(player.getgameId()));
			return player;
		}catch (SQLException e) {
			ErrorUtil.err("根据玩家ID查询玩家信息失败", e);
			return player;
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 保存或者修改玩家信息
	 * 
	 * @time 2017年10月31日
	 * @param player 玩家信息
	 */
	public static void saveOrUpdate(final Player player) {
		
		if(isHasMember(player.getgameId())) {
			updateMember(player);
		}else {
			addMember(player);
		}
	}
	
	
	/**
	 * 玩家是否存在
	 * 
	 * @time 2017年10月31日
	 * @param playerId 玩家ID
	 * @return
	 */
	public static boolean isHasMember(String playerId) {
		boolean hasMember = false;
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql = "select count(*) from members  where playerName = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, playerId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt(1) == 1) {
					hasMember = true;
					break;
				}
			}
		}catch(Exception e) {
			
		}finally{
			close(con,ps);
		}
		return hasMember;
	}
	
	//修改人员名单---未测试
	public static void updateMember(final Player player) {
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update members set playerName=?,gudong=?,teamId=?,edu=? where playerId =?";
			ps = con.prepareStatement(sql);
			ps.setString(1, player.getPlayerName());
			ps.setString(2, player.getGudong());
			ps.setString(3, player.getTeamName());
			ps.setString(4, player.getEdu());
			ps.setString(5, player.getgameId());
//			ps.setString(6, "0");//是否是父类
			ps.executeUpdate();
			log.info("================修改人员...finishes");
		}catch (SQLException e) {
			ErrorUtil.err("修改人员名单失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	/**
	 * 导入人员名单
	 * 
	 * @time 2017年11月19日
	 * @param map
	 */
	public static void insertMembers(final Map<String,Player> map) {
		if(map != null && map.size() > 0) {
			String incorrectPlayerName = "";
			long start = System.currentTimeMillis();
			try {
				con = DBConnection.getConnection();
				String sql;
				if(map != null && map.size() > 0) {
					sql = "delete from members";
					ps = con.prepareStatement(sql);
					ps.execute();
				}
				
				Player player ;
				sql = "insert into members values(?,?,?,?,?,?)";
				ps = con.prepareStatement(sql);
				int count = 0;
				con.setAutoCommit(false);
				for(Map.Entry<String, Player> entry : map.entrySet()) {
					player = entry.getValue();
					incorrectPlayerName = player.getPlayerName();
					ps.setString(1, player.getgameId());
					ps.setString(2, player.getPlayerName());
					ps.setString(3, player.getGudong());
					ps.setString(4, player.getTeamName());
					ps.setString(5, player.getEdu());
					ps.setString(6, "0");
					ps.addBatch();
					//ps.execute();//批量插入应该用ps.executeBatch()
					if ((++count) % map.size() == 0) { // 每500条刷新并写入数据库
						ps.executeBatch();  
						con.commit();  
						// 清空stmt中积攒的sql  
						ps.clearBatch(); 
					}
				}
				ps.executeBatch();
				ps.clearBatch();
				long end = System.currentTimeMillis();
				log.info("导入人员名单完成，耗时："+(end - start)+"毫秒");
			}catch (SQLException e) {
				ErrorUtil.err(incorrectPlayerName+"=导入人员名单进数据库失败", e);
			}finally{
				close(con,ps);
			}
		}
	}
	
	//导入昨日留底数据（仅在导入和锁定最后一场时用到）
	public static void insertPreData(String dataTime,String preData) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if("2017-01-01".equals(dataTime)) {
				//查看数据库是不是有2017-01-01的数据
				sql = "select * from yesterday_data where dateTime = '2017-01-01'";
				ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				if(!rs.next()) {
					sql = "insert into yesterday_data values(?,?)";
					ps = con.prepareStatement(sql);
					ps.setString(1, dataTime);
					ps.setString(2, preData);
					ps.execute();
					return;
				}
				//若无
				sql = "update yesterday_data set preData = ? where dateTime = '2017-01-01'";
				ps = con.prepareStatement(sql);
				ps.setString(1, preData);
				ps.execute();
				return;
			}
			
			
			sql = "insert into yesterday_data values(?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, dataTime);
			ps.setString(2, preData);
			ps.execute();
			log.info("================昨日留底插入进数据库...finishes");
		}catch (SQLException e) {
			ErrorUtil.err("昨日留底插入失败",e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 查找最新的昨日留底数据
	 * @author 小林
	 */
	public static String  Load_Date =  ""; 
	public static Map<String,String> getLastPreData() {
		Map<String, String> map = new HashMap<>();
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql;
			sql = "select * from yesterday_data yd where yd.dateTime = (select MAX(dateTime) from yesterday_data )";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String res = "";
			while(rs.next()){
				log.info("加载的昨日留底时间是："+rs.getString(1));
				Load_Date = rs.getString(1);
				res = rs.getString(2);
				break;
			}
			//封闭数据
			if(StringUtil.isBlank(res)) {
				ShowUtil.show("获取昨日留底数据失败！原因：数据库中没有昨日留底数据！");
				return map;
			}
			map = JSON.parseObject(res, new TypeReference<Map<String,String>>(){});
//			//资金
//			Map<String,String> zijinMap = _map.get("资金");
//			//实时开销(可以实时金额那里拿)
//			Map<String,String> presentPayoutMap = _map.get("实时开销");
//			//时实金额
//			Map<String,String> presentMoneyMap = _map.get("时实金额");
//			log.info(JSON.toJSONString(presentMoneyMap));
//			//昨日利润
//			Map<String,String> yesterdayProfitMap = _map.get("昨日利润");
//			//联盟对帐
//			Map<String,String> LMMap = _map.get("联盟对帐");
		}catch (SQLException e) {
			ErrorUtil.err("查找最新的昨日留底数据失败",e);
		}finally{
			close(con,ps);
		}
		return map;
	}
	
	/**
	 * 查找最新的锁定数据
	 * @param dataTime
	 * @param preData
	 */
	public static Map<String,String> getLastLockedData() {
		Map<String,String> map = new HashMap<>();
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql;
			sql = "select * from last_locked_data l where l.ju = (select MAX(ju) from last_locked_data)";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			String res = "";
			while(rs.next()){
				log.info("中途加载的最大局是："+rs.getString(1));
				res = rs.getString(2);
				break;
			}
			//封闭数据
			if(StringUtil.isBlank(res)) {
				ShowUtil.show("没有可供中途需要加载的锁定数据！");
				return map;
			}
			map = JSON.parseObject(res, new TypeReference<Map<String,String>>(){});
//			//资金
//			Map<String,String> zijinMap = _map.get("资金");
//			//实时开销(可以实时金额那里拿)
//			Map<String,String> presentPayoutMap = _map.get("实时开销");
//			//时实金额
//			Map<String,String> presentMoneyMap = _map.get("时实金额");
//			log.info(JSON.toJSONString(presentMoneyMap));
//			//昨日利润
//			Map<String,String> yesterdayProfitMap = _map.get("昨日利润");
//			//联盟对帐
//			Map<String,String> LMMap = _map.get("联盟对帐");
		}catch (SQLException e) {
			ErrorUtil.err("查找最新的锁定数据失败",e);
			return map;
		}finally{
			close(con,ps);
		}
		return map;
	}
	
	/**
	 * 添加新团队回水
	 * @time 2017年11月12日
	 * @param hs
	 * @return
	 */
	public static boolean addTeamHS(final Huishui hs) {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into teamhs values(?,?,?,?,?,?,?,?,?,?,?)";//11列
			ps = con.prepareStatement(sql);
			ps.setString(1, hs.getTeamId().toUpperCase());
			ps.setString(2, hs.getTeamName());
			ps.setString(3, hs.getHuishuiRate());
			ps.setString(4, hs.getInsuranceRate());
			ps.setString(5, hs.getGudong());
			ps.setString(6, hs.getZjManaged());
			ps.setString(7, hs.getBeizhu());
			ps.setString(8, hs.getProxyHSRate());
			ps.setString(9, hs.getProxyHBRate());
			ps.setString(10, hs.getProxyFWF());
			ps.setString(11, hs.getShowInsure());
			ps.execute();
		}catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("插入一条团队回水进数据库失败",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 修改团队回水比例
	 * 
	 * @time 2017年11月17日
	 * @param teamId
	 * @param teamHsRate
	 * @return
	 */
	public static boolean updateTeamHsRate(String teamId,String teamHsRate) {
		teamId=teamId.toUpperCase();
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update teamhs set huishuiRate=? where teamId =?";
			ps = con.prepareStatement(sql);
			ps.setString(1, teamHsRate);
			ps.setString(2, teamId);
			ps.executeUpdate();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err("修改团队回水比例失败", e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 修改团队保险比例
	 * 
	 * @time 2018年2月8日
	 * @param teamId
	 * @param teamHsRate
	 * @return
	 */
	public static boolean updateTeamHsBaoxianRate(String teamId,String teamHsRate) {
		teamId=teamId.toUpperCase();
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update teamhs set insuranceRate=? where teamId =?";
			ps = con.prepareStatement(sql);
			ps.setString(1, teamHsRate);
			ps.setString(2, teamId);
			ps.executeUpdate();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err("修改团队保险比例失败", e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 修改代理查询中导出是否显示团队保险
	 * @param teamId
	 * @param teamHsRate
	 * @return
	 */
	public static boolean updateTeamHsShowInsure(String teamId,String showInsure) {
		teamId=teamId.toUpperCase();
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update teamhs set showInsure=? where teamId =?";
			ps = con.prepareStatement(sql);
			ps.setString(1, showInsure);
			ps.setString(2, teamId);
			ps.executeUpdate();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err("修改代理查询中导出是否显示团队保险失败", e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 团队回水修改
	 * 
	 * @time 2018年1月5日
	 * @param hs
	 */
	public static void updateTeamHS(final Huishui hs) {
		try {
			con = DBConnection.getConnection();
			String sql = "update teamhs set teamName=?,huishuiRate=?,insuranceRate=?,"
					+ "gudong=?,zjManaged=?,beizhu=?,proxyHSRate=?,proxyHBRate=?,proxyFWF=?,showInsure=?  where teamId = ?";//11列
			ps = con.prepareStatement(sql);
			ps.setString(1, hs.getTeamName());
			ps.setString(2, hs.getHuishuiRate());
			ps.setString(3, hs.getInsuranceRate());
			ps.setString(4, hs.getGudong());
			ps.setString(5, hs.getZjManaged());
			ps.setString(6, hs.getBeizhu());
			ps.setString(7, hs.getProxyHSRate());
			ps.setString(8, hs.getProxyHBRate());
			ps.setString(9, hs.getProxyFWF());
			ps.setString(10, hs.getShowInsure());
			ps.setString(11, hs.getTeamId().toUpperCase());
			ps.execute();
			log.info("团队回水修改");
		}catch (SQLException e) {
			ErrorUtil.err("团队回水修改失败",e);
		}finally{
			close(con,ps);
		}
	}
	/**
	 * 团队回水入库
	 * 
	 * @time 2017年11月19日
	 * @param map
	 */
	public static void insertTeamHS(final Map<String,Huishui> map) {
		if(map != null && map.size() > 0) {
			try {
				con = DBConnection.getConnection();
				if(map != null && map.size() > 0) {
					sql = "delete from teamhs";
					ps = con.prepareStatement(sql);
					ps.execute();
				}
				
				Huishui hs ;
				String sql;
				log.info("团队回水进数据库开始...");
				for(Map.Entry<String, Huishui> entry : map.entrySet()) {
					hs = entry.getValue();
					sql = "insert into teamhs values(?,?,?,?,?,?,?,?,?,?,?)";//10列
					ps = con.prepareStatement(sql);
					ps.setString(1, hs.getTeamId().toUpperCase());
					ps.setString(2, hs.getTeamName());
					ps.setString(3, hs.getHuishuiRate());
					ps.setString(4, hs.getInsuranceRate());
					ps.setString(5, hs.getGudong());
					ps.setString(6, hs.getZjManaged());
					ps.setString(7, hs.getBeizhu());
					ps.setString(8, hs.getProxyHSRate());
					ps.setString(9, hs.getProxyHBRate());
					ps.setString(10, hs.getProxyFWF());
					ps.setString(11, hs.getShowInsure());
					ps.execute();
				}
				log.info("团队回水进数据库结束！size:"+map.size());
			}catch (SQLException e) {
				ErrorUtil.err("批量团队回水进数据库失败",e);
			}finally{
				close(con,ps);
			}
		}
	}
	
	
	/**
	 * 锁定时保存所有缓存数据
	 * 备注：如果插入到一半失败了呢，后期考虑引入事务
	 */
	public static int saveLastLockedData() {
		int lockedIndex = 0;
		Map<String,String> lastLockedDataMap = DataConstans.getLockedDataMap();
		String json_all_locked_data = JSON.toJSONString(lastLockedDataMap);
		int ju_size = DataConstans.All_Locked_Data_Map.size();
		try {
			log.info("================插入锁定数据进数据库...开始");
			con = DBConnection.getConnection();
			
			//删除原先数据
//			String sql = "DELETE from last_locked_data";
//			ps = con.prepareStatement(sql);
//			ps.execute();
			
			//插入最新锁定数据
			sql = "insert into last_locked_data values(?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, ju_size+"");
			ps.setString(2, json_all_locked_data);
			ps.execute();
			log.info("================插入锁定数据进数据库...结束");
			
			//删除原先数据
			String sql = "DELETE from last_locked_data where ju < "+(ju_size);//ju_size-2
			ps = con.prepareStatement(sql);
			ps.execute();
			
			lockedIndex = ju_size;
			return lockedIndex;
		}catch (SQLException e) {
			if(e.getMessage().contains("Incorrect")) {
				ErrorUtil.err("锁定时保存所有缓存数据失败,特殊字符串引起",e);
			}else {
				ErrorUtil.err("锁定时保存所有缓存数据失败",e);
			}
		}finally{
			close(con,ps);
		}
		return lockedIndex;
	}
	
	
	/**
	 * 获取所有的人员名单
	 * @return
	 */
	public static List<Player> getAllMembers(){
		List<Player> result = new ArrayList<Player>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from members";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Player p;
			while(rs.next()){
				p = new Player();
				p.setGameId(rs.getString(1));
				p.setPlayerName(rs.getString(2));
				p.setGudong(rs.getString(3));
				p.setTeamName(rs.getString(4));
				p.setEdu(rs.getString(5));
				result.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			close(con,ps);
		}
		return result;
	}
	
	
	
	/**
	 * 获取所有的团队回水
	 * @return
	 */
	public static List<Huishui> getAllTeamHS(){
		List<Huishui> result = new ArrayList<Huishui>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from teamhs";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			Huishui hs;
			while(rs.next()){
				hs = new Huishui();
				hs.setTeamId(rs.getString(1));
				hs.setTeamName(rs.getString(2));
				hs.setHuishuiRate(rs.getString(3));
				hs.setInsuranceRate(rs.getString(4));
				hs.setGudong(rs.getString(5));
				hs.setZjManaged(rs.getString(6));
				hs.setBeizhu(rs.getString(7));
				hs.setProxyHSRate(rs.getString(8));
				hs.setProxyHBRate(rs.getString(9));
				hs.setProxyFWF(rs.getString(10));
				hs.setShowInsure(rs.getString(11));
				result.add(hs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			close(con,ps);
		}
		return result;
	}
	
	
	
	
	/******************************************************** 关闭流  ******/
	public static void close(Connection c,Statement s) {
		close(c);
		close(s);
		
	}
	public static void close(Connection c) {
		if(c != null) {
			try {
				c.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(Statement s) {
		if(s != null) {
			try {
				s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static  Set<String> daySet = new LinkedHashSet<>();
	
	public static boolean isPreData2017VeryFirst(){
		return "2017-01-01".equals(Load_Date) ? true : false;
	}
	
	/**
	 * 清空并恢复到最开始的数据
	 * @return
	 */
	public static boolean clearAllData() {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql1 = "delete from last_locked_data";
			String sql2 = "DELETE from yesterday_data where dateTime != '2017-01-01'";
			String sql3 = "DELETE from historyrecord ";
			
			ps = con.prepareStatement(sql1);
			ps.execute();
			ps = con.prepareStatement(sql2);
			ps.execute();
			ps = con.prepareStatement(sql3);
			ps.execute();
		} catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("清空并恢复到最开始的数据",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 结束今天统计时把last_locked_data设置ju = 0
	 * 
	 * @return
	 */
	public static boolean handle_last_locked_data() {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql = "select count(*) from last_locked_data";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int count = 0;
			while(rs.next()){
				count = Integer.valueOf(rs.getString(1));
			}
			if(count > 0) {
				//将最大值设置为0(最大值就是
				int maxJu = getMaxJu();
				//更新已锁定数据
				update_last_locked_data(maxJu);
			}
		} catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("结束今天统计时把last_locked_data设置ju = 0失败",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	//获取已锁定数据最大的牌局
	private static int getMaxJu() {
		int maxJu = 0;
		try {
			con = DBConnection.getConnection();
			String sql = "select max(ju) from last_locked_data";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				maxJu = Integer.valueOf(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(con,ps);
		}
		return maxJu;
	}
	
	//结束今天统计时更新已锁定数据
	private static void update_last_locked_data(int maxJu) {
		try {
			con = DBConnection.getConnection();
			String sql = "update last_locked_data set ju = 0 where ju = ?";
			ps = con.prepareStatement(sql);
			ps.setInt(1, maxJu);
			ps.executeUpdate();
			
			//删除ju != 0的数据
		    sql = "DELETE from last_locked_data where ju > 0";
			ps = con.prepareStatement(sql);
			ps.execute();
			
		} catch (SQLException e) {
			ErrorUtil.err("结束今天统计时更新已锁定数据",e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 打开软件时获取合并ID的数据
	 * 
	 * @time 2017年11月4日
	 * @return
	 */
	public static Map<String,Set<String>> getCombineData(){
		Map<String,Set<String>> combineMap = new HashMap<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select parentId,subIdJson,update_time from combine_ids";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			int count = 0;
			String parentId = "";
			String subIdJson = "";
			while(rs.next()){
				parentId = rs.getString(1);
				subIdJson = rs.getString(2);
				Set<String> subIdSet = JSON.parseObject(subIdJson, new TypeReference<Set<String>>() {});
				combineMap.put(parentId, subIdSet);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(con,ps);
		}
		return combineMap;
	}

	/**
	 * 保存或更新合并ID关系
	 * 
	 * @time 2017年11月4日
	 * @param parentId 父ID
	 * @return
	 */
	public static boolean saveOrUpdateCombineId(String parentId) {
		Set<String> subIdSet = DataConstans.Combine_Super_Id_Map.get(parentId);
		boolean hasCombineRelation = isHasCombineId(parentId);
		if(subIdSet.size() == 0 && hasCombineRelation) {//针对没有子ID集合的更新关系应该是删除此合并关系
			cancelCombineId(parentId);
			return true;
		}
		String subIdJson = JSON.toJSONString(subIdSet);
		String time = TimeUtil.getTime();
		if(StringUtil.isBlank(parentId) || StringUtil.isBlank(subIdJson))
			return false;
		
		if(hasCombineRelation) {
			return updateCombineId(parentId,subIdJson,time);
		}else {
			return addNewCombineId(parentId,subIdJson,time);
		}
	}
	
	/**
	 * 更新合并ID关系
	 * 
	 * @time 2017年11月4日
	 * @param superId 父ID
	 * @param subIdJson 子IDJSON值
	 * @param time 更新时间
	 * @return
	 */
	public static boolean updateCombineId(String superId, String subIdJson, String time) {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql = "update combine_ids set subIdJson=?,update_time=? where parentId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, subIdJson);
			ps.setString(2, time);
			ps.setString(3, superId);
			
			ps.executeUpdate();
			
		} catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("更新一条合并ID进数据库失败",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	
	/**
	 * 添加新合并ID关系
	 * 
	 * @time 2017年11月4日
	 * @param superId 父ID
	 * @param subIdJson 子IDJSON值
	 * @param time 更新时间
	 * @return
	 */
	public static boolean addNewCombineId(String parentId,String subIdJson,String time) {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into combine_ids values(?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, parentId);
			ps.setString(2, subIdJson);
			ps.setString(3, time);
			ps.execute();
		}catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("插入一条合并ID进数据库失败",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	
	/**
	 * 合并ID是否存在
	 * 根据父ID查询数据库中是否记录
	 * 
	 * @time 2017年10月31日
	 * @param playerId 玩家ID
	 * @return
	 */
	public static boolean isHasCombineId(String parentId) {
		boolean hasCombineId = false;
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql = "select count(*) from combine_ids  where parentId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, parentId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt(1) == 1) {
					hasCombineId = true;
					break;
				}
			}
		}catch(Exception e) {
			
		}finally{
			close(con,ps);
		}
		return hasCombineId;
	}
	
	/**
	 * 解除合并ID关系
	 * 
	 * @time 2017年11月4日
	 * @param parentId
	 */
	public static void cancelCombineId(String parentId) {
		if(isHasCombineId(parentId)) {
			try {
				con = DBConnection.getConnection();
				String sql  = "delete from combine_ids where parentId = ?";
				ps = con.prepareStatement(sql);
				ps.setString(1, parentId);
				ps.execute();
			}catch (SQLException e) {
				ErrorUtil.err("数据库解除合关ID关系失败",e);
			}finally{
				close(con,ps);
			}
		}
	}
	
	/**
	 * 是否应该点击开始新一天统计按钮
	 * 场景：上一场已经结束今日统计后，下一场应该只能开始新一天按钮，而不应该是中途继续
	 * 
	 * @time 2017年11月12日
	 * @return 1:开始新一天按钮 2:中途继续按钮
	 */
	public static int  newStaticOrContinue() {
		int buttonCode = 1;
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql = "select count(*),min(ju) from last_locked_data";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) {
				int count = rs.getInt(1);
				String ju = rs.getString(2);
				if(count >= 1 && !"0".equals(ju)) {
					buttonCode = 2;
				}
			}
		}catch(Exception e) {
			buttonCode = 1;
		}finally{
			close(con,ps);
		}
		return buttonCode;
	}
	
	
	/***********************************************************************************
	 * 
	 * 小工具CRUD
	 * 
	 **********************************************************************************/
	/**
	 * 从数据库获取所有俱乐部信息
	 * 
	 * @time 2017年11月24日
	 * @return
	 */
	public static Map<String,Club> getAllClub(){
		Map<String,Club> map = new HashMap<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from club";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Club club = new Club();
				club.setClubId(rs.getString(1));
				club.setName(rs.getString(2));
				club.setEdu(rs.getString(3));
				club.setZhuoFei(rs.getString(4));
				club.setYiJieSuan(rs.getString(5));
				club.setZhuoFei2(rs.getString(6));
				club.setZhuoFei3(rs.getString(7));
				club.setYiJieSuan2(rs.getString(8));
				club.setYiJieSuan3(rs.getString(9));
				club.setEdu2(rs.getString(10));
				club.setEdu3(rs.getString(11));
				club.setGudong(rs.getString(12));
				map.put(club.getClubId(), club);
			}
		} catch (SQLException e) {
			ErrorUtil.err("从数据库获取所有俱乐部信息失败",e);
		} finally{
			close(con,ps);
		}
		return map;
	}
	
	/**
	 * 删除俱乐部
	 * @time 2017年11月22日
	 * @param id 俱乐部ID
	 */
	public static void delClub(final String id) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(id)) {
				sql = "delete from club where clubId = '"+id+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
				//是否需要删除对应的人员？
				
				
			}
		}catch (SQLException e) {
			ErrorUtil.err("根据ID("+id+")删除俱乐部失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	/**
	 * 添加新俱乐部
	 * @time 2017年11月22日
	 * @param club 
	 * @param lmType 哪个联盟
	 */
	public static void addClub(final Club club) {
		try {
			//数据库中没有则添加
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into club values(?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, club.getClubId());
			ps.setString(2, club.getName());
			ps.setString(3, club.getEdu());
			ps.setString(4, club.getZhuoFei());
			ps.setString(5, club.getYiJieSuan());
			ps.setString(6, club.getZhuoFei2());
			ps.setString(7, club.getZhuoFei3());
			ps.setString(8, club.getYiJieSuan2());
			ps.setString(9, club.getYiJieSuan3());
			ps.setString(10, club.getEdu2());
			ps.setString(11, club.getEdu3());
			ps.setString(12, club.getGudong());
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err("添加新俱乐部失败", e);
		}finally{
			close(con,ps);
		}
	}
	

	
	/**
	 * 俱乐部是否存在
	 * 
	 * @time 2017年10月31日
	 * @param id 俱乐部ID
	 * @return
	 * @throws Exception 
	 */
	public static boolean isHasClub(String id) throws Exception {
		if(StringUtil.isBlank(id))  return false;
		
		boolean hsRecord = false;
		try {
			//获取数据
			con = DBConnection.getConnection();
			String sql = "select count(*) from club  where clubId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				if(rs.getInt(1) == 1) {
					hsRecord = true;
					break;
				}
			}
		}catch(Exception e) {
			log.error(id+"查询俱乐部是否存在失败",e);
			throw e;
		}finally{
			close(con,ps);
		}
		return hsRecord;
	}
	
	/**
	 * 更新俱乐部额度
	 * 
	 * @time 2017年11月22日
	 * @param club
	 * @return
	 */
	public static boolean updateClub(final Club club) {
		boolean isOK = true;
		try {
			con = DBConnection.getConnection();
			String sql = "update club set name=?,edu=?,zhuoFei=?,yiJieSuan=?,zhuoFei2=?,zhuoFei3=?,yiJieSuan2=?,yiJieSuan3=?,edu2=?,edu3=?,gudong=?  where clubId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, club.getName());
			ps.setString(2, club.getEdu());
			ps.setString(3, club.getZhuoFei());
			ps.setString(4, club.getYiJieSuan());
			ps.setString(5, club.getZhuoFei2());
			ps.setString(6, club.getZhuoFei3());
			ps.setString(7, club.getYiJieSuan2());
			ps.setString(8, club.getYiJieSuan3());
			ps.setString(9, club.getEdu2());
			ps.setString(10, club.getEdu3());
			ps.setString(11, club.getGudong());
			ps.setString(12, club.getClubId());
			ps.executeUpdate();
		} catch (SQLException e) {
			isOK = false;
			ErrorUtil.err("更新俱乐部额度失败",e);
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	

	/**
	 * 新增或修改俱乐部信息
	 * @time 2017年11月22日
	 * @param club
	 * @throws Exception 
	 */
	public static void saveOrUpdateClub(final Club club) throws Exception {
		if(isHasClub(club.getClubId())) {
			updateClub(club);
		}else {
			addClub(club);
		}
	}
	
	
	/**
	 * 删除某一天的战绩记录
	 * 备注：这个会删除多条
	 * 
	 * @time 2017年11月22日
	 * @param as_of 日期（如2017-02-01）
	 */
	public static void delRecord(final String as_of) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(as_of)) {
				sql = "delete from Record where as_of = '"+as_of+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err("删除某一天的战绩记录失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 删除某一场的战绩记录
	 * 
	 * @time 2017年11月23日
	 * @param idSubStr
	 */
	public static void delRecordLike(final String idSubStr) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(idSubStr)) {
				sql = "delete from Record where id like '"+idSubStr.trim()+"%'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err(idSubStr+",删除场次战绩记录失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 添加战绩记录
	 * @time 2017年11月22日
	 * @param record
	 */
	public static void addRecord(final Record record) {
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into record values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, record.getId());
			ps.setString(2, record.getTableId());
			ps.setString(3, record.getClubId());
			ps.setString(4, record.getPlayerId());
			ps.setString(5, record.getScore());
			ps.setString(6, record.getInsurance());
			ps.setString(7, record.getBlind());
			ps.setString(8, record.getDay());
			ps.setString(9, record.getClubName());
			ps.setString(10, record.getLmType());
			ps.setString(11, record.getTeamId());
			ps.setString(12, record.getInsuranceEach());
			ps.setString(13, record.getIsJiesuaned());
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err("添加战绩记录失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 联盟对帐批量插入战绩记录
	 * 
	 * @time 2017年11月19日
	 * @param map
	 */
	public static void addRecordList(final List<Record> recordList) {
		if(recordList != null && recordList.size() > 0) {
			int size = recordList.size();
			String incorrectPlayerId = "";
			long start = System.currentTimeMillis();
			try {
				con = DBConnection.getConnection();
				String sql;
				
				sql = "insert into record values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
				ps = con.prepareStatement(sql);
				int count = 0;
				con.setAutoCommit(false);
				for(Record record : recordList) {
					incorrectPlayerId = record.getClubId();
					ps.setString(1, record.getId());
					ps.setString(2, record.getTableId());
					ps.setString(3, record.getClubId());
					ps.setString(4, record.getPlayerId());
					ps.setString(5, record.getScore());
					ps.setString(6, record.getInsurance());
					ps.setString(7, record.getBlind());
					ps.setString(8, record.getDay());
					ps.setString(9, record.getClubName());
					ps.setString(10, record.getLmType());
					ps.setString(11, record.getTeamId());
					ps.setString(12, record.getInsuranceEach());
					ps.setString(13, record.getIsJiesuaned());
					ps.addBatch();
					if ((++count) % size == 0) { // 每size条刷新并写入数据库(只插入一次)
						ps.executeBatch();  
						con.commit();  
						ps.clearBatch();// 清空stmt中积攒的sql   
					}
				}
				ps.executeBatch();
				ps.clearBatch();
				long end = System.currentTimeMillis();
				log.info(recordList.get(0).getTableId()+"联盟对帐批量插入战绩记录完成，耗时："+(end - start)+"毫秒");
			}catch (SQLException e) {
				ErrorUtil.err(incorrectPlayerId+",联盟对帐批量插入战绩记录进数据库失败", e);
			}finally{
				close(con,ps);
			}
		}
	}
	
	/**
	 * 获取已锁定的战绩记录中最大的时间
	 * @time 2017年11月25日
	 * @return
	 */
	public static String getMaxRecordTime() {
		String maxRecordTime = "";
		try {
			con = DBConnection.getConnection();
			String sql = "select max(as_of) from record";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				maxRecordTime = rs.getString(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(con,ps);
		}
		return maxRecordTime;
	}
	
	/**
	 * 获取最新的战绩记录列表（单位：天）
	 * @time 2017年11月25日
	 * @param maxRecordTime
	 */
	public static List<Record> getRecordsByMaxTime(String maxRecordTime) {
		List<Record> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  record where as_of =  ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, maxRecordTime);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Record record = new Record();
				record.setId(rs.getString(1));
				record.setTableId(rs.getString(2));
				record.setClubId(rs.getString(3));
				record.setPlayerId(rs.getString(4));
				record.setScore(rs.getString(5));
				record.setInsurance(rs.getString(6));
				record.setBlind(rs.getString(7));
				record.setDay(rs.getString(8));
				record.setClubName(rs.getString(9));
				record.setLmType(rs.getString(10));
				record.setTeamId(rs.getString(11));
				record.setInsuranceEach(rs.getString(12));
				record.setIsJiesuaned(rs.getString(13));
				list.add(record);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取最新的战绩记录（单位：天）失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	/**
	 * 获取最新的战绩记录列表（单位：当天某俱乐部）
	 * @time 2017年11月25日
	 * @param maxRecordTime
	 */
	public static List<Record> getRecordsByMaxTimeAndClub(String maxRecordTime, String clubId) {
		List<Record> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  record where as_of =  ? and clubId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, maxRecordTime);
			ps.setString(2, clubId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Record record = new Record();
				record.setId(rs.getString(1));
				record.setTableId(rs.getString(2));
				record.setClubId(rs.getString(3));
				record.setPlayerId(rs.getString(4));
				record.setScore(rs.getString(5));
				record.setInsurance(rs.getString(6));
				record.setBlind(rs.getString(7));
				record.setDay(rs.getString(8));
				record.setClubName(rs.getString(9));
				record.setLmType(rs.getString(10));
				record.setTeamId(rs.getString(11));
				record.setInsuranceEach(rs.getString(12));
				record.setIsJiesuaned(rs.getString(13));
				list.add(record);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取最新的战绩记录（单位：当天俱乐部）失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	
	public static List<Record> getRecordsByClubId(String clubId) {
		List<Record> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  record where  clubId = ?";
			ps = con.prepareStatement(sql);
			ps.setString(1, clubId);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Record record = new Record();
				record.setId(rs.getString(1));
				record.setTableId(rs.getString(2));
				record.setClubId(rs.getString(3));
				record.setPlayerId(rs.getString(4));
				record.setScore(rs.getString(5));
				record.setInsurance(rs.getString(6));
				record.setBlind(rs.getString(7));
				record.setDay(rs.getString(8));
				record.setClubName(rs.getString(9));
				record.setLmType(rs.getString(10));
				record.setTeamId(rs.getString(11));
				record.setInsuranceEach(rs.getString(12));
				record.setIsJiesuaned(rs.getString(13));
				list.add(record);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取最新的战绩记录（单位：当天俱乐部）失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	
	/**
	 * 获取最新的所有战绩记录列表（单位：天）
	 * 由于前面的会被删掉，帮只取最最后一天的数据
	 * @time 2017年11月25日
	 * @param maxRecordTime
	 */
	public static List<Record> getAllRecords() {
		List<Record> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  record";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				Record record = new Record();
				record.setId(rs.getString(1));
				record.setTableId(rs.getString(2));
				record.setClubId(rs.getString(3));
				record.setPlayerId(rs.getString(4));
				record.setScore(rs.getString(5));
				record.setInsurance(rs.getString(6));
				record.setBlind(rs.getString(7));
				record.setDay(rs.getString(8));
				record.setClubName(rs.getString(9));
				record.setLmType(rs.getString(10));
				record.setTeamId(rs.getString(11));
				record.setInsuranceEach(rs.getString(12));
				record.setIsJiesuaned(rs.getString(13));
				list.add(record);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取最新的战绩记录（单位：天）失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	/**
	 * 清空所有俱乐部桌费和已结算
	 * @time 2017年11月26日
	 * @param player
	 * @throws SQLException 
	 */
	public static void clearAllClub_ZF_YiJiSuan() {
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update club set zhuoFei='0',zhuoFei2='0',zhuoFei3='0',yiJieSuan='0',yiJieSuan2='0',yiJieSuan3='0'";
			ps = con.prepareStatement(sql);
			ps.executeUpdate();
			log.info("数据库：清空所有俱乐部桌费和已结算OK！");
		}catch (SQLException e) {
			ErrorUtil.err("清空所有俱乐部桌费和已结算失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	/**
	 * 清空所有统计信息(club and record)
	 * 
	 * @time 2017年11月14日
	 * @param playerId
	 */
	public static void del_club_and_record() {
		try {
			con = DBConnection.getConnection();
			String sql;
//			sql = "delete from club ";
//			ps = con.prepareStatement(sql);
//			ps.execute();
			
			sql = "delete from record ";
			ps = con.prepareStatement(sql);
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err("清空所有统计信息失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/****************************************************************
	 * 
	 *  俱乐部银行卡信息表操作
	 *  数据表：clubBank
	 *  数据模型：ClubBankModel
	 * 
	 ****************************************************************/
	/**
	 * 增加或修改俱乐部银行卡信息
	 */
	public static boolean addOrUpdateClubBank(final ClubBankModel bank) {
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
//			sql = "insert into clubBank values(?,?,?,?,?,?,?)";
			sql = "replace into clubBank values(?,?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, bank.getClubId());
			ps.setString(2, bank.getClubName());
			ps.setString(3, bank.getMobilePayType());
			ps.setString(4, bank.getPersonName());
			ps.setString(5, bank.getPhoneNumber());
			ps.setString(6, bank.getBankType());
			ps.setString(7, bank.getBankAccountInfo());
			ps.execute();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err("增加俱乐部银行卡信息记录失败", e);
			isOK = false;
		}finally{
			close(con,ps);
		}
		return isOK;
	}
//	/**
//	 * 修改俱乐部银行卡信息
//	 */
//	public static boolean updateClubBank(final ClubBankModel bank) {
//		boolean isOK = false;
//		try {
//			con = DBConnection.getConnection();
//			String sql;
////			sql = "update clubbank set clubName=?,mobilePayType=?,personName=?,phoneNumber=?,bankType=?,bankAccountInfo=? where clubId=?";
//			sql = "update clubbank set clubName=?,mobilePayType=?,personName=?,phoneNumber=?,bankType=?,bankAccountInfo=? where clubId=?";
//			ps = con.prepareStatement(sql);
//			ps.setString(1, bank.getClubName());
//			ps.setString(2, bank.getMobilePayType());
//			ps.setString(3, bank.getPersonName());
//			ps.setString(4, bank.getPhoneNumber());
//			ps.setString(5, bank.getBankType());
//			ps.setString(6, bank.getBankAccountInfo());
//			ps.setString(7, bank.getClubId());
//			ps.execute();
//			isOK = true;
//		}catch (SQLException e) {
//			ErrorUtil.err("修改俱乐部银行卡信息记录失败", e);
//			isOK = false;
//		}finally{
//			close(con,ps);
//		}
//		return isOK;
//	}
	
	/**
	 * 获取所有俱乐部银行卡信息
	 * 
	 * @time 2017年12月18日
	 * @return
	 */
	public static Map<String,ClubBankModel> getAllClubBanks() {
		Map<String,ClubBankModel> map = new HashMap<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  clubBank";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				ClubBankModel bank = new ClubBankModel();
				bank.setClubId(rs.getString(1));
				bank.setClubName(rs.getString(2));
				bank.setMobilePayType(rs.getString(3));
				bank.setPersonName(rs.getString(4));
				bank.setPhoneNumber(rs.getString(5));
				bank.setBankType(rs.getString(6));
				bank.setBankAccountInfo(rs.getString(7));
				map.put(rs.getString(1), bank);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取所有俱乐部银行卡信息失败",e);
		}finally{
			close(con,ps);
		}
		return map;
	}
	
	/***************************************************************************
	 * 
	 * 				others表
	 * 
	 **************************************************************************/
	
	/**
	 * 获取others表记录，根据key
	 * 
	 * @time 2018年2月3日
	 * @return
	 */
	public static String getValueByKey(final String key) {
		String value = "{}";
		try {
			con = DBConnection.getConnection();
			String sql = "select value from  others o where o.key = '"+key+"'";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				value = rs.getString(1);
			}
		} catch (SQLException e) {
			ErrorUtil.err("根据key("+key+")获取others表记录失败",e);
		}finally{
			close(con,ps);
		}
		return value;
	}
	
	/**
	 * 根据key删除value
	 * 
	 * @time 2018年1月29日
	 * @param key
	 */
	public static void delValueByKey(final String key) {
		try {
			con = DBConnection.getConnection();
			String sql;
			if(!StringUtil.isBlank(key)) {
				sql = "delete from others where key = '"+key+"'";
				ps = con.prepareStatement(sql);
				ps.execute();
			}
		}catch (SQLException e) {
			ErrorUtil.err("others表根据key("+key+")删除失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	/**
	 * 保存others记录
	 * 如股东贡献值中的客服记录
	 * @time 2018年2月3日
	 * @param key
	 * @param value
	 * @return
	 */
	public static boolean saveOrUpdateOthers(final String key, final String value) {
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "replace into others values(?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, key);
			ps.setString(2, value);
			ps.execute();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err(key+":"+value+",保存others记录失败", e);
			isOK = false;
		}finally{
			close(con,ps);
		}
		return isOK;
	}

	/**
	 * others表添加记录
	 * 
	 * @time 2018年1月30日
	 * @param key
	 * @param value
	 */
	public static void addValue(final String key, final String value) {
		try {
			//数据库中没有则添加
			con = DBConnection.getConnection();
			String sql;
			sql = "insert into others values(?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, key);
			ps.setString(2, value);
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err("others表添加记录（key:"+key+",value:"+value+"）失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	/***************************************************************************
	 * 
	 * 				次日上码表
	 * 
	 **************************************************************************/
	/**
	 * 保存次日上码
	 * @time 2018年2月5日
	 * @param nextday
	 * @return
	 */
	public static boolean saveOrUpdate_SM_nextday(final ShangmaNextday nextday) {
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "replace into shangma_nextday(playerId,playerName,changci,shangma,time,type) values(?,?,?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, nextday.getPlayerId());
			ps.setString(2, nextday.getPlayerName());
			ps.setString(3, nextday.getChangci());
			ps.setString(4, nextday.getShangma());
			ps.setString(5, nextday.getTime());
			ps.setString(6, nextday.getType());
			ps.execute();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err(nextday.getPlayerId()+":"+nextday.getChangci()+",保存次日上码记录失败", e);
			isOK = false;
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 获取玩家的次日数据
	 * @time 2018年2月5日
	 * @return
	 */
	public static List<ShangmaNextday> getAllSM_nextday() {
		List<ShangmaNextday> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select * from  shangma_nextday n where n.type = '0' ";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				ShangmaNextday nextday = new ShangmaNextday();
				nextday.setPlayerId(rs.getString(1));
				nextday.setPlayerName(rs.getString(2));
				nextday.setChangci(rs.getString(3));
				nextday.setShangma(rs.getString(4));
				nextday.setTime(rs.getString(5));
				nextday.setType(rs.getString(6));
				list.add(nextday);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取玩家的次日数据失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	/**
	 * 用户自行加载完次日数据后，将数据表中的type设置为1，表示已经加载过
	 * @time 2018年2月5日
	 * @param bank
	 * @return
	 */
	public static boolean setNextDayLoaded() {
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "update shangma_nextday n  set n.type = '1' where n.type = '0' ";
			ps = con.prepareStatement(sql);
			ps.execute();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err("修改次日数据记录（为加载）失败", e);
			isOK = false;
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/***************************************************************************
	 * 
	 * 				桌费表
	 * 
	 **************************************************************************/
	/**
	 * 保存或修改历史桌费
	 * 
	 * @time 2018年2月11日
	 * @param zhuofei
	 * @return
	 */
	public static boolean saveOrUpdate_club_zhuofei(final ClubZhuofei zhuofei) {
		boolean isOK = false;
		try {
			con = DBConnection.getConnection();
			String sql;
			sql = "replace into club_zhuofei(time,clubId,zhuofei,lmType) values(?,?,?,?)";
			ps = con.prepareStatement(sql);
			ps.setString(1, zhuofei.getTime());
			ps.setString(2, zhuofei.getClubId());
			ps.setString(3, zhuofei.getZhuofei());
			ps.setString(4, zhuofei.getLmType());
			ps.execute();
			isOK = true;
		}catch (SQLException e) {
			ErrorUtil.err(zhuofei.getTime()+":"+zhuofei.getClubId()+",保存或修改历史桌费失败", e);
			isOK = false;
		}finally{
			close(con,ps);
		}
		return isOK;
	}
	
	/**
	 * 获取联盟1的所有历史桌费
	 * 
	 * @time 2018年2月11日
	 * @return
	 */
	public static List<ClubZhuofei> get_LM1_all_club_zhuofei() {
		List<ClubZhuofei> list = new ArrayList<>();
		try {
			con = DBConnection.getConnection();
			String sql = "select cz.time, cz.clubId, cz.zhuofei, cz.lmType, c.name, c.gudong from  club_zhuofei cz  "
					+ "LEFT JOIN club c on cz.clubId = c.clubId where cz.lmType='联盟1'";
			ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				ClubZhuofei zhuofei = new ClubZhuofei(rs.getString(1),rs.getString(2),rs.getString(3),
						rs.getString(4),rs.getString(5),rs.getString(6));
				list.add(zhuofei);
			}
		} catch (SQLException e) {
			ErrorUtil.err("获取所有历史桌费失败",e);
		}finally{
			close(con,ps);
		}
		return list;
	}
	
	/**
	 * 删除所有的历史联盟桌费
	 * 
	 * @time 2018年2月11日
	 * @param key
	 */
	public static void del_all_club_zhuofei() {
		try {
			con = DBConnection.getConnection();
			String sql  = "delete from club_zhuofei ";
			ps = con.prepareStatement(sql);
			ps.execute();
		}catch (SQLException e) {
			ErrorUtil.err("删除所有的历史联盟桌费失败", e);
		}finally{
			close(con,ps);
		}
	}
	
	
	
}
