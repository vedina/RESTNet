package net.idea.restnet.sparql;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;

/**
 * 
 * @author nina
 * 
 */
public class TDBEndpointRouter extends MyRouter {
    public static final String resource = "/sparql";
    public static final String query = "/query";

    public TDBEndpointRouter(Context context) {
	super(context);
	attachDefault(TripleStoreResource.class);
	attach(String.format("%s/{%s}", query, TripleStoreResource.resourceKey), TripleStoreResource.class);

    }
}
