package net.idea.restnet.user.alerts.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.c.exception.InvalidUserException;
import net.idea.restnet.user.DBUser;

/**
 * Adds an alert
 * 
 * @author nina
 * 
 */
public class AddAlert extends AbstractAlertUpdate<DBUser> {
    public static final String[] sql_addAlert_byuserid = new String[] { "insert into alert (name,query,qformat,rfrequency,rinterval,iduser,sent) values (?,?,?,?,?,?,now()) " };
    public static final String[] sql_addAlert_byusername = new String[] { "insert into alert "
	    + "SELECT null,?,?,?,?,?,iduser,now(),now() from user where username=? " };

    public AddAlert(DBAlert alert, DBUser author) {
	super(alert);
	setGroup(author);
    }

    public AddAlert() {
	this(null, null);
    }

    public List<QueryParam> getParameters(int index) throws AmbitException {
	if (getGroup() == null)
	    throw new InvalidUserException();
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<String>(String.class, getObject().getTitle() == null ? getObject().getQuery()
		.getContent() : getObject().getTitle()));
	params.add(new QueryParam<String>(String.class, getObject().getQuery().getContent()));
	params.add(new QueryParam<String>(String.class, getObject().getQuery().getType().name()));
	params.add(new QueryParam<String>(String.class, getObject().getRecurrenceFrequency().name()));
	params.add(new QueryParam<Integer>(Integer.class, getObject().getRecurrenceInterval()));
	if (getGroup().getID() > 0)
	    params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
	else if (getGroup().getUserName() != null)
	    params.add(new QueryParam<String>(String.class, getGroup().getUserName()));
	else
	    throw new InvalidUserException();
	return params;

    }

    public String[] getSQL() throws AmbitException {
	if (getGroup() == null)
	    throw new InvalidUserException();
	else if (getGroup().getID() > 0)
	    return sql_addAlert_byuserid;
	else if (getGroup().getUserName() != null)
	    return sql_addAlert_byusername;
	throw new InvalidUserException();
    }

    public void setID(int index, int id) {
	getObject().setID(id);
    }

    @Override
    public boolean returnKeys(int index) {
	return true;
    }
}