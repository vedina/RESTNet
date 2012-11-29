package net.idea.restnet.db.aalocal;

import java.sql.Connection;
import java.sql.ResultSet;

import net.idea.modbcum.p.QueryExecutor;
import net.idea.restnet.db.DBConnection;
import net.idea.restnet.db.aalocal.user.ReadUserRoles;

import org.restlet.Context;
import org.restlet.data.ClientInfo;
import org.restlet.security.Enroler;
import org.restlet.security.Role;

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
		executor.setCloseConnection(false);
	}	

	@Override
	public synchronized void  enrole(ClientInfo clientInfo) {
		if ((clientInfo.getUser()==null) || 
			(clientInfo.getUser().getIdentifier()==null) ||
			("".equals(clientInfo.getUser().getIdentifier()))) return;
				
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
				if (role!=null) clientInfo.getRoles().add(createRole(role));
			}
		} catch (Exception x) {
			x.printStackTrace();
		} finally {
			try {executor.setConnection(null);} catch (Exception x) {};
			try {rs.close();} catch (Exception x) {};
			try {c.close();} catch (Exception x) {};
		}

	}
	
	protected Role createRole(String name) {
		return new DBRole(name,String.format("%s%s", name.substring(0,1).toUpperCase(),name.substring(1).replace("_", " ")));	 
	}
	/**
	 * Override to set  the proper config
	 * @return
	 */
	protected String getConfigFile() {
		return config;
	}
}
