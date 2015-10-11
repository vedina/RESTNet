package net.idea.restnet.aa.opensso.users;

import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.resource.CatalogResource;
import net.idea.restnet.i.aa.OpenSSOCookie;

import org.opentox.aa.exception.AAException;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

public class OpenSSOUserResource extends CatalogResource<OpenSSOUser> {
	public static final String resource = "openssouser";

	@Override
	protected Iterator<OpenSSOUser> createQuery(Context context,
			Request request, Response response) throws ResourceException {

		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new OpenSSOUser();
		}
		if (user instanceof OpenSSOUser)
			return new SingleItemIterator<OpenSSOUser>(((OpenSSOUser) user));
		else
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}

	@Override
	protected void setTokenCookies(Variant variant, boolean secure) {
		User user = getRequest().getClientInfo().getUser();
		if (user instanceof OpenSSOUser) {
			if ((user == null) || (((OpenSSOUser) user).getToken() == null))
				super.setTokenCookies(variant, secure);
			else {
				CookieSetting cS = new CookieSetting(0, OpenSSOCookie.CookieName,
						((OpenSSOUser) user).getToken());
				cS.setAccessRestricted(true);
				cS.setSecure(secure);
				cS.setComment("OpenSSO token");
				cS.setPath("/");
				this.getResponse().getCookieSettings().add(cS);

				this.getResponse().getCookieSettings().add(cS);
			}
		}
	}

	@Override
	public IProcessor<Iterator<OpenSSOUser>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(new OpenSSOUserHTMLReporter(
					getRequest(), getDocumentation(), getHTMLBeauty()),
					MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(new OpenSSOUsersURIReporter(
					getRequest(), getDocumentation()) {
				@Override
				public void processItem(OpenSSOUser src, Writer output) {
					super.processItem(src, output);
					try {
						output.write('\n');
					} catch (Exception x) {
					}
				}
			}, MediaType.TEXT_URI_LIST);

		} else
			// html
			return new StringConvertor(new OpenSSOUserHTMLReporter(
					getRequest(), getDocumentation(), getHTMLBeauty()),
					MediaType.TEXT_HTML);

	}


	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		if ((entity != null)
				&& MediaType.APPLICATION_WWW_FORM.equals(entity.getMediaType())) {
			Form form = new Form(entity);

			try {
				OpenSSOToken ssoToken = new OpenSSOToken(OpenSSOServicesConfig
						.getInstance().getOpenSSOService());
				String username = form.getFirstValue("user");
				String pass = form.getFirstValue("password");
				String redirect = form.getFirstValue("targetUri", true);

				if (ssoToken.login(username, pass)) {
					OpenSSOUser user = new OpenSSOUser();
					user.setToken(ssoToken.getToken());
					getRequest().getClientInfo().setUser(user);
					user.setIdentifier(username);
					OpenSSOCookie.setCookieSetting(this.getResponse().getCookieSettings(),ssoToken.getToken(), useSecureCookie(getRequest()));

					if (redirect != null)
						this.getResponse().redirectSeeOther(redirect);
					else
						this.getResponse().redirectSeeOther(
								String.format("%s", getRequest().getRootRef()));
					return null;

				} else {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
					queryObject = createQuery(getContext(), getRequest(),
							getResponse());

					return get(variant);
				}

			} catch (Exception x) {
				throw new ResourceException(new Status(
						Status.SERVER_ERROR_BAD_GATEWAY, x));
			}
		} else
			throw new ResourceException(
					Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE);

	}	

	@Override
	protected Representation delete() throws ResourceException {
		String token = getToken();
		if (token != null)
			try {
				OpenSSOToken ssoToken = new OpenSSOToken(getOpenSSOService());
				if (ssoToken.logout()) {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY, x);
			}
		return get();
	}

	/**
	 * Logout
	 */
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		String token = getToken();
		if (token != null)
			try {
				OpenSSOToken ssoToken = new OpenSSOToken(getOpenSSOService());
				ssoToken.setToken(token);
				if (ssoToken.logout()) {
					getRequest().getClientInfo().setUser(null);
					this.getResponse().getCookieSettings()
							.removeAll(OpenSSOCookie.CookieName);
				}
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY, x);
			}
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		getRequest().getOriginalRef().setQuery(null);
		getRequest().getResourceRef().setQuery(null);
		return get(variant);
	}

	protected boolean useSecureCookie(Request request) {
		return Protocol.HTTPS.equals(request.getProtocol());

	}

	/**
	 * Override to set the proper configuration
	 * 
	 * @return
	 * @throws AAException
	 */
	protected String getOpenSSOService() throws AAException {
		return OpenSSOServicesConfig.getInstance().getOpenSSOService();
	}
}
