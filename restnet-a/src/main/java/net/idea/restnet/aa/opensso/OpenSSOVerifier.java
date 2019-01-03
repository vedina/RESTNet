package net.idea.restnet.aa.opensso;

import org.opentox.aa.OTAAParams;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Cookie;
import org.restlet.data.Protocol;
import org.restlet.security.User;
import org.restlet.security.Verifier;
import org.restlet.util.Series;

import net.idea.restnet.i.aa.OpenSSOCookie;

/**
 * subjectid=token , as header parameter
 * 
 * @author nina
 * 
 */
public class OpenSSOVerifier implements Verifier {
	protected boolean enabled = false;

	public OpenSSOVerifier() {
		this(isEnabled());
	}

	public static boolean isEnabled() {
		try {
			return OpenSSOServicesConfig.getInstance().isEnabled();
		} catch (Exception x) {
		}
		return true;
	}

	public OpenSSOVerifier(boolean enabled) {
		this.enabled = enabled;
	}

	public int verify(Request request, Response response) {

		Series headers = (Series) request.getAttributes().get("org.restlet.http.headers");
		String token = null;
		if (headers != null)
			token = headers.getFirstValue(OTAAParams.subjectid.toString());

		if (token == null) // backup, check cookies
			token = getTokenFromCookies(request);

		if (token == null) { // still nothing
			request.getCookies().removeAll(OpenSSOCookie.CookieName);
			return enabled ? Verifier.RESULT_MISSING : Verifier.RESULT_VALID;
		} else
			token = token.trim();

		if ((token != null) && (!"".equals(token))) {

			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig.getInstance().getOpenSSOService());
				ssoToken.setToken(token);
				if (ssoToken.isTokenValid()) {
					setUser(ssoToken, request);
					return Verifier.RESULT_VALID;
				} else {
					request.getCookies().removeAll(OpenSSOCookie.CookieName);
					return enabled ? Verifier.RESULT_INVALID : Verifier.RESULT_VALID;
				}
			} catch (Exception x) {
				x.printStackTrace(); // TODO
				return enabled ? Verifier.RESULT_MISSING : Verifier.RESULT_VALID;
			}
		} else {
			request.getCookies().removeAll(OpenSSOCookie.CookieName);
			return enabled ? Verifier.RESULT_MISSING : Verifier.RESULT_VALID;
		}

	}

	protected void setUser(OpenSSOToken ssoToken, Request request) throws Exception {
		request.getClientInfo().setUser(createUser(ssoToken, request));
	}

	protected User createUser(OpenSSOToken ssoToken, Request request) throws Exception {
		OpenSSOUser user = new OpenSSOUser();
		user.setToken(ssoToken.getToken());
		OpenSSOCookie.setCookie(request.getCookies(), ssoToken.getToken(), useSecureCookie(request));
		return user;
	}

	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if (OpenSSOCookie.CookieName.equals(cookie.getName()))
				return cookie.getValue();
			/*
			 * System.out.println("name = " + cookie.getName());
			 * System.out.println("value = " + cookie.getValue());
			 * System.out.println("domain = " + cookie.getDomain());
			 * System.out.println("path = " + cookie.getPath());
			 * System.out.println("version = " + cookie.getVersion());
			 */
		}
		return null;
	}

	protected boolean useSecureCookie(Request request) {
		return Protocol.HTTPS.equals(request.getProtocol());
	}
}
