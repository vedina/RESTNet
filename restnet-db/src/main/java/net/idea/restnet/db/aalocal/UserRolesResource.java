package net.idea.restnet.db.aalocal;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.Role;
import org.restlet.security.User;

public class UserRolesResource extends CatalogResource<String> {

    /** Current user. */
    User user;

    /** Its role(s). */
    List<String> messages = new ArrayList<String>();

    @Override
    protected Iterator<String> createQuery(Context context, Request request, Response response)
	    throws ResourceException {
	try {
	    this.user = getClientInfo().getUser();
	    if (user == null)
		messages.add("Not logged in user");
	    else
		messages.add(user.getIdentifier());
	    if (getClientInfo().getRoles() == null)
		messages.add("No roles");
	    else
		for (Role role : getClientInfo().getRoles())
		    messages.add(role.toString());
	} catch (Exception x) {
	    messages.add(x.getMessage());
	}
	return messages.iterator();
    }

}
