package net.idea.restnet.aa.local;

import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

public class UserLoginFormResource<U extends User> extends UserLoginPOSTResource<U> {
    public static final String resource = "login";

    @Override
    protected Representation post(Representation entity, Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected Representation delete() throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    /**
     * Logout
     */
    @Override
    protected Representation delete(Variant variant) throws ResourceException {
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

    }
}
