package com.kendy.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

/**
 * 自定义的输入对话框
 * 
 * @author 林泽涛
 * @time 2017年11月19日 上午12:10:35
 */
public class InputDialog {
	
	private Dialog<Pair<String, String>> dialog;
	private TextInputDialog textDialog;
	
	
	
	/**
	 * 
	 */
	public InputDialog() {
		super();
		// TODO Auto-generated constructor stub
	}
	/**
	 * 两个输入文字的对话框
	 * 
	 * @param keyText1
	 * @param keyText2
	 */
	public InputDialog(String title,String keyText1,String keyText2) {
		
		this.dialog = new Dialog<>();
    	dialog.setTitle(title);
    	dialog.setHeaderText(null);
    	
    	// Set the button types.
    	ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
    	dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

    	// Create the username and password labels and fields.
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 15, 10, 10));

    	TextField teamId = new TextField();
    	teamId.setPromptText("");
    	TextField hsRate = new TextField();
    	hsRate.setPromptText("");

    	grid.add(new Label(keyText1), 0, 0);
    	grid.add(teamId, 1, 0);
    	grid.add(new Label(keyText2), 0, 1);
    	grid.add(hsRate, 1, 1);

    	// Enable/Disable login button depending on whether a username was entered.
    	Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);

    	dialog.getDialogPane().setContent(grid);

    	// Request focus on the username field by default.
    	Platform.runLater(() -> teamId.requestFocus());

    	// Convert the result to a username-password-pair when the login button is clicked.
    	dialog.setResultConverter(dialogButton -> {
    	    if (dialogButton == loginButtonType) {
    	        return new Pair<>(teamId.getText(), hsRate.getText());
    	    }
    	    return null;
    	});
	}
	
	
	private Dialog<Map<String, String>> multyDialog;
	public void InputMultyDialog(String title,List<String> paramList) {
		
		this.multyDialog = new Dialog<>();
		multyDialog.setTitle(title);
		multyDialog.setHeaderText(null);
    	
    	// Set the button types.
//    	ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
    	ButtonType loginButtonType = new ButtonType("确定", ButtonData.APPLY);
    	
    	
    	multyDialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

    	// Create the username and password labels and fields.
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 15, 10, 10));

    	TextField teamId = new TextField();
    	teamId.setPromptText("");
//    	TextField hsRate = new TextField();
//    	hsRate.setPromptText("");
//
//    	grid.add(new Label(keyText1), 0, 0);
//    	grid.add(teamId, 1, 0);
//    	grid.add(new Label(keyText2), 0, 1);
//    	grid.add(hsRate, 1, 1);

    	int size = paramList.size();
    	TextField[] textFieldArr = new TextField[size];
		for(int row=0 ; row< size ; row++) {
			String leftName = paramList.get(row);
			textFieldArr[row] = new TextField();
			textFieldArr[row].setPromptText("");
        	grid.add(new Label(leftName), 0, row);
        	grid.add(textFieldArr[row], 1, row);
    	}
    	
    	

    	// Enable/Disable login button depending on whether a username was entered.
    	Node loginButton = multyDialog.getDialogPane().lookupButton(loginButtonType);

    	multyDialog.getDialogPane().setContent(grid);

    	// Request focus on the username field by default.
    	Platform.runLater(() -> teamId.requestFocus());

    	// Convert the result to a username-password-pair when the login button is clicked.
    	multyDialog.setResultConverter(dialogButton -> {
    		Map<String,String> map = new HashMap<>();
    	    if (dialogButton == loginButtonType) {
    			for(int row=0 ; row< size ; row++) {
    				String leftName = paramList.get(row);
    				String val = textFieldArr[row].getText();
    				map.put(leftName, val);
    			}
    	    }
    	    return map;
    	});
	}
	
	/**
	 * 适用于修改窗口
	 * 
	 * @time 2017年12月19日
	 * @param title
	 * @param paramMap 参数Map,最好是linkedHashMap
	 */
	public void InputMultyDialog(String title,Map<String,String> paramMap) {
		
		this.multyDialog = new Dialog<>();
		multyDialog.setTitle(title);
		multyDialog.setHeaderText(null);
    	
    	// Set the button types.
    	ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
    	
    	multyDialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

    	// Create the username and password labels and fields.
    	GridPane grid = new GridPane();
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(20, 15, 10, 10));

    	TextField teamId = new TextField();
    	teamId.setPromptText("");
