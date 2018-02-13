package application;
	
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.kendy.util.ErrorUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 程序主窗口
 * 
 * @author 林泽涛
 * @time 2017年10月21日 下午10:01:39
 */
public class Main extends Application {
	
	private static Logger log = Logger.getLogger(Main.class);
	static {
		try {
			////生产环境可用
			String logName = "log4j.properties";
			PropertyConfigurator.configure(
					Main.class.getClassLoader().getResourceAsStream(logName));
			log.info("日志组件初始化成功");
		} catch (Exception e) {
			ErrorUtil.err("日志组件初始化失败");
		}
	}
	
	
	//共用窗口（kendy
	public static Stage primaryStage0;
	
	public static MyController myController;
	public static FXMLLoader _fxmlLoader;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			//检查是否需要注册
//			String mac = PropertiesUtil.readProperty("mac");
//			String mac2 = IPUtil.getLocalMac();
//			if(StringUtil.isBlank(mac)) {
//
//				
//				//发送验证码到陈老板的 邮箱
//				new Thread(new Runnable() {
//						@Override
//						public void run() {
//							log.info("启动线程发送邮箱注册码...");
//							try {
//							Email email = new SimpleEmail();
//							email.setHostName("smtp.163.com");
//							email.setSmtpPort(465);
//							email.setAuthenticator(new DefaultAuthenticator("a1260466457@163.com", "greatkendy123"));
//							email.setSSLOnConnect(true);
//							email.setFrom("a1260466457@163.com");
//							email.setSubject(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "财务软件注册码");
//							email.setMsg(mac2);
//							email.addTo("352408694@qq.com");
//							// email.addTo("1260466457@qq.com");
//							email.send();
//								log.info("apache邮箱发送成功！");
//								ShowUtil.show("注册码已经发到银河ATM的邮箱!",10);
//							} catch (Exception e) {
//								ErrorUtil.err("邮箱注册码发送失败", e);
//							}
//						}
//					}
//				).start();
//				
//				//注册界面
//				TextInputDialog dialog = new TextInputDialog("");
//				dialog.setTitle("注册");
//				dialog.setHeaderText(null);
//				dialog.setContentText("请输入注册码:");
//				Optional<String> result = dialog.showAndWait();
//				dialog.setOnCloseRequest(e -> {exit();});
////				dialog.getDialogPane().getButtonTypes().removeAll(ButtonType.CANCEL);
//				
//				if (result.isPresent()){
//				    if(!mac2.equals(result.get())) {
//				    	ErrorUtil.err("注册码不正确！ 软件即将退出！");
//				    	dialog.setContentText("");
//				    	exit();
//				    }else {
//				    	//6C-4B-90-1C-F0-2F  00-50-56-C0-00-08
//				    	ShowUtil.show("恭喜注册成功!",2);
//				    	PropertiesUtil.writeProperty("mac", mac2);
//				    }
//				}
//			}else {
//				if(!mac.equals(mac2)) {
//					ErrorUtil.err("检测到一个注册码被多台电脑使用，抱歉，软件不允许使用！");
//					exit();
//				}
//			}
//			if(StringUtil.isBlank(PropertiesUtil.readProperty("mac"))) {
//				exit();
//			}
			
			
			
//			Parent root = FXMLLoader.load(getClass().getResource("MainStage4.fxml"));

			FXMLLoader fxmlLoader = new FXMLLoader();
			Parent root = fxmlLoader.load(getClass().getResource("MainStageeess3.fxml").openStream());
//			Pane p = fxmlLoader.load(getClass().getResource("MainStage4.fxml").openStream());
//			Parent root = p.getParent()
			MyController mc = (MyController) fxmlLoader.getController();
			
			
			
//			try {
//				primaryStage.getIcons().add(new Image("file:resource/images/icon.png"));
//			} catch (Exception e) {
//				log.error("找不到icon图标！");
//				e.printStackTrace();
//			}
			primaryStage.setTitle(Constants.TITLE+Constants.VERSION);
            primaryStage.setScene(new Scene(root));
            //primaryStage.setResizable(false); 
            primaryStage.show();
            
            
            primaryStage0 = primaryStage;//add by kendy
            myController = mc;
            _fxmlLoader = fxmlLoader;
            
            primaryStage.setOnCloseRequest(e -> {
//        		Alert alert = new Alert(AlertType.CONFIRMATION);
//        		alert.setTitle("提示");
//        		alert.setHeaderText(null);
//        		alert.setContentText("\r\n确定退出吗?");
//        		Optional<ButtonType> result = alert.showAndWait();
//        		if (result.get() == ButtonType.OK){
//        			exit();
//        		}else {
//        			return;
//        		}
        		exit();
            });
               
            
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public  void exit(){
		log.info("====================即将关闭所有程序！");
		System.exit(0);
	}
	

}