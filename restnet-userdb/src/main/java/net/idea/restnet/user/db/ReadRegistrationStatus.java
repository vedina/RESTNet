package net.idea.restnet.user.db;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.u.RegistrationStatus;
import net.idea.restnet.u.UserRegistration;
import net.idea.restnet.user.DBUser;

public class ReadRegistrationStatus extends AbstractQuery<DBUser, UserRegistration, EQCondition, UserRegistration>
	implements IQueryRetrieval<UserRegistration>, IDBConfig {
    /**
	 * 
	 */
    private static final long serialVersionUID = 6228939989116141217L;

    protected String databaseName = null;

    @Override
    public void setDatabaseName(String name) {
	databaseName = name;
    }

    @Override
    public String getDatabaseName() {
	return databaseName;
    }

    protected static String sql = "SELECT user_name,created,confirmed,status from `%s`.user_registration where user_name=?";

    public ReadRegistrationStatus() {
	super();
    }

    @Override
    public double calculateMetric(UserRegistration object) {
	return 1;
    }

    public boolean isPrescreen() {
	return false;
    }

    public List<QueryParam> getParameters() throws AmbitException {
	List<QueryParam> params = null;
	if (getFieldname() == null || getFieldname().getUserName() == null)
	    throw new AmbitException("Empty argument!");
	params = new ArrayList<QueryParam>();
	params.add(new QueryParam<String>(String.class, getFieldname().getUserName()));
	return params;
    }

    public String getSQL() throws AmbitException {
	if (getFieldname() == null || getFieldname().getUserName() == null)
	    throw new AmbitException("Empty argument!");
	if (getDatabaseName() == null)
	    throw new AmbitException("Database not specified!");
	return String.format(sql, getDatabaseName());
    }

    @Override
    public UserRegistration getObject(ResultSet rs) throws AmbitException {
	try {
	    UserRegistration p = new UserRegistration();
	    p.setConfirmationCode(null);
	    p.setStatus(RegistrationStatus.valueOf(rs.getString("status")));
	    p.setTimestamp_confirmed(rs.getLong("confirmed"));
	    p.setTimestamp_created(rs.getLong("created"));
	    return p;
	} catch (Exception x) {
	    return null;
	}
    }

    @Override
    public String toString() {
	return "Registration";
    }

}
