package net.idea.restnet.c.task;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.StringConvertor;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.c.reporters.TaskHTMLReporter;
import net.idea.restnet.c.reporters.TaskJSONReporter;
import net.idea.restnet.c.reporters.TaskURIReporter;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

public class FactoryTaskConvertor<USERID> {
    private HTMLBeauty htmlBeauty;
    protected ITaskStorage<USERID> storage;

    public FactoryTaskConvertor(ITaskStorage<USERID> storage) {
	this(storage, null);
    }

    public FactoryTaskConvertor(ITaskStorage<USERID> storage, HTMLBeauty htmlbeauty) {
	super();
	this.storage = storage;
	this.htmlBeauty = htmlbeauty;
    }

    public synchronized IProcessor<Iterator<UUID>, Representation> createTaskConvertor(Variant variant,
	    Request request, ResourceDoc doc) throws AmbitException, ResourceException {
	String filenamePrefix = request.getResourceRef().getPath();
	return new StringConvertor(createTaskReporter(variant, request, doc), variant.getMediaType(), filenamePrefix);
    }

    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporter(Variant variant, Request request,
	    ResourceDoc doc) throws AmbitException, ResourceException {

	Reporter<Iterator<UUID>, Writer> reporter = null;

	if (variant.getMediaType().equals(MediaType.APPLICATION_RDF_XML)
		|| variant.getMediaType().equals(MediaType.APPLICATION_RDF_TURTLE)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_N3)
		|| variant.getMediaType().equals(MediaType.TEXT_RDF_NTRIPLES))
	    reporter = createTaskReporterRDF(variant, request, doc);
	else if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT))
	    reporter = createTaskReporterJSON(variant, request, doc);
	else if (variant.getMediaType().equals(MediaType.APPLICATION_JSON))
	    reporter = createTaskReporterJSON(variant, request, doc);
	else if (variant.getMediaType().equals(MediaType.TEXT_URI_LIST))
	    reporter = createTaskReporterURI(request, doc);
	else if (variant.getMediaType().equals(MediaType.TEXT_HTML))
	    reporter = createTaskReporterHTML(request, doc, htmlBeauty);
	else
	    // MediaType.TEXT_URI_LIST
	    reporter = createTaskReporterURI(request, doc);
	return reporter;
    }

    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterURI(Request request, ResourceDoc doc)
	    throws AmbitException, ResourceException {

	return new TaskURIReporter<USERID>(storage, request, doc) {
	    @Override
	    public void processItem(UUID item, Writer output) {

		super.processItem(item, output);
		try {
		    output.write('\n');
		} catch (Exception x) {
		}
	    }
	};
    }

    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterJSON(Variant variant, Request request,
	    ResourceDoc doc) throws AmbitException, ResourceException {
	return new TaskJSONReporter(storage, request, doc);
    }

    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterRDF(Variant variant, Request request,
	    ResourceDoc doc) throws AmbitException, ResourceException {
	// return new
	// TaskRDFReporter<USERID>(storage,request,variant.getMediaType(),doc);
	throw new ResourceException(Status.SERVER_ERROR_NOT_IMPLEMENTED);
    }

    public synchronized Reporter<Iterator<UUID>, Writer> createTaskReporterHTML(Request request, ResourceDoc doc,
	    HTMLBeauty htmlbeauty) throws AmbitException, ResourceException {
	return new TaskHTMLReporter<USERID>(storage, request, doc, htmlbeauty);
    }

    public synchronized Representation createTaskRepresentation(UUID task, Variant variant, Request request,
	    Response response, ResourceDoc doc) throws ResourceException {
	try {
	    if (task == null)
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	    IProcessor<Iterator<UUID>, Representation> p = createTaskConvertor(variant, request, doc);
	    // task.update();
	    // System.out.println("convertor" + task.getUri() + " " +
	    // task.getStatus());
	    // response.setStatus(task.isDone()?Status.SUCCESS_OK:Status.SUCCESS_ACCEPTED);
	    // task.update();
	    // System.out.println("convertor" + response.getStatus());
	    return p.process(new SingleTaskIterator<USERID>(task));
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

    public synchronized Representation createTaskRepresentation(Iterator<UUID> tasks, Variant variant, Request request,
	    Response response, ResourceDoc doc) throws ResourceException {
	try {
	    // System.out.println("convertor" );
	    if (tasks == null)
		throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
	    IProcessor<Iterator<UUID>, Representation> p = createTaskConvertor(variant, request, doc);
	    return p.process(tasks);
	} catch (ResourceException x) {
	    throw x;
	} catch (Exception x) {
	    throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, x.getMessage(), x);
	}
    }

}
