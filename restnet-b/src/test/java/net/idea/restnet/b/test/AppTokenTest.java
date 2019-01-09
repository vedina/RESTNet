package net.idea.restnet.b.test;

import org.junit.Test;

import junit.framework.Assert;
import net.idea.restnet.b.AppToken;

public class AppTokenTest {
	@Test
	public void test() throws Exception {
		AppToken t = new AppToken();
		Assert.assertEquals("random32", t.getTokenType());
		AppToken t1 = new AppToken();
		//System.out.println(t);
		Assert.assertFalse(t.equals(t1));
	}
}
