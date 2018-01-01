package com.kendy.excel;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.hssf.util.HSSFColor;

/**
 * 导出Excel公共方法
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:52:14
 */
public class ExportExcel  {
	
	private static Logger log = Logger.getLogger(ExportExcel.class);
    
//    //显示的导出表的标题
    private String title;
    //导出表的列名
    private String[] rowName ;
    
    //导出表的列名
    private String[] rowName2 ;
    
    private String out;
    private String teamId;
    private String time;
    private boolean isManage;
    
    private List<Object[]>  dataList = new ArrayList<Object[]>();
    
    private List<Object[]>  dataList2 = new ArrayList<Object[]>();
    private String YES = "√";
    
    private String NO = "✘";
    
    
    //构造方法，传入要导出的数据
    public ExportExcel(String teamId,String time, boolean isManage, String title, 
    		String[] rowName,List<Object[]>  dataList,String out,String[] rowName2,List<Object[]>  dataList2){
    	this.teamId = teamId;
    	this.time = time;
    	this.isManage = isManage;
    	this.title = title;
        this.dataList = dataList;
        this.rowName = rowName;
        this.rowName2 = rowName2;
        this.dataList2 = dataList2;
        this.out = out;
    }
            
    /*
     * 导出数据
     * */
    public void export() throws Exception{
        try{
            HSSFWorkbook workbook = new HSSFWorkbook();                        // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(title);                     // 创建工作表
            
            // 产生表格标题行
            HSSFRow rowm = sheet.createRow(0);
            HSSFCell cellTiltle = rowm.createCell(0);
            
//            sheet样式定义【getColumnTopStyle()/getStyle()均为自定义方法 - 在下面  - 可扩展
            HSSFCellStyle columnTopStyle = this.getColumnTopStyle(workbook);//获取列头样式对象
            HSSFCellStyle style = this.getStyle(workbook);                    //单元格样式对象
            
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (rowName.length-1)));  
            cellTiltle.setCellStyle(columnTopStyle);
            cellTiltle.setCellValue(title);
            /*************************************  正文  ****************************/
            //时间：
            HSSFRow timeRow = sheet.createRow(1);
            HSSFCell timeLabel = timeRow.createCell(0);
            cellTiltle.setCellStyle(style);
            timeLabel.setCellValue("时间："+time);
            //团队ID
            HSSFRow teamRow = sheet.createRow(2);
            HSSFCell teamLabel = teamRow.createCell(0);
            cellTiltle.setCellStyle(style);
            teamLabel.setCellValue("团队ID："+teamId);
            HSSFCell isManagedLabel = teamRow.createCell(1);
            cellTiltle.setCellStyle(style);
            isManagedLabel.setCellValue("战绩是否代管理："+ (isManage ? YES : NO));
            
            
            // 定义所需列数
            int columnNum = rowName.length;
            HSSFRow rowRowNames = sheet.createRow(3);                // 在索引2的位置创建行(最顶端的行开始的第二行)
            HSSFRow rowRowName = sheet.createRow(4);                // 在索引2的位置创建行(最顶端的行开始的第二行)
            
            /*************************************  标题栏  ****************************/
            for(int n=0;n<columnNum;n++){
                HSSFCell  cellRowName = rowRowName.createCell(n);                //创建列头对应个数的单元格
                cellRowName.setCellType(HSSFCell.CELL_TYPE_STRING);                //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(rowName[n]);
                cellRowName.setCellValue(text);                                    //设置列头单元格的值
                cellRowName.setCellStyle(columnTopStyle);                        //设置列头单元格样式
            }
            
            
            //将查询出的数据设置到sheet对应的单元格中
            for(int i=0;i<dataList.size();i++){
                
                Object[] obj = dataList.get(i);//遍历每个对象
                HSSFRow row = sheet.createRow(i+5);//创建所需的行数
                
                for(int j=0; j<obj.length; j++){
                    HSSFCell  cell = null;   //设置单元格的数据类型
                    cell = row.createCell(j,HSSFCell.CELL_TYPE_STRING);
                    if(!"".equals(obj[j]) && obj[j] != null){
                        cell.setCellValue(obj[j].toString());                        //设置单元格的值
                    }
                    cell.setCellStyle(style);                                    //设置单元格样式
                }
            }
            
