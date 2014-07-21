package net.idea.restnet.db.aalocal.policy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.i.aa.RESTPolicy;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.security.RoleAuthorizer;

import com.mysql.fabric.xmlrpc.base.Array;

public class PolicyAuthorizer extends RoleAuthorizer {
	private Context context;
	protected String config;
	protected PolicyQuery query = new PolicyQuery();
	protected QueryExecutor<PolicyQuery> executor = new QueryExecutor<PolicyQuery>();
	protected RESTPolicy policy;
	public PolicyAuthorizer(Context context,String configfile,String dbName) {
		super();
		this.context = context;
		this.config = configfile;
		query.setDatabaseName(dbName);
	}
	
	public boolean authorizeSpecialCases(Request request, Response response,List<String> uri) {
		return false;
	}
	@Override
	public boolean authorize(Request request, Response response) {
		List<String> uri = new ArrayList<String>();
		if (authorizeSpecialCases(request,response,uri)) return true;
		
		if ((request.getClientInfo()==null) 
				|| (request.getClientInfo().getUser()==null)
				|| (request.getClientInfo().getUser().getIdentifier()==null)) return false;
		
		
		int maxRetry = 3;
		Connection c = null;
		ResultSet rs = null;
		try {
			for (int j=uri.size()-1; j>=0; j--) {

				policy = new RESTPolicy();
				policy.setUri(uri.get(j));
				
				query.setFieldname(policy);
				query.setValue(request.getClientInfo().getUser().getIdentifier());
				DBConnection dbc;
				for (int i=0; i < maxRetry; i++) {
					dbc = new DBConnection(context,getConfigFile());
					try {
						c = dbc.getConnection();
						executor.setConnection(c);
						rs = executor.process(query);
						int found = 0;
						boolean ok = false;
						while (rs.next()) {
							ok = query.getObject(rs);
							found++;
							break;
						}
						if (found>0) return ok;
						else break;
					} catch (Exception x) {
						x.printStackTrace();
					} finally {
						try {rs.close(); rs = null;} catch (Exception x) {};
						try {c.close(); c = null;} catch (Exception x) {};
					}
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
