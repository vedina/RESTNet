package net.idea.restnet.aa.local;

import net.idea.restnet.aa.cookie.CookieAuthenticator;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;
	
public class UserLogoutPOSTResource<U extends User> extends UserLoginPOSTResource<U> {
		public static final String resource = "signout";
	
	
	/**
	 * Intercepted by {@link CookieAuthenticator} 
	 */
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
			
	     this.getResponse().redirectSeeOther(String.format("%s/login",getRequest().getRootRef()));
	     return null;
	}
	
}	