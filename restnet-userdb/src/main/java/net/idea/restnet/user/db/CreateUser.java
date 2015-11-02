package net.idea.restnet.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.b.Organisation;
import net.idea.restnet.db.aalocal.DBRole;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.db.CreateGroup;
import net.idea.restnet.groups.user.db.AddGroupByName;
import net.idea.restnet.u.UserCredentials;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.u.db.CreateRegistration;
import net.idea.restnet.user.DBUser;

public class CreateUser extends AbstractUpdate<UserCredentials, DBUser> implements IDBConfig {
    private static String user_sql = "insert into `%s`.user (iduser,username,email,title,firstname,lastname,weblog,homepage,keywords,reviewer,institute) values (?,?,?,?,?,?,?,?,?,?,?)";

    protected CreateRegistration registerUser;
    protected CreateGroup createOrg;
    protected AddGroupByName<DBOrganisation> orgs;

    public CreateUser(DBUser user, UserRegistration reg, String dbname) {
	this(user, reg, new DBRole("user", "Any user"), dbname);
    }

    public CreateUser(DBUser user, UserRegistration reg, DBRole role, String dbname) {
	super(user);
	createOrg = new CreateGroup(null);
	orgs = new AddGroupByName<DBOrganisation>(user, null);
	registerUser = new CreateRegistration(user, reg, role, dbname);
	setObject(user);
	setGroup(user.getCredentials());
	setDatabaseName(dbname);
    }

    @Override
    public void setObject(DBUser object) {
	super.setObject(object);
	if (registerUser != null)
	    registerUser.setGroup(object);

	if (orgs != null) {
	    if (object.getOrganisations() == null || object.getOrganisations().size() == 0) {
		DBOrganisation empty = new DBOrganisation();
		empty.setTitle("Not specified");
		orgs.setObject(empty);
		createOrg.setObject(empty);
	    } else {
		for (Organisation o : object.getOrganisations()) {
		    orgs.setObject((DBOrganisation) o);
		    if (createOrg != null)
			createOrg.setObject((DBOrganisation) o);
		    break;
		}
	    }
	    orgs.setGroup(object);
	}
    }

    @Override
    public String[] getSQL() throws AmbitException {
	String[] sql1 = createOrg.getSQL();
	String[] sql2 = orgs.getSQL();
	String[] sql3 = registerUser.getSQL();
	String[] newsql = new String[sql1.length + sql2.length + sql3.length + 1];
	newsql[0] = String.format(user_sql, getDatabaseName());
	for (int i = 0; i < sql1.length; i++)
	    newsql[i + 1] = sql1[i];
	for (int i = 0; i < sql2.length; i++)
	    newsql[i + sql1.length + 1] = sql2[i];
	for (int i = 0; i < sql3.length; i++)
	    newsql[i + sql1.length + sql2.length + 1] = sql3[i];

	return newsql;
    }

    @Override
    public List<QueryParam> getParameters(int index) throws AmbitException {

	List<QueryParam> params1 = new ArrayList<QueryParam>();
	switch (index) {
	case 0: {
	    params1.add(new QueryParam<Integer>(Integer.class, null));
	    params1.add(new QueryParam<String>(String.class, getObject().getUserName()));
	    params1.add(new QueryParam<String>(String.class, getObject().getEmail()));
	    params1.add(new QueryParam<String>(String.class, getObject().getTitle()));
	    params1.add(new QueryParam<String>(String.class, getObject().getFirstname()));
	    params1.add(new QueryParam<String>(String.class, getObject().getLastname()));
	    params1.add(new QueryParam<String>(String.class, getObject().getWeblog() == null ? null : getObject()
		    .getWeblog().toString()));
	    params1.add(new QueryParam<String>(String.class, getObject().getHomepage() == null ? null : getObject()
		    .getHomepage().toString()));
	    params1.add(new QueryParam<String>(String.class, getObject().getKeywords() == null ? "" : getObject()
		    .getKeywords()));
	    params1.add(new QueryParam<Boolean>(Boolean.class, getObject().isReviewer()));
	    params1.add(new QueryParam<String>(String.class, getObject().getOrganisations() == null ? "" : getObject()
		    .getOrganisations().size() == 0 ? "" : getObject().getOrganisations().get(0).getTitle()));
	    return params1;
	}
	case 1:
	    return createOrg.getParameters(0);
	case 2:
	    return orgs.getParameters(0);
	default:
	    return registerUser.getParameters(index - 3);
	}
    }

    @Override
    public void setID(int index, int id) {
	switch (index) {
	case 0: {
	    getObject().setID(id);
	    break;
	}
	case 1: {
	    createOrg.setID(0, id);
	    break;
	}
	case 2: {
	    break;
	}
	default: {
	    registerUser.setID(index - 3, id);
	}
	}
    }

    @Override
    public boolean returnKeys(int index) {
	return true;
    }

    @Override
    public void setDatabaseName(String name) {
	registerUser.setDatabaseName(name);
    }

    @Override
    public String getDatabaseName() {
	return registerUser.getDatabaseName();
    }
}
