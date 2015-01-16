package net.idea.restnet.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractObjectUpdate;
import net.idea.restnet.groups.DBOrganisation;
import net.idea.restnet.groups.db.CreateGroup;
import net.idea.restnet.groups.user.db.AddGroupByName;
import net.idea.restnet.user.DBUser;

public class UpdateUser extends AbstractObjectUpdate<DBUser> {
    protected CreateGroup createOrg;
    protected AddGroupByName<DBOrganisation> addOrg;
    protected DBUser.fields[] update_fields = { DBUser.fields.email, DBUser.fields.title, DBUser.fields.firstname,
	    DBUser.fields.lastname, DBUser.fields.homepage, DBUser.fields.keywords, DBUser.fields.reviewer };

    private String sql = "update user set %s where iduser = ?";
    private String sql_deleteorgs = "delete from user_organisation where iduser = ?";

    public UpdateUser(DBUser user) {
	super(null);
	setObject(user);
    }

    public UpdateUser() {
	this(null);
    }

    @Override
    public void setObject(DBUser user) {
	super.setObject(user);
	if ((user == null) || (user.getOrganisations() == null) || user.getOrganisations().size() == 0) {
	    // skip
	} else {
	    DBOrganisation org = (DBOrganisation) user.getOrganisations().get(0);
	    addOrg = new AddGroupByName<DBOrganisation>(null, null);
	    createOrg = new CreateGroup(null);
	    addOrg.setGroup(user);
	    addOrg.setObject(org);
	    createOrg.setObject(org);
	}
    }

    public List<QueryParam> getParameters(int index) throws AmbitException {
	List<QueryParam> params = new ArrayList<QueryParam>();
	switch (index) {
	case 0: {
	    for (DBUser.fields field : update_fields) {
		if (field.getValue(getObject()) != null)
		    params.add(field.getParam(getObject()));
	    }
	    if (params.size() == 0)
		throw new AmbitException("No parameters");
	    params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
	    return params;
	}
	case 1: {
	    return createOrg.getParameters(0);
	}
	case 2: {
	    params.add(new QueryParam<Integer>(Integer.class, getObject().getID()));
	    return params;
	}
	default: {
	    return addOrg.getParameters(0);
	}
	}

    }

    public String[] getSQL() throws AmbitException {

	StringBuilder b = null;
	for (DBUser.fields field : update_fields) {
	    if (field.getValue(getObject()) != null) {
		if (b == null)
		    b = new StringBuilder();
		else
		    b.append(", ");
		b.append(field.getSQL());
	    }
	}
	return (createOrg == null) || (addOrg == null) ? new String[] { String.format(sql, b) } : new String[] {
		String.format(sql, b), createOrg.getSQL()[0], sql_deleteorgs, addOrg.getSQL()[0] };
    }

    public void setID(int index, int id) {
	if (index == 1)
	    createOrg.setID(0, id);
    }

    @Override
    public boolean returnKeys(int index) {
	return index == 1;
    }
}