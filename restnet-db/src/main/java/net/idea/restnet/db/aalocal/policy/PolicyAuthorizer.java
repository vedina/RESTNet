package net.idea.restnet.db.aalocal.policy;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.RoleAuthorizer;

public class PolicyAuthorizer extends RoleAuthorizer {
	private Context context;
	protected String config;
	protected PolicyQuery query = new PolicyQuery();
	protected QueryExecutor<PolicyQuery> executor = new QueryExecutor<PolicyQuery>();
	
	public PolicyAuthorizer(Context context,String configfile,String dbName) {
		super();
		this.context = context;
		this.config = configfile;
		query.setDatabaseName(dbName);
	}
	
	public boolean authorizeSpecialCases(Request request, Response response,StringBuilder uri) {
		return false;
	}
	@Override
	public boolean authorize(Request request, Response response) {
		StringBuilder uri = new StringBuilder();
		if (authorizeSpecialCases(request,response,uri)) return true;
		
		if ((request.getClientInfo()==null) 
				|| (request.getClientInfo().getUser()==null)
				|| (request.getClientInfo().getUser().getIdentifier()==null)) return false;
		
		
		int maxRetry = 3;
		Connection c = null;
		ResultSet rs = null;
		try {
			query.setFieldname(uri.toString());
			query.setValue(request.getClientInfo().getUser().getIdentifier());
			DBConnection dbc;
			for (int i=0; i < maxRetry; i++) {
				dbc = new DBConnection(context,getConfigFile());
				try {
					c = dbc.getConnection();
					executor.setConnection(c);
					rs = executor.process(query);
					boolean ok = false;
					while (rs.next()) {
						ok = query.getObject(rs);
						break;
					}
					return ok;
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {rs.close(); rs = null;} catch (Exception x) {};
					try {c.close(); c = null;} catch (Exception x) {};
				}
			}
			return false;
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {if (rs!=null) rs.close();} catch (Exception x) {};
			try {if (c!=null) c.close();} catch (Exception x) {};
		}
		return false;
	}
	
	protected String getConfigFile() {
			return config;
	}

}
