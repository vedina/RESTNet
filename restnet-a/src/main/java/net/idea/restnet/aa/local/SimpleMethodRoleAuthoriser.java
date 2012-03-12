package net.idea.restnet.aa.local;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Method;
import org.restlet.security.Role;
import org.restlet.security.RoleAuthorizer;

public class SimpleMethodRoleAuthoriser extends RoleAuthorizer {
	public SimpleMethodRoleAuthoriser(Role... roles) {
		super();
		for (Role role: roles) getAuthorizedRoles().add(role);
	}

	@Override
	public boolean authorize(Request request, Response response) {
		if (Method.GET.equals(request.getMethod())) return true;
		return super.authorize(request, response);
	}
}
