package com.kendy.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

/**
 * JavaFx中TableView或ListView的简单工具类
 * 
 * @author 林泽涛
 * @time 2017年11月30日 下午10:28:04
 */
public class TableUtil {
	
	public static boolean isNullOrEmpty(TableView table) {
		return table== null && table.getItems() == null;
	}
	
	public static boolean isHasValue(TableView table) {
		return !isNullOrEmpty(table);
	}
	
	public static <E> ObservableList<E> getItem(TableView<E> table){
		ObservableList<E> obList = FXCollections.observableArrayList();
		if(isHasValue(table)) {
			obList = table.getItems();
		}
		return obList;
	}
	
}
