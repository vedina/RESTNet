package net.idea.restnet.aa.local;

import net.idea.restnet.aa.opensso.users.OpenSSOUserResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.reporters.CatalogURIReporter;

import org.restlet.Request;
import org.restlet.security.User;

public class UserLoginURIReporter<U extends User> extends CatalogURIReporter<U> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8868430033131766579L;

    public UserLoginURIReporter(Request baseRef, ResourceDoc doc) {
	super(baseRef, doc);
    }

    public UserLoginURIReporter(ResourceDoc doc) {
	this(null, doc);
    }

    @Override
    public String getURI(String ref, U item) {
	return String.format("%s/%s/%s", ref, OpenSSOUserResource.resource, (item == null)
		|| (item.getIdentifier() == null) ? "" : item.getIdentifier());
    }

}