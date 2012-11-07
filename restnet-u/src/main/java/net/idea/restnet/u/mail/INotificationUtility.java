package net.idea.restnet.u.mail;

import java.io.IOException;

import javax.mail.MessagingException;

import org.opentox.rest.RestException;

public interface INotificationUtility {

	  /**
	   * @param toEmail the user to be notified
	   * @param subject the subject of the notification
	   * @param content the content
	   * @param mimeType the mime type of the content
	   */
	  public void sendNotification(
	      String toEmail, 
	      String subject,
	      Object content,
	      String mimeType) throws IOException, RestException, RuntimeException,MessagingException ;
	  

}
