package net.idea.restnet.db.aalocal.policy;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.i.IQueryRetrieval;
import net.idea.modbcum.i.exceptions.AmbitException;
import net.idea.modbcum.i.query.QueryParam;
import net.idea.modbcum.q.conditions.EQCondition;
import net.idea.modbcum.q.query.AbstractQuery;
import net.idea.restnet.db.aalocal.user.IUser;

import org.restlet.data.Method;

public class PolicyQuery  extends AbstractQuery<String,IUser, EQCondition, Boolean> implements IQueryRetrieval<Boolean> {
	protected Method method = Method.GET;
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4061687019602653207L;

	@Override
	public List<QueryParam> getParameters() throws AmbitException {
		List<QueryParam> params = new ArrayList<QueryParam>();
		params.add(new QueryParam<String>(String.class,getValue().getUserName()));
		params.add(new QueryParam<String>(String.class,getFieldname()));
		return params;
	}

	@Override
	public String getSQL() throws AmbitException {
		return String.format(
				"select prefix,resource,count(*) from policy join user_roles using(role_name) where user_name=? and resource=? and m%s=1",
				getMethod().getName().toLowerCase()
				);
	}

	@Override
	public Boolean getObject(ResultSet rs) throws AmbitException {
		try {
			int count = rs.getInt(3);
			return count>0;
		} catch (Exception x) {
			throw new AmbitException(x);
		}
	}

	@Override
	public double calculateMetric(Boolean arg0) {
		return 1;
	}

	@Override
	public boolean isPrescreen() {
		return false;
	}

}
