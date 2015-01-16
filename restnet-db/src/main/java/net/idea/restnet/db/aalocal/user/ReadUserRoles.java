package net.idea.restnet.db.aalocal.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

public class ReadUserRoles extends AbstractQuery<String, String, EQCondition, String> implements
	IQueryRetrieval<String>, IDBConfig {

    /**
	 * 
	 */
    private static final long serialVersionUID = -769124569395580317L;
    protected static final String sql = "SELECT user_name,role_name FROM %suser_roles where user_name=?";

    @Override
    public String getSQL() throws AmbitException {
	return String.format(sql, databaseName == null ? "" : String.format("`%s`.", databaseName));
    }

    @Override
    public List<QueryParam> getParameters() throws AmbitException {
	if (getValue() == null)
	    throw new AmbitException("No user name!");
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<String>(String.class, getValue()));
	return params;
    }

    @Override
    public String getObject(ResultSet rs) throws AmbitException {
	try {
	    return rs.getString("role_name");
	} catch (Exception x) {
	    throw new AmbitException(x);
	}
    }

    protected String databaseName = null;

    @Override
    public void setDatabaseName(String name) {
	this.databaseName = name;
    }

    @Override
    public String getDatabaseName() {
	return databaseName;
    }

    @Override
    public boolean isPrescreen() {
	return false;
    }

    @Override
    public double calculateMetric(String object) {
	return 1;
    }

}
