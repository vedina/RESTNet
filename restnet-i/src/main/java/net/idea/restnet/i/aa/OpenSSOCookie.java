package net.idea.restnet.i.aa;

import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.util.Series;

public class OpenSSOCookie {
	public static final String CookieName = "subjectid";

	private OpenSSOCookie() {

	}

	public static CookieSetting setCookieSetting(Series<CookieSetting> cookies,
			String token, boolean secure) {
		CookieSetting cs = prepare(token, secure);
		if (cs != null) {
			cookies.removeAll(OpenSSOCookie.CookieName);
			cookies.add(cs);
		}	
		return cs;
	}

	public static Cookie setCookie(Series<Cookie> cookies, String token,
			boolean secure) {

		Cookie cs = bake(token, secure);
		if (cs != null) {
			cookies.removeAll(OpenSSOCookie.CookieName);
			cookies.add(cs);
		}	
		return cs;
	}

	private static CookieSetting prepare(String token, boolean secure) {
		if (token==null || "".equals(token)) return null;
		
		CookieSetting cS = new CookieSetting(0, OpenSSOCookie.CookieName, token);
		// cS.setAccessRestricted(true);
		cS.setSecure(secure);
		cS.setComment("OpenSSO token");
		cS.setPath("/");
		return cS;
	}
	
	private static Cookie bake(String token, boolean secure) {
		if (token==null || "".equals(token)) return null;
		Cookie cS = new Cookie(0, OpenSSOCookie.CookieName, token);
		cS.setPath("/");
		return cS;
	}
}
