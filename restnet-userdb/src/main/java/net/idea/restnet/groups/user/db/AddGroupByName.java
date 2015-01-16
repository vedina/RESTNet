package net.idea.restnet.groups.user.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.groups.GroupType;
import net.idea.restnet.groups.IDBGroup;
import net.idea.restnet.user.DBUser;

public class AddGroupByName<P extends IDBGroup> extends AbstractUpdate<DBUser, P> {
    private static final String sql_addGroupSelect = "insert ignore into user_%s select ?,id%s,1 from user join organisation where iduser=? and organisation.name=? ";

    public AddGroupByName(DBUser user, P group) {
	super();
	setObject(group);
	setGroup(user);
    }

    @Override
    public String[] getSQL() throws AmbitException {
	GroupType gt = getObject().getGroupType();
	return new String[] { String.format(sql_addGroupSelect, gt.getDBname(), gt.getDBname(), gt.getDBname(),
		getObject().getTitle()) };
    }

    @Override
    public List<QueryParam> getParameters(int index) throws AmbitException {
	if ((getGroup() == null) || (getGroup().getID() <= 0))
	    throw new AmbitException("No user!");
	if ((getObject() == null) || (getObject().getTitle() == null) || "".equals(getObject().getTitle()))
	    throw new AmbitException("No group!");
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
	params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
	params.add(new QueryParam<String>(String.class, getObject().getTitle()));
	return params;
    }

    @Override
    public void setID(int index, int id) {
    }

}