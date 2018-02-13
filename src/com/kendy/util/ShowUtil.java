package com.kendy.util;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * 展示工具类
 * 
 * @author 林泽涛
 * @time 2017年11月18日 下午11:16:28
 */
public class ShowUtil {

	private static Logger log =  Logger.getLogger(ShowUtil.class);
	/**
	 * 带有自动关闭功能的提示框
	 * 
	 * @time 2017年11月18日
	 * @param message
	 * @param time
	 */
	 public static void show(String message,int time){
		  int sleepTime = time * 1000;
	      JOptionPane op = new JOptionPane(message+"\r\n"+time+"秒后自动关闭...",JOptionPane.INFORMATION_MESSAGE);  
	      final JDialog dialog = op.createDialog("提示");  
	        
	      // 创建一个新计时器  
	      Timer timer = new Timer();  

	      // n秒 后执行该任务  
	      timer.schedule(new TimerTask() {  
	          public void run() {  
	              dialog.setVisible(false);  
	              dialog.dispose();  
	          }  
	      }, sleepTime);  

	      dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);  
	      dialog.setAlwaysOnTop(true);  
	      dialog.setModal(false);  
	      dialog.setVisible(true);  
		 
	  }
	 
	 
	 /**
	  * 错误信息提示框
	  * 
	  * @time 2017年11月18日
	  * @param message
	  */
	  public static void show(String message){
		  Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("提示");
			alert.setHeaderText(null);
			alert.setContentText("\r\n"+message);
			alert.showAndWait();
			
	  }
	  /**
	   * 警告信息
	   * 
	   * @time 2017年11月19日
	   * @param message
	   */
	  public static void warn(String message){
		  Alert alert = new Alert(AlertType.WARNING);
		  alert.setTitle("提示");
		  alert.setHeaderText(null);
		  alert.setContentText("\r\n"+message);
		  alert.showAndWait();
		  
	  }
	  
	  
}
