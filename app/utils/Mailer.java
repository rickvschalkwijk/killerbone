package utils;

import play.Play;

import com.typesafe.config.ConfigFactory;
import com.typesafe.plugin.*;

public class Mailer 
{
	private MailerPlugin mailerPlugin;
	private String mailerFromEmail;
	
	public Mailer()
	{
		mailerPlugin = Play.application().plugin(MailerPlugin.class);
		mailerFromEmail = ConfigFactory.load().getString("smtp.fromEmail");
	}
	
	//-----------------------------------------------------------------------//
	
	/**
	 * Send text or html mail to one of more recipients.
	 * @param String
	 * @param String[]
	 * @param String
	 * @param MailType
	 */
	public void sendMail(String subject, String[] recipients, String body, MailType typeOfEmail)
	{
		MailerAPI mail = mailerPlugin.email();
		
		mail.setSubject(subject);
		mail.addRecipient(recipients);
		mail.addFrom(mailerFromEmail);

		switch (typeOfEmail) {
		case HTML:
			mail.sendHtml(body);
			break;
		case TEXT:
			mail.send(body);
			break;
		default:
			mail.send(body);
			break;
		}	
	}

	//-----------------------------------------------------------------------//
	
	public enum MailType
	{
		TEXT, HTML
	}
}
