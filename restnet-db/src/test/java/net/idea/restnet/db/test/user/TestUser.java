package net.idea.restnet.db.test.user;

import net.idea.restnet.db.aalocal.user.IUser;

import org.junit.Test;

public class TestUser implements IUser {
	String username;
	String pass;
	@Override
	public String getUserName() {
		return username;
	}
	@Override
	public void setUserName(String userName) {
		this.username = userName;
	}

	@Override
	public String getPassword() {
		return pass;
	}

	@Override
	public void setPassword(String password) {
		this.pass = password;

	}

	@Test
	public void test() {
		
	}
}
