package net.idea.restnet.db.aalocal;

import java.sql.Connection;
import java.sql.ResultSet;

import org.restlet.Context;
import org.restlet.security.SecretVerifier;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.user.UserAuth;

public class DBVerifier extends SecretVerifier {
	protected Context context;
	protected String config;
	protected QueryExecutor<UserAuth> executor = new QueryExecutor<UserAuth>(true);
	protected UserAuth query = new UserAuth();

	public DBVerifier(Context context) {
		this(context, null, null);
	}

	public DBVerifier(Context context, String configfile, String dbName) {
		super();
		this.context = context;
		this.config = configfile;
		query.setDatabaseName(dbName);
	}

	@Override
	public int verify(String identifier, char[] inputSecret) {
		int maxRetry = 3;
		Connection c = null;
		ResultSet rs = null;
		try {
			query.setFieldname(identifier);
			query.setValue(new String(inputSecret));
			DBConnection dbc;
			for (int i = 0; i < maxRetry; i++) {
				dbc = new DBConnection(context, getConfigFile());
				try {
					c = dbc.getConnection();
					executor.setConnection(c);
					rs = executor.process(query);
					boolean ok = false;
					while (rs.next()) {
						ok = query.getObject(rs);
						break;
					}
					return ok?RESULT_VALID:RESULT_INVALID;
				} catch (Exception x) {
					x.printStackTrace();
				} finally {
					try {
						rs.close();
						rs = null;
					} catch (Exception x) {
					}
					;
					try {
						c.close();
						c = null;
					} catch (Exception x) {
					}
					;
				}
			}
			return RESULT_MISSING;
		} catch (Exception x) {
			
			x.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception x) {
			}
			;
			try {
				if (c != null)
					c.close();
			} catch (Exception x) {
			}
			;
		}
		return RESULT_MISSING;
	}

	/**
	 * Override to set the proper config
	 * 
	 * @return
	 */
	protected String getConfigFile() {
		return config;
	}

}
