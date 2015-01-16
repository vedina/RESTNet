package net.idea.restnet.db.aalocal.policy;

import java.sql.Connection;

import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class CallablePolicyCreator extends CallableDBUpdateTask<IRESTPolicy<Integer>, Form, String> {
    protected IRESTPolicy<Integer> policyToUpdate;

    public CallablePolicyCreator(IRESTPolicy<Integer> policy, Method method, Form input, String baseReference,
	    Connection connection, String token, String usersdbname) {
	super(method, input, baseReference, connection, token);
	policyToUpdate = policy;
    }

    @Override
    protected IRESTPolicy<Integer> getTarget(Form input) throws Exception {
	if (Method.POST.equals(method)) {
	    RESTPolicy policy = new RESTPolicy();
	    policy.setRole(input.getFirstValue(RESTPolicy._fields.role.name()));
	    policy.setUri(input.getFirstValue(RESTPolicy._fields.resource.name()));
	    policy.setAllowDELETE(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.delete.name())));
	    policy.setAllowPOST(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.post.name())));
	    policy.setAllowPUT(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.put.name())));
	    policy.setAllowGET(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.get.name())));
	    return policy;
	} else if (Method.DELETE.equals(method)) {
	    return policyToUpdate;
	} else {
	    policyToUpdate.setRole(input.getFirstValue(RESTPolicy._fields.role.name()));
	    policyToUpdate.setUri(input.getFirstValue(RESTPolicy._fields.resource.name()));
	    policyToUpdate.setUri(input.getFirstValue(RESTPolicy._fields.resource.name()));
	    policyToUpdate.setAllowDELETE(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.delete.name())));
	    policyToUpdate.setAllowPOST(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.post.name())));
	    policyToUpdate.setAllowPUT(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.put.name())));
	    policyToUpdate.setAllowGET(getMethodAllowedValue(input.getFirstValue(RESTPolicy._fields.get.name())));
	    return policyToUpdate;
	}
    }

    protected Boolean getMethodAllowedValue(Object value) {
	if (value == null)
	    return null;
	if ("on".equals(value.toString()))
	    return true;
	if ("checked".equals(value.toString()))
	    return true;
	if ("true".equals(value.toString()))
	    return true;
	return null;
    }

    @Override
    protected IQueryUpdate<? extends Object, IRESTPolicy<Integer>> createUpdate(IRESTPolicy<Integer> target)
	    throws Exception {
	if (Method.DELETE.equals(method)) {
	    DeletePolicy q = new DeletePolicy();
	    q.setObject(target);
	    return q;
	} else if (Method.POST.equals(method)) {
	    CreatePolicy q = new CreatePolicy();
	    q.setObject(target);
	    return q;
	} else if (Method.PUT.equals(method)) {
	    UpdatePolicy q = new UpdatePolicy();
	    q.setObject(target);
	    return q;
	}
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);

    }

    @Override
    protected String getURI(IRESTPolicy<Integer> target) throws Exception {
	return target.getPolicyURI(baseReference + "/admin" + RESTPolicyResource.resource);
    }

    @Override
    public String toString() {
	if (Method.POST.equals(method)) {
	    return String.format("Create policy");
	} else if (Method.PUT.equals(method)) {
	    return String.format("Update policy");
	} else if (Method.DELETE.equals(method)) {
	    return String.format("Delete policy");
	}
	return "Read policy";
    }

}
