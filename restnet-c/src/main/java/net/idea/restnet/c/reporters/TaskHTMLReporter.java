package net.idea.restnet.c.reporters;

import java.io.Writer;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import net.idea.restnet.c.AbstractResource;
import net.idea.restnet.c.ResourceDoc;
import net.idea.restnet.c.SimpleTaskResource;
import net.idea.restnet.c.html.HTMLBeauty;
import net.idea.restnet.i.task.ITaskStorage;
import net.idea.restnet.i.task.Task;
import net.idea.restnet.i.task.Task.TaskStatus;
import net.idea.restnet.i.task.TaskResult;

import org.restlet.Request;

public class TaskHTMLReporter<USERID> extends CatalogURIReporter<UUID> {
	protected boolean headless = false;
	public boolean isHeadless() {
		return headless;
	}


	public void setHeadless(boolean headless) {
		this.headless = headless;
	}
	protected ITaskStorage<USERID> storage;
	protected HTMLBeauty htmlBeauty;
	public HTMLBeauty getHtmlBeauty() {
		return htmlBeauty;
	}


	public void setHtmlBeauty(HTMLBeauty htmlBeauty) {
		this.htmlBeauty = htmlBeauty;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7644836050657868159L;
	public TaskHTMLReporter(ITaskStorage<USERID> storage,Request ref,ResourceDoc doc) {
		this(storage,ref,doc,null);
	}
	public TaskHTMLReporter(ITaskStorage<USERID> storage,Request ref,ResourceDoc doc,HTMLBeauty htmlbeauty) {
		super(ref,doc);
		this.storage = storage;
		this.htmlBeauty = htmlbeauty;
	}
	

	@Override
	public void header(Writer output, Iterator<UUID> query) {
		if (headless) return;
		try {
			if (htmlBeauty==null) htmlBeauty = new HTMLBeauty();
			htmlBeauty.writeHTMLHeader(output, htmlBeauty.getTitle(), getRequest(),"<meta http-equiv=\"refresh\" content=\"10\">",
					getDocumentation()
					);//,"<meta http-equiv=\"refresh\" content=\"10\">");
			
			printNavigation();
			output.write("<table>");
			output.write("<tr><th>Start time</th><th>Elapsed time,ms</th><th>Task</th><th>Name</th><th colspan='2'>Status</th><th></th></tr>");
		} catch (Exception x) {
			
		}
	}
	
	protected void printNavigation() throws Exception {
		String max = getRequest().getResourceRef().getQueryAsForm().getFirstValue(AbstractResource.max_hits);
		max = max==null?"10":max;
		output.write("<h4>Tasks:");
		for (TaskStatus status :TaskStatus.values())
			output.write(String.format("<a href='%s%s?search=%s&%s=%s'>%s</a>&nbsp;",
					baseReference,SimpleTaskResource.resource,status,AbstractResource.max_hits,max,status));
		output.write("</h4><p>");
	}
	@Override
	public void processItem(UUID name, Writer output) {
		Task<TaskResult,USERID> item = storage.findTask(name);
		String t = "";
		String status = "Unknown";
		try {
			t = item.getUri()==null?"":item.getUri().toString();
			status = item.getStatus().toString();
		} catch (Exception x) {
			x.printStackTrace();
			status = "Error";
			t = x.getMessage();
		} finally {
			try {output.write(
					String.format("<tr><td>%s</td><td>%s</td><td><a href='%s%s/%s'>%s</a></td><td><a href='%s'>%s</a></td><td><img src=\"%s/images/%s\"></td><td>%s</td><td>%s</td><td>%s</td></tr>",
							new Date(item.getStarted()),
							item.getTimeCompleted()>0?item.getTimeCompleted()-item.getStarted():"",
							baseReference.toString(),
							SimpleTaskResource.resource,
							item.getUuid(),
							item.getUuid(),
							t,item.getName(),
							baseReference.toString(),
							item.isDone()?"tick.png":"progress.gif",
							status,
							item.getError()==null?"":item.getError().getMessage(),
							item.getPolicyError()==null?"":item.getPolicyError().getMessage()		
							)); } catch (Exception x) {
				x.printStackTrace();
			}
		}
	};
	@Override
	public void footer(Writer output, Iterator<UUID> query) {
		if (headless) return;
		try {
			output.write("</table>");
			//output.write("<form name=\"myForm\"><input type=BUTTON value=\"Stop polling\" onClick=\"stopPolling()\"></form>");
			if (htmlBeauty==null) htmlBeauty = new HTMLBeauty();
			htmlBeauty.writeHTMLFooter(output, "", getRequest());
			

			output.flush();
		} catch (Exception x) {
			
		}
	}
}