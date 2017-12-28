package com.kendy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSON;
import com.kendy.controller.LMController;
import com.kendy.entity.Huishui;
import com.kendy.entity.Player;
import com.kendy.entity.UserInfos;
import com.kendy.other.Wrap;
import com.kendy.util.NumUtil;
import com.kendy.util.ShowUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;

/**
 * 读取Excel
 * @author 林泽涛
 * @time 2017年10月7日 下午3:54:05
 */
public class ExcelReaderUtil {
	
	private static Logger log = Logger.getLogger(ExcelReaderUtil.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 测试本工具类
	 * @throws Exception 
	 * @throws IOException 
	 * @time 2017年10月7日
	 */
	public static void main(String[] args) throws IOException, Exception{
		String path = "C:/Users/Administrator.USER-20170713YG/Desktop/永利科技/昨日留底.xlsx";
		Map<String, String> map = ExcelReaderUtil.readPreDataRecord(new File(path));
        log.info(map.values());
        log.info("finishes...");
	}
	
	
	/**
	 * 创建 Excel 2003 或2007工作簿对象
	 * @time 2017年10月7日
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static Workbook getWeebWork(String filename) throws IOException {
		Workbook workbook = null;
		if (null != filename) {
			String fileType = filename.substring(filename.lastIndexOf("."),
					filename.length());
			FileInputStream fileStream = new FileInputStream(new File(filename));
			if (".xls".equals(fileType.trim().toLowerCase())) {
				workbook = new HSSFWorkbook(fileStream);// 创建 Excel 2003 工作簿对象
			} else if (".xlsx".equals(fileType.trim().toLowerCase())) {
				workbook = new XSSFWorkbook(fileStream);// 创建 Excel 2007 工作簿对象
			}
		}
		return workbook;
	}
	
	/**
	 * 导入人员名单
	 * 
	 * @time 2017年10月7日
	 * @param file
	 * @return
	 */
	public static Map<String,Player> readMembersRecord(File file) throws Exception{
		
		Map<String,Player> membersMap = new LinkedHashMap<>();
		Player player ;
		log.info("读取人员名单开始...");
		try(
			FileInputStream is = new FileInputStream(file)
		){
            Workbook workbook = getWeebWork(file.getAbsolutePath());
            Sheet sheet = workbook.getSheetAt(0);
 
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if(row.getRowNum()!=0){
                	//游戏id
                	String gameId = getCellValue(row.getCell(0));
                	gameId = StringUtil.nvl(gameId,"");
                	//股东名称
                	String gudong = getCellValue(row.getCell(1));
                	gudong = StringUtil.nvl(gudong,"");
                	//团队名称
                	String team = getCellValue(row.getCell(2));
                	team = StringUtil.nvl(team,"");
                	//玩家名称
                	String playerName = getCellValue(row.getCell(3));
                	playerName = StringUtil.nvl(playerName,"");
                	//备注
                	String beizhu = getCellValue(row.getCell(4));
                	if(!StringUtil.isBlank(gameId)){
                		try {
							gameId =  new BigDecimal(gameId).toPlainString();//不要科学记数法
						} catch (Exception e) {
							gameId = getCellValue(row.getCell(0));
						}
                		gudong = StringUtil.isBlank(gudong) ? "W" : gudong;//股东为空的默认设置为W
                		player = new Player(gameId,team,gudong,playerName,beizhu);
                		membersMap.put(gameId, player);
                	}
                }
            }
        } 
		log.info("读取人员名单结束! size:"+membersMap.size());
		return membersMap;
	}
	
	private static String getCellValue(Cell cell){
		if(cell == null){
			return "";
		}else{
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			return cell.toString();
		}
	}
	
