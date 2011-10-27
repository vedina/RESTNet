package net.idea.restnet.c.task;

import java.util.UUID;

import net.idea.restnet.c.PageParams;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.data.Form;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * Mockup class.
 * Does nothing , just sleeps for delay ms (default 1min) and returns the URI specified in the constructor.
 * 
 * For testing purposes.
 * @author nina
 *
 */
public class CallableMockup<USERID> extends CallableProtectedTask<USERID> {
	protected long delay;
	protected Object resultURI;
	protected Exception error;
	protected UUID uuid;
	
	public UUID getUuid() {
		return uuid;
	}
	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}
	public CallableMockup(Form form,USERID token) {
		super(token);
		try {
			this.delay = Long.parseLong(PageParams.params.delay.getFirstValue(form).toString());
		} catch (Exception x) {
			this.delay = 30000;
		}
		try {
			this.error = new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,PageParams.params.error.getFirstValue(form).toString());
		} catch (Exception x) {
			this.error = null;
		}		
		///resultURI = PageParams.params.dataset_uri.getFirstValue(form);
		//if (resultURI==null) resultURI = PageParams.params.model_uri.getFirstValue(form);
		resultURI = null;
	}
	@Override
	public TaskResult doCall() throws Exception {

		Thread.sleep(delay);
		Thread.yield();
		if (error != null) throw error;
		if (resultURI==null) throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,"No result URI specified");
		else return new TaskResult(resultURI.toString());
	}
}
