package com.kendy.util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * 剪切板工具类
 * 功能：用于复制按钮到QQ窗口
 * 
 * @author 林泽涛
 * @time 2017年11月25日 下午1:21:24
 */
public class ClipBoardUtil {

	public static void main(String[] args) throws Exception {
		String path = "D:/1.png";
		BufferedImage buf = ImageIO.read(new File(path));
		
		setClipboardImage((Image)buf);
		
//		setSysClipboardText("复制的内容");
		
		System.out.println("finishes...");

	
   }
	
	/**  
	 * 复制图片到剪切板。  
	 */    
	public static void setClipboardImage(final Image image) {    
	    Transferable trans = new Transferable() {    
	        public DataFlavor[] getTransferDataFlavors() {    
	            return new DataFlavor[] { DataFlavor.imageFlavor };    
	        }    
	  
	        public boolean isDataFlavorSupported(DataFlavor flavor) {    
	            return DataFlavor.imageFlavor.equals(flavor);    
	        }    
	  
	        public Object getTransferData(DataFlavor flavor)    
	                throws UnsupportedFlavorException, IOException {    
	            if (isDataFlavorSupported(flavor))    
	                return image;    
	            throw new UnsupportedFlavorException(flavor);    
	        }    
	  
	    };    
	    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(trans, null);    
	}    

	
	
	
   /** 
    * 将字符串复制到剪切板。 
    */  
   public static void setSysClipboardText(String writeMe) {  
       Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();  
       Transferable tText = new StringSelection(writeMe);  
       clip.setContents(tText, null);  
   } 
	


}
