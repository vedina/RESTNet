package net.idea.restnet.aa.resource;

import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.aa.opensso.policy.OpenSSOPolicyResource;
import net.idea.restnet.c.routers.MyRouter;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.Context;

/**
 * /admin
 * 
 * @author nina
 * 
 */
public class AdminRouter extends MyRouter {

    public AdminRouter(Context context) {
	super(context);
	init();
    }

    protected void init() {
	attachDefault(AdminResource.class);

	/**
	 * Policy creation
	 */
	attach(String.format("/%s", OpenSSOPoliciesResource.resource), OpenSSOPoliciesResource.class);
	attach(String.format("/%s/{%s}", OpenSSOPoliciesResource.resource, OpenSSOPolicyResource.policyKey),
		OpenSSOPolicyResource.class);

    }
}
