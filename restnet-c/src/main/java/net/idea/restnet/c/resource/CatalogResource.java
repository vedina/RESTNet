package net.idea.restnet.c.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.PageParams;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.reporters.CatalogHTMLReporter;
import net.idea.restnet.c.reporters.CatalogURIReporter;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskApplication;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.data.CacheDirective;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.ServerInfo;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public abstract class CatalogResource<T> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {
	protected int page = 0;
	protected boolean headless = false;
	
	
	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public long getPageSize() {
		return pageSize;
	}


	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	protected long pageSize = 100;
	@Override
	protected void doInit() throws ResourceException {
		super.doInit();
		customizeVariants(new MediaType[] {
				MediaType.TEXT_URI_LIST,
				MediaType.APPLICATION_JSON,
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.TEXT_HTML,
				MediaType.TEXT_CSV,
				MediaType.APPLICATION_WADL
				});
		headless = getHeadlessParam();
	}



	protected boolean getHeadlessParam() {
		Form form = getRequest().getResourceRef().getQueryAsForm();
		try {
			return Boolean.parseBoolean(form.getFirstValue("headless").toString());
		} catch (Exception x) {
			return false;
		}	
	}

	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(	createHTMLReporter(headless),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor( createURIReporter()	,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON)){
			return createJSONConvertor(variant,filenamePrefix);
		} else if (variant.getMediaType().equals(MediaType.TEXT_CSV)){
			return createCSVConvertor(variant,filenamePrefix);						
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) {
			return createRDFConvertor(variant,filenamePrefix);
		} else //uri 	
			return createDefaultConvertor(variant, filenamePrefix);
		
	}
	public IProcessor<Iterator<T>, Representation> createRDFConvertor(
			Variant variant,String filenamePrefix) throws AmbitException, ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}	
	public IProcessor<Iterator<T>, Representation> createJSONConvertor(
			Variant variant,String filenamePrefix) throws AmbitException, ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}	
	public IProcessor<Iterator<T>, Representation> createCSVConvertor(
			Variant variant,String filenamePrefix) throws AmbitException, ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	public IProcessor<Iterator<T>, Representation> createDefaultConvertor(
			Variant variant,String filenamePrefix) throws AmbitException, ResourceException {
		return new StringConvertor( createURIReporter()	,MediaType.TEXT_URI_LIST,filenamePrefix);
	}	
	protected Reporter createURIReporter() {
		return
		new CatalogURIReporter<T>(getRequest(),getDocumentation());
	}
	
	protected Reporter createHTMLReporter(boolean headles) {
		return new CatalogHTMLReporter(getRequest(),getDocumentation(),getHTMLBeauty(),headless);
	}
	
	
	protected ICallableTask createCallable(Method method,Form form, T item) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}
	
	protected Reference getSourceReference(Form form,T model) throws ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
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
	
	protected String getTaskTitle(T item,Reference source) {
		return String.format("Apply %s %s %s",
				item==null?"":item.toString(),source==null?"":"to",source==null?"":source);
	}
	@Override
	protected Representation processAndGenerateTask(Method method,Representation entity, final Variant variant, final boolean async) throws ResourceException {
			
			Form form = entity.isAvailable()?new Form(entity):new Form();
			
			//models
			Iterator<T> query = queryObject;
			if (query==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
			
			ArrayList<UUID> tasks = new ArrayList<UUID>();
			while (query.hasNext()) 
			try {
				T model = query.next();
				Reference reference = getSourceReference(form,model);
				ICallableTask callable= createCallable(method,form,model);
				ITask<ITaskResult,String> task =  ((ITaskApplication)getApplication()).addTask(
						getTaskTitle(model,reference),
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
				
				ITaskStorage storage = ((ITaskApplication)getApplication()).getTaskStorage();
				FactoryTaskConvertor<Object> tc = getFactoryTaskConvertor(storage);
				if (tasks.size()==1)
					return tc.createTaskRepresentation(tasks.get(0), variant, getRequest(),getResponse(),getDocumentation());
				else
					return tc.createTaskRepresentation(tasks.iterator(), variant, getRequest(),getResponse(),getDocumentation());				
			}
	
	}
	
	protected void setPaging(Form form) {
		String max = form.getFirstValue(max_hits);
		String page = form.getFirstValue(PageParams.params.page.toString());
		String pageSize = form.getFirstValue(PageParams.params.pagesize.toString());
		if (max != null)
		try {
			setPage(0);
			setPageSize(Long.parseLong(form.getFirstValue(max_hits).toString()));
			return;
		} catch (Exception x) {
			
		}
		try {
			setPage(Integer.parseInt(page));
		} catch (Exception x) {
			x.printStackTrace();
			setPage(0);
		}
		try {
			setPageSize(Long.parseLong(pageSize));
		} catch (Exception x) {
			x.printStackTrace();
			setPageSize(1000);
		}			
	}
	

	@Override
	protected Representation get(Variant variant) throws ResourceException {
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
		if (isHtmlbyTemplate() && MediaType.TEXT_HTML.equals(variant.getMediaType())) {
			CookieSetting cS = new CookieSetting(0, "subjectid", getToken());
			cS.setPath("/");
	        this.getResponse().getCookieSettings().add(cS);
	        return getHTMLByTemplate(variant);
    	} else				
    		return super.get(variant);
	}
}
