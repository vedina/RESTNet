package net.idea.restnet.db.aalocal;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.user.ReadUserRoles;

import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.security.Enroler;

public class DbEnroller implements Enroler {
	protected Context context;
	protected String config;
	protected QueryExecutor<ReadUserRoles> executor = new QueryExecutor<ReadUserRoles>();
	protected ReadUserRoles query = new ReadUserRoles();
	

	public DbEnroller(Context context) {
		this(context,null,null);
	}
	
	public DbEnroller(Context context,String configfile,String dbName) {
		super();
		this.context = context;
		this.config = configfile;
		query.setDatabaseName(dbName);
	}	

	@Override
	public void enrole(ClientInfo clientInfo) {

		Connection c = null;
		ResultSet rs = null;
		try {
			query.setValue(new String(clientInfo.getUser().getIdentifier()));
			DBConnection dbc = new DBConnection(context,getConfigFile());
			c = dbc.getConnection();
			executor.setConnection(c);
			rs = executor.process(query);
			while (rs.next()) {
				String role = query.getObject(rs);
				if (role!=null) clientInfo.getRoles().add(new DBRole(role,role));
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {rs.close();} catch (Exception x) {};
			try {c.close();} catch (Exception x) {};
		}

	}
	/**
	 * Override to set  the proper config
	 * @return
	 */
	protected String getConfigFile() {
		return config;
	}
}
