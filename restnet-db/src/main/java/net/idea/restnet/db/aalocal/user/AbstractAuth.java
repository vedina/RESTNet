package net.idea.restnet.db.aalocal.user;

import java.sql.ResultSet;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

public abstract class AbstractAuth extends AbstractQuery<String, String, EQCondition, Boolean>
		implements IQueryRetrieval<Boolean>, IDBConfig {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1535351222781804623L;

	/**
	 * If found, will return true always.
	 */
	@Override
	public Boolean getObject(ResultSet rs) throws AmbitException {
		if (rs != null)
			try {
				while (rs.next())
					rs.getString(1);
			} catch (Exception x) {
			}
		return rs != null;
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
