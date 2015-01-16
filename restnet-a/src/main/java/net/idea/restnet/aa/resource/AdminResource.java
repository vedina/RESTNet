package net.idea.restnet.aa.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.c.resource.CatalogResource;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.resource.ResourceException;

public class AdminResource extends CatalogResource<String> {
    public static final String resource = "admin";
    protected List<String> topics = new ArrayList<String>();

    public AdminResource() {
	super();
	topics.add(String.format("%s/%s", resource, OpenSSOPoliciesResource.resource));
    }

    @Override
    protected Iterator<String> createQuery(Context context, Request request, Response response)
	    throws ResourceException {

	return topics.iterator();
    }

}
