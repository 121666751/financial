package com.kendy.util;


import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 邮件工具类
 * 
 * @author 林泽涛
 * @time 2018年1月1日 下午10:57:33
 */
public class MailUtils {

    private static String HOST = "smtp.163.com"; // smtp服务器
    private static String FROM = "a1260466457@163.com"; // 发件人地址 
    private static String TO =  "352408694@qq.com";//"1260466457@qq.com"; //收件人地址  
    private static String USER = "a1260466457@163.com"; // 用户名
    private static String PWD = "greatkendy123"; //163授权码（授权第三方客户端登陆）
    private static String SUBJECT = "财务软件"; // 邮件标题,不要要测试或test字样，不然会554被网易当成垃圾邮件，有些链接的也会
//554 DT:SPM 发送的邮件内容包含了未被许可的信息，或被系统识别为垃圾邮件。请检查是否有用户发送病毒或者垃圾邮件

    public static void send(String mac) throws Exception {

        Properties props = new Properties();

        // 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
        props.put("mail.smtp.host", HOST);
        // 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
        props.put("mail.smtp.auth", "true");

        // 用刚刚设置好的props对象构建一个session
        Session session = Session.getDefaultInstance(props);

        // 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
        session.setDebug(true);

        // 用session为参数定义消息对象
        MimeMessage message = new MimeMessage(session);
        try {
            // 加载发件人地址
            message.setFrom(new InternetAddress(FROM));
            // 加载收件人地址
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(TO));
            // 加载标题
            message.setSubject(SUBJECT);

//            message.setContent("财务软件：<h1 style = \"color:red;\">"+mac+"</h1>","text/html;charset=UTF-8");
            message.setContent("验证码："+mac,"text/html;charset=UTF-8");
            // 保存邮件
            message.saveChanges();
            // 发送邮件
            Transport transport = session.getTransport("smtp");
            // 连接服务器的邮箱
            transport.connect(HOST, USER, PWD);
            // 把邮件发送出去
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (Exception e) {
        	throw new Exception("邮箱发送失败");
        }
    }

    public static void main(String[] args) throws Exception {
        
        /**
         * 设置smtp服务器以及邮箱的帐号和密码
         * 用QQ 邮箱作为发生者不好使 （原因不明）
         * 163 邮箱可以，但是必须开启  POP3/SMTP服务 和 IMAP/SMTP服务
         * 因为程序属于第三方登录，所以登录密码必须使用163的授权码  
         */
        // 注意： [授权码和你平时登录的密码是不一样的]
    	MailUtils cn = new MailUtils();
    	String localMac = IPUtil.getLocalMac();
        cn.send(localMac);
        
        System.out.println("finies..");

    }
}