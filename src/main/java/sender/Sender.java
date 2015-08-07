package sender;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import conf.GlobalConfig;

public class Sender {

	private static final Logger logger = LoggerFactory.getLogger(Sender.class); 
	
	public static boolean send(final String to,final String appKey, final String subject, final String text){
		try {
			simpleSendEmail(to, appKey, subject, text);
			return true;
		} catch (Exception e) {
			logger.error("error when trying to send an email, to=[{}], appKey=[{}], subject=[{}], text=[{}]", to, appKey, subject, text, e);
			return false;
		}
	}
	
	private static void simpleSendEmail(final String to ,final String appKey, final String subject, final String text) throws Exception {
		final Map<String,String> mailConfigs = (Map<String, String>) GlobalConfig.getByPath("mail");
		
		// 创建Properties 对象
		final Properties props = System.getProperties();
		// 添加smtp服务器属性
		props.put("mail.smtp.host", mailConfigs.get("host"));
		props.put("mail.smtp.auth", "true");
//		props.put("mail.transport.protocol", "smtp");
		// 创建邮件会话
		final Session session = Session.getDefaultInstance(props,
				new Authenticator() {
					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(mailConfigs.get("user"), mailConfigs.get("password"));
					}
				});
		// 定义邮件信息
		final MimeMessage message = new MimeMessage(session);
		final InternetAddress senderAddress = new InternetAddress(mailConfigs.get("user"));
		senderAddress.setPersonal(appKey, "utf-8");
		message.setSender(senderAddress);
		message.addRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(to)});
		message.setSubject(Strings.nullToEmpty(subject));
		message.setText(Strings.nullToEmpty(text));
		// 只要设计了ContentType就可以按html的方式展示了。
		message.setHeader("Content-Type", "text/html");
		// 发送消息
		session.getTransport("smtp").send(message);
		// Transport.send(message); //也可以这样创建Transport对象发送
	}
	
}