            /******************** 添加合计 ********/
            HSSFRow rr = sheet.getRow(1);
            HSSFCell  cellHeji = rr.createCell(rowName.length+1,HSSFCell.CELL_TYPE_STRING);                //创建列头对应个数的单元格
        	HSSFRichTextString cellHejiVal = new HSSFRichTextString(rowName2[0]);
        	cellHeji.setCellValue(cellHejiVal);                                    //设置列头单元格的值
        	cellHeji.setCellStyle(columnTopStyle); 
        	HSSFCell  cellSum = rr.createCell(rowName.length+2,HSSFCell.CELL_TYPE_STRING);                //创建列头对应个数的单元格
        	HSSFRichTextString sumVal = new HSSFRichTextString(rowName2[1]);
        	cellSum.setCellValue(sumVal);                                    //设置列头单元格的值
        	cellSum.setCellStyle(columnTopStyle); 
        	
            for(int i=0;i<dataList2.size();i++){
                 
            	 HSSFRow r = sheet.getRow(i+2);
            	 if(r == null) {
            		 log.error("excel行为空");
            		 continue;
            	 }
                 HSSFCell type = r.createCell(rowName.length+1,HSSFCell.CELL_TYPE_STRING);
                 type.setCellStyle(style);
                 type.setCellValue(dataList2.get(i)[0].toString());
                 
                 HSSFCell sum = r.createCell(rowName.length+2,HSSFCell.CELL_TYPE_STRING);
                 sum.setCellStyle(style);
                 sum.setCellValue(dataList2.get(i)[1].toString());
            }
            
            sheet.setColumnWidth(rowName.length+1, 3500);
            sheet.setColumnWidth(rowName.length+2, 3500);
            /****************************/
            
            
            //让列宽随着导出的列长自动适应
            for (int colNum = 0; colNum < columnNum; colNum++) {
                int columnWidth = sheet.getColumnWidth(colNum) / 256;
                for (int rowNum = 0; rowNum <= sheet.getLastRowNum(); rowNum++) {
                    HSSFRow currentRow;
                    //当前行未被使用过
                    if (sheet.getRow(rowNum) == null) {
                        currentRow = sheet.createRow(rowNum);
                    } else {
                        currentRow = sheet.getRow(rowNum);
                        currentRow.setHeight((short)400);
                    }
                    if (currentRow.getCell(colNum) != null) {
                        HSSFCell currentCell = currentRow.getCell(colNum);
                        if (currentCell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                            int length = currentCell.getStringCellValue().getBytes().length;
                            if (columnWidth < length) {
                                columnWidth = length;
                            }
                        }
                    }
                }
                if(colNum == 0){
                    sheet.setColumnWidth(colNum, (columnWidth-2) * 256);
                }else{
                    sheet.setColumnWidth(colNum, (columnWidth+4) * 256);
                }
            }
            
