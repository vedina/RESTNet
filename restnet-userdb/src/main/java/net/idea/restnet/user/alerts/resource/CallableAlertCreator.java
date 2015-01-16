package net.idea.restnet.user.alerts.resource;

import java.sql.Connection;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.query.IQueryUpdate;
import net.idea.restnet.db.update.CallableDBUpdateTask;
import net.idea.restnet.resources.Resources;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.alerts.db.AbstractAlertUpdate;
import net.idea.restnet.user.alerts.db.AddAlert;
import net.idea.restnet.user.alerts.db.DBAlert;
import net.idea.restnet.user.alerts.db.DeleteAlert;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

public class CallableAlertCreator extends CallableDBUpdateTask<DBAlert, Form, String> {
    protected AlertURIReporter<IQueryRetrieval<DBAlert>> reporter;
    protected DBUser user;
    protected DBAlert item;

    public CallableAlertCreator(Method method, DBAlert item, DBUser user,
	    AlertURIReporter<IQueryRetrieval<DBAlert>> reporter, Form input, String baseReference,
	    Connection connection, String token) {
	super(method, input, baseReference, connection, token);
	this.method = method;
	this.item = item;
	this.reporter = reporter;
	this.user = user;
	setTitle(String.format("%s%s", Method.DELETE.equals(method) ? "Delete saved search" : "Save search",
		item == null ? "" : item.getVisibleQuery()));
    }

    @Override
    protected DBAlert getTarget(Form input) throws Exception {
	if (Method.DELETE.equals(method))
	    return item;
	else if (Method.POST.equals(method)) {
	    DBAlert alert = new DBAlert();
	    alert.setUser(user);
	    for (DBAlert._fields field : DBAlert._fields.values()) {
		String value = input.getFirstValue(field.name());
		if (value != null)
		    field.setValue(alert, value);
	    }
	    return alert;
	} else if (Method.PUT.equals(method)) {
	    /*
	     * String newValue =
	     * input.getFirstValue(DBGroup.fields.name.name()); if
	     * (newValue!=null)
	     * item.setTitle(input.getFirstValue(DBGroup.fields.name.name()));
	     * newValue = input.getFirstValue(DBGroup.fields.ldapgroup.name());
	     * if (newValue!=null)
	     * item.setGroupName(input.getFirstValue(DBGroup.
	     * fields.ldapgroup.name())); return item;
	     */
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
	} else
	    throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected boolean isNewResource() {
	if (Method.POST.equals(method) && (user != null))
	    return false;
	return super.isNewResource();
    }

    @Override
    protected IQueryUpdate<Object, DBAlert> createUpdate(DBAlert alert) throws Exception {
	if (Method.POST.equals(method)) {
	    AbstractAlertUpdate update = new AddAlert(alert, user);
	    return update;
	} else if (Method.DELETE.equals(method)) {
	    AbstractAlertUpdate update = new DeleteAlert(alert, user);
	    return update;
	}
	// else if (Method.PUT.equals(method)) return new UpdateGroup(group);
	throw new ResourceException(Status.CLIENT_ERROR_METHOD_NOT_ALLOWED);
    }

    @Override
    protected String getURI(DBAlert alert) throws Exception {
	return reporter.getURI(alert);
    }

    @Override
    protected String getURI(DBAlert target, Method method) throws Exception {
	if (Method.DELETE.equals(method)) {
	    return String.format("%s%s", reporter.getBaseReference(), Resources.myaccount);
	} else
	    return getURI(target);
    }

    @Override
    protected Object executeQuery(IQueryUpdate<? extends Object, DBAlert> q) throws Exception {
	return super.executeQuery(q);
    }

}