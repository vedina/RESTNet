package net.idea.restnet.db.aalocal;

import java.sql.Connection;
import java.sql.ResultSet;

import org.restlet.Context;
import org.restlet.security.SecretVerifier;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.user.AbstractAuth;
import net.idea.restnet.db.aalocal.user.UserAuth;

public class DBVerifier<Q extends AbstractAuth> extends SecretVerifier {
	protected Context context;
	protected String config;
	protected QueryExecutor<AbstractAuth> executor = new QueryExecutor<AbstractAuth>(true);
	protected AbstractAuth query;
	

	public DBVerifier(Context context, AbstractAuth query) {
		this(context, null, null, query);
	}

	
	public DBVerifier(Context context, String configfile, String dbName, AbstractAuth query) {
		super();
		this.context = context;
		this.config = configfile;
		this.query = query == null ? new UserAuth() : query;
		this.query.setDatabaseName(dbName);
	}

	protected boolean processQueryResult(String identifier) {
		return identifier != null;
	}
	@Override
	public boolean verify(String identifier, char[] inputSecret) {
		if (inputSecret == null) return false;
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
						ok = processQueryResult(query.getObject(rs));
						break;
					}
					return ok;
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
			return false;
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
		return false;
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
