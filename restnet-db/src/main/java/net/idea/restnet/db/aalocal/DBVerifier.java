package net.idea.restnet.db.aalocal;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.user.UserAuth;

import org.restlet.Context;
import org.restlet.security.SecretVerifier;


public class DBVerifier extends SecretVerifier {
	protected Context context;
	protected String config;
	protected QueryExecutor<UserAuth> executor = new QueryExecutor<UserAuth>();
	protected UserAuth query = new UserAuth();

	public DBVerifier(Context context) {
		this(context,null,null);
	}
	
	public DBVerifier(Context context,String configfile,String dbName) {
		super();
		this.context = context;
		this.config = configfile;
		query.setDatabaseName(dbName);
	}

	@Override
	public boolean verify(String identifier, char[] inputSecret) {
		Connection c = null;
		ResultSet rs = null;
		try {
			query.setFieldname(identifier);
			query.setValue(new String(inputSecret));
			DBConnection dbc = new DBConnection(context,getConfigFile());
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
			try {rs.close();} catch (Exception x) {};
			try {c.close();} catch (Exception x) {};
		}
		return false;
	}

	/**
	 * Override to set  the proper config
	 * @return
	 */
	protected String getConfigFile() {
		return config;
	}
	
}
