package net.idea.restnet.b;

import java.security.SecureRandom;
import java.util.Base64;


public class AppToken {
	enum _tokentype  {random32};
	protected String token;
	protected String tokenType = _tokentype.random32.name();
	public String getToken() {
		return token;
	}
	public String getTokenType() {
		return tokenType;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType==null?_tokentype.random32.name():tokenType;
	}
	public AppToken(String token) {
		this(token,_tokentype.random32.name());
	}
	public AppToken(String token, String tokenType) {
		this.token=token;
		setTokenType(tokenType);
	}
	public AppToken() {
		SecureRandom random = new SecureRandom();
		byte bytes[] = new byte[32];
		random.nextBytes(bytes);
		token = Base64.getEncoder().encodeToString(bytes);
		setTokenType(_tokentype.random32.name());
	}
	@Override
	public String toString() {
		return token;
	}
	@Override
	public boolean equals(Object obj) {
		return token.equals(obj.toString());
	}
}
