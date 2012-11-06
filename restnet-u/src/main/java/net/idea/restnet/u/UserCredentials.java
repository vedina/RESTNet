package net.idea.restnet.u;

public class UserCredentials {
	protected final String oldpwd;
	protected final String newpwd;
	
	public String getNewpwd() {
		return newpwd;
	}
	public String getOldpwd() {
		return oldpwd;
	}
	public UserCredentials(String oldpwd,String newpwd) {
		this.oldpwd = oldpwd;
		this.newpwd = newpwd;
	}
}
