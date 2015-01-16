package net.idea.restnet.db.aalocal.user;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;

/**
 * Verifies if username {link {@link #getFieldname()} and password {link
 * {@link #getValue()} exist.
 * 
 * @author nina
 * 
 */
public class UserAuth extends AbstractQuery<String, String, EQCondition, Boolean> implements IQueryRetrieval<Boolean>,
	IDBConfig {

    /**
	 * 
	 */
    private static final long serialVersionUID = 6017803463536586392L;
    protected final String sql = "select user_name from %susers join %suser_registration using(user_name) where status='confirmed' and user_name = ? and user_pass = md5(?)";

    public double calculateMetric(Boolean object) {
	return 1;
    }

    public boolean isPrescreen() {
	return false;
    }

    public List<QueryParam> getParameters() throws AmbitException {

	if ((getValue() == null) || (getFieldname() == null))
	    throw new AmbitException("Empty parameters");
	List<QueryParam> params = new ArrayList<QueryParam>();
	params.add(new QueryParam<String>(String.class, getFieldname()));
	params.add(new QueryParam<String>(String.class, getValue()));
	return params;
    }

    public String getSQL() throws AmbitException {
	return String.format(sql, databaseName == null ? "" : String.format("`%s`.", databaseName),
		databaseName == null ? "" : String.format("`%s`.", databaseName));
    }

    /**
     * If found, will return true always.
     */
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