//    	TextField hsRate = new TextField();
//    	hsRate.setPromptText("");
//
//    	grid.add(new Label(keyText1), 0, 0);
//    	grid.add(teamId, 1, 0);
//    	grid.add(new Label(keyText2), 0, 1);
//    	grid.add(hsRate, 1, 1);

    	List<String> paramList = new ArrayList<>();
    	paramMap.forEach((leftName,defaultValue) -> {
    		paramList.add(leftName);
    	});
    	
    	int size = paramMap.size();
    	TextField[] textFieldArr = new TextField[size];
		for(int row=0 ; row< size ; row++) {
			String leftName = paramList.get(row);
			textFieldArr[row] = new TextField();
			textFieldArr[row].setText(paramMap.get(leftName));
        	grid.add(new Label(leftName), 0, row);
        	grid.add(textFieldArr[row], 1, row);
    	}
    	
    	

    	// Enable/Disable login button depending on whether a username was entered.
    	Node loginButton = multyDialog.getDialogPane().lookupButton(loginButtonType);

    	multyDialog.getDialogPane().setContent(grid);

    	// Request focus on the username field by default.
    	Platform.runLater(() -> teamId.requestFocus());

    	// Convert the result to a username-password-pair when the login button is clicked.
    	multyDialog.setResultConverter(dialogButton -> {
    		Map<String,String> map = new HashMap<>();
    	    if (dialogButton == loginButtonType) {
    			for(int row=0 ; row< size ; row++) {
    				String leftName = paramList.get(row);
    				String val = textFieldArr[row].getText();
    				map.put(leftName, val);
    			}
    	    }
    	    return map;
    	});
	}
	
	/**
	 * 下拉框(后面修改和测试)
	 * 
	 * @time 2018年1月21日
	 * @param title
	 * @param paramMap
	 */
	public void selectionDialog(String title,Map<String,String> paramMap) {
		
		this.multyDialog = new Dialog<>();
		multyDialog.setTitle(title);
		multyDialog.setHeaderText(null);
		
		// Set the button types.
		ButtonType loginButtonType = new ButtonType("确定", ButtonData.OK_DONE);
		
		multyDialog.getDialogPane().getButtonTypes().addAll(loginButtonType);
		
		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 15, 10, 10));
		
		TextField teamId = new TextField();
		teamId.setPromptText("");
//    	TextField hsRate = new TextField();
//    	hsRate.setPromptText("");
//
//    	grid.add(new Label(keyText1), 0, 0);
//    	grid.add(teamId, 1, 0);
//    	grid.add(new Label(keyText2), 0, 1);
//    	grid.add(hsRate, 1, 1);
		
		List<String> paramList = new ArrayList<>();
		paramMap.forEach((leftName,defaultValue) -> {
			paramList.add(leftName);
		});
		
		int size = paramMap.size();
		TextField[] textFieldArr = new TextField[size];
		for(int row=0 ; row< size ; row++) {
			String leftName = paramList.get(row);
			textFieldArr[row] = new TextField();
			textFieldArr[row].setText(paramMap.get(leftName));
			grid.add(new Label(leftName), 0, row);
			grid.add(textFieldArr[row], 1, row);
		}
		
		
		
		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = multyDialog.getDialogPane().lookupButton(loginButtonType);
		
		multyDialog.getDialogPane().setContent(grid);
		
		// Request focus on the username field by default.
		Platform.runLater(() -> teamId.requestFocus());
		
		// Convert the result to a username-password-pair when the login button is clicked.
		multyDialog.setResultConverter(dialogButton -> {
			Map<String,String> map = new HashMap<>();
			if (dialogButton == loginButtonType) {
				for(int row=0 ; row< size ; row++) {
					String leftName = paramList.get(row);
					String val = textFieldArr[row].getText();
					map.put(leftName, val);
				}
			}
			return map;
		});
	}
	/**
	 * 一个输入文字的对话框
	 * 
	 * @param keyText
	 */
	public InputDialog(String title,String keyText) {
		
		this.textDialog = new TextInputDialog("");
		textDialog.setTitle(title);
		textDialog.setHeaderText(null);
		textDialog.setContentText(keyText);
	}
	
	public Optional<Pair<String, String>>  getResult() {
		return this.dialog.showAndWait();
	}
	
	public Optional<Map<String, String>>  getMultyResult() {
		return this.multyDialog.showAndWait();
	}
	
	public Optional<String>  getTextResult() {
		return this.textDialog.showAndWait();
	}
	
	/**
	 * 测试
	 * 
	 * @time 2017年12月19日
	 */
	public static void main() {
		
		List<String> list = Arrays.asList("A","B","C");
		InputDialog dlg = new InputDialog();
		dlg.InputMultyDialog("新增",list);
		Optional<Map<String,String>> resultMap = dlg.getMultyResult();
		System.out.println(resultMap.toString());
	}
	
	

}
