package net.idea.restnet.user;

import java.net.URL;
import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.aalocal.user.UpdateUser;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.DBProject;
import net.idea.restnet.groups.user.db.AddGroupsPerUser;
import net.idea.restnet.u.db.DeleteUser;
import net.idea.restnet.user.db.ReadUser;
import net.idea.restnet.user.resource.UserURIReporter;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class CallableUserCreator extends CallableDBUpdateTask<DBUser,Form,String> {
	protected UserURIReporter<IQueryRetrieval<DBUser>> reporter;
	protected DBUser user;
	
	public CallableUserCreator(Method method,DBUser item,UserURIReporter<IQueryRetrieval<DBUser>> reporter,
						Form input,
						String baseReference,
						Connection connection,String token)  {
		super(method, input,connection,token);
		this.reporter = reporter;
		this.user = item;
		this.baseReference = baseReference;
	}

	@Override
	protected DBUser getTarget(Form input) throws Exception {
		if (input==null) return user;
		
		DBUser user = new DBUser();
		user.setUserName(input.getFirstValue(ReadUser.fields.username.name()));
		user.setFirstname(input.getFirstValue(ReadUser.fields.firstname.name()));
		user.setLastname(input.getFirstValue(ReadUser.fields.lastname.name()));
		user.setTitle(input.getFirstValue(ReadUser.fields.title.name()));
		try {user.setHomepage(new URL(input.getFirstValue(ReadUser.fields.homepage.name()))); } catch (Exception x) {}
		try {user.setWeblog(new URL(input.getFirstValue(ReadUser.fields.weblog.name())));} catch (Exception x) {}
		
		String[] values = input.getValuesArray("organisation_uri");
		if (values != null)
			for (String value:values) try { 
				DBOrganisation org = new DBOrganisation();
				org.setResourceURL(new URL(value));
				org.setID(org.parseURI(baseReference));
				if (org.getID()>0) user.addOrganisation(org);
			} catch (Exception x) {}

		values = input.getValuesArray("project_uri");	
		if (values != null)
			for (String value:values) try { 
				DBProject org = new DBProject();
				org.setResourceURL(new URL(value));
				org.setID(org.parseURI(baseReference));
				if (org.getID()>0) user.addProject(org);
			} catch (Exception x) {}		
 		return user;
	}

	@Override
	protected IQueryUpdate<Object, DBUser> createUpdate(DBUser user)
			throws Exception {
		if (Method.POST.equals(method)) return  new CreateUser(user);
		else if (Method.DELETE.equals(method)) return  new DeleteUser(user);
		else if (Method.PUT.equals(method)) return new  UpdateUser(user);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(DBUser user) throws Exception {
		return reporter.getURI(user);
	}

	@Override
	protected Object executeQuery(IQueryUpdate<Object, DBUser> query)
			throws Exception {
		Object result = super.executeQuery(query);
		if (Method.POST.equals(method)) {
			DBUser user = query.getObject();
			if ((user.getOrganisations()!=null) && (user.getOrganisations().size()>0)) {
				AddGroupsPerUser q = new AddGroupsPerUser(user,user.getOrganisations());
				exec.process(q);
			}
			if ((user.getProjects()!=null) && (user.getProjects().size()>0)) {
				AddGroupsPerUser q = new AddGroupsPerUser(user,user.getProjects());
				exec.process(q);
			}			
		}
		return result;
	}

}
