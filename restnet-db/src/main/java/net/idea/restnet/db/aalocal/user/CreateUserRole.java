package net.idea.restnet.db.aalocal.user;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.update.AbstractUpdate;
import net.idea.restnet.db.aalocal.DBRole;

public class CreateUserRole extends AbstractUpdate<DBRole,IUser> implements IDBConfig{

	@Override
	public String[] getSQL() throws AmbitException {
		return new String[] {
			"insert ignore into roles values (?)",
			"insert ignore into user_roles values (?,?)"
		};
	}

	@Override
	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		switch (index) {
		case 0: {
			params.add(new QueryParam<String>(String.class, getGroup().getName()));
			break;
		}
		case 1: {
			params.add(new QueryParam<String>(String.class, getObject().getUsername()));
			params.add(new QueryParam<String>(String.class, getGroup().getName()));
			break;
		}
		}
		return params;
	}

	@Override
	public void setID(int index, int id) {

	}

	protected String databaseName = null;
	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}
	@Override
	public String getDatabaseName() {
		return databaseName;
	}

}
