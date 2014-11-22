package net.idea.restnet.c;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.exceptions.NotFoundException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.exception.RResourceException;
import net.idea.restnet.c.freemarker.FreeMarkerSupport;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.task.ClientResourceWrapper;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.aa.IAuthToken;
import net.idea.restnet.i.freemarker.IFreeMarkerApplication;
import net.idea.restnet.i.freemarker.IFreeMarkerSupport;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.CacheDirective;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ServerInfo;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.ObjectRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import freemarker.template.Configuration;

/**
 * Abstract class for resources
 * @author nina
 *
 * @param <Q>
 * @param <T>
 * @param <P>
 */
public abstract class AbstractResource<Q,T,P extends IProcessor<Q, Representation>> 
													extends ServerResource implements IAuthToken, IFreeMarkerSupport {
	protected Q queryObject;
	protected Exception error = null;	
	protected Status response_status = Status.SUCCESS_OK;
	public final static String search_param = "search";
	public final static String property = "property";
	public final static String condition = "condition";
	public final static String caseSensitive = "casesens";
	public final static String returnProperties = "returnProperties";
	protected IFreeMarkerSupport freeMarkerSupport = new FreeMarkerSupport();
	public final static String max_hits = "max";

	public String getTemplateName() {
		return null;
	}

	public boolean isHtmlbyTemplate() {
		return freeMarkerSupport.isHtmlbyTemplate();
	}

	public void setHtmlbyTemplate(boolean htmlbyTemplate) {
		freeMarkerSupport.setHtmlbyTemplate(htmlbyTemplate);
	}
	
	public void configureTemplateMap(Map<String, Object> map, Request request, IFreeMarkerApplication app) {
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
        map.put("creator",getClass().getName());
		
	}
	protected ResourceDoc documentation = new ResourceDoc();


	public ResourceDoc getDocumentation() {
		return documentation;
	}
	public void setDocumentation(ResourceDoc documentation) {
		this.documentation = documentation;
	}
	public AbstractResource() {
		super();
		//setAutoDescribed(true);
	}
	public String[] URI_to_handle() {
		return null;
	}

	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		try {ClientResourceWrapper.setTokenFactory(this);} catch (Exception x){}
		BotsGuard.checkForBots(getRequest());
		response_status = Status.SUCCESS_OK;
		queryObject = createQuery(getContext(), getRequest(), getResponse());
		error = null;

	}
	
	@Override
	protected void doRelease() throws ResourceException {
		try {ClientResourceWrapper.setTokenFactory(null);} catch (Exception x){}
		super.doRelease();
	}
	

	protected void customizeVariants(MediaType[] mimeTypes) {
       // List<Variant> variants = new ArrayList<Variant>();
        for (MediaType m:mimeTypes) getVariants().add(new Variant(m));
        //getVariants().put(Method.GET, variants);
        //getVariants().put(Method.POST, variants);
	}
	public abstract P createConvertor(Variant variant) throws AmbitException, ResourceException;
	
	protected   abstract  Q createQuery(Context context, Request request, Response response) throws ResourceException;
	
	protected  Q createUpdateQuery(Method method,Context context, Request request, Response response) throws ResourceException {
		return createQuery(context,request,response);
	}
	@Override
	public List<Variant> getVariants() {
		List<Variant> vars = super.getVariants();
		return vars;
	}
	
	protected void setTokenCookies(Variant variant, boolean secure) {
		CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
		cS.setSecure(secure);
		cS.setComment("OpenSSO token");
		cS.setPath("/");
        this.getResponse().getCookieSettings().add(cS);
        //
		cS = new CookieSetting(0, "subjectid_secure", Boolean.toString(secure));
		cS.setSecure(false);
		cS.setComment("Whether to transfer OpenSSO in secure token");
		cS.setPath("/");
        this.getResponse().getCookieSettings().add(cS);
       
	}
	
	protected Representation getHTMLByTemplate(Variant variant) throws ResourceException {
        Map<String, Object> map = new HashMap<String, Object>();
        if (getClientInfo().getUser()!=null) 
        	map.put("username", getClientInfo().getUser().getIdentifier());
        configureTemplateMap(map, getRequest(), (IFreeMarkerApplication)getApplication());
        return toRepresentation(map, getTemplateName(), MediaType.TEXT_PLAIN);
	}
	
	protected Representation toRepresentation(Map<String, Object> map,
            String templateName, MediaType mediaType) {
        
        return new TemplateRepresentation(
        		templateName,
        		(Configuration)((IFreeMarkerApplication)getApplication()).getConfiguration(),
        		map,
        		MediaType.TEXT_HTML);
}	  
	
	@Override
	protected Representation get(Variant variant) throws ResourceException {
	try {
			Form headers = (Form) getResponse().getAttributes().get("org.restlet.http.headers");
			if (headers == null) {
				headers = new Form();
				getResponse().getAttributes().put("org.restlet.http.headers", headers);
			}
			headers.add("X-Frame-Options", "SAMEORIGIN");
			
			List<CacheDirective> cache = new ArrayList<CacheDirective>();
			cache.add(new CacheDirective("Cache-Control","max-age=2700, private"));
			getResponse().setCacheDirectives(cache);
			
			ServerInfo si = getResponse().getServerInfo();si.setAgent("Restlet");getResponse().setServerInfo(si);
			setTokenCookies(variant, useSecureCookie(getRequest()));
	        // SEND RESPONSE
	        setStatus(Status.SUCCESS_OK);
	        /*
			if (variant.getMediaType().equals(MediaType.APPLICATION_WADL)) {
				WadlRepresentation wadl =  new WadlRepresentation(describe());
				//wadl.setApplication(((WadlApplication)getApplication()).getApplicationInfo(getRequest(), getResponse()));

				return wadl;
			} else	
			*/
	    	if (MediaType.APPLICATION_JAVA_OBJECT.equals(variant.getMediaType())) {
	    		if ((queryObject!=null) && (queryObject instanceof Serializable))
	    		return new ObjectRepresentation((Serializable)queryObject,MediaType.APPLICATION_JAVA_OBJECT);
	    		else throw new ResourceException(Status.CLIENT_ERROR_NOT_ACCEPTABLE);        		
	    	}
	        if (queryObject != null) {
	        	IProcessor<Q, Representation> convertor = null;

		        	try {
		        		getResponse().setStatus(response_status);
		        		convertor = createConvertor(variant);
			        	Representation r = convertor.process(queryObject);
			        	
			        	return r;
		        	} catch (NotFoundException x) {
		        		Representation r = processNotFound(x,variant);
		        		return r;
		    			
		        	} catch (RResourceException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return x.getRepresentation();
		    			
		        	} catch (ResourceException x) {
		    			getResponse().setStatus(x.getStatus());
		    			return null;
		        	} catch (Exception x) {

		    			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
		    			return null;
		        	} finally {
		        		
		        	}

	        	
	        } else {
	        	getResponse().setStatus(response_status==null?Status.CLIENT_ERROR_BAD_REQUEST:response_status,error);
	        	return null;	        	
	        }
		} catch (Exception x) {
			getResponse().setStatus(Status.SERVER_ERROR_INTERNAL,x);
			return null;
		} finally {
			close();
		}
	}		
	
	public void close() {
		
	}
	
	protected Representation processNotFound(NotFoundException x,Variant variant) throws Exception {

		throw new NotFoundException(x.getMessage());

	}
	/**
	 * Returns parameter value and throwsan exception if value is missing of mandatory parameter
	 * @param requestHeaders
	 * @param paramName
	 * @param mandatory
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName,String description, boolean mandatory) throws ResourceException {
		Object o = requestHeaders.getFirstValue(paramName);
		if (o == null)
			if (mandatory)	throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,String.format("Parameter %s [%s] is mandatory!", paramName,description));
			else return null;
		else return o.toString();
	}
	/**
	 * Calls {@link #getParameter(Form, String, boolean)} with false for the last argument
	 * @param requestHeaders
	 * @param paramName
	 * @return
	 * @throws ResourceException
	 */
	protected String getParameter(Form requestHeaders,String paramName,String description) throws ResourceException {
		return getParameter(requestHeaders, paramName,description, false);
	}
	
	@Override
	protected Representation post(Representation entity)
			throws ResourceException {
		return post(entity,null);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			return processAndGenerateTask(Method.POST, entity, variant,true);
		}
	}
	
	@Override
	protected Representation put(Representation entity, Variant variant)
			throws ResourceException {
		synchronized (this) {
			return processAndGenerateTask(Method.PUT, entity, variant,true);
		}
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		synchronized (this) {
			return processAndGenerateTask(Method.DELETE, null, variant,true);
		}
	}
	protected FactoryTaskConvertor getFactoryTaskConvertor(ITaskStorage storage) throws ResourceException {
		return new FactoryTaskConvertor<Object>(storage,getHTMLBeauty());
	}
	protected abstract Representation processAndGenerateTask(final Method method,Representation entity, final Variant variant, final boolean async) throws ResourceException ;
	/*
	 * 
	 * 
	@Override
	protected void describeGet(MethodInfo info) {
        info.setIdentifier("item");
        info.setDocumentation("To retrieve details of a specific item");

        Iterator<Variant> vars = getVariants().iterator();
        while (vars.hasNext()) {
        	Variant var = vars.next();
            RepresentationInfo repInfo = new RepresentationInfo(var.getMediaType());
            //repInfo.setXmlElement("item");
            repInfo.setDocumentation(String.format("%s representation",var.getMediaType()));
            info.getResponse().getRepresentations().add(repInfo);        	
        }


        FaultInfo faultInfo = new FaultInfo(Status.CLIENT_ERROR_NOT_FOUND,"Not found");
        faultInfo.setIdentifier("itemError");
        faultInfo.setMediaType(MediaType.TEXT_HTML);
        info.getResponse().getFaults().add(faultInfo);

	}
	
	*/
	protected String getUserName() {
		return getHeaderValue("user");
	}
	protected String getPassword() {
		return getHeaderValue("password");
	}	
	private String getHeaderValue(String tag) {
		try {
			Form headers = (Form) getRequest().getAttributes().get("org.restlet.http.headers");  
			if (headers==null) return null;
			return headers.getFirstValue(tag);
		} catch (Exception x) {
			return null;
		}
	}
	protected boolean useSecureCookie(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid_secure".equals(cookie.getName())) try {
				return Boolean.parseBoolean(cookie.getValue());
			} catch (Exception x) {
			}
		}
		//secure cookie by default
		return true;
	}
	
	protected String getTokenFromCookies(Request request) {
		for (Cookie cookie : request.getCookies()) {
			if ("subjectid".equals(cookie.getName()))
				return cookie.getValue();
		}
		return null;
	}
	@Override
	public String getToken() {
		String token = getHeaderValue("subjectid");
		
		if (token == null) token = getTokenFromCookies(getRequest());
		return token== null?null:token;
		 
	}

	protected HTMLBeauty getHTMLBeauty() {
		return new HTMLBeauty();
	}
	
	protected Reference getResourceRef(Request request) {
		//return request.getOriginalRef()==null?request.getResourceRef():request.getResourceRef();
		return request.getResourceRef();
	}	
}
