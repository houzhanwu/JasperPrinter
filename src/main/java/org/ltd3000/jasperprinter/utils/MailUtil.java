package org.ltd3000.jasperprinter.utils;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;

import java.util.Properties;

import javax.activation.*;

/**
 * @author xushanshan
 * @date 20181221
 * @class 邮件投递工具类
 *
 */
public class MailUtil {

	// 请按业务系统需要定制单证投递模块

	public static boolean sendEmail(Address[] toAddList, Address[] ccAddList, String subject, String content,
			String filepath) {
		final Properties props = new Properties();// ConfigUtil
		// 表示SMTP发送邮件，需要进行身份验证
		props.put("mail.smtp.auth", ConfigUtil.getProperty("mail.smtp.auth"));

		props.put("mail.smtp.host", ConfigUtil.getProperty("mail.smtp.host"));

		props.put("mail.user", ConfigUtil.getProperty("mail.user"));

		props.put("mail.password", ConfigUtil.getProperty("mail.password"));

		// 构建授权信息，用于进行SMTP进行身份验证
		Authenticator authenticator = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {

				String userName = props.getProperty("mail.user");
				String password = props.getProperty("mail.password");
				return new PasswordAuthentication(userName, password);

			}
		};

		Session mailSession = Session.getInstance(props, authenticator);
		// 创建邮件消息
		MimeMessage message = new MimeMessage(mailSession);
		// 设置发件人
		try {
			InternetAddress form = new InternetAddress(props.getProperty("mail.user"));
			message.setFrom(form);

			// 设置收件人
			message.setRecipients(RecipientType.TO, toAddList);
			// CC
			if (ccAddList != null) {
				if (ccAddList.length > 0) {
					message.setRecipients(RecipientType.CC, ccAddList);
				}
			}

			// 设置邮件标题
			message.setSubject(subject);

			// 创建消息部分
			BodyPart messageBodyPart = new MimeBodyPart();

			// 消息
			messageBodyPart.setText("This is message body");

			// 创建多重消息
			Multipart multipart = new MimeMultipart();

			// 设置文本消息部分
			multipart.addBodyPart(messageBodyPart);

			// 附件部分
			messageBodyPart = new MimeBodyPart();

			DataSource source = new FileDataSource(filepath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(filepath);
			multipart.addBodyPart(messageBodyPart);

			// 设置邮件的内容体
			message.setContent(multipart, "text/html;charset=UTF-8");
			// 发送邮件
			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}

		return true;

	}

	public static void main(String[] args) {
		Address[] list;
		try {
			list = new Address[] { new InternetAddress("1225631395@QQ.COM") };
			sendEmail(list, null, "测试邮件", "这是一份测试邮件", "E:\\09252018_060007_14469@FEILI_wmwhse1@TOPDF.xml");
		} catch (AddressException e) {
			e.printStackTrace();
		}

	}

}