            if(workbook !=null){
            	OutputStream out = null;
                try{
                	File file = new File(this.out+".xls");
                    out = new FileOutputStream(file);
                    workbook.write(out);
                    java.awt.Desktop.getDesktop().open(file);
                }catch (IOException e){
                    e.printStackTrace();
                }finally {
                	if(out != null) out.close();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    /* 
     * 列头单元格样式
     */    
      public HSSFCellStyle getColumnTopStyle(HSSFWorkbook workbook) {
          
            // 设置字体
          HSSFFont font = workbook.createFont();
          //设置字体大小
          font.setFontHeightInPoints((short)11);
          //字体加粗
          font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
          //设置字体名字 
//          font.setFontName("Courier New");
          //设置样式; 
          HSSFCellStyle style = workbook.createCellStyle();
          //设置底边框; 
          style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
          //设置底边框颜色;  
          style.setBottomBorderColor(HSSFColor.BLACK.index);
          //设置左边框;   
          style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
          //设置左边框颜色; 
          style.setLeftBorderColor(HSSFColor.BLACK.index);
          //设置右边框; 
          style.setBorderRight(HSSFCellStyle.BORDER_THIN);
          //设置右边框颜色; 
          style.setRightBorderColor(HSSFColor.BLACK.index);
          //设置顶边框; 
          style.setBorderTop(HSSFCellStyle.BORDER_THIN);
          //设置顶边框颜色;  
          style.setTopBorderColor(HSSFColor.BLACK.index);
          //在样式用应用设置的字体;  
          style.setFont(font);
          //设置自动换行; 
          style.setWrapText(false);
          //设置水平对齐的样式为居中对齐;  
          style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
          //设置垂直对齐的样式为居中对齐; 
          style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
          
          return style;
          
      }
      
      /*  
     * 列数据信息单元格样式
     */  
      public HSSFCellStyle getStyle(HSSFWorkbook workbook) {
            // 设置字体
            HSSFFont font = workbook.createFont();
            //设置字体大小
            //font.setFontHeightInPoints((short)10);
            //字体加粗
            //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            //设置字体名字 
//            font.setFontName("Courier New");
            //设置样式; 
            HSSFCellStyle style = workbook.createCellStyle();
            //设置底边框; 
            style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
            //设置底边框颜色;  
            style.setBottomBorderColor(HSSFColor.BLACK.index);
            //设置左边框;   
            style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
            //设置左边框颜色; 
            style.setLeftBorderColor(HSSFColor.BLACK.index);
            //设置右边框; 
            style.setBorderRight(HSSFCellStyle.BORDER_THIN);
            //设置右边框颜色; 
            style.setRightBorderColor(HSSFColor.BLACK.index);
            //设置顶边框; 
            style.setBorderTop(HSSFCellStyle.BORDER_THIN);
            //设置顶边框颜色;  
            style.setTopBorderColor(HSSFColor.BLACK.index);
            //在样式用应用设置的字体;  
            style.setFont(font);
            //设置自动换行; 
            style.setWrapText(false);
            //设置水平对齐的样式为居中对齐;  
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            //设置垂直对齐的样式为居中对齐; 
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            return style;
      }
      
      
      public static void main(String[] args) throws Exception {
//          String title = Message.getString("manifestIExportTitle");
    	  SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
    	  String teamId = "KK";
    	  String title = teamId + "-"+sdf.format(new Date());
    	  System.out.println(title);
    	  
	      String[] rowsName = new String[]{"玩家ID","玩家名称","原始战绩","战绩","保险","回水","回保","场次"};
	      List<Object[]>  dataList = new ArrayList<Object[]>();
	      Object[] objs = null;
//	      for (int i = 0; i < manifestIMainList.size(); i++) {
//	          ManifestIMain man = manifestIMainList.get(i);
//	          objs = new Object[rowsName.length];
//	          objs[0] = i;
//	          objs[1] = man.getTranNo();
//	          objs[2] = man.getBillNo();
//	          objs[3] = man.getStatusFlagCnName();
//	          objs[4] = man.getLoginName();
//	          SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	          String date = df.format(man.getModiDate());
//	          objs[5] = date;
//	          dataList.add(objs);
//	      }
	      //时间
	      //团队ID 是否代管理
	      //总和信息
	      
	      
//	      
//	      String out = "D:/"+title+System.currentTimeMillis();
//	      ExportExcel ex = new ExportExcel(title,rowsName, dataList,out);
//	      ex.export();
	      System.out.println("finises..");
      }
}