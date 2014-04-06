package net.idea.restnet.c.reporters;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.UUID;

import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.i.task.ITask;
import net.idea.restnet.i.task.ITaskResult;
import net.idea.restnet.i.task.ITaskStorage;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class TaskJSONReporter<USERID> extends TaskURIReporter<USERID> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9136099541989811170L;
	protected String comma = null;
	public TaskJSONReporter(ITaskStorage<USERID> storage) {
		super(storage);
	}
	public TaskJSONReporter(ITaskStorage<USERID> storage,Request request,ResourceDoc doc) {
		super(storage,request,doc);
	}
	protected TaskJSONReporter(ITaskStorage<USERID> storage,Reference baseRef,ResourceDoc doc) {
		super(storage,baseRef,doc);
	}	

	@Override
	public void processItem(UUID item, Writer output) {
		try {
			if (comma!=null) output.write(comma);

			ITask<ITaskResult,USERID> task = storage.findTask(item);
			output.write(task.toJSON());
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
	}	
	@Override
	public void footer(Writer output, Iterator<UUID> query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	@Override
	public void header(Writer output, Iterator<UUID> query) {
		try {
			output.write("{\"task\": [");
		} catch (Exception x) {}
		
	};
}
