package net.idea.restnet.groups.resource;

import java.io.IOException;
import java.io.Writer;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.groups.IDBGroup;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class GroupJSONReporter <Q extends IQueryRetrieval<IDBGroup>>  extends QueryReporter<IDBGroup,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected String comma = null;
	protected Request request;
	protected QueryURIReporter uriReporter;
	
	public Request getRequest() {
		return request;
	}
	public void setRequest(Request request) {
		this.request = request;
	}
	protected Reference baseReference;
	public Reference getBaseReference() {
		return baseReference;
	}

	public GroupJSONReporter(Request request) {
		this.baseReference = (request==null?null:request.getRootRef());
		setRequest(request);
		uriReporter = new GroupQueryURIReporter(request,"");
	
	}	

	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": %s,\n\t\"groupname\": \"%s\",\n\t\"title\": \"%s\"\n}";
	//output.write("Title,First name,Last name,user name,email,Keywords,Reviewer\n");

	@Override
	public Object processItem(IDBGroup user) throws Exception {
		try {
			if (comma!=null) getOutput().write(comma);
			
			String uri = user.getID()>0?uriReporter.getURI(user):"";
			
			getOutput().write(String.format(format,
					uri,
					(user.getID()>0)?String.format("\"G%d\"",user.getID()):null,
					user.getGroupName()==null?"":user.getGroupName(),
					user.getTitle()==null?"":user.getTitle()
					));
			comma = ",";
		} catch (IOException x) {
			Context.getCurrentLogger().severe(x.getMessage());
		}
		return null;
	}	
	@Override
	public void footer(Writer output, Q query) {
		try {
			output.write("\n]\n}");
		} catch (Exception x) {}
	};
	@Override
	public void header(Writer output, Q query) {
		try {
			output.write("{\"group\": [");
		} catch (Exception x) {}
		
	};
	
	public void open() throws DbAmbitException {
		
	}
	@Override
	public void close() throws Exception {
		setRequest(null);
		super.close();
	}
	@Override
	public String getFileExtension() {
		return null;
	}
}