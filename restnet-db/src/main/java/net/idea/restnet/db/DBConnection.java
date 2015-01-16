package net.idea.restnet.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import net.idea.modbcum.c.DatasourceFactory;
import net.idea.modbcum.i.LoginInfo;
import net.idea.modbcum.i.config.Preferences;
import net.idea.modbcum.i.exceptions.AmbitException;

import org.restlet.Context;

public class DBConnection {
    protected static ConcurrentHashMap<String, Properties> propertiesMap = new ConcurrentHashMap<String, Properties>();
    protected String configFile;

    protected LoginInfo loginInfo;

    public LoginInfo getLoginInfo() {
	return loginInfo;
    }

    public DBConnection(Context context, String configFile) {
	super();
	this.configFile = configFile;
	loginInfo = getLoginInfo(context);
    }

    protected synchronized void loadProperties(String configFile) {
	try {
	    Properties properties = propertiesMap.get(configFile);
	    if (properties == null) {
		properties = new Properties();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(configFile);
		properties.load(in);
		in.close();
		propertiesMap.put(configFile, properties);
	    }
	} catch (Exception x) {
	    Properties properties = new Properties();
	    properties.put("Exception", x.getMessage());
	    propertiesMap.put(configFile, properties);
	}
    }

    public boolean allowDBCreate() {
	loadProperties(configFile);
	Object ok = getProperty("database.create");
	return (ok != null) && ok.toString().toLowerCase().equals("true");
    }

    public String rdfWriter() {
	loadProperties(configFile);
	Object rdfwriter = getProperty("rdf.writer");
	return (rdfwriter == null) ? "jena" : rdfwriter.toString();// jena or
								   // stax
    }

    /**
     * 
     * @return
     */
    public boolean dataset_prefixed_compound_uri() {
	loadProperties(configFile);
	Object prefix = getProperty("dataset.members.prefix");
	try {
	    return Boolean.parseBoolean(prefix.toString());
	} catch (Exception x) {
	    return false;
	}
    }

    protected LoginInfo getLoginInfo(Context context) {
	loadProperties(configFile);
	LoginInfo li = new LoginInfo();

	Object p = getProperty("Database");
	li.setDatabase(p == null || ("${ambit.db}".equals(p)) ? "ambit2" : p.toString());
	p = getProperty("Port");
	li.setPort(p == null ? "3306" : p.toString());
	p = getProperty("User");
	li.setUser(p == null ? "guest" : p.toString());
	p = getProperty("Password");
	li.setPassword(p == null ? "guest" : p.toString());
	p = getProperty(Preferences.HOST);
	li.setHostname(p == null || ("${ambit.db.host}".equals(p)) ? "localhost" : p.toString());
	p = getProperty(Preferences.DRIVERNAME);
	li.setDriverClassName(p == null || (p.toString().startsWith("${")) ? "com.mysql.jdbc.Driver" : p.toString());

	if (context.getParameters().getFirstValue(Preferences.DATABASE) != null)
	    li.setDatabase(context.getParameters().getFirstValue(Preferences.DATABASE));
	if (context.getParameters().getFirstValue(Preferences.USER) != null)
	    li.setUser(context.getParameters().getFirstValue(Preferences.USER));
	if (context.getParameters().getFirstValue(Preferences.PASSWORD) != null)
	    li.setPassword(context.getParameters().getFirstValue(Preferences.PASSWORD));
	if (context.getParameters().getFirstValue(Preferences.HOST) != null)
	    li.setHostname(context.getParameters().getFirstValue(Preferences.HOST));
	if (context.getParameters().getFirstValue(Preferences.PORT) != null)
	    li.setPort(context.getParameters().getFirstValue(Preferences.PORT));
	if (context.getParameters().getFirstValue(Preferences.DRIVERNAME) != null)
	    li.setDriverClassName(context.getParameters().getFirstValue(Preferences.DRIVERNAME));

	return li;
    }

    protected String getConnectionURI() throws AmbitException {
	return getConnectionURI(null, null);
    }

