package net.idea.restnet.aa.local;

import java.io.Writer;
import java.util.Iterator;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.aa.cookie.CookieAuthenticator;
import net.idea.restnet.aa.opensso.users.SingleItemIterator;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

public class UserLoginPOSTResource<U extends User> extends CatalogResource<U> {
	public static final String resource = "signin";
	@Override
	protected HTMLBeauty getHTMLBeauty() {
		return new HTMLBeauty();
	}
	
	
	/**
	 * Intercepted by {@link CookieAuthenticator} 
	 */
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {

		 if (getRequest().getChallengeResponse()!=null) {
			 /*
			 CookieSetting cS = new CookieSetting(0, "Credentials",getRequest().getChallengeResponse().getRawValue());
			 cS.setComment("CookieAuthenticator");
			 cS.setPath("/");
			 this.getResponse().setStatus(Status.SUCCESS_OK);
			 this.getResponse().getCookieSettings().removeAll("Credentials");
		     this.getResponse().getCookieSettings().add(cS);
		     this.getRequest().getCookies().add( "Credentials",getRequest().getChallengeResponse().getRawValue());
		     */
		 }
	     this.getResponse().redirectSeeOther(String.format("%s/login",getRequest().getRootRef()));
	     return null;
	}

	@Override
	public IProcessor<Iterator<U>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(createHTMLReporter(false),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new UserLoginURIReporter(getRequest(),getDocumentation()) {
				@Override
				public void processItem(Object item, Writer output) {
					super.processItem(item, output);
					try {
					output.write('\n');
					} catch (Exception x) {}
				}
			},MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(createHTMLReporter(false),MediaType.TEXT_HTML);
		
	}

	@Override
	protected Reporter createHTMLReporter(boolean headles) {
		return new UserLoginHTMLReporter(getRequest(),getDocumentation(),getHTMLBeauty());
	}
	@Override
	protected Iterator<U> createQuery(Context context,
			Request request, Response response) throws ResourceException {
		
		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new User();
		}
		if (user instanceof User) 
			return new SingleItemIterator(user);
		else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	}
	
	
}
