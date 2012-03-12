package net.idea.restnet.db.aalocal;

import java.util.Iterator;
import java.util.List;

import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.security.Role;
import org.restlet.security.User;

public class UserRolesResource extends CatalogResource {

    /** Current user. */
    User user;

    /** Its role(s). */
    List<Role> roles;

    @Override
    protected void doInit() throws ResourceException {
        this.user = getClientInfo().getUser();
        this.roles = getClientInfo().getRoles();
    }

  
	@Override
	protected Iterator<Role> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		return roles.iterator();
	}

}
