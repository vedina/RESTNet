package net.idea.restnet.rdf;

import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.reporter.Reporter;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.rdf.reporter.TaskRDFReporter;

import org.restlet.Request;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * Same as the parent class, with RDF support
 * @author nina
 *
 * @param <USERID>
 */
public class FactoryTaskConvertorRDF<USERID> extends FactoryTaskConvertor<USERID> {

	public FactoryTaskConvertorRDF(ITaskStorage<USERID> storage) {
		super(storage);
	}

	@Override
	public synchronized Reporter<Iterator<UUID>,Writer> createTaskReporterRDF(
			Variant variant, Request request,ResourceDoc doc) throws AmbitException, ResourceException {
		return new TaskRDFReporter<USERID>(storage,request,variant.getMediaType(),doc);
   }	

}
