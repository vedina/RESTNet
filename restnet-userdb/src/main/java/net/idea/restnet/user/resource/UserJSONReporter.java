package net.idea.restnet.user.resource;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import net.idea.modbcum.i.IQueryCondition;
import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.DbAmbitException;
import net.idea.modbcum.p.DefaultAmbitProcessor;
import net.idea.modbcum.p.MasterDetailsProcessor;
import net.idea.modbcum.r.QueryReporter;
import net.idea.restnet.db.QueryURIReporter;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.db.ReadOrganisation;
import net.idea.restnet.groups.resource.GroupQueryURIReporter;
import net.idea.restnet.user.DBUser;
import net.toxbank.client.resource.Organisation;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.data.Reference;

public class UserJSONReporter <Q extends IQueryRetrieval<DBUser>>  extends QueryReporter<DBUser,Q,Writer>  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4566136103208284105L;
	protected String comma = null;
	protected Request request;
	protected QueryURIReporter uriReporter;
	protected GroupQueryURIReporter groupURIReporter ;
	
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

	public UserJSONReporter(Request request) {
		this.baseReference = (request==null?null:request.getRootRef());
		setRequest(request);
		uriReporter = new UserURIReporter(request,"");
		
		getProcessors().clear();
		groupURIReporter = new GroupQueryURIReporter(request);
		IQueryRetrieval<DBOrganisation> queryO = new ReadOrganisation(new DBOrganisation()); 
		
		MasterDetailsProcessor<DBUser, DBOrganisation, IQueryCondition> orgReader = new MasterDetailsProcessor<DBUser, DBOrganisation, IQueryCondition>(queryO) {
			@Override
			protected DBUser processDetail(DBUser target, DBOrganisation detail) throws Exception {
				if (target.getID()>0) {
					detail.setResourceURL(new URL(groupURIReporter.getURI(detail)));
					target.addOrganisation(detail);
				}
				return target;
			}
		};
		
		getProcessors().add(orgReader);
		processors.add(new DefaultAmbitProcessor<DBUser, DBUser>() {
			public DBUser process(DBUser target) throws Exception {
				processItem(target);
				return target;
			};
		});			
	}	

	private static String format = "\n{\n\t\"uri\":\"%s\",\n\t\"id\": %s,\n\t\"username\": \"%s\",\n\t\"title\": \"%s\",\n\t\"firstname\": \"%s\",\n\t\"lastname\": \"%s\",\n\t\"email\": \"%s\",\n\t\"homepage\": \"%s\",\n\t\"keywords\": \"%s\",\n\t\"reviewer\": %s,\n\t\"organisation\": [\n\t\t%s\n\t]\n}";
	private static String formatGroup = "{\n\t\t\"uri\":\"%s\",\n\t\t\"title\": \"%s\"\n\t\t}";
	//output.write("Title,First name,Last name,user name,email,Keywords,Reviewer\n");

	@Override
	public Object processItem(DBUser user) throws Exception {
		try {
			if (comma!=null) getOutput().write(comma);
			
			StringBuilder group = null;
			if (user.getOrganisations()!=null)
				for (Organisation org : user.getOrganisations()) {
					if (group == null) group = new StringBuilder();
					else group.append(",");
					group.append(String.format(formatGroup,
							groupURIReporter.getURI(org),
							org.getTitle()
							));
				}

			String uri = user.getID()>0?uriReporter.getURI(user):"";
			
			getOutput().write(String.format(format,
					uri,
					(user.getID()>0)?String.format("\"U%s\"",user.getID()):null,
					user.getUserName()==null?"":user.getUserName(),
					user.getTitle()==null?"":user.getTitle(),
					user.getFirstname()==null?"":user.getFirstname(),
					user.getLastname()==null?"":user.getLastname(),
					user.getEmail()==null?"":user.getEmail(),
					user.getHomepage()==null?"":user.getHomepage(),
					user.getKeywords()==null?"":user.getKeywords(),
					user.isReviewer(),
					group==null?"":group.toString()
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
			output.write("{\"user\": [");
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