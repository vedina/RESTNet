package net.idea.restnet.u;

import net.idea.restnet.u.mail.Notification;

import org.junit.Test;

public class NotificationTest {
	@Test
	public void test() throws Exception {
		Notification n = new Notification();
		n.sendNotification("jeliazkova.nina@gmail.com", "just testing "+n.getClass().getName(), "nothing important ", "text/plain");
	}
}
