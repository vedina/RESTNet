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
import org.restlet.data.Method;
import org.restlet.security.RoleAuthorizer;

public class PolicyAuthorizer<PQ extends PolicyQuery> extends RoleAuthorizer {
	private Context context;
	protected String config;
	private PQ pquery;

	protected RESTPolicy policy;
	
	public PolicyAuthorizer(Context context, String configfile,String datadbname,String usersdbName) {
		super();
		pquery = createPolicyQuery(datadbname);
		this.context = context;
		this.config = configfile;
		getPolicyQuery().setDatabaseName(usersdbName);

	}

	protected PQ createPolicyQuery(String datadbname) {
		PolicyQuery pq = new PolicyQuery();
		return (PQ)pq;
	}
	protected String rewriteURI(String uri) {
		return uri;
	}

	protected Method rewriteMethod(String uri, Method method) {
		return method;
	}

	public boolean authorizeSpecialCases(Request request, Response response,
			List<String> uri) {
		return false;
	}
	
	protected PQ getPolicyQuery() {
		return pquery;
	}

	@Override
	public boolean authorize(Request request, Response response) {
		List<String> uri = new ArrayList<String>();
		if (authorizeSpecialCases(request, response, uri))
			return true;

		return authorizeByPolicy(request, response, uri);
	}
	public boolean isOwner(String user,QueryExecutor<PolicyQuery> executor,RESTPolicy policy) {
		return false;
	}
	protected RESTPolicy initRESTPolicy() {
		return new RESTPolicy();	
	}
	public boolean authorizeByPolicy(Request request, Response response, List<String> uri) {
		if ((request.getClientInfo() == null)
				|| (request.getClientInfo().getUser() == null)
				|| (request.getClientInfo().getUser().getIdentifier() == null))
			return false;
		
		Connection c = null;
		ResultSet rs = null;
		QueryExecutor<PolicyQuery> executor = new QueryExecutor<PolicyQuery>(
				true);
		try {

			DBConnection dbc = new DBConnection(context, getConfigFile());
			c = dbc.getConnection();
			executor.setCloseConnection(false);
			executor.setConnection(c);
			executor.setCache(true);

			
			for (int j = uri.size() - 1; j >= 0; j--) {
				
				// System.out.print(uri.get(j));
				policy = initRESTPolicy();
				policy.setUri(rewriteURI(uri.get(j)));
				
				if (isOwner(request.getClientInfo().getUser()
						.getIdentifier(),executor,policy)) return true;

				// System.out.print("\tconnection\t");
				// System.out.print(executor.getConnection());
				// System.out.print("\t");
				// System.out.println(executor.getConnection().isClosed());
				getPolicyQuery().setMethod(rewriteMethod(uri.get(j), request.getMethod()));
				getPolicyQuery().setFieldname(policy);
				getPolicyQuery().setValue(request.getClientInfo().getUser()
						.getIdentifier());
				try {
					rs = executor.process(getPolicyQuery());
					int found = 0;
					boolean ok = false;
					while (rs.next()) {
						ok = getPolicyQuery().getObject(rs);
						found++;
					}
					if (found > 0)
						return ok;
					else
						continue;
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {
						executor.closeResults(rs);
					} catch (Exception x) {
					}
					;
				}
			}
			return false;
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {
				if (c != null)
					c.close();
				c = null;
			} catch (Exception x) {
			}
			;
			try {
				if (executor != null)
					executor.close();
				executor.setConnection(null);
			} catch (Exception x) {
			}
			;
		}
		return false;
	}

	protected String getConfigFile() {
		return config;
	}

}
