package playground.logic;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A singelton service class that sends E-mails to users
 */
public class EmailService {
	
	private static EmailService serviceInstance = null;
	
	private EmailService() {
		
	}
	
	public static EmailService getInstance() {
		if(serviceInstance == null)
			serviceInstance = new EmailService();
		return serviceInstance;
	}

	/* Sending mail to the user */
	public void sendEmail(String toEmail, String code,String userName) throws Exception {
		try {
			
			final String fromEmail = "2019A.Kagan@gmail.com";
			final String password = "sryy2018";
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
			props.put("mail.smtp.port", "587");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.starttls.enable", "true");
			
			MimeMessage message = new MimeMessage(Session.getInstance(props, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(fromEmail, password);
				}
			}));
			
			message.setFrom(new InternetAddress(fromEmail,"RateIt"));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));
			message.setSubject("RateIt Confirmation");
			message.setText("Hello " + userName + " and Welcome To RateIt!!"
					+ "\nWe just want to confirm you're you and not a robot."
					+ "\nHere is your code to complete the registration: " + code
					+ "\nEnjoy using our site!");
			Transport.send(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/* Check the validation of the email */
	public boolean isValidEmailAddress(String email) {
		boolean result = true;
		try {
			new InternetAddress(email).validate();
		} catch (AddressException ex) {
			result = false;
		}
		return result;
	}
}
