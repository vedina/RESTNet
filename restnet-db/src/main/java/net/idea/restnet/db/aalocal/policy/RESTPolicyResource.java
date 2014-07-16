package net.idea.restnet.db.aalocal.policy;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.db.QueryResource;
import net.idea.restnet.db.convertors.OutputWriterConvertor;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
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

	
}