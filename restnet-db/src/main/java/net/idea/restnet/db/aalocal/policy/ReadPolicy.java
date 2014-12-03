package net.idea.restnet.db.aalocal.policy;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.idea.restnet.db.aalocal.user.IDBConfig;
import net.idea.restnet.i.aa.IRESTPolicy;
import net.idea.restnet.i.aa.RESTPolicy;

public class ReadPolicy  extends AbstractQuery<IRESTPolicy<Integer>, String, EQCondition, IRESTPolicy<Integer>> 
									implements IQueryRetrieval<IRESTPolicy<Integer>> , IDBConfig{

	/**
	 * 
	 */
	private static final long serialVersionUID = -740189128538619419L;
	
	private static String sql = "select idpolicy,role_name,prefix,resource,mget,mput,mpost,mdelete from %spolicy\n";
	private static String sql_byid = "idpolicy=?\n";
	@Override
	public String getSQL() throws AmbitException {
		String q =  String.format(sql,
				 getDatabaseName()==null?"":String.format("`%s`.",getDatabaseName())
						);
		if (getFieldname()!=null) {
			if (getFieldname().getId()>0) {
				return String.format("%s where %s",q,sql_byid);
			}
		}	
		return q;
	}

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = null;
		if (getFieldname()!=null) {
			if (getFieldname().getId()>0) {
				QueryParam<Integer> p = new QueryParam<Integer>(Integer.class,getFieldname().getId());
				params = new ArrayList<QueryParam>();
				params.add(p);
			}
		}
		return params;
	}

	@Override
	public IRESTPolicy<Integer> getObject(ResultSet rs) throws AmbitException {
		try {
			RESTPolicy policy = new RESTPolicy();
			policy.setId(rs.getInt(1));
			policy.setRole(rs.getString(2));
			policy.setUri(rs.getString(3)+rs.getString(4));
			policy.setAllowGET(rs.getBoolean(5));
			policy.setAllowPUT(rs.getBoolean(6));
			policy.setAllowPOST(rs.getBoolean(7));
			policy.setAllowDELETE(rs.getBoolean(8));
			return policy;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
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

	@Override
	public boolean isPrescreen() {
		return false;
	}

	@Override
	public double calculateMetric(IRESTPolicy<Integer> object) {
		return 1;
	}

	
}
