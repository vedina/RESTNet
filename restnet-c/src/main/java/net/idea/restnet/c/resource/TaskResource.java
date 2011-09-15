package net.idea.restnet.c.resource;

import java.util.Iterator;
import java.util.UUID;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.processors.IProcessor;
import net.idea.restnet.c.SimpleTaskResource;
import net.idea.restnet.c.TaskApplication;
import net.idea.restnet.c.task.FactoryTaskConvertor;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;

/**
 * http://opentox.org/wiki/opentox/Asynchronous_jobs
 * Provide (read)access to running tasks  under URL http://host/application/task. 
 * Provide (read)access to a single task  under URL http://host/application/task/{taskid}. 
 * Task identifiers are unique, generated via {@link UUID} class. 
 * <br>
 * An URL with a task identifier is returned when an asynchronous job is submitted via {@link AsyncJobResource}
 * If accepted, the status code is 201 and the URI of the task resource in the Location header /task/{id} 
 * <br>
 * If a list of tasks has been requested, returns list of URLs of the tasks TODO - introduce Guards for protecting sensitive resources
 * <br>
 *  If a single task is requested and the task is not completed,returns Status code 202  (accepted, processing has not been completed). 
 * <br>
 * If a single task is requested and the task is completed, returns Status code 303 and the new URL in the "Location" header
<pre>
HTTP/1.1 303 See Other
Location: http://example.org/thenewurl
</pre
 * @author nina
 *
 */
public class TaskResource<USERID> extends SimpleTaskResource<USERID> {

	
	@Override
	public synchronized IProcessor<Iterator<UUID>, Representation> createConvertor(
			Variant variant) throws AmbitException, ResourceException {
		ITaskStorage<USERID> storage = ((TaskApplication)getApplication()).getTaskStorage();
		FactoryTaskConvertor<USERID> tc = new FactoryTaskConvertor<USERID>(storage);
	
		return tc.createTaskConvertor(variant, getRequest(),getDocumentation());

	}

}
