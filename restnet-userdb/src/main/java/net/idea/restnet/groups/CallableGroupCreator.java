package net.idea.restnet.groups;

import java.net.URL;
import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.groups.db.CreateGroup;
import net.idea.restnet.groups.db.DeleteGroup;
import net.idea.restnet.groups.db.UpdateGroup;
import net.idea.restnet.groups.resource.GroupQueryURIReporter;
import net.idea.restnet.groups.user.db.AddGroupPerUser;
import net.idea.restnet.groups.user.db.DeleteGroupsPerUser;
import net.idea.restnet.user.DBUser;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class CallableGroupCreator extends CallableDBUpdateTask<IDBGroup,Form,String> {
	protected GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> reporter;
	protected GroupType type;
	protected DBUser user;
	protected IDBGroup item;
	
	public CallableGroupCreator(
						Method method,
						IDBGroup item,
						GroupType type,
						DBUser user,
						GroupQueryURIReporter<IQueryRetrieval<IDBGroup>> reporter,
						Form input, 
						String baseReference,
						Connection connection,String token)  {
		super(method,input,baseReference,connection,token);
		this.method = method;
		this.item = item;
		this.reporter = reporter;
		this.type = type;
		this.user = user;
	}

	@Override
	protected IDBGroup getTarget(Form input) throws Exception {
		if (Method.DELETE.equals(method)) return item;
		else if (Method.POST.equals(method)) {
			IDBGroup group = null; 
			switch (type) {
			case ORGANISATION: {
				group = new DBOrganisation();
				String uri = input.getFirstValue("organisation_uri");
				if (uri!=null)
				try {((DBOrganisation)group).setResourceURL(new URL(uri));
					group.setID(((DBOrganisation)group).parseURI(baseReference));
				} catch (Exception x) {};
				break;
			}
			case PROJECT: {
				group = new DBProject();
				String uri = input.getFirstValue("project_uri");
				if (uri!=null)
				try {
					((DBProject)group).setResourceURL(new URL(uri));
					group.setID(((DBProject)group).parseURI(baseReference));
				} catch (Exception x) { x.printStackTrace();}
				break;
			}
			}
			group.setTitle(input.getFirstValue(DBGroup.fields.name.name()));
			group.setGroupName(input.getFirstValue(DBGroup.fields.ldapgroup.name()));
	 		return group;
		} else if (Method.PUT.equals(method)) {
			String newValue = input.getFirstValue(DBGroup.fields.name.name());
			if (newValue!=null) 
				item.setTitle(input.getFirstValue(DBGroup.fields.name.name()));
			newValue = input.getFirstValue(DBGroup.fields.ldapgroup.name());
			if (newValue!=null)
				item.setGroupName(input.getFirstValue(DBGroup.fields.ldapgroup.name()));
			return item;
		} else throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}
	
	@Override
	protected boolean isNewResource() {
		if (Method.POST.equals(method) && (user !=null)) return false;
		return super.isNewResource();
	}

	@Override
	protected IQueryUpdate<Object, IDBGroup> createUpdate(IDBGroup group)
			throws Exception {
		if (Method.POST.equals(method)) {
			return user==null?new CreateGroup(group):new AddGroupPerUser(user, group);
		}
		else if (Method.DELETE.equals(method)) {
			return user==null?new DeleteGroup(group):new DeleteGroupsPerUser(user,group);
		}
		else if (Method.PUT.equals(method)) return new UpdateGroup(group);
		throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	}

	@Override
	protected String getURI(IDBGroup user) throws Exception {
		return reporter.getURI(user);
	}

	@Override
	protected Object executeQuery(IQueryUpdate<? extends Object, IDBGroup> q)
			throws Exception {
		return super.executeQuery(q);
	}

	@Override
	public String toString() {
		if (Method.POST.equals(method)) {
			return String.format("Create %s",item==null?"":item.getGroupType().name());
		} else if (Method.PUT.equals(method)) {
			return String.format("Update %s",item==null?"":item.getGroupType().name());
		} else if (Method.DELETE.equals(method)) {
			return String.format("Delete %s",item==null?"":item.getGroupType().name());
		}
		return item==null?"Read":item.getGroupType().name();
	}
}
