package com.kendy.excel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.kendy.entity.Club;
import com.kendy.entity.LMSumInfo;

/**
 * 
 * @author sjl
 * @time 2017年12月22日 上午12:15:40
 */
public class TestExportExcel 
{

    public static void main( String[] args ) throws IOException
    { 	
    	/*
    	 * 以下数据为测试用数据，
    	 * 实际调用时应将 'allClubSumMap'、'allClubMap'、'sumOfZF' 赋予正确数据信息
    	 */
    	//测试数据:从allClubSum.txt获取所有联盟总帐统计结果信息
    	String allClubSumString = getStringFromTXT("allClubSum.txt");
    	//测试数据:从allClub.txt获取俱乐部信息
    	String allClubString = getStringFromTXT("allClub.txt");
    	//测试数据：将字符串类型的联盟总帐统计结果转化为Map
    	Map<String, List<LMSumInfo>> allClubSumMap = JSON.parseObject(allClubSumString, new TypeReference<Map<String, List<LMSumInfo>>>() {});;  
    	//测试数据：将字符串类型的俱乐部信息结果转化为Map
    	Map<String,Club> allClubMap = JSON.parseObject(allClubString, new TypeReference<Map<String,Club>>() {});;  
    	//测试数据：合计桌费
    	Integer sumOfZF = 1488;
    	
    	//Excel Title设置
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
    	String excelTitle = "联盟总帐-" + sdf.format(new Date());
    	ExportAllLMExcel exportAllLMExcel = new ExportAllLMExcel();
    	
    	/*
    	 * 以下为多种方法调用，可根据需要定制
    	 */
    	/* 方法一：
    	 * 设置表格中玩家战绩、桌费、当天总账的排列顺序，
    	 * 由于对应模板排序固定了，
    	 * 所以exportAllLMExcel.export中有加载默认的排序顺序，
    	 * 如果修改了模板，只需要在这里set一下排序顺序即可
    	 */
    	/*Map<String, String> sortMap = new HashMap<String, String>(){{put("玩家战绩", "0");put("桌费", "1");put("当天总帐", "2");}}; 
    	exportAllLMExcel.setSortMap(sortMap);*/
    	
    	/* 方法二：
    	 * 可通过构造方法加载联盟数据，方便导出
    	 */
    	/*ExportAllLMExcel exportAllLMExcel = new ExportAllLMExcel(excelTitle,allClubSumMap,allClubMap,sumOfZF);
    	exportAllLMExcel.export();*/
    	
    	/* 方法三：
    	 * 可通过构造方法加载联盟数据以及表格联盟数据排序顺序，方便导出
    	 */
    	/*Map<String, String> sortMap = new HashMap<String, String>(){{put("玩家战绩", "0");put("桌费", "1");put("当天总帐", "2");}}; 
    	ExportAllLMExcel exportAllLMExcel = new ExportAllLMExcel(excelTitle,allClubSumMap,allClubMap,sumOfZF,sortMap);
    	exportAllLMExcel.export();*/
    	
    	/* 方法四：
    	 * 可设置生成Excel文件是否覆盖源文件，默认为true覆盖，如果设置为false且存在同一路径下存在同名文件，则生成时分秒后缀的Excel文件
    	 */
    	//exportAllLMExcel.setCoverFile(false);
    	
    	//执行导出Excel方法
    	exportAllLMExcel.export(allClubSumMap,allClubMap,sumOfZF);
    	System.out.println("finishes");
    }

	/**
	 * 测试用
	 * 从txt读取模拟数据
	 * @param pathname 文件路径
	 * @return
	 * @throws IOException
	 */
	private static String getStringFromTXT(String pathname) throws IOException {
		File filename = new File(pathname); // 要读取以上路径的txt文件
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filename)); // 建立一个输入流对象reader
		BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
		String line = "";
		String result = "";
		while ((line = br.readLine())!=null) {
			result += line; // 一次读入一行数据
		}
		return result;
	}
}
