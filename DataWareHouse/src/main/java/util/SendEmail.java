package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SendEmail {
	static final String from = "leminhlongg0902@gmail.com";
	static final String password = "kxyvjmqrualglkid";

	public static void sendMail(String addressTo, String title, String message) {
		// Properties for mail session
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
		// Create an authenticator
		Authenticator auth = new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, password);
			}
		};

		// Create a mail session
		Session session = Session.getInstance(props, auth);

		// Create a MimeMessage
		MimeMessage msg = new MimeMessage(session);
		try {
			msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
			msg.setFrom(new InternetAddress(from, "Data WareHouse"));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(addressTo, false));
			msg.setSubject(title);
			msg.setSentDate(new Date());

			// Email content
			msg.setText(message, "UTF-8");

			// Send the email
			Transport.send(msg);
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		sendMail("leminhlongit@gmail.com","Thành công", "lấy dữ liệu ngày ... thành công");
	}
}