	/**
	 * 导入回水
	 * @param file
	 * @return
	 */
	public static Wrap readHuishuiRecord(File file){
		
		Map<String,Huishui> huishuiMap = new HashMap<>();
		Wrap wrap = new Wrap();
		try{
			FileInputStream is = new FileInputStream(file);
			log.info("读取团队回水开始...");
			
			Workbook workbook = getWeebWork(file.getAbsolutePath());
			Sheet sheet = workbook.getSheetAt(0);//回水对应的sheet
			
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if(row.getRowNum()>=2){
					//团队id
					String teamId = "";
					//团队名称
					String teamName = "";
					//回水比例
					String huishuiRate = "";
					//备注
					String beizhu = "";
					//保险比例
					String insuranceRate = "";
					//股东
					String gudong = "";
					//是否代理
					String zjManage = "";
					//代理回水比例
					String proxyHsRate = "";
					//代理回保比例
					String proxyHbRate = "";
					//代理服务费
					String proxyFWF = "";
					//积分比例
					String jfPercent = "";
					for(int i=1;i<11;i++){
						Cell cell = row.getCell(i);
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						String value = cell.getStringCellValue();
						if(i==1){
							//团ID====="+value);
							if(!StringUtil.isBlank(value))
								value = value.toUpperCase();
							teamId = value;
						}else if(i==2){
							//团队名字====="+value);
							teamName = StringUtil.isBlank(value) ? "" : value;
						}else if(i==3){
							if(!StringUtil.isBlank(value) && value.contains("E")) {
								value = Double.valueOf(value)+"";
							}
							//回水比例====="+value);
							if(value.contains("%")) {
								value = NumUtil.getNumByPercent(value)+"";
							}
							huishuiRate = value;
						}else if(i==4){
							//保险比例====="+value);
							if(value.contains("%")) {
								value = NumUtil.getNumByPercent(value)+"";
							}
							insuranceRate = StringUtil.isBlank(value) ? "0" : NumUtil.digit4(value);
						}else if(i==5){
							//股东====="+value);
							gudong = value;
						}else if(i==6){
							//战绩是否代理管====="+value);
							zjManage = StringUtil.isBlank(value) ? "否" : value;
						}else if(i==7){
							//备注====="+value);
							beizhu = value;
						}else if(i==8) {
							if(StringUtil.isBlank(value)) {
								value = "0";
							}
							if(value.contains("%")) {
								proxyHsRate = value;
							}else {
								//回水比例====="+NumUtil.getPercentStr(Double.valueOf(value)));
								proxyHsRate = NumUtil.getPercentStr(Double.valueOf(value));
							}
						}else if(i==9) {
							if(StringUtil.isBlank(value)) {
								value = "0";
							}
							if(value.contains("%")) {
								proxyHbRate = value;
							}else {
								//回保比例====="+NumUtil.getPercentStr(Double.valueOf(value)));
								proxyHbRate = NumUtil.getPercentStr(Double.valueOf(value));
							}
						}else if(i==10) {
							//服务费====="+value);
							proxyFWF = value;
						}else if(i==11) {
							//积分比例====="+value);
							if(StringUtil.isBlank(value)) {
								value = "0";
							}
							jfPercent = NumUtil.getPercentStr(Double.valueOf(value));
						}
					}
					if(!StringUtil.isBlank(teamId))
						huishuiMap.put(teamId, new Huishui(teamId,teamName,huishuiRate,insuranceRate,gudong,zjManage,beizhu,proxyHsRate,proxyHbRate,proxyFWF,jfPercent));
				}
			}
			is.close();
			wrap = new Wrap(true,huishuiMap);
			log.info("读取团队回水结束!size:"+huishuiMap.size());
		} catch (Exception e) {
			wrap = new Wrap();
			e.printStackTrace();
		}
		return wrap;
	}
	
	/**
	 * 导入战绩Excel
	 * 
	 * 两个功能：
	 * 	A:导入到场次信息
	 * 	B:导入到联盟Tab
	 * 
	 * @param file 文件夹
	 * @return
	 */
	public static Wrap readZJRecord(File file,String userClubId,String LMType){//新增了联盟类型
		List<UserInfos> userInfoList = new LinkedList<>();
		UserInfos info = null;
		FileInputStream is = null;
		boolean isHasJudged = false;//是否已经判断过为空 波哥要求添加
		try{
			//桌号
			String name = file.getName();
			String tableId = name.substring(name.lastIndexOf("-")+1,name.lastIndexOf(".")); 
			tableId="第"+tableId+"局";
			if( StringUtil.isBlank(userClubId) || DataConstans.Index_Table_Id_Map.containsValue(tableId)){
				return new Wrap(false,"该战绩表("+tableId+"场次)已经导过");
			}
			log.info("开始----------导入战绩Excel");
			
			userInfoList = new LinkedList<>();
			is = new FileInputStream(file);
			
			//获取excel sheet
			Workbook workbook = getWeebWork(file.getAbsolutePath());
			Sheet sheet = workbook.getSheetAt(0);
			
			int rowNum = sheet.getLastRowNum();//若无数据，要提示
			if(rowNum == 0 && !isHasJudged) {
				isHasJudged = true;
				ShowUtil.show("提示：总手数为0！");
			}
			
			
			//开始遍历Excel行数据
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) {
				
				Row row = rowIterator.next();
				
				//add 总手数为空的提示
				Cell ZSScell = row.getCell(6);
				if(ZSScell == null){break;}
				String totalHandCount = ZSScell.toString();//总手数
				Integer _tempCount = 0;
				if(!"总手数".equals(totalHandCount)) {
					try {
						_tempCount = Integer.valueOf(totalHandCount);
						if( _tempCount == 0) {
							if(!isHasJudged) {
								isHasJudged = true;
								ShowUtil.show("提示：总手数为0！");
							}
						}
					}catch(Exception e) { _tempCount = 1;}
				}
				
				info = new UserInfos();
				//排除第一行以及俱乐部ID不匹配的情况
				String clubID = row.getCell(9).toString();
				if(row.getRowNum()!=0  && userClubId.equals(row.getCell(9).toString())){	
					//对于符合条件的进行存储
					int[] clumns = new int[]{6,7,8,9,10,15,17,18,19};
					
					for(int cn : clumns){
						Cell cell = row.getCell(cn);
						
						if(cell==null){
							log.error("出现空值，导入战绩文件夹失败" + "\t");
							return new Wrap();
						}
						cell.setCellType(HSSFCell.CELL_TYPE_STRING);
						String value = cell.getStringCellValue();
						value = StringUtil.isBlank(value) ? "" : value.trim();
						switch(cn){
						case 6: //add 总手数（只判断不导入）
							if((StringUtil.isBlank(value) || "0".equals(value)) && !isHasJudged) {
								isHasJudged = true;
								ShowUtil.show("提示：总手数为0！");
							}
						break;
						case 7: info.setPlayerId(value);
						break;
						case 8: info.setPlayerName(value);
						break;
						case 9: info.setClubId(value);
						break;
						case 10: info.setClubName(value);
						break;
						case 15: info.setInsurance(value);
						break;
						case 18: info.setZj(value);
						break;
						case 19: {
							String dateStr = value.split(" ")[0];
							info.setDay(value);
							//add 导入的第一局作为当天的时间
							if(StringUtil.isBlank(DataConstans.Date_Str)) {
								DataConstans.Date_Str = dateStr;
							}else {
								try {
									if(sdf.parse(dateStr).before(sdf.parse(DataConstans.Date_Str))) {
										DataConstans.Date_Str = dateStr;
									}
								} catch (Exception e) {
									
								}
							}
						break;}
						default:break;
						}
					}
					info.setTableId(tableId);
					userInfoList.add(info);
				}
			}//遍历excel结束
			

			//存储数据  {场次=infoList...}
			DataConstans.zjMap.put(tableId, userInfoList);
			is.close();
			
			//add 添加所有记录到联盟对帐表
			LMController.currentRecordList = LM_ExcelReaderUtil.readRecord(file);
			//LMController.refreshClubList();//放到锁定时去添加
			//LMController.checkOverEdu();
			
            log.info("结束----------导入战绩Excel"+" ===size:"+ userInfoList.size());
        } catch (Exception e) {
            e.printStackTrace();
            return new Wrap();
        } 

		return new Wrap(true,userInfoList);
	}
	
	
	
	
	
	/**
	 * 导入昨日留底Excel
	 * @throws IOException,Exception 
	 */
	public static Map<String,String> readPreDataRecord(File file) throws IOException,Exception{
		
		//昨日留底总数据结构
		Map<String,String> preDataMap = new HashMap<>();
		
		FileInputStream is = null;
			
		if(!file.exists() || !file.isFile() ){
			return new HashMap<>();
		}
		log.info("开始----------导入昨日留底Excel");
		
		is = new FileInputStream(file);
		
		//获取excel sheet
		Workbook workbook = getWeebWork(file.getAbsolutePath());
		Sheet sheet = workbook.getSheetAt(0);
		
		/**开始遍历Excel各范围数据**/
		//资金
		Map<String,String> zijinMap = getMapByPosition(sheet,3,3,9);
		//实时开销(可以实时金额那里拿)
		Map<String,String> presentPayoutMap = getMapByPosition(sheet,13,3,40);//不知够不够
		//实时金额(单独开来)
//		Map<String,String> presentMoneyMap = getMapByPosition(sheet,3,6,1500);//不知1000够不够
		Map<String,String> presentMoneyMap = getMapByPosition_SSJE(sheet,3,6,1500);
		
		//昨日利润
		Map<String,String> yesterdayProfitMap = getMapByPosition(sheet,3,11,9);
		//联盟对帐
		Map<String,String> LMMap = getMapByPosition(sheet,15,11,16);
		
		/**缓存到昨日留底总Map中**/
		preDataMap.put("资金", JSON.toJSONString(zijinMap));
		preDataMap.put("实时开销", JSON.toJSONString(presentPayoutMap));
		preDataMap.put("实时金额", JSON.toJSONString(presentMoneyMap));
		preDataMap.put("昨日利润", JSON.toJSONString(yesterdayProfitMap));//注意，不是整数LM
		preDataMap.put("联盟对帐", JSON.toJSONString(LMMap));//
		
		is.close();
        log.info("结束----------导入昨日留底Excel"+" ==size:"+preDataMap.size());
		return preDataMap;
	}
	
	
	@SuppressWarnings("unused")
	private static boolean isDuplicate = false;
	/**
	 * 根据位置获取相应领域的Map值
	 * @param sheet 昨日留底的电子表
	 * @param x 起始行（都0开始）
	 * @param y 超始列
	 * @param x2 结束行 
	 * @throws Exception 
	 * 
	 */
	private static Map<String,String> getMapByPosition(Sheet sheet,int x,int y,int x2) throws Exception{
		Map<String,String> resultMap = new LinkedHashMap<>();
		String key = "";
		for(int i=x; i<=x2; i++){
			Row row = sheet.getRow(i);
			if(row == null){//针对实时金额Map有读取到row为空的情况，这里是直接退出循环，不影响结果。后面要分析。
				break;
			}
			Cell keyCell = row.getCell(y);
			if(keyCell == null) {
				continue;
			}
			key = keyCell.toString();
			Cell valueCell = row.getCell(y+1);
			if(valueCell ==  null) {
				if(resultMap.get(key) != null ) {
					log.error("重复==================================="+key);
					throw new Exception("重复项："+key+",请检查！");
				}
				resultMap.put(key, "0");
				continue;
			}
		
			valueCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			//添加
			if(!StringUtil.isBlank(key) && !StringUtil.isBlank(key.trim())){
				if(key.endsWith(".0")) {//过滤诸如123被读成123.0的情况
					key = key.substring(0, key.lastIndexOf("."));
				}
//				if(key.endsWith("'")) {//过滤诸如123被读成123.0的情况
//					key = key.substring(0, key.lastIndexOf("'"));
//				}
				if(resultMap.get(key) != null ) {
					log.error("重复==================================="+key);
					throw new Exception("重复项："+key+",请检查！");
				}
				resultMap.put(key, valueCell.getStringCellValue());
			}
		}
		return resultMap;
	}
	
	
	
	/**
	 * 根据位置获取相应领域的Map值(实时金额)
	 * @param sheet 昨日留底的电子表
	 * @param x 起始行（都0开始）
	 * @param y 超始列
	 * @param x2 结束行 
	 * @throws Exception 
	 * 
	 */
	private static Map<String,String> getMapByPosition_SSJE(Sheet sheet,int x,int y,int x2) throws Exception{
		Map<String,String> resultMap = new LinkedHashMap<>();
		String key = "";
		String playerId = "";
		for(int i=x; i<=x2; i++){
			Row row = sheet.getRow(i);
			if(row == null){//针对实时金额Map有读取到row为空的情况，这里是直接退出循环，不影响结果。后面要分析。
				break;
			}
			Cell keyCell = row.getCell(y);
			if(keyCell == null) {
				continue;
			}
			key = keyCell.toString();
			Cell valueCell = row.getCell(y+1);
			if(valueCell ==  null) {
				if(resultMap.get(key) != null ) {
					log.error("重复==================================="+key);
					throw new Exception("重复项："+key+",请检查！");
				}
				resultMap.put(key, "0");
				continue;
			}
			valueCell.setCellType(HSSFCell.CELL_TYPE_STRING);
			
			Cell idCell = row.getCell(y+3);//ID列，即第J列
			if(idCell == null) {
				playerId = "";
			}else {
				idCell.setCellType(HSSFCell.CELL_TYPE_STRING);
				playerId = idCell.getStringCellValue();
			}
			//添加
			if(!StringUtil.isBlank(key) && !StringUtil.isBlank(key.trim())){
				if(key.endsWith(".0")) {//过滤诸如123被读成123.0的情况
					key = key.substring(0, key.lastIndexOf("."));
				}
//				if(key.endsWith("'")) {//过滤诸如123被读成123.0的情况
//					key = key.substring(0, key.lastIndexOf("'"));
//				}
				if(resultMap.get(key) != null ) {
					log.error("重复==================================="+key);
					throw new Exception("重复项："+key+",请检查！");
				}
				if(!StringUtil.isBlank(playerId)) {
					key = key + "###" + playerId;
				}
				resultMap.put(key, valueCell.getStringCellValue());
			}
		}
		return resultMap;
	}
	
	
	/**
	 * 导入合并ID模板Excel
	 * @throws IOException,Exception 
	 */
	public static Map<String,String> readCombineIdRecord(File file) throws IOException,Exception{
		
		//昨日留底总数据结构
		Map<String,String> combineIdMap = new LinkedHashMap<>();
		
		FileInputStream is = null;
			
		if(!file.exists() || !file.isFile() ){
			return new HashMap<>();
		}
		log.info("开始----------导入合并ID模板Excel");
		
		is = new FileInputStream(file);
		
		//获取excel sheet
		Workbook workbook = getWeebWork(file.getAbsolutePath());
		Sheet sheet = workbook.getSheetAt(0);
		
		//检验是否合并ID模板
		Row firstRow = sheet.getRow(0);
		if(firstRow == null || firstRow.getCell(0) == null 
				|| !"父ID".equals(firstRow.getCell(0).getStringCellValue())) {
			return combineIdMap;
		}
		
		/**遍历Excel开始**/
		Row row ;
		for(int i=1; i< 1000; i++){
			
			row = sheet.getRow(i);
			if(row == null) break;
			
			
			if(row.getCell(0) == null || StringUtil.isBlank(row.getCell(0).toString()) 
					|| row.getCell(1) == null || StringUtil.isBlank(row.getCell(1).toString()))
				new Exception("父ID或子ID列表不能为空，请检查！");
			
			row.getCell(0).setCellType(HSSFCell.CELL_TYPE_STRING);
			row.getCell(1).setCellType(HSSFCell.CELL_TYPE_STRING);
			combineIdMap.put(row.getCell(0).toString(), row.getCell(1).toString());
		}
		
		/**遍历Excel结束**/
		
		is.close();
        log.info("结束----------导入合并ID模板Excel"+" ==size:"+combineIdMap.size());
		return combineIdMap;
	}
	
	

}
