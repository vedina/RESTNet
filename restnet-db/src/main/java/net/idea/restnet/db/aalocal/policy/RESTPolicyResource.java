package net.idea.restnet.db.aalocal.policy;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.task.CallableProtectedTask;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.c.task.TaskCreator;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.FactoryTaskConvertorRDF;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class RESTPolicyResource <Q extends IQueryRetrieval<IRESTPolicy<Integer>>> extends	
										QueryResource<Q,IRESTPolicy<Integer>> {
	public static final String resource = "/restpolicy";
	public static final String resourceid = "restpolicyid";
	@Override
	public IProcessor<Q, Representation> createConvertor(Variant variant)
			throws AmbitException, ResourceException {
			String baseRef = getRequest().getRootRef() + "/admin/restpolicy" ;
			if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)) {
				String jsonpcallback = getParams().getFirstValue("jsonp");
				if (jsonpcallback==null) jsonpcallback = getParams().getFirstValue("callback");
				return new OutputWriterConvertor(
						new RESTPolicyJSONReporter(baseRef,jsonpcallback),
						MediaType.APPLICATION_JAVASCRIPT);				
			} else // if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)) {
				return new OutputWriterConvertor(
						new RESTPolicyJSONReporter(baseRef),
						MediaType.APPLICATION_JSON);		
	}
	@Override
	protected Q createQuery(Context context, Request request, Response response)
			throws ResourceException {
		Object id = request.getAttributes().get(resourceid);
		ReadPolicy q = new ReadPolicy();
		if (id!=null) try {
			RESTPolicy policy = new RESTPolicy();
			policy.setId(Integer.parseInt(id.toString()));
			q.setFieldname(policy);
		} catch (Exception x) {
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		return (Q)q;
	}

	@Override
	protected Q createUpdateQuery(Method method, Context context,
			Request request, Response response) throws ResourceException {
		Object key = request.getAttributes().get(RESTPolicyResource.resourceid);
		if (Method.POST.equals(method)) {
			if (key==null) return null;//post allowed only on /user level, not on /user/id
		} else {
			if (key!=null) return super.createUpdateQuery(method, context, request, response);
		}
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected TaskCreator getTaskCreator(Form form, Method method,
			boolean async, Reference reference) throws Exception {
		return super.getTaskCreator(form, method, async, reference);
	}
	
	@Override
	protected CallableProtectedTask<String> createCallable(Method method,
			Form form, IRESTPolicy<Integer> item) throws ResourceException {
		Connection conn = null;
		try {
			DBConnection dbc = new DBConnection(getApplication().getContext(),getConfigFile());
			conn = dbc.getConnection();
			return new CallablePolicyCreator(item,method, form,getRequest().getRootRef().toString(), conn, getToken(),
					getDefaultUsersDB());
		} catch (Exception x) {
			try { conn.close(); } catch (Exception xx) {}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x);
		}
	}
	@Override
	protected boolean isAllowedMediaType(MediaType mediaType)
			throws ResourceException {
		return MediaType.APPLICATION_WWW_FORM.equals(mediaType);
	}
	
	public String getDefaultUsersDB() {
		return "aalocal";
	}
	
	protected String getObjectURI(Form queryForm) throws ResourceException {
		return null;		
	}

	@Override
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage)
			throws ResourceException {
		return new FactoryTaskConvertorRDF(storage,getHTMLBeauty());
	}
}