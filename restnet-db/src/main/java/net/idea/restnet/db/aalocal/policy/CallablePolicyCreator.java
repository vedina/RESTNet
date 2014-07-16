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

	public CallablePolicyCreator(Method method, Form input,
			String baseReference, Connection connection, String token,String usersdbname) {
		super(method, input, baseReference, connection, token);
	}

	@Override
	protected IRESTPolicy<Integer> getTarget(Form input) throws Exception {
		RESTPolicy policy = new RESTPolicy();
		policy.setRole(input.getFirstValue(RESTPolicy._fields.role.name()));
		policy.setUri(input.getFirstValue(RESTPolicy._fields.resource.name()));

		return policy;
	}

	
	@Override
	protected IQueryUpdate<? extends Object, IRESTPolicy<Integer>> createUpdate(
			IRESTPolicy<Integer> target) throws Exception {
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
		return target.getPolicyURI(baseReference);
	}

}