    /*
     * protected String getConnectionURI(Request request) throws AmbitException
     * { if (request == null) return getConnectionURI(null,null); else if
     * (request.getChallengeResponse()==null) return
     * getConnectionURI(null,null); else try { return
     * getConnectionURI(request.getChallengeResponse().getIdentifier(), new
     * String(request.getChallengeResponse().getSecret())); } catch (Exception
     * x) { return getConnectionURI(null,null); } }
     */
    protected String getConnectionURI(String user, String password) throws AmbitException {

	try {
	    LoginInfo li = loginInfo;
	    return DatasourceFactory.getConnectionURI(li.getScheme(), li.getHostname(), li.getPort(), li.getDatabase(),
		    user == null ? li.getUser() : user, password == null ? li.getPassword() : password);
	} catch (Exception x) {
	    throw new AmbitException(x);
	}

    }

    public synchronized Connection getConnection(String user, String password) throws AmbitException, SQLException {
	return getConnection(getConnectionURI(user, password));
    }

    public synchronized Connection getConnection() throws AmbitException, SQLException {
	// if (connectionURI == null)
	// connectionURI = getConnectionURI();
	return getConnection(getConnectionURI(null, null));
    }

    /*
     * public synchronized Connection getConnection(Request request) throws
     * AmbitException , SQLException{ //if (connectionURI == null) //
     * connectionURI = getConnectionURI(); return
     * getConnection(getConnectionURI(request)); }
     */

    public synchronized Connection getConnection(String connectionURI) throws AmbitException, SQLException {
	SQLException error = null;
	Connection c = null;

	ResultSet rs = null;
	Statement t = null;
	for (int retry = 0; retry < 3; retry++)
	    try {
		DataSource ds = DatasourceFactory.getDataSource(connectionURI, loginInfo.getDriverClassName());
		/*
		 * if ( ds instanceof PooledDataSource) { PooledDataSource pds =
		 * (PooledDataSource) ds; System.err.println("num_connections: "
		 * + pds.getNumConnectionsDefaultUser());
		 * System.err.println("num_busy_connections: " +
		 * pds.getNumBusyConnectionsDefaultUser());
		 * System.err.println("num_idle_connections: " +
		 * pds.getNumIdleConnectionsDefaultUser());
		 * System.err.println("num_thread_awaiting: "
		 * +pds.getNumThreadsAwaitingCheckoutDefaultUser());
		 * System.err.println
		 * ("StatementCacheNumCheckedOutStatementsAllUsers: "
		 * +pds.getStatementCacheNumCheckedOutStatementsAllUsers());
		 * System.err.println("num_unclosed_orphaned_connections: "
		 * +pds.getNumUnclosedOrphanedConnectionsAllUsers());
		 * 
		 * System.err.println(); } else
		 * System.err.println("Not a c3p0 PooledDataSource!");
		 */
		c = ds.getConnection();
		t = c.createStatement();
		rs = t.executeQuery("SELECT 1 FROM DUAL");
		while (rs.next()) {
		    rs.getInt(1);
		}
		rs.close();
		rs = null;
		t.close();
		t = null;
		error = null;
		return c;
	    } catch (SQLException x) {
		error = x;
		Context.getCurrentLogger().severe(x.getMessage());
		// remove the connection from the pool
		try {
		    if (c != null)
			c.close();
		} catch (Exception e) {
		}
	    } catch (Throwable x) {
		Context.getCurrentLogger().severe(x.getMessage());
		try {
		    if (c != null)
			c.close();
		} catch (Exception e) {
		}
	    } finally {
		try {
		    if (rs != null)
			rs.close();
		} catch (Exception x) {
		}
		try {
		    if (t != null)
			t.close();
		} catch (Exception x) {
		}
	    }
	if (error != null)
	    throw error;
	else
	    throw new SQLException("Can't establish connection " + connectionURI);
    }

    /*
     * 
     * public synchronized Connection getConnection(String connectionURI) throws
     * AmbitException , SQLException{
     * 
     * return DatasourceFactory.getDataSource(connectionURI).getConnection();
     * 
     * 
     * }
     */
    protected Object getProperty(String key) {
	Properties p = propertiesMap.get(configFile);
	return p == null ? null : p.get(key);
    }

}
