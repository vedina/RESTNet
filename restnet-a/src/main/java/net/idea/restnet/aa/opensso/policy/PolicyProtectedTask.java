package net.idea.restnet.aa.opensso.policy;

import java.util.Hashtable;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.data.Reference;

import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.Task;

public class PolicyProtectedTask extends Task<ITaskResult, String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5631652362392618557L;
	protected boolean autocreatePolicy = true;

	/**
	 * 
	 * @param user
	 * @param autocreatePolicy
	 *            Used to avoid creating policy for the policy itself by POST
	 *            /admin/policy
	 */
	public PolicyProtectedTask(String user, boolean autocreatePolicy) {
		super(user);
		this.autocreatePolicy = autocreatePolicy;
	}

	@Override
	public synchronized void setPolicy() throws Exception {
		if (!autocreatePolicy)
			return;
		if (getUri() == null || !getUri().isNewResource())
			return;
		OpenSSOServicesConfig config = OpenSSOServicesConfig.getInstance();

		if (config.isEnabled()) {
			if (getUserid() == null) { // policy for everybody
				OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
				try {
					if (ssoToken.login("guest", "guest")) {
						OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());

						policy.createGroupPolicy("member", ssoToken, getUri().toString(),
								new String[] { "GET", "PUT", "POST", "DELETE" });
					}
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {
						ssoToken.logout();
					} catch (Exception x) {
					}
				}

			} else { // policy for the user only
				OpenSSOToken ssoToken = new OpenSSOToken(config.getOpenSSOService());
				ssoToken.setToken(getUserid());
				Hashtable<String, String> results = new Hashtable<String, String>();
				ssoToken.getAttributes(new String[] { "uid" }, results);
				OpenSSOPolicy policy = new OpenSSOPolicy(config.getPolicyService());
				// user policy
				Reference newUri = new Reference(getUri().getUri());
				newUri.setQuery(null);

				/*
				 * OpenToxUser user = new OpenToxUser(); int code =
				 * policy.getURIOwner(ssoToken, newUri.toString(), user); if
				 * (200==code) { if ((user.getUsername()!=null) &&
				 * !user.getUsername().equals("null")) throw new
				 * Exception("Has a policy"); }
				 */
				try {
					if (getUri().getPolicy() == null)
						policy.createUserPolicy(results.get("uid"), ssoToken, newUri.toString(),
								new String[] { "GET", "PUT", "POST", "DELETE" });
					else
						for (String pxml : getUri().getPolicy())
							policy.sendPolicy(ssoToken, pxml);
				} catch (Exception x) {
					// TODO write smth in the db why policy creation failed
					// x.printStackTrace();
				}
			}
		}

	}
}
