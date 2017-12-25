package com.kendy.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JEditorPane;

import com.kendy.entity.WanjiaInfo;

/**
 * 文字转图片工具类
 * 
 * @author 林泽涛
 * @time 2017年10月7日 下午4:02:28
 */
public class Text2ImageUtil {

	public static void main(String[] args) throws Exception {
		WanjiaInfo wj = new WanjiaInfo("第120局","kendy","35","100","135","15626460750");
		String html = getHtml(wj);
		BufferedImage img = toImage(html,"utf8");
		ImageIO.write(img, "png", new File("D:/ss.png"));
		System.out.println("finishes...");
	}
	
	public static String getHtml(WanjiaInfo wj) {
		if(wj.getYicunJifen() == null) {
			wj.setYicunJifen("");
		}
		return "<style>.paiju{background-color:white;border:0px;}.td{border:1px black solid;}.table{border-collapse:collapse;}</style><body><table  width=\"400\" class=\"table\">  <tr style=\"background-color:#EEE0E5\">    <th class=\"paiju\">"+wj.getPaiju()+"</th>    <th class=\"td\">玩家</th>    <th class=\"td\">战绩</th>    <th class=\"td\">已存积分</th>    <th class=\"td\">合计</th>  </tr>  <tr style=\"background-color:white\">    <th class=\"paiju\"></th>    <th class=\"td\">"+wj.getWanjiaName()+"</th>    <th class=\"td\">"+wj.getZhangji()+"</th>    <th class=\"td\">"+wj.getYicunJifen()+"</th>    <th class=\"td\">"+wj.getHeji()+"</th>  </tr></table></body>";
	}
	
	
	/**
	 * 文本转图片自制方法：解析html文本并渲染成图片
	 * 
	 * 原理：使用Jsoup解析html文本，再用JDK自带的JEditorPane组件渲染Html，最后将其保存成图片
	 * 问题：渲染gif图时，gif图会比较大，后期待处理
	 * @author 林泽涛
	 * @param html 待渲染的Html文本
	 * @return 图片缓冲
	 * @throws Exception
	 */
	public static BufferedImage toImage(String html,String sysCode) throws Exception{
		
		//加上<html>节点使其更像一个网页源代码
		html = "<html>" + html + "</html>";
		
		//JEditorPane组件设置
	    JEditorPane editorPane = new JEditorPane();  
	    editorPane.setEditable(false);  
	    editorPane.setSize(410,100);  //750是模拟淘宝文本编辑器的宽度
	    editorPane.setContentType("text/html");//必填，它会根据这个类型去渲染html
	    editorPane.read(new ByteArrayInputStream(html.getBytes(sysCode)), "");//备注，后期看下什么原因  GBK
	    
	    //截图
		Dimension prefSize = editorPane.getSize(); //获取JEditorPane的大小
		BufferedImage img = new BufferedImage(
				prefSize.width, 
				editorPane.getPreferredSize().height, //使高度自动化
				BufferedImage.TYPE_INT_BGR);
		
		//渲染html文本到缓冲区
		Graphics graphics = img.getGraphics();
		editorPane.setSize(prefSize);
		editorPane.paint(graphics);
	    
	    return img;
	}

}
