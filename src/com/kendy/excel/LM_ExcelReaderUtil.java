package com.kendy.excel;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kendy.entity.Record;
import com.kendy.util.ErrorUtil;
import com.kendy.util.StringUtil;

import application.DataConstans;

/**
 * 联盟对帐的Excel工具类
 * 
 * @author 小林
 * @time 2017年11月23日 下午10:41:35
 */
public class LM_ExcelReaderUtil {

	private static Logger log = Logger.getLogger(ExcelReaderUtil.class);
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 战绩导入到小工具（旧版本）
	 * 备注：此功能小胖已经砍掉了，以下300多行代码不用了，坑爹的小胖
	 * 
	 * @time 2017年11月23日
	 * @param file
	 * @return
	 */
	public static List<Record> readRecord(File file){
		List<Record> result = new ArrayList<Record>();
		try
        {
			FileInputStream is = new FileInputStream(file);
            HSSFWorkbook workbook = new HSSFWorkbook(is);
			//桌号
			String name = file.getName();
			String tableId = name.substring(name.lastIndexOf("-")+1,name.lastIndexOf(".")); 
			tableId="第"+tableId+"局";
            
            HSSFSheet sheet = workbook.getSheetAt(0);
 
            //Iterate through each rows one by one
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) 
            {
                Row row = rowIterator.next();
                if(row.getCell(0)== null) {break;}
                Record record = new Record(); 
                if(row.getRowNum()!=0){
                	int count2 = row.getLastCellNum();
                    for(int cn=0;cn<row.getLastCellNum();cn++){
                        Cell cell = row.getCell(cn);
                        record.setTableId(tableId);
                        if(cell==null){
                        	log.error("cell 为null,继续下一个循环");
                        	continue;
                        }
                        else{
                        	switch(cn){
                        	case 3: record.setBlind(cell.getStringCellValue());
                        		break;
                        	case 7: record.setPlayerId(cell.getStringCellValue());
                        		break;
                        	case 9: record.setClubId(cell.getStringCellValue());
                        		break;
                        	case 10: record.setClubName(cell.getStringCellValue());
                    			break;
                        	case 16: record.setInsurance(cell.getStringCellValue());
                        		break;
                        	case 18: record.setScore(cell.getStringCellValue());
                        		break;
                        	case 19: 
                        		String dateStr = cell.getStringCellValue().split(" ")[0];
    							//add 导入的第一局作为当天的时间
    							if(StringUtil.isBlank(DataConstans.Date_Str)) {
    								DataConstans.Date_Str = dateStr;
    							}else {
    								try {
    									if(sdf.parse(dateStr).after(sdf.parse(DataConstans.Date_Str))) {
    										dateStr = DataConstans.Date_Str;
    									}
    								} catch (Exception e) {
    									log.error("XXXXXXX  导入的第一局作为当天的时间软件失败 XXXXXX",e);
    								}
    							}
    							record.setDay(dateStr);
                        		break;
                        	}
                        }
                    }
                    //数据库 key: 时间#第次#俱乐部ID#玩家ID
                    String id = record.getDay()+"#"+record.getTableId()+"#"+record.getClubId()+"#"+record.getPlayerId();
                    record.setId(id);
                    result.add(record);
                }
            }
            is.close();
        } 
        catch (Exception e) {
        	ErrorUtil.err("当场战绩导入到联盟Tab失败",e);
        }
		return result;
	}
	
	/**
	 * 战绩导入到小工具（新版本）
	 * 
	 * @time 2018年1月10日
	 * @param file
	 * @return
	 */
	public static List<Record> readRecord_NewVersion(File file){
		List<Record> result = new ArrayList<Record>();
		try
		{
			FileInputStream is = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(is);
			//桌号
			String name = file.getName();
			String tableId = name.substring(name.lastIndexOf("-")+1,name.lastIndexOf(".")); 
			tableId="第"+tableId+"局";
			
			XSSFSheet sheet = (XSSFSheet)workbook.getSheetAt(0);
			
			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) 
			{
				Row row = rowIterator.next();
				if(row.getCell(0)== null) {break;}
				Record record = new Record(); 
				if(row.getRowNum()!=0){
					int count2 = row.getLastCellNum();
					String dateStr="";
					for(int cn=0;cn<row.getLastCellNum();cn++){
						Cell cell = row.getCell(cn);
						record.setTableId(tableId);
						if(cell==null){
							log.error("cell 为null,继续下一个循环");
							continue;
						}
						else{
							switch(cn){
							case 4: record.setBlind(cell.getStringCellValue());
							break;
							case 8: record.setPlayerId(cell.getStringCellValue());
							break;
							case 10: record.setClubId(cell.getStringCellValue());log.info("===="+cell.getStringCellValue());
							break;
							case 11: record.setClubName(cell.getStringCellValue());
							break;
							case 17: record.setInsurance(cell.getStringCellValue());
							break;
							case 19: record.setScore(cell.getStringCellValue());
							break;
							case 20: 
								dateStr = cell.getStringCellValue().split(" ")[0];
								if("结束时间".equals(dateStr)) break;
								//add 导入的第一局作为当天的时间
								if(StringUtil.isBlank(DataConstans.Date_Str)) {
									DataConstans.Date_Str = dateStr;
								}else {
									try {
										if(sdf.parse(dateStr).after(sdf.parse(DataConstans.Date_Str))) {
											dateStr = DataConstans.Date_Str;
										}
									} catch (Exception e) {
										log.error("XXXXXXX  导入的第一局作为当天的时间软件失败 XXXXXX",e);
									}
								}
								record.setDay(dateStr);
								break;
							}
						}
					}
					if("结束时间".equals(dateStr)) continue;
					//数据库 key: 时间#第次#俱乐部ID#玩家ID
					String id = record.getDay()+"#"+record.getTableId()+"#"+record.getClubId()+"#"+record.getPlayerId();
					record.setId(id);
					result.add(record);
				}
			}
			is.close();
		} 
		catch (Exception e) {
			ErrorUtil.err("当场战绩导入到联盟Tab失败",e);
		}
		return result;
	}
}
