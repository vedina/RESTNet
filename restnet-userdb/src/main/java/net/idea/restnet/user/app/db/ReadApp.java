package net.idea.restnet.user.app.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.idea.restnet.c.exception.InvalidAlertException;
import net.idea.restnet.c.exception.InvalidUserException;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.user.DBUser;
import net.idea.restnet.user.alerts.db.DBAlert;

public class ReadApp extends AbstractQuery<DBUser, DBUApp, EQCondition, DBUApp>
		implements IQueryRetrieval<DBUApp>, IDBConfig {
	protected String sql = "select username,token,tokentype,name,referer,created,expire,scope from `%s`.apps  ";
	/**
	 * 
	 */
	private static final long serialVersionUID = 888018870900333768L;
	protected String databaseName = null;

	@Override
	public void setDatabaseName(String name) {
		databaseName = name;
	}

	@Override
	public String getDatabaseName() {
		return databaseName;
	}

	public ReadApp(int id) {
		this(new DBUApp(id));
	}

	public ReadApp(DBUApp item) {
		super();
		setValue(item);
	}

	public ReadApp(DBUApp item, DBUser user) {
		this(item);
		setFieldname(user);
	}

	@Override
	public String getSQL() throws AmbitException {
		StringBuilder b = new StringBuilder();
		b.append(String.format(sql,getDatabaseName(),getDatabaseName(),getDatabaseName()));
		String d = " where ";
		if (getFieldname() != null) {
			if (getFieldname().getID() > 0) {
				b.append(d);
				b.append(DBAlert._fields.iduser.getCondition());
			} else if (getFieldname().getUserName() != null) {
				b.append(d);
				b.append(" username=? ");
			} else
				throw new InvalidUserException();
		} else
			throw new InvalidUserException();
		d = " and ";
		if ((getValue() != null) && (getValue().getKey() != null)) {
			b.append(d);
			b.append(DBUApp._fields.token.getCondition());
		}

		return b.toString();
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();

		if (getFieldname() != null) {
			if (getFieldname().getID() > 0)
				params.add(new QueryParam<Integer>(Integer.class, getFieldname().getID()));
			else if (getFieldname().getUserName() != null)
				params.add(new QueryParam<String>(String.class, getFieldname().getUserName()));
			else
				throw new InvalidUserException();
		} else
			throw new InvalidUserException();
		if (getValue() != null) {
			if (getValue().getKey() == null)
				throw new InvalidAlertException();
			params.add(new QueryParam<String>(String.class, getValue().getKey().getToken()));
		}
		return params;
	}

	// new DBGroup(getValue().getGroupType());
	@Override
	public DBUApp getObject(ResultSet rs) throws AmbitException {
		try {
			DBUApp item = new DBUApp();
			for (DBUApp._fields field : DBUApp._fields.values())
				field.setParam(item, rs);

			return item;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(DBUApp object) {
		return 1;
	}

	

}