package net.idea.restnet.user.app.db;

import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.restnet.c.exception.InvalidUserException;
import net.idea.restnet.user.DBUser;

public class AddApp extends AbstractAppUpdate<DBUser> {
	public static final String[] sql_addApp_byuserid = new String[] {
			"insert into apps SELECT username,?,?,?,?,now(),DATE_ADD(now(), INTERVAL 60 DAY),? from user where iduser=? ",
			};
	public static final String[] sql_addApp_byusername = new String[] {
			"insert into apps SELECT username,?,?,?,?,now(),DATE_ADD(now(), INTERVAL 60 DAY),? from user where username=? ",
			};
	

	public AddApp(DBUApp app, DBUser author) {
		super(app);
		setGroup(author);
	}

	public AddApp() {
		this(null, null);
	}

	public List<QueryParam> getParameters(int index) throws AmbitException {
		if (getGroup() == null)
			throw new InvalidUserException();
		switch (index) {
		case 0: {
			List<QueryParam> params = new ArrayList<QueryParam>();
			params.add(new QueryParam<String>(String.class, getObject().getName()));
			params.add(new QueryParam<String>(String.class, getObject().getKey().getToken()));
			params.add(new QueryParam<String>(String.class, getObject().getKey().getTokenType()));
			params.add(new QueryParam<String>(String.class, getObject().getReferer()));
			params.add(new QueryParam<String>(String.class, getObject().getScope()));

			if (getGroup().getID() > 0)
				params.add(new QueryParam<Integer>(Integer.class, getGroup().getID()));
			else if (getGroup().getUserName() != null)
				params.add(new QueryParam<String>(String.class, getGroup().getUserName()));
			else
				throw new InvalidUserException();
			return params;
		}
		
		}
		return null;
	}

	public String[] getSQL() throws AmbitException {
		if (getGroup() == null)
			throw new InvalidUserException();
		else if (getGroup().getID() > 0)
			return sql_addApp_byuserid;
		else if (getGroup().getUserName() != null)
			return sql_addApp_byusername;
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