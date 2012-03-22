package net.idea.restnet.sparql;

import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;
import org.restlet.resource.Directory;

public class SPARQLEndpointRouter extends MyRouter {

	public SPARQLEndpointRouter(Context context) {
		super(context);
		attach("", TDBOntologyResource.class);
		attach("/", TDBOntologyResource.class);
		attach(String.format("/query/{%s}",TDBOntologyResource.resourceKey), TDBOntologyResource.class);	
	    
		Directory metaDir = new Directory(getContext(), "war:///META-INF");
		Directory jqueryDir = new Directory(getContext(), "war:///jquery");
		Directory styleDir = new Directory(getContext(), "war:///style");
 		attach("/meta/", metaDir);
		 
 		attach("/jquery/", jqueryDir);
 		attach("/style/", styleDir); 	
 		
		//Just sets the token, don't return error if not valid one
		//Filter authn = new OpenSSOAuthenticator(getContext(),false,"opentox.org",new OpenSSOFakeVerifier(false));
		//authn.setNext(router);
	}
}
