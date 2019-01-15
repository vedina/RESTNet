package net.idea.restnet.user.app.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.user.DBUser;

public class DeleteApp extends AbstractAppUpdate<DBUser> {
	protected static final String[] sql_key = { "UPDATE apps set enabled=0 where token=?" };

	public DeleteApp(DBUApp app, DBUser user) {
		super(app);
		setGroup(user);
	}

	public DeleteApp() {
		this(null, null);
	}

	public List<QueryParam> getParameters(int index) throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();

		if (getObject() != null)
			params.add(new QueryParam<String>(String.class, getObject().getKey().getToken()));
		else
			throw new AmbitException("Missing key!");
		return params;

	}

	public String[] getSQL() throws AmbitException {
		return sql_key;

	}

	public void setID(int index, int id) {

	}
}