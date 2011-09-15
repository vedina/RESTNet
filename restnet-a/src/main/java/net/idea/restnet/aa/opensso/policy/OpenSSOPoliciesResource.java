package net.idea.restnet.aa.opensso.policy;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.aa.opensso.OpenSSOServicesConfig;
import net.idea.restnet.aa.opensso.OpenSSOUser;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.resource.CatalogResource;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

import org.opentox.aa.opensso.OpenSSOPolicy;
import org.opentox.aa.opensso.OpenSSOToken;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.security.User;

/**
 * <pre>
 * /opentoxuser/userid/policy?uri=<uri-to-retrieve-policy-for>
 * </pre>
 * @author nina
 *
 */
public class OpenSSOPoliciesResource extends CatalogResource<Policy> {
	public static final String resource = "policy";

	@Override
	protected Iterator<Policy> createQuery(Context context, Request request,
			Response response) throws ResourceException {
		List<Policy> p = new ArrayList<Policy>();
		Form form = request.getResourceRef().getQueryAsForm();
		String uri = form.getFirstValue(search_param);
		if (uri==null) return p.iterator(); //throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"Parameter missing: ?search=<uri-to-retrieve-policy-for>");
		
		User user = request.getClientInfo().getUser();
		if (user == null) {
			user = new OpenSSOUser();
			((OpenSSOUser)user).setUseSecureCookie(useSecureCookie(request));
		}
		if (user instanceof OpenSSOUser) {
			String token = getToken();
			if (token==null) throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED);
			
			OpenSSOServicesConfig config = null;
			Hashtable<String, String> policies = new Hashtable<String, String>();
			String policyService=null;
			try {
				config = OpenSSOServicesConfig.getInstance();
				policyService = config.getPolicyService();
				OpenSSOPolicy policy = new OpenSSOPolicy(policyService);				
				OpenSSOToken ssotoken = new OpenSSOToken(config.getOpenSSOService());
				ssotoken.setToken(token);
				policy.getURIOwner(ssotoken,uri,(OpenSSOUser)user, policies);
				if (policies.size()==0) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
				//too bad, refactor the policy class to not use hashtable
				
				Enumeration<String> e = policies.keys();
				while (e.hasMoreElements()) {
					p.add(new Policy(e.nextElement()));
					
				}
				return p.iterator();
					
			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_BAD_GATEWAY,policyService,x);
			}
			
		} else throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
	} 
	
	@Override
	public IProcessor<Iterator<Policy>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {

		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(
					createHTMLReporter(),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor(	new PolicyURIReporter(getRequest(),getDocumentation()),MediaType.TEXT_URI_LIST);
			
		} else //html 	
			return new StringConvertor(createHTMLReporter(),MediaType.TEXT_HTML);
		
	}
	/**
	 * POST to create / delete policy
	 * expects
	 * curl -d "uri= " -d "name= " -d "type=group|user" -d "get=on" -d "post=on"
	 */
	@Override
	protected ICallableTask createCallable(Form form, Policy item)
			throws ResourceException {
		return new CallablePolicyCreator<String>(form, getToken(),getRequest().getRootRef());
	}
	
	protected Reporter createHTMLReporter() {
		return new PolicyHTMLReporter(getRequest(),true,getDocumentation());
	}
	

	@Override
	protected Reference getSourceReference(Form form, Policy model)
			throws ResourceException {

		return null;
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			ArrayList<UUID> tasks = new ArrayList<UUID>();
		
			try {
			Form form = entity.isAvailable()?new Form(entity):new Form();
			
				ICallableTask callable= createCallable(form,null);
				Task<TaskResult,String> task =  ((TaskApplication)getApplication()).addTask(
						String.format("Create policy"),
						callable,
						getRequest().getRootRef(),
						getToken()
						);	
				task.update();
				setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
				tasks.add(task.getUuid());

			} catch (ResourceException x) {
				throw x;
			} catch (Exception x) {
				throw new ResourceException(Status.SERVER_ERROR_INTERNAL,x.getMessage(),x);
			}
			if (tasks.size()==0)
				throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			else {
				
				ITaskStorage storage = ((TaskApplication)getApplication()).getTaskStorage();
				FactoryTaskConvertor<Object> tc = new FactoryTaskConvertor<Object>(storage);
				if (tasks.size()==1)
					return tc.createTaskRepresentation(tasks.get(0), variant, getRequest(),getResponse(),getDocumentation());
				else
					return tc.createTaskRepresentation(tasks.iterator(), variant, getRequest(),getResponse(),getDocumentation());				
			}
		}
	}	
}
