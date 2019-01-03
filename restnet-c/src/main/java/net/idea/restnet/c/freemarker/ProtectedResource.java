package net.idea.restnet.c.freemarker;

import org.owasp.encoder.Encode;
import org.restlet.Request;
import org.restlet.data.CacheDirective;
import org.restlet.data.Cookie;
import org.restlet.data.Header;
import org.restlet.data.Protocol;
import org.restlet.data.Reference;
import org.restlet.data.ServerInfo;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;

import net.idea.restnet.c.BotsGuard;
import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.i.aa.IAuthToken;
import net.idea.restnet.i.aa.OpenSSOCookie;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;

public abstract class ProtectedResource extends ServerResource implements IAuthToken {

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {
			ClientResourceWrapper.setTokenFactory(this);
		} catch (Exception x) {
		}
		BotsGuard.checkForBots(getRequest());
	}

	@Override
	protected void doRelease() throws ResourceException {
		try {
			ClientResourceWrapper.setTokenFactory(null);
		} catch (Exception x) {
		}
		super.doRelease();
	}

	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if (OpenSSOCookie.CookieName.equals(cookie.getName()))
				return cookie.getValue();
		}
		return null;
	}

	@Override
	public Object getToken() {
		String token = getHeaderValue(OpenSSOCookie.CookieName);

		if (token == null)
			token = getTokenFromCookies(getRequest());
		return token == null ? null : token;

	}

	protected String getUserName() {
		return getHeaderValue("user");
	}

	protected String getPassword() {
		return getHeaderValue("password");
	}

	private String getHeaderValue(String tag) {
		try {
			Series headers = (Series) getRequest().getAttributes().get("org.restlet.http.headers");
			if (headers == null)
				return null;
			return headers.getFirstValue(tag);
		} catch (Exception x) {
			return null;
		}
	}

	protected boolean useSecureCookie(Request request) {
		return Protocol.HTTPS.equals(request.getProtocol());
	}

	protected Reference getResourceRef(Request request) {
		// return
		// request.getOriginalRef()==null?request.getResourceRef():request.getResourceRef();
		return request.getResourceRef();
	}

	protected void setTokenCookies(Variant variant, boolean secure) {
		if (((IFreeMarkerApplication) getApplication()).isSendTokenAsCookie()) {
			OpenSSOCookie.setCookieSetting(this.getResponse().getCookieSettings(),
					getToken() == null ? null : getToken().toString(), useSecureCookie(getRequest()));
		}
	}

	protected void setFrameOptions(String value) {
		Series headers = (Series) getResponse().getAttributes().get("org.restlet.http.headers");
		if (headers == null) {
			headers = new Series(Header.class);
			getResponse().getAttributes().put("org.restlet.http.headers", headers);
		}
		headers.removeAll("X-Frame-Options");
		headers.add("X-Frame-Options", value);
		ServerInfo si = getResponse().getServerInfo();
		si.setAgent("Restlet");
		getResponse().setServerInfo(si);
		setCacheHeaders();
	}

	protected void setCacheHeaders() {
		getResponse().getCacheDirectives().add(CacheDirective.privateInfo());
		getResponse().getCacheDirectives().add(CacheDirective.maxAge(2700));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {

		// This header forbids using the page in iframe
		setFrameOptions("SAMEORIGIN");
		return super.get(variant);
	}

	protected Reference cleanedResourceRef(Reference ref) {
		return new Reference(Encode.forJavaScriptSource(ref.toString()));
	}
}
