package net.idea.restnet.u.mail;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;

public class Notification implements INotificationUtility {
	private Session mailSession;  
	private String adminEmail;
	private boolean useMailAuth; 
	private String mailUser;
	private String mailPassword;	
	private Properties config;
	private Logger log;
	
	public Notification() throws IOException {
		this("conf/notification.pref");
	}
	/**
	<!-- mail notification -->
	    <notification.mail.smtp.starttls.enable>true</notification.mail.smtp.starttls.enable>
	    <notification.mail.smtp.auth>true</notification.mail.smtp.auth>
	    <notification.mail.host>mx.ideaconsult.net</notification.mail.host>
	    <notification.mail.user>nina</notification.mail.user>
	    <notification.mail.password>sinanica2516</notification.mail.password>
	    <notification.mail.transport.protocol>smtp</notification.mail.transport.protocol>
	    <notification.mail.smtp.port>587</notification.mail.smtp.port>
        <notification.mail.smtp.auth>true</notification.mail.smtp.auth>
	     
    	<notification.admin.email>nina@ideaconsult.net</notification.admin.email>
        
        <!-- SMTP Auth -->
        
        
        <!-- SMTP -->
       
       
        
        <notification.mail.debug>nina@ideaconsult.net</notification.mail.debug>
      
        
	 * @param configFile
	 * @throws IOException
	 */
	public Notification(String configFile) throws IOException {
		config = new Properties();
		config.load(getClass().getClassLoader().getResourceAsStream(configFile));
	    log = Logger.getLogger("net.idea.restnet.u.mail");
	    try {
	      adminEmail = config.get("notification.admin.email").toString();
      
	      mailUser = config.get("mail.user").toString();
	      mailPassword = config.get("mail.password").toString();
	      useMailAuth = "true".equalsIgnoreCase(config.get("mail.smtp.auth").toString());
	      
	      /*
	       * A workaround to trust all certificates. Better to import the cert in the server key store
		   * */
	      MailSSLSocketFactory socketFactory= new MailSSLSocketFactory();
	      socketFactory.setTrustAllHosts(true);
	      config.put("mail.smtp.ssl.socketFactory", socketFactory);
	     // this doesn't work
	      //config.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

	      if (useMailAuth) {
	        Authenticator auth = new Authenticator() {
	          public PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(mailUser, mailPassword);
	          }
	        };
	        mailSession = Session.getInstance(config, auth);
	      }
	      else {
	        mailSession = Session.getInstance(config);
	      }
	    }
	    catch (Exception e) {
	      log.log(Level.SEVERE, "Error getting alerts configuration", e);
	    }
	  }
	@Override
	public void sendNotification(String toEmail,String subject, Object content,String mimeType) throws MessagingException {
		  
	    if (mailSession != null) {
	      Message msg = new MimeMessage(mailSession);
	      msg.setSubject(subject);
	      msg.setFrom(new InternetAddress(adminEmail));
	      msg.setRecipient(RecipientType.TO, new InternetAddress(toEmail));
	      msg.setContent(content, mimeType);
	      
	      if (useMailAuth) {
	        Transport tr = mailSession.getTransport();
	        try {
	          tr.connect();
	          msg.saveChanges();
	          tr.sendMessage(msg, msg.getAllRecipients());
	        } finally {
	          try { tr.close(); } catch (Exception e) {}
	        }
	      } else {
	        Transport.send(msg);
	      }
	    } else {
	      log.log(Level.SEVERE, "Tried to send alert notification but the mail session has not been configured");
	    }
	  } 
}
