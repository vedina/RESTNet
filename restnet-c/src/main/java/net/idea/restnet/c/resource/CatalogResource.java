package net.idea.restnet.c.resource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.PageParams;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.reporters.CatalogHTMLReporter;
import net.idea.restnet.c.reporters.CatalogURIReporter;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ICallableTask;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Algorithms as per http://opentox.org/development/wiki/Algorithms
 * @author nina
 *
 */
public abstract class CatalogResource<T extends Serializable> extends AbstractResource<Iterator<T>,T,IProcessor<Iterator<T>, Representation>> {
	protected int page = 0;
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
				MediaType.APPLICATION_RDF_XML,
				MediaType.APPLICATION_RDF_TURTLE,
				MediaType.TEXT_RDF_N3,
				MediaType.TEXT_RDF_NTRIPLES,
				MediaType.APPLICATION_JAVA_OBJECT,
				MediaType.TEXT_HTML,
				MediaType.APPLICATION_WADL
				});
		
	}


	@Override
	public IProcessor<Iterator<T>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		String filenamePrefix = getRequest().getResourceRef().getPath();
		if (variant.getMediaType().equals(MediaType.TEXT_HTML)) {
			return new StringConvertor(	createHTMLReporter(),MediaType.TEXT_HTML);
		} else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST)) {
			return new StringConvertor( createURIReporter()	,MediaType.TEXT_URI_LIST);
		} else if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML) ||
				variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_N3) ||
				variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES)
				) {
			return createRDFConvertor(variant,filenamePrefix);
		} else //uri 	
			return new StringConvertor( createURIReporter()	,MediaType.TEXT_URI_LIST,filenamePrefix);
		
	}
	public IProcessor<Iterator<T>, Representation> createRDFConvertor(
			Variant variant,String filenamePrefix) throws AmbitException, ResourceException {
		throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
	}	
	protected Reporter createURIReporter() {
		return
		new CatalogURIReporter<T>(getRequest(),getDocumentation());
	}
	
	protected Reporter createHTMLReporter() {
		return new CatalogHTMLReporter(getRequest(),getDocumentation(),getHTMLBeauty());
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
				Task<TaskResult,String> task =  ((TaskApplication)getApplication()).addTask(
						String.format("Apply %s %s %s",model.toString(),reference==null?"":"to",reference==null?"":reference),
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
	

	/*
	@Override
	public void describe(String arg0, ResourceInfo info) {
		// TODO Auto-generated method stub
		super.describe(arg0, info);
	}


	@Override
	protected void describePost(MethodInfo info) {
		// TODO Auto-generated method stub
		super.describePost(info);
	}
	@Override
	protected void describePut(MethodInfo info) {
		// TODO Auto-generated method stub
		super.describePut(info);
	}
	@Override
	protected void describeOptions(MethodInfo info) {
		info.setDocumentation("Not implemented");
		super.describeOptions(info);
	}
	@Override
	protected Representation describeVariants() {
		//TODO 
		return super.describeVariants();
	}
	@Override
	protected void describeDelete(MethodInfo info) {
        info.setDocumentation("Delete the current item.");

        RepresentationInfo repInfo = new RepresentationInfo();
        repInfo.setDocumentation("No representation is returned.");
        repInfo.getStatuses().add(Status.SUCCESS_NO_CONTENT);
        info.getResponse().getRepresentations().add(repInfo);
	}
	*/
}
