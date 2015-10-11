package net.idea.restnet.i.aa;

import org.restlet.data.CookieSetting;

public class OpenSSOCookie {
	public static final String CookieName = "subjectid";
	private OpenSSOCookie() {
		
	}
	
	public static CookieSetting bake(String token, boolean secure) {
		CookieSetting cS = new CookieSetting(0, OpenSSOCookie.CookieName,
				token);
		cS.setAccessRestricted(true);
		cS.setSecure(secure);
		cS.setComment("OpenSSO token");
		cS.setPath("/");
		return cS;
	}
}
