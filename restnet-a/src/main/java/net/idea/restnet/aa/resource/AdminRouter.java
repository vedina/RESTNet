package net.idea.restnet.aa.resource;

import net.idea.restnet.aa.opensso.policy.OpenSSOPoliciesResource;
import net.idea.restnet.aa.opensso.policy.OpenSSOPolicyResource;
import net.idea.restnet.c.routers.MyRouter;

import org.restlet.Context;

/**
 * /admin 
 * @author nina
 *
 */
public class AdminRouter extends MyRouter {

	public AdminRouter(Context context) {
		super(context);
		attachDefault(AdminResource.class);

		/**
		 * Policy creation
		 */
		attach(String.format("/%s",OpenSSOPoliciesResource.resource),OpenSSOPoliciesResource.class);
		attach(String.format("/%s/{%s}",OpenSSOPoliciesResource.resource,OpenSSOPolicyResource.policyKey),OpenSSOPolicyResource.class);
		
	}

}
